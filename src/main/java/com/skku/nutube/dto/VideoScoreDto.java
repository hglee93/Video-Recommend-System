package com.skku.nutube.dto;

public class VideoScoreDto implements Comparable<VideoScoreDto> {
    private Integer videoId;
    private Double similarity;

    public VideoScoreDto(Integer videoId, Double similarity) {
        this.videoId = videoId;
        this.similarity = similarity;
    }

    @Override
    public int compareTo(VideoScoreDto score) {
        return score.similarity.compareTo(this.similarity);
    }

    public int getVideoId() {
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

}
