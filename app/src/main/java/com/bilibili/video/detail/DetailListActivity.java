package com.bilibili.video.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bilibili.video.R;
import com.bilibili.video.base.BaseActivity;
import com.bilibili.video.indicator.IPagerIndicatorView;
import com.bilibili.video.indicator.IPagerTitle;
import com.bilibili.video.indicator.IndicatorLayout;
import com.bilibili.video.indicator.ViewPagerIndicatorAdapter;
import com.bilibili.video.indicator.ViewPagerIndicatorLayout;
import com.bilibili.video.indicator.ViewPagerIndicatorView;
import com.bilibili.video.indicator.ViewPagerTitleView;
import com.bilibili.video.indicator.ViewPagerWrapper;
import com.bilibili.video.model.Channel;
import com.bilibili.video.model.Site;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;


public class DetailListActivity extends BaseActivity {
    private static final String CHANNEL_ID = "channid";
    private static final String TAG = DetailListActivity.class.getSimpleName();
    @BindView(R.id.pager)
    ViewPager mViewPager;
    @BindView(R.id.viewpager_indicator)
    IndicatorLayout coolIndicatorLayout;
    private int mChannelId;
    String []mSiteNames = new String[]{
            "搜狐视频","乐视视频"
    };
    private List<String> mDataSet = Arrays.asList(mSiteNames);


    public static void lauchDetailList(Activity context, int channelId) {
        Intent intent = new Intent(context, DetailListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(CHANNEL_ID, channelId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail_list;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            mChannelId = intent.getIntExtra(CHANNEL_ID, 0);
            // Log.d(TAG,"<<<<"+mChannelId);
        }
        Channel channel = new Channel(mChannelId + 1, this);
        String title = channel.getChannelName();
        mToolbar.setTitle(title);
        setSupportActionBar();
        setSupportArrowActionBar(true);



        ViewPagerIndicatorLayout viewPagerIndicatorLayout = new ViewPagerIndicatorLayout(this);
        viewPagerIndicatorLayout.setAdapter(new ViewPagerIndicatorAdapter() {
            @Override
            public int getCount() {
                return mDataSet.size();
            }

            @Override
            public IPagerTitle getTitle(Context context, final int index) {
                ViewPagerTitleView viewPagerITitleView = new ViewPagerTitleView(context);
                viewPagerITitleView.setText(mDataSet.get(index));
                viewPagerITitleView.setNormalColor(Color.parseColor("#333333"));
                viewPagerITitleView.setSelectedColor(Color.parseColor("#e94220"));
                viewPagerITitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return viewPagerITitleView;
            }

            @Override
            public IPagerIndicatorView getIndicator(Context conext) {
                ViewPagerIndicatorView viewPaperIndicatorView = new  ViewPagerIndicatorView(conext);
                viewPaperIndicatorView.setFillColor(Color.parseColor("#ebe4e3"));
                return viewPaperIndicatorView;
            }
        });

        coolIndicatorLayout.setPagerIndicatorLayout(viewPagerIndicatorLayout);
        ViewPagerWrapper.with(coolIndicatorLayout, mViewPager).compose();
        mViewPager.setAdapter(new SitePagerAdapter(getSupportFragmentManager(), this, mChannelId));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initData() {

    }




    private class SitePagerAdapter extends FragmentPagerAdapter {
        private Context mContext;
        private int mChannelId;
        private HashMap<Integer, DetailListFragment> mPagerMap;

        public SitePagerAdapter(FragmentManager fm, Context context, int channelId) {
            super(fm);
            mContext = context;
            mChannelId = channelId;
            mPagerMap = new HashMap<>();
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = DetailListFragment.newInstance(position + 1, mChannelId);
            return fragment;
        }

        @Override
        public int getCount() {
            return Site.MAX_SIZE;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            Object object = super.instantiateItem(container, position);
            if (object instanceof DetailListFragment) {
                mPagerMap.put(position, (DetailListFragment) object);
            }
            return object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.destroyItem(container, position, object);
            mPagerMap.remove(position);
        }
    }
}
