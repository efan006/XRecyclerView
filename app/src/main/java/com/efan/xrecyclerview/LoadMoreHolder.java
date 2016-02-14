package com.efan.xrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by efan on 15-11-3.
 */
public abstract class LoadMoreHolder extends RecyclerView.ViewHolder {

    public LoadMoreHolder(View itemView) {
        super(itemView);
    }

    public abstract void setState(LoadMoreAdapter.State state);
}
