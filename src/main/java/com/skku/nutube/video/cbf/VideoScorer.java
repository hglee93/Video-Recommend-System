package com.skku.nutube.video.cbf;

import com.skku.nutube.dto.VideoDto;
import com.skku.nutube.dto.VideoScoreDto;
import org.lenskit.results.BasicResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class VideoScorer {

    public List<VideoScoreDto> scoreWithDetails(Map<String, Double> userProfile, Map<Integer, Map<String, Double>> itemVectors) {

        List<VideoScoreDto> videoScoreDtoList = new ArrayList<>();

        Set<Integer> items = itemVectors.keySet();

        for (Integer item: items) {

            Map<String, Double> iv = itemVectors.get(item);
            // TODO Compute the cosine of this item and the user's profile, store it in the output list
            // TODO And remove this exception to say you've implemented it
            // If the denominator of the cosine similarity is 0, skip the item

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

            if(squareUser.equals(0.0) || squareItem.equals(0.0)) {
                continue;
            }

            Double cs = mulUserItem / (squareItem * squareUser);

            videoScoreDtoList.add(new VideoScoreDto(item, cs));
        }

        return videoScoreDtoList;
    }
}
