package com.bilibili.video.api;

import com.bilibili.video.model.AlbumList;
import com.bilibili.video.model.ErrorInfo;

public interface OnGetChannelAlbumListener {
    void  onGetChannelAlbumSuccess(AlbumList albumList);
    void  onGetChannelAlbumFailed(ErrorInfo errorInfo);
}
