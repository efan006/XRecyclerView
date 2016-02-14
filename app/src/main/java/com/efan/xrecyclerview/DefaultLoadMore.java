package com.efan.xrecyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rey.material.widget.ProgressView;

/**
 * Created by efan on 15-9-21.
 */
public class DefaultLoadMore extends LoadMoreAdapter<DefaultLoadMore.DefaultLoadMoreHolder> {


    public DefaultLoadMore(Mode mode) {
        super(mode);
    }

    public DefaultLoadMore() {

    }


    @Override
    public DefaultLoadMoreHolder onCreateFooterViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.x_recycler_load_more, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (state) {
                    case IDLE:
                    case ERROR:
                        setState(State.LOADING);
                        if (listener != null) {
                            listener.onLoadMore();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        holder = new DefaultLoadMoreHolder(view);
        return holder;
    }


    public static class DefaultLoadMoreHolder extends LoadMoreHolder {

        private final TextView textView;
        private final ProgressView progress;

        public DefaultLoadMoreHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text_view);
            progress = (ProgressView) itemView.findViewById(R.id.progress);
        }

        public void setState(State state) {
            switch (state) {
                case IDLE:
                    progress.setVisibility(View.GONE);
                    progress.stop();
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(R.string.x_recycler_load_more);
                    break;
                case LOADING:
                    progress.setVisibility(View.VISIBLE);
                    progress.start();
                    textView.setVisibility(View.GONE);
                    textView.setText(R.string.x_recycler_loading);
                    break;
                case END:
                    progress.setVisibility(View.GONE);
                    progress.stop();
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(R.string.x_recycler_end);
                    break;
                case ERROR:
                    progress.setVisibility(View.GONE);
                    progress.stop();
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(R.string.x_recycler_error);
                    break;
            }
        }
    }

}
