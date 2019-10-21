package com.skku.nutube.dto;

public class VideoDto {

    private Long videoId;

    private String videoName;

    private Double score;

    public VideoDto(Long videoId, String videoName, Double score) {
        this.videoId = videoId;
        this.videoName = videoName;
        this.score = score;
    }

    public Long getVideoId() {
        return videoId;
    }

    public String getVideoName() {
        return videoName;
    }

    public Double getScore() {
        return score;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
