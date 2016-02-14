package com.efan.xrecyclerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by efan on 15-12-15.
 */
public class FABGroup extends LinearLayout{

    public FABGroup(Context context) {
        this(context, null);
    }

    public FABGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FABGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FABGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void inflate(@LayoutRes int layoutId){
        inflate(getContext(), layoutId, this);
    }

    private boolean isRunning;
    private boolean visible;


    public void anim(boolean show){
        if (isRunning){
            return;
        }
        if (visible == show){
            return;
        }
        visible = show;
        isRunning = true;
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(200);
        valueAnimator.setInterpolator(new FastOutSlowInInterpolator());
        if (show){
            valueAnimator.setFloatValues(1f, 0f);
        } else {
            valueAnimator.setFloatValues(0f, 1f);
        }
        ViewGroup.LayoutParams lp = getLayoutParams();
        final int offsetY;
        if (lp instanceof RelativeLayout.LayoutParams){
            offsetY = ((RelativeLayout.LayoutParams)lp).bottomMargin + getMeasuredHeight();
        } else if (lp instanceof CoordinatorLayout.LayoutParams){
            offsetY = ((CoordinatorLayout.LayoutParams)lp).bottomMargin + getMeasuredHeight();
        } else {
            offsetY = 400 + getMeasuredHeight();
        }
        final List<View> children = new ArrayList<>();
        final List<Float> offsetX = new ArrayList<>();
        float centerX = getMeasuredWidth() / 2f;

        ViewGroup parent = (ViewGroup)getChildAt(0);
        for(int i = 0; i < parent.getChildCount(); i++){
            View child = parent.getChildAt(i);
            if (child.getVisibility() != View.GONE){
                children.add(child);
                float x = (child.getRight() + child.getLeft()) / 2f;
                offsetX.add(centerX - x);
            }
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setTranslationY(offsetY * value);
                for(int i = 0; i < children.size(); i++){
                    if (children.get(i) != null){
                        children.get(i).setTranslationX(offsetX.get(i) * value);
                    }
                }
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                isRunning = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isRunning = false;
            }
        });
        valueAnimator.start();
    }



}
