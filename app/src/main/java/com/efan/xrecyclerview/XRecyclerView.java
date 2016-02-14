package com.efan.xrecyclerview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by efan on 15-11-2.
 */
public class XRecyclerView extends FrameLayout {

    public XRecyclerView(Context context) {
        this(context, null);
    }

    public XRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public XRecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    SwipeRefreshLayout mRefreshLayout;
    ObservableRecyclerView mRecycler;
    XAdapter mAdapter;

    public static class Builder{
        @LayoutRes private int layoutId = R.layout.x_recycler_main;
        @ColorRes private int[] colorSchemeResIds = new int[]{
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_red_light
        };

        private RecyclerView.LayoutManager layoutManager;
        private List<XAdapter.HeaderAdapter> mHeaderList;
        private RecyclerView.Adapter mRealAdapter;
        private BodyAdapter mBodyAdapter = new DefaultBodyAdapter();
        private LoadMoreAdapter mLoadMoreAdapter;
        private List<RecyclerView.ItemDecoration> itemDecorations = new ArrayList<>();
        private RecyclerView.ItemAnimator mItemAnimator;
        private SwipeRefreshLayout.OnRefreshListener onRefreshListener;
        private LoadMoreAdapter.OnLoadMoreListener onLoadMoreListener;
        private boolean canPullDown = true;


        public Builder setLayoutId(@LayoutRes int layoutId){
            this.layoutId = layoutId;
            return this;
        }

        public Builder setColorSchemeResIds(@ColorRes int... colorResIds){
            this.colorSchemeResIds = colorResIds;
            return this;
        }

        public Builder setLayoutManager(@NonNull RecyclerView.LayoutManager layoutManager){
            this.layoutManager = layoutManager;
            return this;
        }

        public Builder setHeader(List<XAdapter.HeaderAdapter> headerList) {
            this.mHeaderList = headerList;
            return this;
        }

        public Builder setDataAdapter(@NonNull RecyclerView.Adapter realAdapter){
            this.mRealAdapter = realAdapter;
            return this;
        }

        public Builder setBodyAdapter(@NonNull BodyAdapter bodyAdapter){
            this.mBodyAdapter = bodyAdapter;
            return this;
        }

        public Builder setLoadMoreAdapter(@NonNull LoadMoreAdapter loadMoreAdapter){
            this.mLoadMoreAdapter = loadMoreAdapter;
            return this;
        }

        public Builder setCanPullDown(boolean canPullDown) {
            this.canPullDown = canPullDown;
            return this;
        }

        public Builder setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener onRefreshListener){
            this.onRefreshListener = onRefreshListener;
            return this;
        }

        public Builder setOnLoadMoreListener(LoadMoreAdapter.OnLoadMoreListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
            return this;
        }

        public Builder addItemDecoration(RecyclerView.ItemDecoration itemDecoration){
            itemDecorations.add(itemDecoration);
            return this;
        }

        public Builder setItemAnimator(RecyclerView.ItemAnimator itemAnimator){
            this.mItemAnimator = itemAnimator;
            return this;
        }


        public void build(XRecyclerView xRecycler) {
            View v = LayoutInflater.from(xRecycler.getContext()).inflate(layoutId, xRecycler);
            xRecycler.mRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.refresh_layout);
            xRecycler.mRecycler = (ObservableRecyclerView) v.findViewById(R.id.recycler_view);

            xRecycler.setRefreshColorRes(colorSchemeResIds);
            if (layoutManager == null){
                layoutManager = new LinearLayoutManager(xRecycler.getContext());
            }
            xRecycler.setLayoutManager(layoutManager);
            if (mLoadMoreAdapter == null){
                mLoadMoreAdapter = new DefaultLoadMore();
            }
            XAdapter xAdapter = new XAdapter(mRealAdapter, mBodyAdapter, mLoadMoreAdapter);
            xRecycler.setXAdapter(xAdapter);
            xRecycler.setHeader(mHeaderList);
            for(int i = 0; i < itemDecorations.size(); i++){
                xRecycler.addItemDecoration(itemDecorations.get(i));
            }
            if (mItemAnimator != null){
                xRecycler.setItemAnimator(mItemAnimator);
            }
            xRecycler.addItemDecoration(new BodyDecoration());
            xRecycler.setCanPullDown(canPullDown);
            xRecycler.setOnRefreshListener(onRefreshListener);
            xRecycler.setOnLoadMoreListener(onLoadMoreListener);
        }
    }

    private static class DefaultBodyAdapter extends BodyAdapter {


        @Override
        protected BodyHolder getBodyHolder(View view) {
            return new DefaultBodyHolder(view);
        }

        @Override
        public void onBindBodyView(BodyHolder holder) {
            holder.setState(state);
        }


        private class DefaultBodyHolder extends BodyHolder{

            public DefaultBodyHolder(View itemView) {
                super(itemView);
            }

            @Override
            protected View initLoadingStub(ViewStub loadingStub) {
                loadingStub.setLayoutResource(R.layout.x_recycler_body_loading);
                View loading = loadingStub.inflate();
                ProgressView progress = (ProgressView)loading.findViewById(R.id.progress);
                progress.start();
                return loading;
            }

            @Override
            protected View initEmptyStub(ViewStub emptyStub) {
                emptyStub.setLayoutResource(R.layout.x_recycler_body_error);
                View empty = emptyStub.inflate();
                empty.findViewById(R.id.button).setOnClickListener(reloadListener);
                ((TextView)empty.findViewById(R.id.text_view)).setText(R.string.x_recycler_empty);
                return empty;
            }

            @Override
            protected View initErrorStub(ViewStub errorStub) {
                errorStub.setLayoutResource(R.layout.x_recycler_body_error);
                View error = errorStub.inflate();
                error.findViewById(R.id.button).setOnClickListener(reloadListener);
                return error;
            }

            @Override
            protected void setLoading() {
                ProgressView progress = (ProgressView)loading.findViewById(R.id.progress);
                progress.start();
            }


            @Override
            protected void setError(int errorCode) {
                TextView textView = (TextView)error.findViewById(R.id.text_view);
                String errorMsg = "";
                if (errorCode > 0){
                    errorMsg = "(code:" + errorCode + ")";
                }
                textView.setText("加载失败" + errorMsg + "，请重试");
            }


        }
    }


    boolean canPullDown;
    boolean canLoadMore;

    public void setCanPullDown(boolean canPullDown){
        this.canPullDown = canPullDown;
        mRefreshLayout.setRefreshing(false);
        mRefreshLayout.setEnabled(canPullDown);
    }

    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
        if (canLoadMore) {
            mAdapter.enableLoadMore();
        } else {
            mAdapter.disableLoadMore();
        }
    }

    public void setLoading(){
        if (canPullDown && !mAdapter.isEmpty()){
            mRefreshLayout.setRefreshing(true);
        } else {
            mAdapter.setLoading();
        }
    }

    public void onComplete(boolean isRefresh, int result, boolean hasNextPage){
        if (isRefresh) {
            mRefreshLayout.setRefreshing(false);
        }
        mAdapter.loadDone(isRefresh, result, hasNextPage);
    }

    public void notifyHeaderChanged(){
        mAdapter.notifyHeaderChanged();
    }
    public void notifyHeaderChanged(int position){
        mAdapter.notifyHeaderChanged(position);
    }

    public void notifyDataSetChanged(){
        mAdapter.notifyDataSetChanged();
    }

    public void notifyItemRemoved(int pos){
        mAdapter.notifyItemRemoved(mAdapter.getXPosition(pos));
    }

    public void notifyItemChanged(int pos){
        mAdapter.notifyItemChanged(mAdapter.getXPosition(pos));
    }

    public RecyclerView.ViewHolder findHolder(int pos){
        return mRecycler.findViewHolderForAdapterPosition(mAdapter.getXPosition(pos));
    }


    public void scrollToTop(){
        mRecycler.smoothScrollToPosition(0);
    }

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener){
        checkXAdapter();
        mRefreshLayout.setOnRefreshListener(listener);
        mAdapter.setOnRefreshListener(listener);
        setCanPullDown(canPullDown && listener != null);
    }

    public void setOnLoadMoreListener(LoadMoreAdapter.OnLoadMoreListener listener) {
        checkXAdapter();
        mAdapter.setOnLoadMoreListener(listener);
        setCanLoadMore(listener != null);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layout) {
        mRecycler.setLayoutManager(layout);
    }

    public void setRefreshColorRes(@ColorRes int... colorResIds){
        mRefreshLayout.setColorSchemeResources(colorResIds);
    }

    public void setRefreshingColor(int... colors){
        mRefreshLayout.setColorSchemeColors(colors);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decor){
        mRecycler.addItemDecoration(decor);
    }

    public void removeItemDecoration(RecyclerView.ItemDecoration decor){
        mRecycler.removeItemDecoration(decor);
    }

    public void setItemAnimator(RecyclerView.ItemAnimator itemAnimator){
        mRecycler.setItemAnimator(itemAnimator);
    }

    private void checkXAdapter(){
        if (mAdapter == null){
            throw new IllegalStateException("can not set this when adapter is NULL");
        }
    }

    public void setXAdapter(@NonNull XAdapter adapter){
        mAdapter = adapter;
        mRecycler.setAdapter(adapter);
    }

    public XAdapter getXAdapter(){
        return mAdapter;
    }

    public void setDataAdapter(@NonNull RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter){
        checkXAdapter();
        mAdapter.setRealAdapter(adapter);
    }

    public void setLoadMoreAdapter(@NonNull LoadMoreAdapter loadMore) {
        checkXAdapter();
        mAdapter.setLoadMoreAdapter(loadMore);
    }

    public void setBodyAdapter(@NonNull BodyAdapter bodyAdapter) {
        checkXAdapter();
        mAdapter.setBodyAdapter(bodyAdapter);
    }

    public void setHeader(List<XAdapter.HeaderAdapter> headerList){
        checkXAdapter();
        mAdapter.setHeader(headerList);
    }

    public void addOnScrollListener(RecyclerView.OnScrollListener onScrollListener){
        mRecycler.addOnScrollListener(onScrollListener);
    }

    public void clearOnScrollListeners(){
        mRecycler.clearOnScrollListeners();
    }

    public void setScrollViewCallbacks(ObservableScrollViewCallbacks listener){
        mRecycler.setScrollViewCallbacks(listener);
    }

}

