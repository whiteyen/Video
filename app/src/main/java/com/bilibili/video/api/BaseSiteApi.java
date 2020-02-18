package com.bilibili.video.api;

import com.bilibili.video.model.Channel;

public abstract class BaseSiteApi {
    public abstract  void onGetChannelAlbums(Channel channel,int pageNo,int pageSize,OnGetChannelAlbumListener listener);
}
