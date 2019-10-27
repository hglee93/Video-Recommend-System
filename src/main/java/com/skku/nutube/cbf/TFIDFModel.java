package com.skku.nutube.cbf;

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
 * @see TFIDFModelProvider
 */

@Shareable
@DefaultProvider(TFIDFModelProvider.class)
public class TFIDFModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<Long, Map<String, Double>> itemVectors;

    TFIDFModel(Map<Long, Map<String, Double>> itemVectors) {
        ImmutableMap.Builder<Long,Map<String,Double>> bld = ImmutableMap.builder();
        for (Map.Entry<Long,Map<String,Double>> e: itemVectors.entrySet()) {
            bld.put(e.getKey(), ImmutableMap.copyOf(e.getValue()));
        }
        this.itemVectors = bld.build();
    }

    public Map<String, Double> getItemVector(long item) {
        Map<String, Double> vec = itemVectors.get(item);
        if (vec == null) {
            return Collections.emptyMap();
        } else {
            return vec;
        }
    }
}
