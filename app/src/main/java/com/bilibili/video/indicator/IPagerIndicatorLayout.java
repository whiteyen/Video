package com.bilibili.video.indicator;

public interface IPagerIndicatorLayout extends IPagerChangeListener{

    //附在CoorIndicatorLayout
    void onAttachCoolIndicatorLayout();

    void onDetachCoolIndicatorLayout();
}
