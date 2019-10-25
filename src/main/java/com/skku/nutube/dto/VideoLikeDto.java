package com.skku.nutube.dto;

public class VideoLikeDto {

    private Integer userId;

    private Integer videoId;

    private Integer like;

    public VideoLikeDto() {}

    public VideoLikeDto(Integer userId, Integer videoId, Integer like) {
        this.userId = userId;
        this.videoId = videoId;
        this.like = like;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getVideoId() {
        return videoId;
    }

    public void setVideoId(Integer videoId) {
        this.videoId = videoId;
    }

    public Integer getLike() {
        return like;
    }

    public void setLike(Integer like) {
        this.like = like;
    }
}
