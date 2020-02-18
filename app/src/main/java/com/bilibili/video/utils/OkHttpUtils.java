package com.bilibili.video.utils;

import com.bilibili.video.AppManger;
import com.bilibili.video.api.OnGetChannelAlbumListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpUtils {
    public  static final String REQUST_TAG = "okhttp";
    public static Request buildRequst(String url){
        if(AppManger.isNetWorkAvailable()){
            Request request = new Request.Builder()
                    .tag(REQUST_TAG)
                    .url(url)
                    .build();
            return request;
        }
        return null;
    }
    public static void excute(String url, Callback callback){
        Request request = buildRequst(url);
        excute(request,callback);

    }
    public static void excute(Request request,Callback callback){
        AppManger.getOkhttpClient().newCall(request).enqueue(callback);
    }

}
