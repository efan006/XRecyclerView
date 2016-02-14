package com.efan.xrecyclerview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by efan on 15-12-25.
 */
public class GridDividerDecoration extends RecyclerView.ItemDecoration {

    private final int size;

    public GridDividerDecoration(int size) {
        this.size = size;
    }


    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    private int getDataCount(RecyclerView parent){
        if (parent.getAdapter() instanceof XAdapter){
            return ((XAdapter) parent.getAdapter()).getDataCount();
        } else {
            return parent.getChildCount();
        }
    }

    private int getStartPos(RecyclerView parent){
        if (parent.getAdapter() instanceof XAdapter){
            return ((XAdapter) parent.getAdapter()).getXPosition(0);
        } else {
            return 0;
        }
    }

//
//    private boolean isFirstColumn(RecyclerView parent, int pos, int spanCount){
//        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
//        if (layoutManager instanceof GridLayoutManager) {
//            if (pos % spanCount == 0) {
//                return true;
//            }
//        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
//            int orientation = ((StaggeredGridLayoutManager) layoutManager)
//                    .getOrientation();
//            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
//                if (pos % spanCount == 0){
//                    return true;
//                }
//            } else {
//                if (pos < spanCount){
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount, int childCount) {
//        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
//        if (layoutManager instanceof GridLayoutManager) {
//            if ((pos + 1) % spanCount == 0) {  // 如果是最后一列，则不需要绘制右边
//                return true;
//            }
//        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
//            int orientation = ((StaggeredGridLayoutManager) layoutManager)
//                    .getOrientation();
//            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
//                if ((pos + 1) % spanCount == 0){// 如果是最后一列，则不需要绘制右边
//                    return true;
//                }
//            } else {
//                childCount = childCount - childCount % spanCount;
//                if (pos >= childCount){// 如果是最后一列，则不需要绘制右边
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    private boolean isFirstRow(RecyclerView parent, int pos, int spanCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if (pos < spanCount) {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if (pos < spanCount)
                    return true;
            } else{
                throw new UnsupportedOperationException("unsupported");
            }
        }
        return false;
    }

    private boolean isLastRow(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int lastCount = childCount % spanCount;
            if (lastCount == 0){
                lastCount = spanCount;
            }
            if (pos >= childCount - lastCount) {// 如果是最后一行，则不需要绘制底部
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            } else{ // StaggeredGridLayoutManager 且横向滚动 // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int spanCount = getSpanCount(parent);
        int dataCount = getDataCount(parent);
        int startPos = getStartPos(parent);
        int itemPos = parent.getChildAdapterPosition(view);
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;

        if (itemPos >= startPos && itemPos < startPos + dataCount){
            int dataPos = itemPos - startPos;
            boolean isFirstRow = isFirstRow(parent, dataPos, spanCount);
            boolean isLastRow = isLastRow(parent, dataPos, spanCount, dataCount);
//            boolean isFirstColumn = isFirstColumn(parent, dataPos, spanCount);
//            boolean isLastColumn = isLastColumn(parent, dataPos, spanCount, dataCount);
            int padding = size / 2;
            left = padding;
            right = padding;
            top = isFirstRow ? 0 : size / 2;
            bottom = isLastRow ? 0 : size / 2;
        }
        outRect.set(left, top, right, bottom);
    }

}
