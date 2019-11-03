package com.skku.nutube.dto;

import lombok.ToString;

@ToString
public class VideoScoreDto implements Comparable<VideoScoreDto> {
    private Integer videoId;
    private String videoTitle;
    private Double similarity;

    public VideoScoreDto() {}

    public VideoScoreDto(Integer videoId, String videoTitle, Double similarity) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.similarity = similarity;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    @Override
    public int compareTo(VideoScoreDto score) {
        return score.similarity.compareTo(this.similarity);
    }

    public Integer getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public Double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Double similarity) {
        this.similarity = similarity;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VideoScoreDto{");
        sb.append("videoId=" + this.videoId);
        sb.append(", videoTitle=" + this.videoTitle);
        sb.append(", similarity=" + this.similarity + "}");
        return sb.toString();
    }
}
