package com.efan.xrecyclerview;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

/**
 * Created by efan on 15-10-24.
 */
public abstract class BodyAdapter<BH extends BodyAdapter.BodyHolder> {

    enum State {
        LOADING, EMPTY, ERROR
    }

    protected State state = State.LOADING;

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener;

    public void setLoading() {
        state = State.LOADING;
        if (holder != null){
            holder.setState(state);
        }
    }

    public void setError(int errorCode){
        state = State.ERROR;
        if (holder != null){
            holder.errorCode = errorCode;
            holder.setState(state);
        }
    }

    public void setEmpty(){
        state = State.EMPTY;
        if (holder != null){
            holder.setState(state);
        }
    }

    private BH holder;

    protected abstract BH getBodyHolder(View view);

    public BH onCreateBodyViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.x_recycler_body, parent, false);
        holder = getBodyHolder(view);
        return holder;
    }

    public void onBindBodyView(BH holder) {
        holder.setState(state);
    }

    public abstract class BodyHolder extends RecyclerView.ViewHolder {

        protected final View loading;
        protected final View empty;
        protected final View error;
        protected int errorCode;



        protected View.OnClickListener reloadListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (state) {
                    case EMPTY:
                    case ERROR:
                        BodyAdapter.this.setLoading();
                        if (onRefreshListener != null) {
                            onRefreshListener.onRefresh();
                        }
                        break;
                    default:
                        break;
                }
            }
        };

        public BodyHolder(View itemView) {
            super(itemView);
            ViewStub loadingStub = (ViewStub)itemView.findViewById(R.id.loading_stub);
            loading = initLoadingStub(loadingStub);

            ViewStub emptyStub = (ViewStub)itemView.findViewById(R.id.empty_stub);
            empty = initEmptyStub(emptyStub);

            ViewStub errorStub = (ViewStub)itemView.findViewById(R.id.error_stub);
            error = initErrorStub(errorStub);

        }

        protected abstract View initLoadingStub(ViewStub loadingStub);
        protected abstract View initEmptyStub(ViewStub emptyStub);
        protected abstract View initErrorStub(ViewStub errorStub);

        protected abstract void setError(int errorCode);
        protected abstract void setLoading();


        public void setState(State state) {
            switch (state) {
                case LOADING:
                    loading.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                    error.setVisibility(View.GONE);
                    setLoading();
                    break;
                case EMPTY:
                    loading.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                    error.setVisibility(View.GONE);
                    break;
                case ERROR:
                    loading.setVisibility(View.GONE);
                    empty.setVisibility(View.GONE);
                    error.setVisibility(View.VISIBLE);
                    setError(errorCode);
                    break;
            }
        }
    }

}
