package com.wyuxks.downloadmaster.config;

/**
 * 创建者      xks
 * 创建时间   2016/8/23 15:15
 * 描述	     封装/存放/组合 和下载相关的参数
 * 更新者     $Author: admin $
 * 更新描述   ${TODO}
 */
public class Info {
    private String artist;
    private String duration;
    private String name;
    private String url;
    private int downStatus; //下载状态
    private int selectStatus; //选中状态
    private long size; //歌曲大小


    public Info(String artist, String duration, String name, String url, int downStatus, int selectStatus, long size) {
        this.artist = artist;
        this.duration = duration;
        this.name = name;
        this.url = url;
        this.downStatus = downStatus;
        this.selectStatus = selectStatus;
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getDownStatus() {
        return downStatus;
    }

    public void setDownStatus(int downStatus) {
        this.downStatus = downStatus;
    }

    public int getSelectStatus() {
        return selectStatus;
    }

    public void setSelectStatus(int selectStatus) {
        this.selectStatus = selectStatus;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
