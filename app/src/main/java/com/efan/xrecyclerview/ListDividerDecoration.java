package com.efan.xrecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 为RecyclerView添加分割线
 */
public class ListDividerDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int size = 1;
    private int startPadding = 0;
    private int endPadding = 0;

    private boolean showLast;
    private boolean showFirst;

    public ListDividerDecoration(Context context){
        this(context, null);
    }

    public ListDividerDecoration(Context context, Drawable mDivider){
        this(context, mDivider, false ,false);
    }

    public ListDividerDecoration(Context context, Drawable mDivider, boolean showFirst, boolean showLast){
        this.mDivider = mDivider;
        this.showFirst = showFirst;
        this.showLast = showLast;
        size = context.getResources().getDimensionPixelSize(R.dimen.dp_half);
    }

    public void setSize(int size){
        this.size = size;
    }

    public void setPadding(int startPadding, int endPadding){
        this.startPadding = startPadding;
        this.endPadding = endPadding;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mDivider == null || size <= 0){
            super.onDraw(c, parent, state);
            return;
        }
        boolean vertical = getOrientation(parent) == OrientationHelper.VERTICAL;
        if (showFirst){
            if (vertical){
                drawTop(c, parent);
            } else {
                drawLeft(c, parent);
            }

        }

        int itemCount = state.getItemCount();
        if (!showLast){
            itemCount --;
        }
        if (vertical){
            int left = startPadding;
            int right = parent.getWidth() - endPadding;
            for(int i=0; i < itemCount; i++){
                RecyclerView.ViewHolder holder = parent.findViewHolderForPosition(i);
                View child = null;
                if (holder != null){
                    child = holder.itemView;
                }
                if (child != null){
                    RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams)child.getLayoutParams();
                    int top = child.getBottom() + lp.bottomMargin;
                    int bottom = top + size;
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }
            }
        } else {
            int top = startPadding;
            int bottom = parent.getHeight() - endPadding;
            for(int i=0; i < itemCount; i++){
                RecyclerView.ViewHolder holder = parent.findViewHolderForPosition(i);
                View child = null;
                if (holder != null){
                    child = holder.itemView;
                }
                if (child != null) {
                    RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
                    int left = child.getRight() + lp.rightMargin;
                    int right = left + size;
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }
            }
        }
    }

    private void drawTop(Canvas c, RecyclerView parent){
        int left = startPadding;
        int right = parent.getWidth() - endPadding;
        RecyclerView.ViewHolder holder = parent.findViewHolderForPosition(0);
        View child = null;
        if (holder != null){
            child = holder.itemView;
        }
        if (child != null) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            int bottom = child.getTop() - lp.topMargin;
            int top = bottom - size;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private void drawLeft(Canvas c, RecyclerView parent){
        int top = startPadding;
        int bottom = parent.getHeight() - endPadding;
        RecyclerView.ViewHolder holder = parent.findViewHolderForPosition(0);
        View child = null;
        if (holder != null){
            child = holder.itemView;
        }
        if (child != null) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            int right = child.getLeft() - lp.leftMargin;
            int left = right - size;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (size <= 0) {
            super.getItemOffsets(outRect, view, parent, state);
            return;
        }
        int position = parent.getChildPosition(view);
        int itemCount = state.getItemCount();

        boolean firstItem = position == 0;
        boolean lastItem = position == itemCount - 1;
        boolean dividerBefore = showFirst || !firstItem;
        boolean dividerAfter = showLast && lastItem;

        if (getOrientation(parent) == OrientationHelper.VERTICAL) {
            outRect.top = dividerBefore ? size : 0;
            outRect.bottom = dividerAfter ? size : 0;
        } else {
            outRect.left = dividerBefore ? size : 0;
            outRect.right = dividerAfter ? size : 0;
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
