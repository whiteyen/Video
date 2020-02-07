package com.bilibili.video;

import android.content.Context;

import java.io.Serializable;

public class Channel implements Serializable {
    public static final int SHOW = 1;//电视剧
    public static final int MOVIE = 2;//电影
    public static final int COMIC = 3;//动漫
    public static final int DOCUMENTRY = 4;//纪录片
    public static final int MUSIC = 5;//音乐
    public static final int VARIETY = 6;//综艺
    public static final int LIVE = 7;//直播
    public static final int FAVORITE = 8;//收藏
    public static final int HISTORY = 9;//历史记录
    public static final int MAX_COUNT = 9;//频道数

    private int channelId;
    private String channelName;
    private Context mContext;

    public Channel(int id, Context context) {
        channelId = id;
        mContext = context;
        switch (channelId) {
            case SHOW:
                channelName = "电视剧";
                break;
            case MOVIE:
                channelName = "电影";
                break;
            case COMIC:
                channelName = "动漫";
                break;
            case DOCUMENTRY:
                channelName = "纪录片";
                break;
            case MUSIC:
                channelName = "音乐";
                break;
            case VARIETY:
                channelName = "综艺";
                break;
            case LIVE:
                channelName = "直播";
                break;
            case FAVORITE:
                channelName = "收藏";
                break;
            case HISTORY:
                channelName = "历史";
                break;
        }
    }

    public int getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }
}
