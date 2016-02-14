package com.efan.xrecyclerview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by efan on 15-12-4.
 */
public class XContentDecoration extends RecyclerView.ItemDecoration {

    private XAdapter xAdapter;
    private Drawable bgDrawable;

    public XContentDecoration(XAdapter xAdapter) {
        this.xAdapter = xAdapter;
    }


    public void setBgDrawable(Drawable bgDrawable) {
        this.bgDrawable = bgDrawable;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (bgDrawable == null){
            super.onDraw(c, parent, state);
            return;
        }
        int start = xAdapter.getXPosition(0);
        int end = xAdapter.getXPosition(xAdapter.getDataCount() - 1);
        int top = getRect(parent, start).top;
        int bottom = getRect(parent, end).bottom;
        if (bottom == 0){
            bottom = parent.getBottom();
        }
        int left = 0;
        int right = parent.getWidth();

        if (top < bottom){
            bgDrawable.setBounds(left, top, right, bottom);
            bgDrawable.draw(c);
        }
    }

    private Rect getRect(RecyclerView parent, int position){
        RecyclerView.ViewHolder holder = parent.findViewHolderForAdapterPosition(position);
        View child = null;
        if (holder != null){
            child = holder.itemView;
        }

        if (child != null){
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams)child.getLayoutParams();
            return new Rect(child.getLeft() - lp.leftMargin,
                    child.getTop() - lp.topMargin,
                    child.getRight() + lp.rightMargin,
                    child.getBottom() + lp.bottomMargin);
        }
        return new Rect();
    }

}
