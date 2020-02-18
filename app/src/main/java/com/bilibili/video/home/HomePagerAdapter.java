package com.bilibili.video.home;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bilibili.video.R;

public class HomePagerAdapter extends PagerAdapter {
    private Context mContext;
    private int [] mDes = new int[]{
            R.string.a_name,
            R.string.b_name,
            R.string.c_name,
            R.string.d_name,
            R.string.e_name
    };
    private int [] mImg = new int[]{
            R.drawable.a,
            R.drawable.b,
            R.drawable.c,
            R.drawable.d,
            R.drawable.e
    };
    public HomePagerAdapter(Activity activity){
        mContext = activity;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.home_pic_item,null,false);
        TextView textView = view.findViewById(R.id.tv_dec);
        ImageView imageView = view.findViewById(R.id.iv_img);
        textView.setText(mDes[position]);
        imageView.setImageResource(mImg[position]);
        container.addView(view);
        return view;

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return 5;
    }
}
