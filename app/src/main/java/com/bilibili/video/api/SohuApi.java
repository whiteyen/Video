package com.bilibili.video.api;

import android.text.TextUtils;
import android.util.Log;

import com.bilibili.video.AppManger;
import com.bilibili.video.detail.OnVideoSelectedListener;
import com.bilibili.video.model.Album;
import com.bilibili.video.model.AlbumList;
import com.bilibili.video.model.Channel;
import com.bilibili.video.model.ErrorInfo;
import com.bilibili.video.model.Site;
import com.bilibili.video.model.sohu.DetailResult;
import com.bilibili.video.model.sohu.Result;
import com.bilibili.video.model.sohu.ResultAlbum;
import com.bilibili.video.model.sohu.Video;
import com.bilibili.video.model.sohu.VideoList;
import com.bilibili.video.model.sohu.VideoResult;
import com.bilibili.video.utils.OkHttpUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SohuApi extends BaseSiteApi{

    private static final String TAG = SohuApi.class.getSimpleName();
    private static final int SOHU_CHANNELID_MOVIE = 1; //搜狐电影频道ID
    private static final int SOHU_CHANNELID_SERIES = 2; //搜狐电视剧频道ID
    private static final int SOHU_CHANNELID_VARIETY = 7; //搜狐综艺频道ID
    private static final int SOHU_CHANNELID_DOCUMENTRY = 8; //搜狐纪录片频道ID
    private static final int SOHU_CHANNELID_COMIC = 16; //搜狐动漫频道ID
    private static final int SOHU_CHANNELID_MUSIC = 24; //搜狐音乐频道ID

    //某一专辑详情
    //http://api.tv.sohu.com/v4/album/info/9112373.json?plat=6&poid=1&api_key=9854b2afa779e1a6bff1962447a09dbd&sver=6.2.0&sysver=4.4.2&partner=47
    private final static String API_KEY = "plat=6&poid=1&api_key=9854b2afa779e1a6bff1962447a09dbd&sver=6.2.0&sysver=4.4.2&partner=47";
    private final static String API_ALBUM_INFO = "http://api.tv.sohu.com/v4/album/info/";
    //http://api.tv.sohu.com/v4/search/channel.json?cid=2&o=1&plat=6&poid=1&api_key=9854b2afa779e1a6bff1962447a09dbd&sver=6.2.0&sysver=4.4.2&partner=47&page=1&page_size=1
    private final static String API_CHANNEL_ALBUM_FORMAT = "http://api.tv.sohu.com/v4/search/channel.json" +
            "?cid=%d&o=1&plat=6&poid=1&api_key=9854b2afa779e1a6bff1962447a09dbd&" +
            "sver=6.2.0&sysver=4.4.2&partner=47&page=%d&page_size=%d";
    //http://api.tv.sohu.com/v4/album/videos/9112373.json?page=1&page_size=50&order=0&site=1&with_trailer=1&plat=6&poid=1&api_key=9854b2afa779e1a6bff1962447a09dbd&sver=6.2.0&sysver=4.4.2&partner=47
    private final static String API_ALBUM_VIDOES_FORMAT = "http://api.tv.sohu.com/v4/album/videos/%s.json?page=%s&page_size=%s&order=0&site=1&with_trailer=1&plat=6&poid=1&api_key=9854b2afa779e1a6bff1962447a09dbd&sver=6.2.0&sysver=4.4.2&partner=47";
    // 播放url
    //http://api.tv.sohu.com/v4/video/info/3669315.json?site=1&plat=6&poid=1&api_key=9854b2afa779e1a6bff1962447a09dbd&sver=4.5.1&sysver=4.4.2&partner=47&aid=9112373
    private final static String API_VIDEO_PLAY_URL_FORMAT = "http://api.tv.sohu.com/v4/video/info/%s.json?site=1&plat=6&poid=1&api_key=9854b2afa779e1a6bff1962447a09dbd&sver=4.5.1&sysver=4.4.2&partner=47&aid=%s";
    //真实url格式 m3u8
    //http://hot.vrs.sohu.com/ipad3669271_4603585256668_6870592.m3u8?plat=6uid=f5dbc7b40dad477c8516885f6c681c01&pt=5&prod=app&pg=1
    @Override
    public void onGetChannelAlbums(Channel channel, int pageNo, int pageSize, OnGetChannelAlbumListener listener) {
        String url = getChannelAlbumUrl(channel, pageNo, pageSize);
      //  Log.d(TAG,url);
        doGetChannelAlbumsByUrl(url, listener);
    }

    public void doGetChannelAlbumsByUrl(final String url, final OnGetChannelAlbumListener listener) {
        OkHttpUtils.excute(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null) {
                    ErrorInfo info  = buildErrorInfo(url, "doGetChannelAlbumsByUrl", e, ErrorInfo.ERROR_TYPE_URL);
                    listener.onGetChannelAlbumFailed(info);

                } Log.d(TAG,e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    ErrorInfo info  = buildErrorInfo(url, "doGetChannelAlbumsByUrl", null, ErrorInfo.ERROR_TYPE_HTTP);
                    listener.onGetChannelAlbumFailed(info);
                    return;
                }
               // Log.d(TAG,response.message());
                // 1、取到数据映射Result
                // 2、转换ResultAlbum变成Album
                // 3、Album存到AlbumLis中
                Result result = AppManger.getGson().fromJson(response.body().string(), Result.class);
                AlbumList albumList = toConvertAlbumList(result);
                if (albumList != null) {
                    if (albumList.size() > 0 && listener != null) {
                        listener.onGetChannelAlbumSuccess(albumList);
                    }
                } else {
                    ErrorInfo info  = buildErrorInfo(url, "doGetChannelAlbumsByUrl", null, ErrorInfo.ERROR_TYPE_DATA_CONVERT);
                    listener.onGetChannelAlbumFailed(info);
                }

            }
        });
    }

    private AlbumList toConvertAlbumList(Result result) {
        if (result.getData().getResultAlbumList().size() > 0) { //说明有数据
            AlbumList albumList = new AlbumList();
          //  Log.d(TAG,String.valueOf(result.getData().getResultAlbumList().size()));
            for (ResultAlbum resultAlbum : result.getData().getResultAlbumList()) {
                Album album  = new Album(Site.SOHU);
                album.setAlbumDesc(resultAlbum.getTvDesc());
                album.setAlbumId(resultAlbum.getAlbumId());
                album.setHorImgUrl(resultAlbum.getHorHighPic());
                album.setMainActor(resultAlbum.getMainActor());
                album.setTip(resultAlbum.getTip());
                album.setTitle(resultAlbum.getAlbumName());
                album.setVerImgUrl(resultAlbum.getVerHighPic());
                album.setDirector(resultAlbum.getDirector());
                albumList.add(album);
            }
            return albumList;
        }

        return  null;
    }

    private ErrorInfo buildErrorInfo(String url, String functionName, Exception e, int type) {
        ErrorInfo info  = new ErrorInfo(Site.SOHU, type);
      //  info.setExceptionString(e.getMessage());
        info.setFunctionName(functionName);
        info.setUrl(url);
        info.setTag(TAG);
        info.setClassName(TAG);
        return info;
    }

    private String getChannelAlbumUrl(Channel channel, int pageNo, int pageSize) {
        //格式化url

        return String.format(API_CHANNEL_ALBUM_FORMAT, toConvertChannelId(channel),pageNo, pageSize);
    }

    //自定义频道ID与真实频道id转换
    private int toConvertChannelId(Channel channel) {
        int channelId = -1;//-1 无效值
       // Log.d(TAG,channel.getChannelName()+"<<<>>>>"+channel.getChannelId());
        switch (channel.getChannelId()) {
            case Channel.SHOW:
                channelId = SOHU_CHANNELID_SERIES;
                break;
            case Channel.MOVIE:
                channelId = SOHU_CHANNELID_MOVIE;
                break;
            case Channel.COMIC:
                channelId = SOHU_CHANNELID_COMIC;
                break;
            case Channel.MUSIC:
                channelId = SOHU_CHANNELID_MUSIC;
                break;
            case Channel.DOCUMENTRY:
                channelId = SOHU_CHANNELID_DOCUMENTRY;
                break;
            case Channel.VARIETY:
                channelId = SOHU_CHANNELID_VARIETY;
                break;
        }
        return channelId;
    }


    public void onGetAlbumDetail(Album album, OnGetAlbumDetailListener listener) {
        final String url = API_ALBUM_INFO + album.getAlbumId()+".json?"
                +API_KEY;

        OkHttpUtils.excute(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ErrorInfo info  = buildErrorInfo(url, "onGetAlbumDetail", null, ErrorInfo.ERROR_TYPE_HTTP);
                listener.onGetAlbumDetailFailed(info);
                return;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    ErrorInfo info  = buildErrorInfo(url, "onGetAlbumDetail", null, ErrorInfo.ERROR_TYPE_HTTP);
                    listener.onGetAlbumDetailFailed(info);
                    return;
                }

                DetailResult result =  AppManger.getGson().fromJson(response.body().string(),DetailResult.class);
                if(result.getResultAlbum() != null){
                    if (result.getResultAlbum().getLastVideoCount()>0){
                            album.setVideoTotal(result.getResultAlbum().getLastVideoCount());
                    }else {
                        album.setVideoTotal(result.getResultAlbum().getTotalVideoCount());
                    }
                    //set完数据后，进行通知
                    if(listener!=null){
                        listener.onGetAlbumDetailSuccess(album);
                    }
                }
            }
        });
    }
    public void onGetVideo(int pageSize,int pageNo, final Album album, OnGetVideoListener listener) {
        final String url = String.format(API_ALBUM_VIDOES_FORMAT,album.getAlbumId(),pageNo,pageSize);
        OkHttpUtils.excute(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ErrorInfo info  = buildErrorInfo(url, "onGetVideo", null, ErrorInfo.ERROR_TYPE_HTTP);
                listener.onGetVideoFailed(info);
                return;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    ErrorInfo info  = buildErrorInfo(url, "onGetVideo", null, ErrorInfo.ERROR_TYPE_HTTP);
                    listener.onGetVideoFailed(info);
                    return;
                }
                VideoResult result = AppManger.getGson().fromJson(response.body().string(),VideoResult.class);
                if(result!=null){
                    VideoList videoList = new VideoList();
                    if(result.getData()!=null){
                        for(Video video:result.getData().getVideoList()){
                            Video v = new Video();
                            v.setSite(album.getSite().getSiteId());
                            v.setHorHighPic(video.getHorHighPic());
                            v.setVerHighPic(video.getVerHighPic());
                            v.setVid(video.getVid());
                            v.setAid(video.getAid());
                            v.setVideoName(video.getVideoName());
                            videoList.add(v);
                        }
                    }
                    if(listener!=null){
                        listener.onGetVideoSuccess(videoList);
                    }

                }
            }
        });
    }
    //取视频播放url
    public void onGetVideoPlayUrl(Video video, OnGetVideoPlayUrlListener listener){
            String url = String .format(API_VIDEO_PLAY_URL_FORMAT,video.getVid(),video.getAid());
            OkHttpUtils.excute(url, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ErrorInfo info  = buildErrorInfo(url, "onGetVideoPlayUrl", null, ErrorInfo.ERROR_TYPE_HTTP);
                    listener.onGetFailed(info);
                    return;
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        ErrorInfo info  = buildErrorInfo(url, "onGetVideoPlayUrl", null, ErrorInfo.ERROR_TYPE_HTTP);
                        listener.onGetFailed(info);
                        return;
                    }
                    try {
                        JSONObject result = new JSONObject(response.body().string());
                        JSONObject data =result.optJSONObject("data");
                        String normalUrl = data.optString("url_nor");
                        if(!TextUtils.isEmpty(normalUrl)){
                            normalUrl += "uid=" + getUUID() + "&pt=5&prod=app&pg=1";
                            video.setNormalUrl(normalUrl);
                            //通知获取到标清码流
                            Log.d(TAG,"normal:"+normalUrl);
                            if(listener!=null){
                                listener.onGetNormalUrl(video,normalUrl);
                            }
                        }

                        String highUrl = data.optString("url_high");

                        if(!TextUtils.isEmpty(highUrl)){
                            highUrl += "uid=" + getUUID() + "&pt=5&prod=app&pg=1";
                            Log.d(TAG,"high:"+highUrl);
                            video.setHighUrl(highUrl);
                            //通知获取到标清码流
                            if(listener!=null){
                                listener.onGetHighUrl(video,highUrl);
                            }
                        }
                        String superUrl = data.optString("url_super");

                        if(!TextUtils.isEmpty(superUrl)){
                            Log.d(TAG,"superurl"+superUrl);
                            superUrl += "uid=" + getUUID() + "&pt=5&prod=app&pg=1";
                            video.setSuperUrl(superUrl);
                            //通知获取到标清码流
                            if(listener!=null){
                                listener.onGetSuperUrl(video,superUrl);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

    }
    private String getUUID(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-","");
    }
}
