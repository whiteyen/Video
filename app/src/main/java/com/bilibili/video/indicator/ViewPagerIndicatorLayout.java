package com.bilibili.video.indicator;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.bilibili.video.R;

import java.util.ArrayList;
import java.util.List;
/*组配通知*/
public class ViewPagerIndicatorLayout extends FrameLayout implements  IPagerIndicatorLayout, IPagerTitle {
    private HorizontalScrollView mScrollView;
    private LinearLayout mTitleContainer;//title容器
    private LinearLayout mIndicatorContainer;
    private  ViewPagerIndicatorHelper mViewPagerIndicatorHelper;
    private ViewPagerIndicatorAdapter mAdapter;
    private IPagerIndicatorView mIndicator;
    private List<PositionData> mPositionData = new ArrayList<>();
    private float mScrollPivotX = 0.5f;//滚动中心点
    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            mViewPagerIndicatorHelper.setTotalCount(mAdapter.getCount());
            init();
        }

        @Override
        public void onInvalidated() {

        }
    };

    public ViewPagerIndicatorLayout(  Context context) {
        super(context);
        mViewPagerIndicatorHelper = new ViewPagerIndicatorHelper();
        mViewPagerIndicatorHelper.setScrollListener(this);
    }

    private void init() {

        removeAllViews();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.pager_indicator_layout,this);
        mScrollView = view.findViewById(R.id.scrollView);
        mIndicatorContainer = view.findViewById(R.id.ll_indicator_container);

        mTitleContainer = view.findViewById(R.id.ll_title_container);
        initTitleAndIndicator();


    }
    // add title  and indicator to   container
    private void initTitleAndIndicator() {
        for(int i = 0,j=mViewPagerIndicatorHelper.getTotalCount();i<j;i++){
            if(mAdapter!=null){
                IPagerTitle v = mAdapter.getTitle(getContext(),i);
                if(v instanceof ViewPagerTitleView){
                    View view = (View) v;
                    LinearLayout.LayoutParams LP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);
                    mTitleContainer.addView(view,LP);//add to container
                }
            }

        }
        if(mAdapter!=null){
            mIndicator = mAdapter.getIndicator(getContext());
            if(mIndicator instanceof View){
                LinearLayout.LayoutParams LP = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);
                mIndicatorContainer.addView((View)mIndicator,LP);
            }

        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(mAdapter!=null){
            preParePositionData();
            if(mIndicator!=null){
                mIndicator.setPositionData(mPositionData);
            }
            if(mViewPagerIndicatorHelper.getSctollState() == ViewPager.SCROLL_STATE_IDLE){
                //TODO 通知到外面
                onPagerScrolled(mViewPagerIndicatorHelper.getCurrentIndex(),0.0f,0);
                onPagerSelected(mViewPagerIndicatorHelper.getCurrentIndex());

            }
        }
    }
    // prepare the data;
    private void preParePositionData() {
        mPositionData.clear();
        for(int i = 0,j=mViewPagerIndicatorHelper.getTotalCount();i<j;i++){
            PositionData data = new PositionData();
            View v = mTitleContainer.getChildAt(i);
            if(v != null){
                data.mLeft =  v.getLeft();
                data.mRight = v.getRight();
                data.mBottom = v.getBottom();
                data.mTop = v.getTop();
            }
            if(v instanceof  IViewPagerTitleView){
                IViewPagerTitleView view = ((IViewPagerTitleView)v);
                data.mContentLeft = view.getContentLeft();
                data.mContentRight = view.getContentRight();
                data.mContentTop = view.getContentTop();
                data.mContentBottom = view.getContentBottom();
            }else {
                data.mContentLeft =  v.getLeft();
                data.mContentRight = v.getRight();
                data.mContentBottom = v.getBottom();
                data.mContentTop = v.getTop();
            }
            mPositionData.add(data);
        }
    }
    public void setAdapter(ViewPagerIndicatorAdapter adapter){
        if(mAdapter == adapter){
            return;
        }
        if(mAdapter != null){
            mAdapter.unregisterDataSetObservable(mDataSetObserver);
        }
       mAdapter = adapter;
        if(mAdapter!=null){
            mAdapter.registerDataSetObservable(mDataSetObserver);
            mAdapter.notifySetDataChanged();
        }else {
            //适配器为空，设置totalCount
            mViewPagerIndicatorHelper.setTotalCount(0);
            init();
        }
    }

    @Override
    public void onAttachCoolIndicatorLayout() {
        init();//延迟到onAttach ， 添加到onAttachCoolIndicatorLayout
    }

    @Override
    public void onDetachCoolIndicatorLayout() {

    }

    @Override
    public void onSelected(int index, int totalCount) {
        if(mAdapter != null){
            if(mTitleContainer ==null){
                return;
            }
            View view = mTitleContainer.getChildAt(index);
            if(view instanceof  IPagerTitle){
                ( (IPagerTitle)view).onSelected(index,totalCount);
            }
        }
    }

    @Override
    public void onDisSelected(int index, int totalCount) {
        if(mAdapter != null){
            if(mTitleContainer ==null){
                return;
            }
            View view = mTitleContainer.getChildAt(index);
            if(view instanceof  IPagerTitle){
                ( (IPagerTitle)view).onDisSelected(index,totalCount);
            }
        }
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean isLeftToRight) {
        if(mAdapter != null){
            if(mTitleContainer ==null){
                return;
            }
            View view = mTitleContainer.getChildAt(index);
            if(view instanceof  IPagerTitle){
                ( (IPagerTitle)view).onLeave(index,totalCount,leavePercent,isLeftToRight);
            }
        }
    }
    //Title
    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean isLeftToRight) {
            if(mAdapter != null){
                if(mTitleContainer ==null){
                    return;
                }
                View view = mTitleContainer.getChildAt(index);
                if(view instanceof  IPagerTitle){
                    ( (IPagerTitle)view).onEnter(index,totalCount,enterPercent,isLeftToRight);
                }
            }
    }

    @Override
    public void onPagerSelected(int position) {
        if(mAdapter!=null){
            mViewPagerIndicatorHelper.onPageSelected(position);
            if(mIndicator!=null){
                mIndicator.onPagerSelected(position);
            }
        }
    }

    @Override
    public void onPagerScrolled(int position, float positionOffsetPercent, float positionOffsetPixel) {
            if(mAdapter != null){
                mViewPagerIndicatorHelper.onPageScrolled(position,positionOffsetPercent,positionOffsetPixel);
                if(mIndicator != null){
                    mIndicator.onPagerScrolled(position,positionOffsetPercent,positionOffsetPixel);
                }
            }
            if(mScrollView != null && mPositionData.size()>0){
                int currentPosition = Math.min(mPositionData.size()-1,position);
                int nextPosition = Math.min(mPositionData.size()-1,position+1);
                PositionData current = mPositionData.get(currentPosition);
                PositionData next = mPositionData.get(nextPosition);
                float scrollTo = current.horizonalCenter() - mScrollView.getWidth()*mScrollPivotX;
                float nextscrollTo = next.horizonalCenter() - mScrollView.getWidth()*mScrollPivotX;
                mScrollView.scrollTo((int) ((int)scrollTo+(nextscrollTo-scrollTo)*positionOffsetPercent),0);


            }
    }

    @Override
    public void onPagerScrollStateChanged(int state) {
        if(mAdapter != null){
            mViewPagerIndicatorHelper.onPageScrollStateChanged(state);
            if(mIndicator != null){
               mIndicator.onPagerScrollStateChanged(state);
            }
        }
    }

    public void setScrollPivotX(float scrollPivotX) {
        mScrollPivotX = scrollPivotX;
    }
}
