package com.skku.nutube.video.custom.cbf;

import com.skku.nutube.dto.VideoLikeDto;
import com.skku.nutube.repository.VideoLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserProfileLearner {

    @Autowired
    VideoLikeRepository videoLikeRepository;

    public Map<String, Double> makeUserProfile(Integer userId, Map<Integer, Map<String, Double>> itemVectors){

        Map<String,Double> profile = new HashMap<>();

        List<VideoLikeDto> videoLikeDtoList = videoLikeRepository.selectLikesByUserId(userId);

        // Iterate over the user's ratings to build their profile
        for (VideoLikeDto v: videoLikeDtoList) {
            if (v.getLike() == 1) {
                // TODO Get this item's vector and add it to the user's profile
                Map<String, Double> itemVector = itemVectors.get(v.getVideoId());
                for(Map.Entry<String, Double> item : itemVector.entrySet()) {
                    if(profile.containsKey(item.getKey()) == true) {
                        Double value = profile.get(item.getKey());
                        profile.put(item.getKey(), value + item.getValue());
                    } else {
                        profile.put(item.getKey(), item.getValue());
                    }
                }
            }
        }

        return profile;
    }
}
