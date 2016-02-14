package com.efan.xrecyclerview;

import android.support.v7.widget.GridLayoutManager;

public class GridHeaderLookup extends GridLayoutManager.SpanSizeLookup {
    XAdapter adapter;
    int spanCount;

    public GridHeaderLookup(XAdapter adapter, int spanCount) {
        this.adapter = adapter;
        this.spanCount = spanCount;
    }

    public int getSpanSize(int position) {
        boolean isHeaderOrFooter = this.adapter.getItemViewType(position) < 0;
        return isHeaderOrFooter?this.spanCount:1;
    }
}