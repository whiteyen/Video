package com.bilibili.video;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bilibili.video.base.BaseFragment;
import com.hejunlin.superindicatorlibray.CircleIndicator;
import com.hejunlin.superindicatorlibray.LoopViewPager;

import butterknife.BindView;

public class HomeFragment extends BaseFragment {


    @BindView(R.id.loop)
    LoopViewPager mLoopViewPager;
    @BindView(R.id.indicator)
    CircleIndicator mCircleIndicator;
    @BindView(R.id.gv_channel)
    GridView mGridView;
    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {
        mLoopViewPager.setAdapter(new HomePagerAdapter(getActivity()));
        mLoopViewPager.setLooperPic(true);//自动轮询
        mCircleIndicator.setViewPager(mLoopViewPager);
        mLoopViewPager.setLooperPic(true);

        mGridView.setAdapter(new ChannelAdapter());
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 6:
                        //TODO 跳转直播
                        break;
                    case 7:
                        //TODO 跳转收藏
                        break;
                    case 8:
                        //TODO  跳转收藏记录
                        break;
                     default:
                         //TODO 跳转频道
                         break;
                }
            }
        });
    }


    private class ChannelAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return Channel.MAX_COUNT;
        }

        @Override
        public Object getItem(int position) {
            return new Channel(position+1,getActivity());
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Channel chanel = (Channel) getItem(position);
            ViewHolder holder = null;
            if(convertView == null){
                convertView=LayoutInflater.from(getActivity()).inflate(R.layout.home_grid_item,null);
                holder = new ViewHolder();
                holder.mTextView = convertView.findViewById(R.id.tv_dec);
                holder.mImageView= convertView.findViewById(R.id.iv_img);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.mTextView.setText(chanel.getChannelName());
            int imgResId = -1;
            int id = chanel.getChannelId();
            switch (id) {
                case Channel.SHOW:
                    imgResId = R.drawable.ic_show;
                    break;
                case Channel.MOVIE:
                    imgResId = R.drawable.ic_movie;
                    break;
                case Channel.COMIC:
                    imgResId = R.drawable.ic_comic;
                    break;
                case Channel.DOCUMENTRY:
                    imgResId = R.drawable.ic_movie;
                    break;
                case Channel.MUSIC:
                    imgResId = R.drawable.ic_music;
                    break;
                case Channel.VARIETY:
                    imgResId = R.drawable.ic_variety;
                    break;
                case Channel.LIVE:
                    imgResId = R.drawable.ic_live;
                    break;
                case Channel.FAVORITE:
                    imgResId = R.drawable.ic_bookmark;
                    break;
                case Channel.HISTORY:
                    imgResId = R.drawable.ic_history;
                    break;
            }

            holder.mImageView.setImageDrawable(getActivity().getResources().getDrawable(imgResId));

            return convertView;
        }

    }
    static class ViewHolder{
        TextView mTextView;
        ImageView mImageView;
    }
}
