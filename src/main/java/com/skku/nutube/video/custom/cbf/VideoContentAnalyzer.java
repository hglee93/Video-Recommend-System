package com.skku.nutube.video.custom.cbf;

import com.skku.nutube.dto.VideoDto;
import com.skku.nutube.repository.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class VideoContentAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(VideoContentAnalyzer.class);

    @Autowired
    VideoRepository videoRepository;

    private Map<Integer, Map<String, Double>> itemVectors;

    private Map<Integer, String> itemTitleVectors;

    private static final Double PARAM_K = 2.0;

    private static final Double PARAM_B = 0.75;

    public void buildItemVectors() {

        logger.info("Building ItemVectors");

        Map<String, Double> docFreq = new HashMap<>();
        Map<Integer, Map<String, Double>> itemVectors = new HashMap<>();

        // ItemTitle Vector Initialize
        itemTitleVectors = new HashMap<>();
        List<VideoDto> videoDtoList = videoRepository.selectVideo();
        for(VideoDto videoDto : videoDtoList) {
            itemTitleVectors.put(videoDto.getVideoId(), videoDto.getVideoName());
        }

        // Calculate TF
        List<Integer> items = videoRepository.selectItemId();
        for(Integer item : items) {
            Map<String, Double> work = new HashMap<>();
            List<String> tagList = videoRepository.selectTagListByItemId(item);

            for(String tag : tagList) {

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

        // Calculate IDF
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
            /*Double euclideanNorm = 0.0;
            for(Map.Entry<String, Double> e : tv.entrySet()) {
                euclideanNorm += (e.getValue() * e.getValue());
            }
            euclideanNorm = Math.sqrt(euclideanNorm);
            for(Map.Entry<String, Double> e : tv.entrySet()) {
                e.setValue(e.getValue() / euclideanNorm);
            }*/
            modelData.put(entry.getKey(), tv);
        }

        Map<String, Double> sample = modelData.get(1728);

        for(Map.Entry<String, Double> e : sample.entrySet()) {
            System.out.println(e.getKey() + " : " + e.getValue());
        }

        this.itemVectors = modelData;
    }

    public void buildItemVectorsBM25() {

        logger.info("Building ItemVectors");

        Map<String, Double> docFreq = new HashMap<>();
        Map<Integer, Map<String, Double>> itemVectors = new HashMap<>();

        // ItemTitle Vector Initialize
        itemTitleVectors = new HashMap<>();
        List<VideoDto> videoDtoList = videoRepository.selectVideo();
        for(VideoDto videoDto : videoDtoList) {
            itemTitleVectors.put(videoDto.getVideoId(), videoDto.getVideoName());
        }

        // Calculate TF
        List<Integer> items = videoRepository.selectItemId();
        Map<Integer, Integer> itemTagCount= new HashMap<>();
        Double avgTagCount = 0.0;

        for(Integer item : items) {
            Map<String, Double> work = new HashMap<>();
            List<String> tagList = videoRepository.selectTagListByItemId(item);

            // Store Each Item's the number of tags.
            itemTagCount.put(item, tagList.size());
            avgTagCount += tagList.size();

            for(String tag : tagList) {

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

        avgTagCount = avgTagCount / items.size();

        logger.info("Computed TF vectors for {} items", itemVectors.size());

        // Calculate IDF
        final double logN = Math.log(items.size());
        for (Map.Entry<String, Double> e : docFreq.entrySet()) {
            e.setValue(logN - Math.log(e.getValue()));
        }

        Map<Integer, Map<String, Double>> modelData = new HashMap<>();

        for (Map.Entry<Integer, Map<String, Double>> entry : itemVectors.entrySet()) {

            Map<String, Double> tv = new HashMap<>(entry.getValue());

            // Calculate BM25 Score
            for(Map.Entry<String, Double> e : tv.entrySet()) {
                // tv is TF Vector
                // docFreq is IDF Vector
                Double value = e.getValue() * (PARAM_K + 1);
                value = value / (e.getValue() + PARAM_K * ((1 - PARAM_B) + (PARAM_B * (itemTagCount.get(entry.getKey()) / avgTagCount))));

                e.setValue(value * docFreq.get(e.getKey()));
            }

            // Normalization.
            /*Double euclideanNorm = 0.0;
            for(Map.Entry<String, Double> e : tv.entrySet()) {
                euclideanNorm += (e.getValue() * e.getValue());
            }
            euclideanNorm = Math.sqrt(euclideanNorm);

            for(Map.Entry<String, Double> e : tv.entrySet()) {
                e.setValue(e.getValue() / euclideanNorm);
            }*/
            modelData.put(entry.getKey(), tv);
        }

        Map<String, Double> sample = modelData.get(1728);

        for(Map.Entry<String, Double> e : sample.entrySet()) {
            System.out.println(e.getKey() + " : " + e.getValue());
        }

        this.itemVectors = modelData;
    }

    public Map<String, Double> getItemVector(Long videoId) {
        return itemVectors.get(videoId);
    }

    public Map<Integer, String> getItemTitleVectors() {
        return itemTitleVectors;
    }

    public Map<Integer, Map<String, Double>> getItemVectors() {
        return itemVectors;
    }
}
