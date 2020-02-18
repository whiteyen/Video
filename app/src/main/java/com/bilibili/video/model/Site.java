package com.bilibili.video.model;

import android.content.Context;

import java.io.Serializable;



public class Site implements Serializable {
    public static final int LETV = 2;//乐视
    public static final int SOHU = 1;//搜狐

    public static int MAX_SIZE = 2;
    private int siteId;
    private String siteName;
    private Context mContext;

    public Site(int id, Context context) {
        siteId = id;
        mContext = context;
        switch (siteId) {
            case SOHU:
                siteName = "电视剧";
                break;
            case LETV:
                siteName = "电影";
                break;

        }
    }

    public Site(int id) {
        siteId = id;
        switch (siteId) {
            case SOHU:
                siteName = "电视剧";
                break;
            case LETV:
                siteName = "电影";
                break;

        }
    }


    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
}
