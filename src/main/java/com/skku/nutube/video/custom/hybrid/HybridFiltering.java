package com.skku.nutube.video.custom.hybrid;

import com.skku.nutube.dto.VideoScoreDto;
import com.skku.nutube.repository.VideoRepository;
import com.skku.nutube.video.custom.cbf.ContentBasedFilter;
import com.skku.nutube.video.custom.cf.CollaborativeFiltering;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HybridFiltering {

    @Autowired
    ContentBasedFilter contentBasedFilter;

    @Autowired
    CollaborativeFiltering collaborativeFiltering;

    @Autowired
    VideoRepository videoRepository;

    static final int TOPN = 10;

    private Map<Integer, Double> scoreWithDetails(Integer userId) {

        Map<Integer, Double> cbItemScore = contentBasedFilter.scoreWithDetails(userId);

        Map<Integer, Double> cfItemScore = collaborativeFiltering.scoreWithDetails(userId);

        Map<Integer, Double> hybridItemScore = new HashMap<>();

        for(Map.Entry<Integer, Double> cbScore : cbItemScore.entrySet()) {
            hybridItemScore.put(cbScore.getKey(), (cbScore.getValue() * 0.5) + (cfItemScore.get(cbScore.getKey()) * 0.5));
        }

        return  hybridItemScore;
    }

    public List<VideoScoreDto> recommend(Integer userId) {
        Map<Integer, Double> itemScore = scoreWithDetails(userId);
        List<VideoScoreDto> videoScoreDtoList = new ArrayList<>();

        List<Integer> list = new ArrayList();
        list.addAll(itemScore.keySet());

        Collections.sort(list, new Comparator() {
            public int compare(Object o1,Object o2) {
                Object v1 = itemScore.get(o1);
                Object v2 = itemScore.get(o2);
                //return ((Comparable) v2).compareTo(v1);
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
            videoScoreDto.setSimilarity(itemScore.get(videoId));

            videoScoreDtoList.add(videoScoreDto);
        }

        for (VideoScoreDto dto : videoScoreDtoList) {
            System.out.println(dto.getVideoId());
            System.out.println(dto.getVideoTitle());
            System.out.println(dto.getSimilarity());
        }

        return videoScoreDtoList;
    }
}
