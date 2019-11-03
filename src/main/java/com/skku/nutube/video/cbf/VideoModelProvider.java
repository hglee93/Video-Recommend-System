package com.skku.nutube.video.cbf;

import com.skku.nutube.repository.VideoListRepository;
import it.unimi.dsi.fastutil.longs.LongSet;
import org.lenskit.data.dao.DataAccessObject;
import org.lenskit.data.entities.CommonTypes;
import org.lenskit.data.entities.Entity;
import org.lenskit.data.entities.TypedName;
import org.lenskit.inject.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoModelProvider implements Provider<VideoModel> {
    private static final Logger logger = LoggerFactory.getLogger(VideoModelProvider.class);

    private VideoListRepository videoListRepository;

    //@Inject
    //public VideoModelProvider(@Transient DataAccessObject dao) {
        //this.dao = dao;
    //}

    public VideoModelProvider(){
        videoListRepository = new VideoListRepository();
    }

    @Override
    public VideoModel get() {
        logger.info("Building Model");

        Map<String, Double> docFreq = new HashMap<>();
        Map<Integer, Map<String, Double>> itemVectors = new HashMap<>();

        //LongSet items = dao.getEntityIds(CommonTypes.ITEM);
        List<Integer> items = videoListRepository.selectItemId();

        for(Integer item : items) {
            Map<String, Double> work = new HashMap<>();
            List<String> tagList = videoListRepository.selectTagListByItemId(item);

            for(String tag : tagList) {
                //System.out.println(tag);
                tag = tag.replaceAll("\r", "");
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
        /*for (long item : items) {
            Map<String, Double> work = new HashMap<>();
            for (Entity tagApplication : dao.query(VideoTagData.ITEM_TAG_TYPE)
                                            .withAttribute(VideoTagData.ITEM_ID, item)
                                            .get()) {
                String tag = tagApplication.get(VideoTagData.TAG);

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
        }*/

        logger.info("Computed TF vectors for {} items", itemVectors.size());

        final double logN = Math.log(items.size());
        for (Map.Entry<String, Double> e : docFreq.entrySet()) {
            e.setValue(logN - Math.log(e.getValue()));
        }

        Map<Integer, Map<String, Double>> modelData = new HashMap<>();
        for (Map.Entry<Integer, Map<String, Double>> entry : itemVectors.entrySet()) {
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
            modelData.put(entry.getKey(), tv);
        }

        return new VideoModel(modelData);
    }
}
