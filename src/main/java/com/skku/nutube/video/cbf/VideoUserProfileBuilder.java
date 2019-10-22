package com.skku.nutube.video.cbf;

import org.lenskit.data.ratings.Rating;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Build a user profile from all positive ratings.
 */
public class VideoUserProfileBuilder implements UserProfileBuilder {
    /**
     * The lowest rating that will be considered in the user's profile.
     */
    private static final double RATING_THRESHOLD = 3.5;

    /**
     * The tag model, to get item tag vectors.
     */
    private final VideoModel model;

    @Inject
    public VideoUserProfileBuilder(VideoModel m) {
        model = m;
    }

    @Override
    public Map<String, Double> makeUserProfile(@Nonnull List<Rating> ratings) {
        // Create a new vector over tags to accumulate the user profile
        Map<String,Double> profile = new HashMap<>();

        // Iterate over the user's ratings to build their profile
        for (Rating r: ratings) {
            if (r.getValue() >= 1) {
                // TODO Get this item's vector and add it to the user's profile
                Map<String, Double> itemVector = model.getItemVector(r.getItemId());
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

        for(Map.Entry<String, Double> entry : profile.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        // The profile is accumulated, return it.
        return profile;
    }
}
