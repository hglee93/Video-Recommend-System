package com.skku.nutube.video.custom.cf;

import com.skku.nutube.dto.VideoLikeDto;
import com.skku.nutube.dto.VideoScoreDto;
import com.skku.nutube.repository.VideoLikeRepository;
import com.skku.nutube.repository.VideoRepository;
import com.skku.nutube.repository.VideoUserRepository;
import com.skku.nutube.video.custom.cbf.UserProfileLearner;
import com.skku.nutube.video.custom.cbf.VideoContentAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CollaborativeFiltering {

    @Autowired
    VideoLikeRepository videoLikeRepository;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    VideoUserRepository videoUserRepository;

    @Autowired
    UserProfileLearner userProfileLearner;

    @Autowired
    VideoContentAnalyzer videoContentAnalyzer;

    static final int TOPK= 10;

    static final int TOPN= 10;

    public Double calJaccardSimilarity(Map<Integer, Integer> userProfileA, Map<Integer, Integer> userProfileB) {

        Double intersection = 0.0;
        Double union = 0.0;
        Double js = 0.0;

        Map<Integer, Integer> outerMap;
        Map<Integer, Integer> innerMap;

        if(userProfileA.size() < userProfileB.size()) {
            outerMap = userProfileA;
            innerMap = userProfileB;
        } else {
            outerMap = userProfileB;
            innerMap = userProfileA;
        }

        for(Map.Entry<Integer, Integer> entry : outerMap.entrySet()) {
            if(innerMap.containsKey(entry.getKey()) == true) {
                intersection += 1.0;
            }
        }

        union = userProfileA.size() + userProfileB.size() - intersection;
        js = intersection / union;

        return js;
    }

    public Map<Integer, Double> getUserSimVector(Integer targetId, Map<Integer, Integer> targetProfile) {

        Map<Integer, Double> allSimVector = new HashMap<>();

        List<Integer> userIdList = videoUserRepository.selectUserId();

        // 사용자 유사도 계산
        for(Integer uid : userIdList) {

            if(uid.equals(targetId)) {
                continue;
            }

            Map<Integer, Integer> userProfile = makeUserProfile(uid);
            Double sim = calJaccardSimilarity(targetProfile, userProfile);
            allSimVector.put(uid, sim);
        }

        // TOP-K Neighbor 저장
        List<Map.Entry<Integer, Double>> simList = new ArrayList<>(allSimVector.entrySet());
        Collections.sort(simList, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        Map<Integer, Double> topKVector = new HashMap<>();

        for(int i = 0; i < TOPK; i++) {
            Map.Entry<Integer, Double> ele = simList.get(i);
            topKVector.put(ele.getKey(), ele.getValue());
        }

        return topKVector;
    }

    public Double caculateAverage(Map<Integer, Integer> Profile) {

        Double avgTargetProfile = 0.0;

        for(Map.Entry<Integer, Integer> e : Profile.entrySet()) {
            avgTargetProfile += e.getValue();
        }

        avgTargetProfile = avgTargetProfile / Profile.size();

        return avgTargetProfile;
    }

    public Map<Integer, Integer> makeUserProfile(Integer userId) {
        List<VideoLikeDto> targetVideoLikeDto = videoLikeRepository.selectLikesByUserId(userId);

        Map<Integer, Integer> targetProfile = new HashMap<>();

        for(VideoLikeDto dto : targetVideoLikeDto) {
            targetProfile.put(dto.getVideoId(), dto.getLike());
        }

        return targetProfile;
    }

    public Map<Integer, Double> scoreWithDetails(Integer userId) {

        List<VideoScoreDto> videoScoreDtoList = new ArrayList<>();
        Map<Integer, Double> itemScoreMap = new HashMap<>();

        // Calculate TOP K User Similarity
        Map<Integer, Integer> targetProfile = makeUserProfile(userId);
        Map<Integer, Double> topkUserSimilarity = getUserSimVector(userId, targetProfile);

        // Get TOP K UserProfile List
        Map<Integer, Map<Integer, Integer>> topkUserProfileList = new HashMap<>();
        Double sumSimilarity = 0.0;

        for(Map.Entry<Integer, Double> u : topkUserSimilarity.entrySet()) {
            Map<Integer, Integer> userProfile = makeUserProfile(u.getKey());
            topkUserProfileList.put(u.getKey(), userProfile);
            sumSimilarity = sumSimilarity + (u.getValue() >= 0 ? u.getValue() : u.getValue() * -1);
        }

        // Calculate Target Average rating score.
        Double targetAvg = caculateAverage(targetProfile);

        List<Integer> videoList = videoRepository.selectItemId();

        for(Integer videoId : videoList) {

            Double secondTerm = 0.0;

            for(Map.Entry<Integer, Double> userSim : topkUserSimilarity.entrySet()) {
                Map<Integer, Integer> userProfile = topkUserProfileList.get(userSim.getKey());
                Double userAvg = caculateAverage(userProfile);

                Double userPreference = 0.0;
                if (userProfile.containsKey(videoId) == true) {
                    userPreference = Double.valueOf(userProfile.get(videoId));
                }

                secondTerm += (userSim.getValue() * (userPreference - userAvg));
            }

            secondTerm = secondTerm / sumSimilarity;

            Double score = targetAvg + secondTerm;

            itemScoreMap.put(videoId, score);
        }

        return itemScoreMap;
    }

    public List<VideoScoreDto> recommend(Integer userId) {

        Map<Integer, Double> itemScoreMap = scoreWithDetails(userId);
        List<VideoScoreDto> videoScoreDtoList = new ArrayList<>();

        // 내림차순 정렬
        List<Integer> list = new ArrayList();
        list.addAll(itemScoreMap.keySet());

        Collections.sort(list, new Comparator() {
            public int compare(Object o1,Object o2) {
                Object v1 = itemScoreMap.get(o1);
                Object v2 = itemScoreMap.get(o2);
                return ((Comparable) v1).compareTo(v2);
            }
        });

        Collections.reverse(list);

        for(int i = 0; i < TOPN; i++) {

            VideoScoreDto videoScoreDto = new VideoScoreDto();
            Integer videoId = list.get(i);
            String videoTitle = videoRepository.selectTitleByItemId(videoId);

            videoScoreDto.setVideoId(videoId);
            videoScoreDto.setVideoTitle(videoTitle);
            videoScoreDto.setSimilarity(itemScoreMap.get(videoId));

            videoScoreDtoList.add(videoScoreDto);
        }

        return videoScoreDtoList;
    }
}
