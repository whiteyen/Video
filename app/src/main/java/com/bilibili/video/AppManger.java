package com.bilibili.video;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;

public class AppManger extends Application {
        private static Gson mGson;
        private static OkHttpClient mOkhttpClient;
        private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mGson = new Gson();
        mContext = this;
    }
    public static Gson getGson(){
        return mGson;
    }
    public static OkHttpClient getOkhttpClient(){
        mOkhttpClient = new OkHttpClient();
        return mOkhttpClient;
    }
    public static Context getmContext(){
        return mContext;
    }
    public static Resources getResource(){
        return mContext.getResources();
    }
    /*
       当前网络是否可用
     */
    public static boolean isNetWorkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnected();
    }
    public static boolean isNetWorkWifiAvaliable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(1)!=null){
            NetworkInfo.State state= connectivityManager.getNetworkInfo(1).getState();
            if(state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTED){
                return true;
            }else {
                return  false;
            }
        }
        return false;

    }
}
