package com.bilibili.video.api;

import com.bilibili.video.model.ErrorInfo;
import com.bilibili.video.model.sohu.Video;
import com.bilibili.video.model.sohu.VideoList;

public interface OnGetVideoPlayUrlListener {
    void onGetSuperUrl(Video video,String url);

    void onGetNormalUrl(Video video,String url);

    void onGetHighUrl(Video video,String url);//

    void onGetFailed(ErrorInfo info);

}
