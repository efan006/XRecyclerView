package com.efan.xrecyclerview;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by efan on 15-9-21.
 */
public class XAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<HeaderAdapter> headerList = new ArrayList<>();
    private LoadMoreAdapter loadMore;
    private BodyAdapter bodyAdapter;

    private boolean moreEnable = false;

    private static final int TYPE_BODY = -1;
    private static final int TYPE_MORE = -2;
    private static final int HF_OFFSET = -3;


    private RecyclerView.Adapter realAdapter;

    public XAdapter(@NonNull RecyclerView.Adapter realAdapter,
                    @NonNull BodyAdapter bodyAdapter, @NonNull LoadMoreAdapter loadMore){
        this.realAdapter = realAdapter;
        this.bodyAdapter = bodyAdapter;
        this.loadMore = loadMore;

        moreEnable = true;
    }

    public void setLoadMoreAdapter(@NonNull LoadMoreAdapter loadMore) {
        this.loadMore = loadMore;
        moreEnable = true;
    }

    public void setBodyAdapter(@NonNull BodyAdapter bodyAdapter) {
        this.bodyAdapter = bodyAdapter;
    }

    public void setRealAdapter(@NonNull RecyclerView.Adapter<? extends RecyclerView.ViewHolder> realAdapter) {
        this.realAdapter = realAdapter;
    }

    public boolean isEmpty(){
        return realAdapter.getItemCount() == 0;
    }

    public void setHeader(List<HeaderAdapter> headerList){
        this.headerList.clear();
        if (headerList != null && !headerList.isEmpty()){
            this.headerList.addAll(headerList);
        }
        notifyDataSetChanged();
    }

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener){
        bodyAdapter.setOnRefreshListener(listener);
    }

    public void setOnLoadMoreListener(LoadMoreAdapter.OnLoadMoreListener listener){
        loadMore.setListener(listener);
    }

    public void setLoading(){
        bodyAdapter.setLoading();
    }

    public void disableLoadMore(){
        if (moreEnable){
            moreEnable = false;
            notifyDataSetChanged();
        }
    }

    public void enableLoadMore(){
        if (!moreEnable){
            moreEnable = true;
            notifyDataSetChanged();
        }
    }

    public void notifyHeaderChanged(){
        if (headerList.size() > 0){
            notifyItemRangeChanged(0, headerList.size());
        }
    }
    public void notifyHeaderChanged(int position){
        if (headerList.size() > 0 && position < headerList.size() - 1){
            notifyItemChanged(position);
        }
    }
    public void loadDone(boolean isRefresh, int result, boolean hasNextPage){
        loadMore.setState(LoadMoreAdapter.State.IDLE);
        if (isRefresh){                         //刷新数据
            if (isEmpty()){                     //数据为空
                if (result == 0){                   //成功说明确实为空
                    bodyAdapter.setEmpty();
                } else {                        //失败则设置失败
                    bodyAdapter.setError(result);
                }
            }
            if (moreEnable){
                enableLoadMore();
            }
        } else {
            if (moreEnable){
                if (result == 0){
                    if (hasNextPage){           //可继续加载
                        loadMore.setState(LoadMoreAdapter.State.IDLE);
                    } else {                    //已经到结尾
                        loadMore.setState(LoadMoreAdapter.State.END);
//                        disableLoadMore();
                    }
                } else {                        //加载失败
                    loadMore.setState(LoadMoreAdapter.State.ERROR);
                }
            }
        }
        notifyDataSetChanged();
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_BODY){
            return bodyAdapter.onCreateBodyViewHolder(parent);
        } else if (viewType == TYPE_MORE){
            return loadMore.onCreateFooterViewHolder(parent);
        } else if (viewType <= HF_OFFSET){
            int pos = HF_OFFSET - viewType;
            return headerList.get(pos).onCreateHeaderViewHolder(parent);
        } else {
            return realAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_BODY){
            bodyAdapter.onBindBodyView((BodyAdapter.BodyHolder) holder);
        } else if (viewType == TYPE_MORE){
            loadMore.onBindFooterView((LoadMoreHolder) holder);
        } else if (viewType <= HF_OFFSET){
            int pos = HF_OFFSET - viewType;
            headerList.get(pos).onBindHeaderView(holder);
        } else {
            realAdapter.onBindViewHolder(holder, position - headerList.size());
        }
    }


    @Override
    public int getItemViewType(int position) {
        int dataPos = getDataPosition(position);
        if (dataPos < 0){                                       //数据左越界，属于头部
            return HF_OFFSET - position;
        } else if (dataPos == realAdapter.getItemCount()){          //数据右越界第一个
            return isEmpty() ? TYPE_BODY : TYPE_MORE;
        } else {
            int type = realAdapter.getItemViewType(dataPos);
            if (type >= 0){
                return type;
            } else {
                throw new IllegalStateException("The DataItemViewType can not be negative");
            }
        }
    }

    @Override
    public int getItemCount() {
        int footer = isEmpty() || moreEnable ? 1 : 0;
        return headerList.size() + realAdapter.getItemCount() + footer;
    }

    public int getDataCount(){
        return realAdapter.getItemCount();
    }

    private int getDataPosition(int position){
        return position - headerList.size();
    }

    public int getXPosition(int position){
        return position + headerList.size();
    }



    public int getResizeIndex(){
        boolean hasFooter = isEmpty() || moreEnable;
        return hasFooter ? headerList.size() + realAdapter.getItemCount() : -1;
    }



    public interface HeaderAdapter<VH extends RecyclerView.ViewHolder> {
        VH onCreateHeaderViewHolder(ViewGroup parent);
        void onBindHeaderView(VH holder);
    }

    public static View inflate(ViewGroup parent, int layoutId){
        return LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
    }

}
