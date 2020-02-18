package com.bilibili.video.model.sohu;

import com.google.gson.annotations.Expose;

public class VideoResult {
    @Expose
    private long status;

    @Expose
    private String statusText;



    @Expose
    private VideoData data;


    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public VideoData getData() {
        return data;
    }

    public void setData(VideoData data) {
        this.data = data;
    }
}
