package com.efan.xrecyclerview;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import java.util.ArrayList;
import java.util.List;

public class DemoActivity extends AppCompatActivity {

    List<ItemDto> dataList = new ArrayList<>();
    XRecyclerView xRecyclerView;
    MyAdapter mAdapter = new MyAdapter();
    LinearLayoutManager listLM;
    GridLayoutManager gridLM;
    ListDividerDecoration listDividerDecoration;
    GridDividerDecoration gridDividerDecoration;
    List<XAdapter.HeaderAdapter> headerAdapters = new ArrayList<>();
    FABGroup fabGroup;

    private static final int Refresh_Success = 0;
    private static final int Refresh_Empty = 1;
    private static final int Refresh_Error = 2;

    private static final int SPAN_COUNT = 3;
    private boolean atGrid = false;

    private int nextRefreshState = Refresh_Success;
    private boolean nextMoreSuccess = true;

    private int getNextRefresh(){
        switch (nextRefreshState){
            case Refresh_Error:
                return Refresh_Empty;
            case Refresh_Empty:
                return Refresh_Success;
            case Refresh_Success:
            default:
                return Refresh_Error;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        initFabGroup();

        xRecyclerView = (XRecyclerView) findViewById(R.id.x_recycler_view);

        listLM = new LinearLayoutManager(this);
        Drawable divider = new ColorDrawable(Color.parseColor("#FF0000"));
        listDividerDecoration = new ListDividerDecoration(this, divider);
        gridDividerDecoration = new GridDividerDecoration(30);

        new XRecyclerView.Builder()
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refresh();
                    }
                })
                .setOnLoadMoreListener(new LoadMoreAdapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        loadMore();
                    }
                })
                .setDataAdapter(mAdapter)
                .setLayoutManager(listLM)
                .addItemDecoration(listDividerDecoration)
                .build(xRecyclerView);

        gridLM = new GridLayoutManager(this, SPAN_COUNT);
        GridHeaderLookup lookup = new GridHeaderLookup(xRecyclerView.getXAdapter(), SPAN_COUNT);
        gridLM.setSpanSizeLookup(lookup);

        xRecyclerView.setHeader(headerAdapters);
        xRecyclerView.setScrollViewCallbacks(scrollCallback);
        xRecyclerView.setLoading();
        refresh();
    }

    private void initFabGroup() {
        fabGroup = (FABGroup) findViewById(R.id.fab_group);
        fabGroup.inflate(R.layout.fabgroup_opt);
        fabGroup.anim(true);


        fabGroup.findViewById(R.id.add_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHeader();
            }
        });
        fabGroup.findViewById(R.id.remove_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeHeader();
            }
        });

        fabGroup.findViewById(R.id.change_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAdapter();
            }
        });

    }

    private void changeAdapter() {
        atGrid = !atGrid;
        if (atGrid){
            xRecyclerView.removeItemDecoration(listDividerDecoration);
            xRecyclerView.addItemDecoration(gridDividerDecoration);
            mAdapter.setGrid();
            xRecyclerView.setLayoutManager(gridLM);
        } else {
            xRecyclerView.removeItemDecoration(gridDividerDecoration);
            xRecyclerView.addItemDecoration(listDividerDecoration);
            mAdapter.setList();
            xRecyclerView.setLayoutManager(listLM);
        }
        xRecyclerView.invalidate();
        xRecyclerView.notifyDataSetChanged();
    }

    private ObservableScrollViewCallbacks scrollCallback = new ObservableScrollViewCallbacks() {
        @Override
        public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

        }

        @Override
        public void onDownMotionEvent() {

        }

        @Override
        public void onUpOrCancelMotionEvent(ScrollState scrollState) {
            if (scrollState == ScrollState.UP) {
                fabGroup.anim(false);
            } else if (scrollState == ScrollState.DOWN) {
                fabGroup.anim(true);
            }
        }
    };


    private void refresh(){
        xRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (nextRefreshState){
                    case Refresh_Error:
                        dataList.clear();
                        xRecyclerView.onComplete(true, -1, false);
                        break;
                    case Refresh_Empty:
                        dataList.clear();
                        xRecyclerView.onComplete(true, 0, false);
                        break;
                    case Refresh_Success:
                    default:
                        dataList.clear();
                        dataList.addAll(Data.list0);
                        xRecyclerView.onComplete(true, 0, true);
                        break;
                }
                nextRefreshState = getNextRefresh();
                nextMoreSuccess = true;
            }
        }, 1000);
    }

    private void loadMore(){
        xRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (nextMoreSuccess){
                    if (dataList.size() == 0){
                        dataList.addAll(Data.list0);
                        xRecyclerView.onComplete(false, 0, true);
                    } else if (dataList.size() == Data.list0.size()){
                        dataList.addAll(Data.list1);
                        xRecyclerView.onComplete(false, 0, true);
                    } else {
                        xRecyclerView.onComplete(false, 0, false);
                    }
                } else {
                    xRecyclerView.onComplete(false, -1, true);
                }
                nextMoreSuccess = !nextMoreSuccess;
            }
        }, 1000);
    }

    private void removeHeader() {
        if (!headerAdapters.isEmpty()){
            headerAdapters.remove(headerAdapters.size() - 1);
        }
        xRecyclerView.setHeader(headerAdapters);
        xRecyclerView.notifyDataSetChanged();
    }


    private void addHeader(){
        headerAdapters.add(new XAdapter.HeaderAdapter<HeaderHolder>() {
            @Override
            public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.head, parent, false);
                return new HeaderHolder(view);
            }

            @Override
            public void onBindHeaderView(HeaderHolder holder) {
                holder.bind("这是头部");
            }
        });
        xRecyclerView.setHeader(headerAdapters);
        xRecyclerView.notifyDataSetChanged();
    }

    private static class MyHolder extends RVHolder{

        public MyHolder(View itemView) {
            super(itemView);
        }

        public void bind(ItemDto item){
            setImageResource(R.id.image_view, item.getDrawableId());
            setText(R.id.text_view, item.getName());
        }
    }

    private class HeaderHolder extends RVHolder{

        public HeaderHolder(View itemView) {
            super(itemView);
        }

        public void bind(String data){
            setText(R.id.text_view, data);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private static final int LIST = 0;
        private static final int GRID = 1;

        private int TYPE = LIST;

        public void setList(){
            TYPE = LIST;
        }

        public void setGrid(){
            TYPE = GRID;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (TYPE){
                case LIST:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                    return new MyHolder(view);
                case GRID:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
                    return new MyHolder(view);
                default:
                    return null;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((MyHolder)holder).bind(dataList.get(position));
        }

        @Override
        public int getItemViewType(int position) {
            return TYPE;
        }


        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }
}
