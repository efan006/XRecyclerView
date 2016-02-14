package com.efan.xrecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by efan on 15-10-26.
 */
public class BodyDecoration extends RecyclerView.ItemDecoration {


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (checkSupport(parent)) {
            resize(parent);
        }
    }

    private boolean checkSupport(RecyclerView parent) {
        return parent.getAdapter() instanceof XAdapter;
    }

    private boolean reachTop(RecyclerView parent) {
        return !ViewCompat.canScrollVertically(parent, -1);
    }

    private void resize(RecyclerView parent) {
        XAdapter hfAdapter = (XAdapter) parent.getAdapter();
        int pos = hfAdapter.getResizeIndex();           //获取需要修改尺寸的Item
        RecyclerView.ViewHolder holder = parent.findViewHolderForAdapterPosition(pos);
        View child = null;
        if (holder != null && (holder instanceof LoadMoreHolder
                || holder instanceof BodyAdapter.BodyHolder)) {
            child = holder.itemView;
        }
        if (child != null) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            if (pos == 0) {                              //上方没有更多数据,直接设全部高度
                lp.height = parent.getHeight();
                child.setLayoutParams(lp);
            } else {
                int top = child.getTop() - lp.topMargin;
                int newHeight;
                int minHeight = (int) parent.getContext().getResources().getDimension(R.dimen.x_recycler_min_height);  //最小尺寸
                if (reachTop(parent)) {                  //列表滑动到最顶时，填满底部
                    newHeight = Math.max(parent.getHeight() - top, minHeight);
                } else {
                    newHeight = minHeight;              //不在最顶时，设为最小高度
                }
                if (lp.height != newHeight) {
                    lp.height = newHeight;
                    child.setLayoutParams(lp);
                }
            }
        }
    }


    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
