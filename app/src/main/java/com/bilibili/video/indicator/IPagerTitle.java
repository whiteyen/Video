package com.bilibili.video.indicator;

public interface IPagerTitle {

    void onSelected(int index,int totalCount);

    void onDisSelected(int index,int totalCount);
    // leavePercent 1.0f 完全离开
    // isLeftToRight 是否从左向右离开
    void onLeave(int index,int totalCount,float leavePercent,boolean isLeftToRight);

    void onEnter(int index,int totalCount,float enterPercent,boolean isLeftToRight);
}
