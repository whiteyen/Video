package com.bilibili.video.indicator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class IndicatorLayout extends FrameLayout {
    private IPagerIndicatorLayout mPagerIndicatorLayout;

    public IndicatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IndicatorLayout(Context context) {
        super(context);
    }

    public void setPagerIndicatorLayout(IPagerIndicatorLayout pagerIndicatorLayout) {
        if (mPagerIndicatorLayout == pagerIndicatorLayout) {
            return;
        }
        //先Detach
        if (mPagerIndicatorLayout != null) {
            mPagerIndicatorLayout.onDetachCoolIndicatorLayout();
        }
        //后Attach
        mPagerIndicatorLayout = pagerIndicatorLayout;
        removeAllViews();
        if (mPagerIndicatorLayout != null) {
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            addView(((View) mPagerIndicatorLayout), lp);
            mPagerIndicatorLayout.onAttachCoolIndicatorLayout();
        }

    }

    public void onPagerSelected(int position) {
        if (mPagerIndicatorLayout != null) {
            mPagerIndicatorLayout.onPagerSelected(position);
        }
    }

    public void onPagerScrolled(int position, float positionOffsetPercent, int positionOffsetPixel) {
        if (mPagerIndicatorLayout != null) {
            mPagerIndicatorLayout.onPagerScrolled(position, positionOffsetPercent, positionOffsetPixel);
        }
    }

    public void onPagerScrollStateChanged(int state) {
        if (mPagerIndicatorLayout != null) {
            mPagerIndicatorLayout.onPagerScrollStateChanged(state);
        }
    }
}
