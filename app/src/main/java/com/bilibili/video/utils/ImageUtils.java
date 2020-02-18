package com.bilibili.video.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bilibili.video.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ImageUtils {
    private static final float VER_POSTER_RATIO = 0.73f;
    private static final float HOR_POSTER_RATIO = 1.5f;

    public static void displayImage(ImageView view,String url){
        if(view != null && url != null){
            Glide.with(view.getContext()).load(url).into(view);
        }
    }


    public static void displayImage(ImageView view,String url,int width,int height){
        if(view != null && url != null && width>0 && height>0){
            if(width >height){
                 Glide.with(view.getContext())
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter()
                        .error(R.drawable.ic_launcher)//出错时使用
                        .override(height,width)
                        .into(view);
            }else {
                Glide.with(view.getContext())
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter()
                        .error(R.drawable.ic_launcher)//出错时使用
                        .override(width,height)
                        .into(view);
            }
        }

    }
    // 让图片获得最佳比例
    public static Point getVerPostSize(Context context,int columns){
        int width = getScreenWidthPixel(context)/columns;
        width = (int) (width - context.getResources().getDimension(R.dimen.dimen_8dp));
        int height = Math.round((float)width/VER_POSTER_RATIO);
        Point point = new Point();
        point.x = width;
        point.y = height;
        return point;
    }
    public static Point getHorPostSize(Context context,int columns){
        int width = getScreenWidthPixel(context)/columns;
        width = (int) (width - context.getResources().getDimension(R.dimen.dimen_8dp));
        int height = Math.round((float)width/HOR_POSTER_RATIO);
        Point point = new Point();
        point.x = width;
        point.y = height;
        return point;
    }


    private static int getScreenWidthPixel(Context context) {
        WindowManager wm = (WindowManager)context.getSystemService(context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        return width;

    }

}
