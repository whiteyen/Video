package com.bilibili.video.api;

import android.content.Context;
import android.net.sip.SipSession;
import android.util.Log;

import com.bilibili.video.model.Album;
import com.bilibili.video.model.Channel;
import com.bilibili.video.model.Site;
import com.bilibili.video.model.sohu.Video;

public class SiteApi {
    public static  void onGetChannelAlbums(Context context,int pageNo, int pageSize, int siteId, int channelId, OnGetChannelAlbumListener listener){

        switch (siteId){
            case Site.LETV:
                new LetvApi().onGetChannelAlbums(new Channel(channelId,context),pageNo,pageSize,listener);
                break;
            case Site.SOHU:
                new SohuApi().onGetChannelAlbums(new Channel(channelId,context),pageNo,pageSize,listener);
                break;
        }
    }
    public static void onGetAlbumDetail( Album album,OnGetAlbumDetailListener listener){
        int siteId = album.getSite().getSiteId();
        switch (siteId){
            case Site.LETV:
                new LetvApi().onGetAlbumDetail(album,listener);
                break;
            case Site.SOHU:
                new SohuApi().onGetAlbumDetail(album,listener);
                break;
        }
    }
    //取video相关信息
    public static void onGetVideo( int pageSize,int pageNo, Album album, OnGetVideoListener listener){
        int siteId = album.getSite().getSiteId();
        switch (siteId){
            case Site.LETV:
                new LetvApi().onGetVideo(pageSize,pageNo,album,listener);
                break;
            case Site.SOHU:
                new SohuApi().onGetVideo(pageSize,pageNo,album,listener);
                break;
        }
    }
    public static void onGetVideoPlayUrl( Video video, OnGetVideoPlayUrlListener listener){
        int siteId = video.getSite();
        switch (siteId){
            case Site.LETV:
                new LetvApi().onGetVideoPlayUrl(video,listener);
                break;
            case Site.SOHU:
                new SohuApi().onGetVideoPlayUrl(video,listener);
                break;
        }
    }
}
