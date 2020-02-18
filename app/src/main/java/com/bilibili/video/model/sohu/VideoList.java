package com.bilibili.video.model.sohu;

import android.util.Log;

import java.util.ArrayList;

public class VideoList extends ArrayList <Video>{
    private static final String TAG = VideoList.class.getSimpleName();
    public void debug(){
        for(Video a:this){
            Log.d(TAG,">>videoList"+ a.toString());
        }
    }

}
