package com.efan.xrecyclerview;

import android.view.ViewGroup;

/**
 * Created by efan on 15-11-3.
 */
public abstract class LoadMoreAdapter<Holder extends LoadMoreHolder> {

    public LoadMoreAdapter(Mode mode) {
        this.mode = mode;
    }

    public LoadMoreAdapter() {
    }

    protected enum State {
        IDLE, LOADING, END, ERROR
    }

    protected State state = State.IDLE;

    public enum Mode {
        AUTO, CLICK
    }

    protected Mode mode = Mode.AUTO;

    public interface OnLoadMoreListener {
        void onLoadMore();
    }


    protected OnLoadMoreListener listener;

    public void setListener(OnLoadMoreListener listener) {
        this.listener = listener;
    }

    protected Holder holder;

    public void setState(State state){
        this.state = state;
        if (holder != null){
            holder.setState(state);
        }
    }

    protected abstract Holder onCreateFooterViewHolder(ViewGroup parent);

    public void onBindFooterView(Holder holder) {
        if (mode == Mode.AUTO && state == State.IDLE){
            setState(State.LOADING);
            if (listener != null){
                listener.onLoadMore();
            }
        } else {
            holder.setState(state);
        }
    }


}
