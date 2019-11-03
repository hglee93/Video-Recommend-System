package com.skku.nutube.controller;

import com.google.common.base.Throwables;
import com.skku.nutube.dto.VideoDto;
import com.skku.nutube.dto.VideoLikeDto;
import com.skku.nutube.dto.VideoScoreDto;
import com.skku.nutube.repository.VideoLikeRepository;
import com.skku.nutube.repository.VideoListRepository;
import com.skku.nutube.repository.VideoRepository;
import com.skku.nutube.video.custom.cbf.ContentBasedFilter;
import com.skku.nutube.video.custom.cf.CollaborativeFiltering;
import com.skku.nutube.video.custom.hybrid.HybridFiltering;
import org.lenskit.LenskitConfiguration;
import org.lenskit.LenskitRecommender;
import org.lenskit.LenskitRecommenderEngine;
import org.lenskit.api.ItemRecommender;
import org.lenskit.api.Result;
import org.lenskit.api.ResultList;
import org.lenskit.config.ConfigHelpers;
import org.lenskit.data.dao.DataAccessObject;
import org.lenskit.data.dao.file.StaticDataSource;
import org.lenskit.data.entities.CommonAttributes;
import org.lenskit.data.entities.CommonTypes;
import org.lenskit.data.entities.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
public class VideoListController {

    private Path dataFile = Paths.get("data/movielens.yml");
    private Path videoDataFile = Paths.get("videodata/videorec.yml");

    Logger logger = LoggerFactory.getLogger(VideoListController.class);

    @Autowired
    VideoListRepository videoListRepository;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    VideoLikeRepository videoLikeRepository;

    @Autowired
    ContentBasedFilter contentBasedFilter;

    @Autowired
    CollaborativeFiltering collaborativeFiltering;

    @Autowired
    HybridFiltering hybridFiltering;

    static final int THRESHOLD_CB = 10;

    static final int THRESHOLD_CF = 20;

    @RequestMapping(value = "/video/recommend", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public List<VideoScoreDto> getRecommendVideoList(@RequestParam(value = "userId", defaultValue = "0")String userId) throws IOException {

        List<VideoLikeDto> videoLikeDtoList = videoLikeRepository.selectLikesByUserId(Integer.valueOf(userId));

        List<VideoScoreDto> videoScoreDtoList = null;

        if(videoLikeDtoList.size() < THRESHOLD_CB) {
            videoScoreDtoList = contentBasedFilter.recommend(Integer.valueOf(userId));
        } else if(videoLikeDtoList.size() < THRESHOLD_CF) {
            videoScoreDtoList = collaborativeFiltering.recommend(Integer.valueOf(userId));
        } else {
            videoScoreDtoList = hybridFiltering.recommend(Integer.valueOf(userId));
        }

        return videoScoreDtoList;
    }

    @RequestMapping(value = "/video/list", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public List<VideoScoreDto> getVideoList() throws IOException {

        List<VideoDto> videoList = videoRepository.selectVideo();

        List<VideoScoreDto> videoScoreDtoList = new ArrayList<>();

        Random random = new Random();

        for(int i = 0; i < 20; i++) {
            int randomId = random.nextInt(videoList.size());
            VideoScoreDto videoScoreDto = new VideoScoreDto();
            videoScoreDto.setVideoId(videoList.get(randomId).getVideoId());
            videoScoreDto.setVideoTitle(videoList.get(randomId).getVideoName());
            videoScoreDto.setSimilarity(0.0);
            videoScoreDtoList.add(videoScoreDto);
        }

        return videoScoreDtoList;
    }

    @RequestMapping(value = "/like", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public void putVideoLike(@RequestParam(value = "userId", defaultValue = "0")String userId,
                             @RequestParam(value = "videoId", defaultValue = "0")String videoId) throws IOException {
        videoLikeRepository.insertLike(Integer.valueOf(userId), Integer.valueOf(videoId));
    }

}
