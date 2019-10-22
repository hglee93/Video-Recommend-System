package com.skku.nutube.controller;

import com.google.common.base.Throwables;
import com.skku.nutube.dto.VideoDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
public class VideoListController {

    private Path dataFile = Paths.get("data/movielens.yml");
    private Path videoDataFile = Paths.get("videodata/videorec.yml");

    Logger logger = LoggerFactory.getLogger(VideoListController.class);

    @RequestMapping(value = "/video/list", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public List<VideoDto> getVideoList(@RequestParam(value = "userId", defaultValue = "0")String userId) throws IOException {

        LenskitConfiguration config = null;
        List<VideoDto> resultList = null;

        try {
            config = ConfigHelpers.load(new File("etc/basic.groovy"));
        } catch (IOException e) {
            throw new RuntimeException("could not load configuration", e);
        }

        DataAccessObject dao;
        try {
            StaticDataSource data = StaticDataSource.load(dataFile);
            // get the data from the DAO
            dao = data.get();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }

        LenskitRecommenderEngine engine = LenskitRecommenderEngine.build(config, dao);
        logger.info("built recommender engine");

        try (LenskitRecommender rec = engine.createRecommender(dao)) {
            logger.info("obtained recommender from engine");
            // we want to recommend items
            ItemRecommender irec = rec.getItemRecommender();
            assert irec != null; // not null because we configured one
            //ResultList recommendations = irec.recommend(42, 10);
            //List<Long> recommendations = irec.recommend(320, 10);

            /*for(Long itemId : recommendations) {
                System.out.println(itemId);
            }*/
            // for users
            ResultList recommendations = irec.recommendWithDetails(Integer.parseInt(userId), 10, null, null);
            System.out.format("Recommendations for user %d:\n", Integer.parseInt(userId));

            resultList = new ArrayList<>();

            for (Result item : recommendations) {
                Entity itemData = dao.lookupEntity(CommonTypes.ITEM, item.getId());
                String name = null;
                if (itemData != null) {
                    name = itemData.maybeGet(CommonAttributes.NAME);
                }
                //System.out.format("\t%d (%s): %.2f\n", item.getId(), name, item.getScore());
                resultList.add(new VideoDto(item.getId(), name, item.getScore()));
            }
        }


        return resultList;
    }
    @RequestMapping(value = "/test/list", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public List<VideoDto> getTestList(@RequestParam(value = "userId", defaultValue = "0")String userId) throws IOException {

        LenskitConfiguration config = null;
        List<VideoDto> resultList = null;

        try {
            config = ConfigHelpers.load(new File("etc/videobasic.groovy"));
        } catch (IOException e) {
            throw new RuntimeException("could not load configuration", e);
        }

        DataAccessObject dao;
        try {
            StaticDataSource data = StaticDataSource.load(videoDataFile);
            // get the data from the DAO
            dao = data.get();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }

        LenskitRecommenderEngine engine = LenskitRecommenderEngine.build(config, dao);
        logger.info("built recommender engine");

        try (LenskitRecommender rec = engine.createRecommender(dao)) {
            logger.info("obtained recommender from engine");
            // we want to recommend items
            ItemRecommender irec = rec.getItemRecommender();
            assert irec != null; // not null because we configured one
            //ResultList recommendations = irec.recommend(42, 10);
            //List<Long> recommendations = irec.recommend(320, 10);

            /*for(Long itemId : recommendations) {
                System.out.println(itemId);
            }*/
            // for users
            ResultList recommendations = irec.recommendWithDetails(Integer.parseInt(userId), 10, null, null);
            System.out.format("Recommendations for user %d:\n", Integer.parseInt(userId));

            resultList = new ArrayList<>();

            for (Result item : recommendations) {
                Entity itemData = dao.lookupEntity(CommonTypes.ITEM, item.getId());
                String name = null;
                if (itemData != null) {
                    name = itemData.maybeGet(CommonAttributes.NAME);
                }
                //System.out.format("\t%d (%s): %.2f\n", item.getId(), name, item.getScore());
                resultList.add(new VideoDto(item.getId(), name, item.getScore()));
            }
        }


        return resultList;
    }
}
