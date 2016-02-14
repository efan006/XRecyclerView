package com.efan.xrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

/**
 * Created by efan on 15-10-15.
 */
public class RVHolder extends RecyclerView.ViewHolder {

    public RVHolder(View itemView) {
        super(itemView);
    }

    protected Context getContext(){
        return itemView.getContext();
    }

    public void setText(int viewId, CharSequence text){
        setViewVisible(viewId, View.VISIBLE);
        TextView tv = (TextView)itemView.findViewById(viewId);
        tv.setText(text);
    }


    public void setTextGoneIfEmpty(int viewId, CharSequence text){
        if (!TextUtils.isEmpty(text)){
            setText(viewId, text);
        } else {
            setViewVisible(viewId, View.GONE);
        }
    }

    public void setTextSize(int viewId, int spValue){
        TextView tv = (TextView)itemView.findViewById(viewId);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, spValue);
    }

    public void setTextColor(int viewId, int color) {
        TextView tv = (TextView)itemView.findViewById(viewId);
        tv.setTextColor(color);
    }

    public void displayImage(int viewId, String path, DisplayImageOptions options){
        setViewVisible(viewId, View.VISIBLE);
        path = fixedUri(path);
        ImageView img = (ImageView)itemView.findViewById(viewId);
        ImageLoader.getInstance().displayImage(path, img, options);
    }

    public void displayImageInvisibleIfNull(int viewId, String path, DisplayImageOptions options){
        if (!TextUtils.isEmpty(path)) {
            displayImage(viewId, path, options);
            setViewVisible(viewId, View.VISIBLE);
        } else {
            setViewVisible(viewId, View.INVISIBLE);
        }
    }

    public void setImageResource(int viewId, int resId){
        ImageView img = (ImageView)itemView.findViewById(viewId);
        img.setVisibility(View.VISIBLE);
        img.setImageResource(resId);
    }

    public void setViewVisible(int viewId, int visibility){
        itemView.findViewById(viewId).setVisibility(visibility);
    }

    public void setSelected(int viewId, boolean selected){
        itemView.findViewById(viewId).setSelected(selected);
    }

    public void setOnClickListener(int viewId, View.OnClickListener listener){
        itemView.findViewById(viewId).setOnClickListener(listener);
    }

    public void setOnLongClickListener(int viewId, View.OnLongClickListener listener){
        itemView.findViewById(viewId).setOnLongClickListener(listener);
    }

    public static String fixedUri(String path) {
        String imageUri;
        ImageDownloader.Scheme scheme = ImageDownloader.Scheme.ofUri(path);
        if(scheme == ImageDownloader.Scheme.UNKNOWN) {
            imageUri = ImageDownloader.Scheme.FILE.wrap(path);
        } else {
            imageUri = path;
        }

        return imageUri;
    }
}
