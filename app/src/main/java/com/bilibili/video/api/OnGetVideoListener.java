package com.bilibili.video.api;

import com.bilibili.video.model.Album;
import com.bilibili.video.model.ErrorInfo;
import com.bilibili.video.model.sohu.VideoList;

public interface OnGetVideoListener {
    void onGetVideoSuccess(VideoList videoList);
    void onGetVideoFailed(ErrorInfo info);

}
