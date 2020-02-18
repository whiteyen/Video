package com.bilibili.video.model.sohu;

import com.google.gson.annotations.Expose;

/*
* 搜狐数据返回集
* */
public class DetailResult {
    @Expose
    private long status;

    @Expose
    private String statusText;



    //详情
    @Expose
    private ResultAlbum data;

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


    public ResultAlbum getResultAlbum() {
        return data;
    }

    public void setResultAlbum(ResultAlbum resultAlbum) {
        this.data = resultAlbum;
    }
}
