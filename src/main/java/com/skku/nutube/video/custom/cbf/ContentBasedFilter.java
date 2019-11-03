package com.skku.nutube.video.custom.cbf;

import com.skku.nutube.dto.VideoScoreDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ContentBasedFilter {

    @Autowired
    VideoContentAnalyzer videoContentAnalyzer;

    @Autowired
    UserProfileLearner userProfileLearner;

    @Autowired
    VideoScorer videoScorer;

    static final int TOPN = 10;

    public Map<Integer, Double> scoreWithDetails(Integer userId) {
        Map<Integer, Double> itemScore = new HashMap<>();
        videoContentAnalyzer.buildItemVectors();

        Map<Integer, Map<String, Double>> itemVectors = videoContentAnalyzer.getItemVectors();
        Map<Integer, String> itemTitleVectors = videoContentAnalyzer.getItemTitleVectors();
        Map<String, Double> profile = userProfileLearner.makeUserProfile(userId, itemVectors);

        List<VideoScoreDto> videoScoreDtoList = videoScorer.scoreWithDetails(profile, itemVectors, itemTitleVectors);
        for(VideoScoreDto dto : videoScoreDtoList) {
            itemScore.put(dto.getVideoId(), dto.getSimilarity());
        }

        return itemScore;
    }

    public List<VideoScoreDto> recommend(Integer userId) {
        videoContentAnalyzer.buildItemVectors();
        //videoContentAnalyzer.buildItemVectorsBM25();

        Map<Integer, Map<String, Double>> itemVectors = videoContentAnalyzer.getItemVectors();
        Map<Integer, String> itemTitleVectors = videoContentAnalyzer.getItemTitleVectors();
        Map<String, Double> profile = userProfileLearner.makeUserProfile(userId, itemVectors);

        List<VideoScoreDto> videoScoreDtoList = videoScorer.scoreWithDetails(profile, itemVectors, itemTitleVectors);

        Collections.sort(videoScoreDtoList);

        return videoScoreDtoList.subList(0, TOPN);
    }
}
