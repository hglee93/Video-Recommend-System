package com.skku.nutube.video.cbf;

import com.skku.nutube.repository.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class VideoContentAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(VideoContentAnalyzer.class);

    @Autowired
    VideoRepository videoRepository;

    private Map<Integer, Map<String, Double>> itemVectors;

    @PostConstruct
    public void buildItemVectors() {
        logger.info("Building ItemVectors");

        Map<String, Double> docFreq = new HashMap<>();
        Map<Integer, Map<String, Double>> itemVectors = new HashMap<>();

        List<Integer> items = videoRepository.selectItemId();

        for(Integer item : items) {
            Map<String, Double> work = new HashMap<>();
            List<String> tagList = videoRepository.selectTagListByItemId(item);

            for(String tag : tagList) {
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

        this.itemVectors = modelData;
    }

    public Map<String, Double> getItemVector(Long videoId) {
        return itemVectors.get(videoId);
    }

    public Map<Integer, Map<String, Double>> getItemVectors() {
        return itemVectors;
    }
}
