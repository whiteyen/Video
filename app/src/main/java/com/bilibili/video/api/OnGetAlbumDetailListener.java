package com.bilibili.video.api;

import com.bilibili.video.model.Album;
import com.bilibili.video.model.ErrorInfo;

public interface OnGetAlbumDetailListener {
    void onGetAlbumDetailSuccess(Album album);
    void onGetAlbumDetailFailed(ErrorInfo info);

}
