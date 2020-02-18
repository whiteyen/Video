package com.bilibili.video.model.sohu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*
*   真正数据
* */
public class ResultAlbum {
    //@SerializedName
    //表示将对象的属性跟json里的字段对应映射，解决定义属性与Json不一致
    @SerializedName("tv_dec")
    @Expose
    private  String tvDesc;

    @Expose
    private String director;

    @SerializedName("hor_high_pic")
    @Expose
    private String horHighPic;

    @SerializedName("ver_high_pic")
    @Expose
    private String verHighPic;

    @SerializedName("main_actor")
    @Expose
    private String mainActor;

    @SerializedName("album_name")
    @Expose
    private String albumName;

    @Expose
    private String tip;

    @SerializedName("aid")
    @Expose
    private String albumId;

    @SerializedName("latest_video_count")
    @Expose
    private int lastVideoCount;//专辑最新集数

    @SerializedName("total_video_count")
    @Expose
    private int totalVideoCount;//专辑最新集数



    public String getTvDesc() {
        return tvDesc;
    }

    public void setTvDesc(String tvDesc) {
        this.tvDesc = tvDesc;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getHorHighPic() {
        return horHighPic;
    }

    public void setHorHighPic(String horHighPic) {
        this.horHighPic = horHighPic;
    }

    public String getVerHighPic() {
        return verHighPic;
    }

    public void setVerHighPic(String verHighPic) {
        this.verHighPic = verHighPic;
    }

    public String getMainActor() {
        return mainActor;
    }

    public void setMainActor(String mainActor) {
        this.mainActor = mainActor;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public int getLastVideoCount() {
        return lastVideoCount;
    }

    public void setLastVideoCount(int lastVideoCount) {
        this.lastVideoCount = lastVideoCount;
    }

    public int getTotalVideoCount() {
        return totalVideoCount;
    }

    public void setTotalVideoCount(int totalVideoCount) {
        this.totalVideoCount = totalVideoCount;
    }
}
