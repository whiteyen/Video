package com.bilibili.video.indicator;

public class PositionData {

    public int mLeft;
    public int mRight;
    public int mBottom;
    public int mTop;

    public int mContentTop;
    public int mContentRight;
    public int mContentBottom;
    public int mContentLeft;

    public int width(){
        return mRight - mLeft;
    }

    public int height(){
        return mBottom-mTop;
    }
    public int contentWidth(){
        return mContentRight - mContentLeft;
    }
    public int contentHeight(){
        return mContentBottom - mContentTop;
    }
    public int horizonalCenter(){
        return mLeft+width()/2;
    }
    public int verticalCenter(){
        return mTop+height()/2;
    }
}
