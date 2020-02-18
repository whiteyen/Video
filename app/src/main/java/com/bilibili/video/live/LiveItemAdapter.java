package com.bilibili.video.live;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bilibili.video.R;
import com.bilibili.video.player.PlayActivity;


public class LiveItemAdapter extends RecyclerView.Adapter<LiveItemAdapter.ViewHolder> {

    private Context mContext;
    // 数据集
    private String[] mDataList = new String[] {
            "CCTV-1 综合","CCTV-3 综艺","CCTV-5 体育",
            "CCTV-6 电影"
    };

    private int[] mIconList = new int[] {
            R.drawable.cctv_1, R.drawable.cctv_3,  R.drawable.cctv_5,
            R.drawable.cctv_6
    };

    private String [] mUrlList = new String[]{
            "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8",
            "http://ivi.bupt.edu.cn/hls/cctv3hd.m3u8",
            "http://ivi.bupt.edu.cn/hls/cctv5hd.m3u8",
            "http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8"
    };

    public LiveItemAdapter(Context context) {
        mContext = context;
    }
    // view 相关
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.live_item, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    // 数据相关
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mIcon.setImageResource(mIconList[position]);
        holder.mTitle.setText(mDataList[position]);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayActivity.launch(mContext,mUrlList[position],mDataList[position]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mIcon;
        public TextView mTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.iv_live_icon);
            mTitle = (TextView) itemView.findViewById(R.id.tv_live_title);
        }
    }

}
