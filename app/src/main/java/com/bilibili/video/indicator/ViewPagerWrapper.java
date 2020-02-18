package com.bilibili.video.indicator;

import android.support.v4.view.ViewPager;

public class ViewPagerWrapper {
    private IndicatorLayout mIndicatorLayout;
    private ViewPager mViewPager;

    public ViewPagerWrapper(IndicatorLayout indicatorLayout,ViewPager viewPager) {
        mIndicatorLayout = indicatorLayout;
        mViewPager = viewPager;
    }
    public static ViewPagerWrapper with(IndicatorLayout layout,ViewPager viewPager){
        return new ViewPagerWrapper(layout,viewPager);
    }
    /**
     * 组合
     */
    public void compose() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mIndicatorLayout.onPagerScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                mIndicatorLayout.onPagerSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mIndicatorLayout.onPagerScrollStateChanged(state);
            }
        });
    }
}
