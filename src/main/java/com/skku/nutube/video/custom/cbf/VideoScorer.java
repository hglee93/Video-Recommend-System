package com.skku.nutube.video.custom.cbf;

import com.skku.nutube.dto.VideoScoreDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class VideoScorer {

    public Double calCosineSimilarity(Map<String, Double> userProfile, Map<String, Double> iv){
        // TODO Compute the cosine of this item and the user's profile, store it in the output list
        // TODO And remove this exception to say you've implemented it
        Double mulUserItem = 0.0;

        for (Map.Entry<String, Double> e : iv.entrySet()) {

            if (userProfile.containsKey(e.getKey()) == false) {
                continue;
            }

            Double userPreference = userProfile.get(e.getKey());
            mulUserItem += (e.getValue() * userPreference);
        }

        Double squareUser = 0.0;
        for(Map.Entry<String, Double> e : userProfile.entrySet()) {
            squareUser += (e.getValue() * e.getValue());
        }
        squareUser = Math.sqrt(squareUser);

        Double squareItem = 0.0;
        for(Map.Entry<String, Double> e : iv.entrySet()) {
            squareItem += (e.getValue() * e.getValue());
        }

        squareItem = Math.sqrt(squareItem);

        // If the denominator of the cosine similarity is 0, skip the item
        if(squareUser.equals(0.0) || squareItem.equals(0.0)) {
            return -1.0;
        }

        Double cs = mulUserItem / (squareItem * squareUser);
        return cs;
    }

    public List<VideoScoreDto> scoreWithDetails(Map<String, Double> userProfile,
                                                Map<Integer, Map<String, Double>> itemVectors,
                                                Map<Integer, String> itemTitleVectors) {

        List<VideoScoreDto> videoScoreDtoList = new ArrayList<>();

        Set<Integer> items = itemVectors.keySet();

        for (Integer item: items) {

            Map<String, Double> iv = itemVectors.get(item);
            Double cs = calCosineSimilarity(userProfile, iv);

            if(cs.equals(-1.0)) {
                continue;
            }

            videoScoreDtoList.add(new VideoScoreDto(item, itemTitleVectors.get(item), cs));
        }

        return videoScoreDtoList;
    }
}
