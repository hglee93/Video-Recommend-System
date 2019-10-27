package com.skku.nutube.dto;

public class VideoDto {

    private Integer videoId;

    private String videoName;

    public VideoDto() {
    }

    public VideoDto(Integer videoId, String videoName) {
        this.videoId = videoId;
        this.videoName = videoName;
    }

    public Integer getVideoId() {
        return videoId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoId(Integer videoId) {
        this.videoId = videoId;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

}
