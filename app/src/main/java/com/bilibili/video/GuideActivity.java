package com.bilibili.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GuideActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.ll_dot)
    LinearLayout llDot;
    private List<View> mViewList;
    private ViewPager mViewPager;
    private ImageView mDotList[];
    private int mLastPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        initView();
        initViewPager();
        initDots();
    }

    private void initDots() {
        LinearLayout dotsLayout = (LinearLayout) findViewById(R.id.ll_dot);
        mDotList = new ImageView[mViewList.size()];
        for (int i = 0; i < mViewList.size(); i++) {
            mDotList[i] = (ImageView) dotsLayout.getChildAt(i);
            mDotList[i].setEnabled(false);
        }
        mDotList[0].setEnabled(true);
        mLastPosition = 0;
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        MyPagerAdapter adapter = new MyPagerAdapter(mViewList, this);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(this);
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        mViewList = new ArrayList<>();
        mViewList.add(inflater.inflate(R.layout.activity_guide_one, null));
        mViewList.add(inflater.inflate(R.layout.activity_guide_two, null));
        mViewList.add(inflater.inflate(R.layout.activity_guide_three, null));
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        setCurrentPosition(i);
    }

    private void setCurrentPosition(int position) {
        mDotList[position].setEnabled(true);
        mDotList[mLastPosition].setEnabled(false);
        mLastPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    class MyPagerAdapter extends PagerAdapter {
        private List<View> mViewList;
        private Context mContext;

        MyPagerAdapter(List<View> list, Context context) {
            super();//调用父类的构造方法
            mViewList = list;
            mContext = context;
        }

        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            if (mViewList != null) {

                container.addView(mViewList.get(position));
                if (position == mViewList.size() - 1) {
                    mViewList.get(position).findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startHomeActivity();
                        }
                    });
                }
                return mViewList.get(position);
            }
            return null;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            if (mViewList != null) {
                if (mViewList.size() > 0) {
                    container.removeView(mViewList.get(position));
                }
            }

        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;  //判断能不能进行复用
        }


        @Override
        public int getCount() {
            if (mViewList != null) {
                return mViewList.size();
            }
            return 0;
        }
    }

    private void startHomeActivity() {
        Intent intent = new Intent(GuideActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
