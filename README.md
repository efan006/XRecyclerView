# XRecyclerView
针对RecyclerView的常用功能做了一些扩展:
* 下拉刷新
* 滑动到底部自动/点击加载更多
* 数据加载失败/数据为空的提示
* 设置分割线
* 监听上下滑动，控制悬浮按钮的弹出/收起
* 增加/删除头部
* 切换布局

### 用法
配置布局
``` xml
...
    <com.efan.xrecyclerview.XRecyclerView
        android:id="@+id/x_recycler_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

    </com.efan.xrecyclerview.XRecyclerView>
...
```

获取XRecyclerView控件后，调用构造器初始化

``` java
new XRecyclerView.Builder()
        //如果不需要下拉刷新功能，不设置监听即可
        .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        })
        //如果不需要加载更多功能，不设置监听即可
        .setOnLoadMoreListener(new LoadMoreAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMore();
            }
        })
        .setDataAdapter(mAdapter)
        .build(xRecyclerView);
```

##### 设置状态的方法
``` java
setCanPullDown(boolean canPullDown) //开关下拉刷新功能
setCanLoadMore(boolean canLoadMore) //开关加载更多功能
setLoading()  //设置成loading状态，如果允许下拉并且数据不为空，则是顶部loading；否则是内容loading
onComplete(boolean isRefresh, int result, boolean hasNextPage) 
//isRefresh 为true表示刷新回调，false表示加载更多回调
//result 是结果码，为0表示成功，其他值表示出错
//hasNextPage 是否还有下一页，区别是底部显示状态不一样
```

##### 设置分割线
``` java
Drawable divider = new ColorDrawable(Color.parseColor("#FF0000"));  //设置分割线为红色
ItemDecoration listDividerDecoration = new ListDividerDecoration(this, divider);
ItemDecoration listDividerDecoration.setSize(10);                   //设置分割线大小，默认是0.5dp
gridDividerDecoration = new GridDividerDecoration(30);              //设置格子布局的间距大小

//可以在Builder里设置
new XRecyclerView.Builder()
        .setDataAdapter(mAdapter)
        .addItemDecoration(listDividerDecoration)
        .build(xRecyclerView);
//也可以在构造完后设置
xRecyclerView..addItemDecoration(listDividerDecoration);
```

##### 绑定滑动监听
``` java
ObservableScrollViewCallbacks scrollCallback = new ObservableScrollViewCallbacks() {
        @Override
        public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

        }

        @Override
        public void onDownMotionEvent() {

        }

        @Override
        public void onUpOrCancelMotionEvent(ScrollState scrollState) {
            if (scrollState == ScrollState.UP) {

            } else if (scrollState == ScrollState.DOWN) {

            }
        }
    };
    

xRecyclerView.setScrollViewCallbacks(scrollCallback);
```
##### 增加/删除头部
自行维护一个Adapter列表，然后调用setHeader方法

``` java
List<XAdapter.HeaderAdapter> headerAdapters = new ArrayList<>();
headerAdapters.add(new XAdapter.HeaderAdapter<HeaderHolder>() {
            @Override
            public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.head, parent, false);
                return new HeaderHolder(view);
            }

            @Override
            public void onBindHeaderView(HeaderHolder holder) {
                holder.bind("这是头部");
            }
        });
xRecyclerView.setHeader(headerAdapters);
xRecyclerView.notifyDataSetChanged();
```

##### 配置布局
默认是list列表布局，如果需要用到grid布局，需要注意配置Lookup
因为要保证头部和底部占满整个宽度
``` java
GridLayoutManager gridLM = new GridLayoutManager(this, SPAN_COUNT);
GridHeaderLookup lookup = new GridHeaderLookup(xRecyclerView.getXAdapter(), SPAN_COUNT);
gridLM.setSpanSizeLookup(lookup);
xRecyclerView.setLayoutManager(gridLM);
```

##### 切换布局
如果要动态切换布局，建议运用Adapter的ViewType以避免缓存ViewHolder没有切换成新布局
比如：

``` java
private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int LIST = 0;
    private static final int GRID = 1;

    private int TYPE = LIST;

    public void setList(){
        TYPE = LIST;
    }

    public void setGrid(){
        TYPE = GRID;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (TYPE){
            case LIST:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                return new MyHolder(view);
            case GRID:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
                return new MyHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MyHolder)holder).bind(dataList.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE;
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
```

需要切换时，替换分割线，切换Item布局，切换LayoutManager
``` java
private void changeAdapter() {
        atGrid = !atGrid;
        if (atGrid){
            xRecyclerView.removeItemDecoration(listDividerDecoration);
            xRecyclerView.addItemDecoration(gridDividerDecoration);
            mAdapter.setGrid();
            xRecyclerView.setLayoutManager(gridLM);
        } else {
            xRecyclerView.removeItemDecoration(gridDividerDecoration);
            xRecyclerView.addItemDecoration(listDividerDecoration);
            mAdapter.setList();
            xRecyclerView.setLayoutManager(listLM);
        }
        xRecyclerView.notifyDataSetChanged();
    }
```

### 附FabGroup
FabGroup是我写的一个悬浮按钮群组控件，具体用法先看代码吧，以后可能分离成独立工程，再详细讲

