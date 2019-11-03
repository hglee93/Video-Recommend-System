package com.skku.nutube.video.cbf;

import com.google.common.collect.ImmutableMap;
import org.grouplens.grapht.annotation.DefaultProvider;
import org.lenskit.inject.Shareable;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * The model for a TF-IDF recommender.  The model just remembers the normalized tag vector for each
 * item.
 *
 * @see VideoModelProvider
 */
// LensKit models are annotated with @Shareable so they can be serialized and reused
@Shareable
// This model class will be built by the model builder
@DefaultProvider(VideoModelProvider.class)
public class VideoModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<Integer, Map<String, Double>> itemVectors;

    /**
     * Constructor for the model.  This is package-private; the only way to build a model is with
     * the {@linkplain VideoModelProvider model builder}.
     *
     * @param itemVectors A map of item IDs to tag vectors.
     */
    VideoModel(Map<Integer, Map<String, Double>> itemVectors) {
        ImmutableMap.Builder<Integer,Map<String,Double>> bld = ImmutableMap.builder();
        for (Map.Entry<Integer,Map<String,Double>> e: itemVectors.entrySet()) {
            bld.put(e.getKey(), ImmutableMap.copyOf(e.getValue()));
        }
        this.itemVectors = bld.build();
    }

    /**
     * Get the normalized tag vector for a particular item.
     *
     * @param item The item.
     * @return The item's tag vector.  If the item is not known to the model, then this vector is
     *         empty.
     */
    public Map<String, Double> getItemVector(long item) {
        // Look up the item
        Map<String, Double> vec = itemVectors.get(item);
        if (vec == null) {
            // We don't know the item! Return an empty vector
            return Collections.emptyMap();
        } else {
            return vec;
        }
    }
}
