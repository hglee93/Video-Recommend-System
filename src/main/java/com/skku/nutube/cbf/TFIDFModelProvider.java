package com.skku.nutube.cbf;

import it.unimi.dsi.fastutil.longs.LongSet;
import org.lenskit.data.dao.DataAccessObject;
import org.lenskit.data.entities.CommonTypes;
import org.lenskit.data.entities.Entity;
import org.lenskit.inject.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.nio.DoubleBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Builder for computing {@linkplain TFIDFModel TF-IDF models} from item tag data.  Each item is
 * represented by a normalized TF-IDF vector.
 *
 * @author <a href="http://www.grouplens.org">GroupLens Research</a>
 */
public class TFIDFModelProvider implements Provider<TFIDFModel> {
    private static final Logger logger = LoggerFactory.getLogger(TFIDFModelProvider.class);

    private final DataAccessObject dao;

    @Inject
    public TFIDFModelProvider(@Transient DataAccessObject dao) {
        this.dao = dao;
    }

    @Override
    public TFIDFModel get() {
        logger.info("Building TF-IDF model");

        Map<String, Double> docFreq = new HashMap<>();
        Map<Long, Map<String, Double>> itemVectors = new HashMap<>();

        LongSet items = dao.getEntityIds(CommonTypes.ITEM);
        for (long item : items) {

            Map<String, Double> work = new HashMap<>();

            for (Entity tagApplication : dao.query(TagData.ITEM_TAG_TYPE)
                                            .withAttribute(TagData.ITEM_ID, item)
                                            .get()) {
                String tag = tagApplication.get(TagData.TAG);
                if (work.containsKey(tag) == true) {
                    work.put(tag, work.get(tag) + 1.0);
                } else {
                    work.put(tag, 1.0);
                }
                if(work.get(tag) == 1.0) {
                    if (docFreq.containsKey(tag) == true) {
                        docFreq.put(tag, docFreq.get(tag) + 1.0);
                    } else {
                        docFreq.put(tag, 1.0);
                    }
                }

            }

            itemVectors.put(item, work);
        }

        logger.info("Computed TF vectors for {} items", itemVectors.size());

        final double logN = Math.log(items.size());
        for (Map.Entry<String, Double> e : docFreq.entrySet()) {
            e.setValue(logN - Math.log(e.getValue()));
        }

        Map<Long, Map<String, Double>> modelData = new HashMap<>();
        for (Map.Entry<Long, Map<String, Double>> entry : itemVectors.entrySet()) {
            Map<String, Double> tv = new HashMap<>(entry.getValue());

            for(Map.Entry<String, Double> e : tv.entrySet()) {
                e.setValue(e.getValue() * docFreq.get(e.getKey()));
            }

            Double euclideanNorm = 0.0;

            for(Map.Entry<String, Double> e : tv.entrySet()) {
                euclideanNorm += (e.getValue() * e.getValue());
            }

            euclideanNorm = Math.sqrt(euclideanNorm);

            for(Map.Entry<String, Double> e : tv.entrySet()) {
                e.setValue(e.getValue() / euclideanNorm);
            }

            if(entry.getKey().equals(2231L)) {
                for(Map.Entry<String, Double> e : tv.entrySet()) {
                    System.out.println(e.getKey() + ", " + e.getValue());
                }
            }
            modelData.put(entry.getKey(), tv);
        }

        return new TFIDFModel(modelData);
    }
}
