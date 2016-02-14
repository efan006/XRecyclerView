package com.efan.xrecyclerview;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * 为线性RecyclerView增加头部与尾部Padding，不同于直接设padding，（直接设padding 内容会被遮盖）
 */
public class PaddingDecoration extends RecyclerView.ItemDecoration {

    private int startPadding;
    private int endPadding;

    public PaddingDecoration(int startPadding, int endPadding) {
        this.startPadding = startPadding;
        this.endPadding = endPadding;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        int itemCount = parent.getAdapter().getItemCount();
        if (itemCount == 0){
            super.getItemOffsets(outRect, view, parent, state);
            return;
        }
        int position = parent.getChildPosition(view);

        int spanCount = 1;
        if (parent.getLayoutManager() instanceof GridLayoutManager){
            spanCount = ((GridLayoutManager)parent.getLayoutManager()).getSpanCount();
        } else if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager)parent.getLayoutManager()).getSpanCount();
        }

        int dividerBefore = (position < spanCount) ? startPadding : 0;
        int dividerAfter = (position / spanCount == (itemCount - 1) / spanCount) ? endPadding : 0;

        if (dividerBefore <= 0 && dividerAfter <= 0){
            super.getItemOffsets(outRect, view, parent, state);
            return;
        }

        if (getOrientation(parent) == OrientationHelper.VERTICAL){
            outRect.top = dividerBefore;
            outRect.bottom = dividerAfter;
        } else {
            outRect.left = dividerBefore;
            outRect.right = dividerAfter;
        }

    }

    private int getOrientation(RecyclerView parent) {
        RecyclerView.LayoutManager lm = parent.getLayoutManager();
        if(lm instanceof LinearLayoutManager) {
            return ((LinearLayoutManager)lm).getOrientation();
        } else {
            throw new IllegalStateException("ListDividerDecoration can only be used with a LinearLayoutManager");
        }
    }

}
