package com.bilibili.video.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bilibili.video.R;

public class PullLoadRecyclerView extends LinearLayout {
    private static final String TAG = PullLoadRecyclerView.class.getSimpleName();
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private  boolean mIsRefresh=false;//是否是刷新
    private boolean mIsLoadMore=false;//是否加载更多
    private RecyclerView mRecyclerView;
    private View mFootView;
    private  AnimationDrawable mAnimationDrawable;
    private  OnPullLoadMoreListener mOnPullLoadMoreListener;
    public PullLoadRecyclerView(Context context) {
        super(context);
        initView(context);
    }

    public PullLoadRecyclerView(Context context,  AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PullLoadRecyclerView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    @SuppressLint("ResourceAsColor")
    private void initView(Context context) {
        mContext = context;
        View view = LayoutInflater.from(mContext).inflate(R.layout.pull_loadmore_layout,null);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        //提供下拉更新
        mSwipeRefreshLayout.setColorSchemeColors(R.color.brown,R.color.black,R.color.orange);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshOnRefresh());

        mRecyclerView  = view.findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);//设置固定大小
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//使用默认动画

        mRecyclerView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mIsRefresh||mIsLoadMore;
            }
        });
        mRecyclerView.setVerticalScrollBarEnabled(false);
        mRecyclerView.addOnScrollListener(new RecyclerViewOnScroll());

        mFootView = view.findViewById(R.id.footer_view);
        ImageView imageView = mFootView.findViewById(R.id.iv_load_img);
        imageView.setBackgroundResource(R.drawable.imooc_loading);
        mAnimationDrawable = (AnimationDrawable) imageView.getBackground();

        TextView textView = mFootView.findViewById(R.id.tv_load_text);
        mFootView.setVisibility(View.GONE);

        this.addView(view);
    }
    //外部可以设置recyclerview的列数
    public void setGridLayout(int spanCount){
        GridLayoutManager manager = new GridLayoutManager(mContext,spanCount);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
    }
    public void setAdapter(RecyclerView.Adapter adapter){
        if (adapter !=null){
            mRecyclerView.setAdapter(adapter);
        }
    }
    class SwipeRefreshOnRefresh implements SwipeRefreshLayout.OnRefreshListener{
        @Override
        public void onRefresh() {
            if(!mIsRefresh){
                mIsRefresh = true;
                refreshData();
            }
        }
    }
    class RecyclerViewOnScroll extends  RecyclerView.OnScrollListener{
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int firstItem = 0;
            int lastItem = 0;

            RecyclerView.LayoutManager recyclerViewLayoutManager= recyclerView.getLayoutManager();
            int totalCount = recyclerViewLayoutManager.getItemCount();
            if (recyclerViewLayoutManager instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerViewLayoutManager;
                //第一个完全可见
                firstItem = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
                //最后一个完全可见
                lastItem = gridLayoutManager.findLastCompletelyVisibleItemPosition();
                if (firstItem == 0 || firstItem == RecyclerView.NO_POSITION) {
                    lastItem =  gridLayoutManager.findLastVisibleItemPosition();
                }
                //什么时候触发上拉刷新
               /* if(mSwipeRefreshLayout.isEnabled()){
                    mSwipeRefreshLayout.setEnabled(true);
                }else {
                    mSwipeRefreshLayout.setEnabled(false);
                }*/
//                1、加载更多是false
//                2、totalCount-1 == lastItem
//                3. mSwipeRefreshLayout可以用
//                4.不是下拉刷新状态
//                5.偏移量dx>0  dy>0
                if(!mIsLoadMore
                        &&  totalCount-1 ==lastItem
                        &&   mSwipeRefreshLayout.isEnabled()
                        && !mIsRefresh
                        &&(dx>0||dy>0)) {
                    mIsLoadMore=true;
                    mSwipeRefreshLayout.setEnabled(false);
                    loadMoreData();
                }else {
                    mSwipeRefreshLayout.setEnabled(true);
                }
            }
        }
    }

    private void loadMoreData() {
        if(mOnPullLoadMoreListener!=null){
            mFootView.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(300).setListener(new AnimatorListenerAdapter() {


                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mFootView.setVisibility(View.VISIBLE);
                    mAnimationDrawable.start();
                }
            }).start();

            invalidate();
            mOnPullLoadMoreListener.loadMore();
        }
    }
    public void setRefreshCompleted(){
        mIsRefresh = false;
        setRefreshing(false);
    }
    //设置是否正在刷新
    private void setRefreshing(boolean isRefreshing) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(isRefreshing);
            }
        });
    }
    public void setLoadMoreCompleted(){
        mIsRefresh = false;
        mIsLoadMore = false;
        setRefreshing(false);
        mFootView.animate().translationY(mFootView.getHeight())
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(300).start();
    }
    private void refreshData() {
        if (mOnPullLoadMoreListener!=null){
            mOnPullLoadMoreListener.reFresh();
        }
    }

    public interface  OnPullLoadMoreListener{
        void reFresh();
        void loadMore();
    }
    public void setOnPullLoadMoreListener(OnPullLoadMoreListener listener){
        mOnPullLoadMoreListener = listener;
    }
}
