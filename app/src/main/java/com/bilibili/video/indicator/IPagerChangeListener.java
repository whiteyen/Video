package com.bilibili.video.indicator;

public interface IPagerChangeListener {
    //页面选中时回调
    void onPagerSelected(int position);


    void onPagerScrolled(int position,float positionOffsetPercent,float positionOffsetPixel);

    //页面滑动状态发生变化时回调
    void onPagerScrollStateChanged(int position);

}
