package com.bilibili.video.indicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Interpolator;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.List;

public class ViewPagerIndicatorView extends View implements IPagerIndicatorView {
    private static final String TAG = ViewPagerIndicatorView.class.getSimpleName();
    private Paint mPaint;
    private int mVerticalPadding;
    private int mHorizonalPadding;
    private RectF mRect = new RectF();
    private LinearInterpolator mStartInterpolator = new LinearInterpolator();
    private LinearInterpolator mEndInterpolator = new LinearInterpolator();
    //指示器
    private int mRoundRadius;
    private int mFillColor;


    private List<PositionData> mPositionData;

    public ViewPagerIndicatorView(Context context) {
        super(context);
        init(context);
    }
    private int dip2px(Context context, int dip) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5);
    }

    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mVerticalPadding = dip2px(context,6);
        mHorizonalPadding = dip2px(context,10);
    }

    @Override
    public void setPositionData(List<PositionData> list) {
        mPositionData = list;
    }

    @Override
    public void onPagerSelected(int position) {

    }

    @Override
    public void onPagerScrolled(int position, float positionOffsetPercent, float positionOffsetPixel) {
            if(mPositionData == null){
                return;
            }
            int currentPosition = Math.min(mPositionData.size() - 1,position);
            int nextPosition = Math.min(mPositionData.size()-1,position+1);
            PositionData current = mPositionData.get(currentPosition);
            PositionData next = mPositionData.get(nextPosition);
            mRect.left = current.mContentLeft - mHorizonalPadding + (next.mContentLeft - current.mContentLeft) * mEndInterpolator.getInterpolation(positionOffsetPercent);
            mRect.top = current.mContentTop - mVerticalPadding;
            //后面乘以控件移动的速率
            mRect.right = current.mContentRight + mHorizonalPadding + (next.mContentRight - current.mContentRight) * mStartInterpolator.getInterpolation(positionOffsetPercent);
            mRect.bottom = current.mContentBottom + mVerticalPadding;
            mRoundRadius = (int) (mRect.height()/2);
            invalidate();
    }

    @Override
    public void onPagerScrollStateChanged(int position) {

    }
//画指示器颜色
    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mFillColor);
        canvas.drawRoundRect(mRect,mRoundRadius,mRoundRadius,mPaint);
        Log.d(TAG,"ggg");

    }

    public void setFillColor(int color){
        mFillColor = color;
    }
}
