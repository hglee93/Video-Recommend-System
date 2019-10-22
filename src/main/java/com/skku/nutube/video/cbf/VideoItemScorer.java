package com.skku.nutube.video.cbf;

import org.lenskit.api.Result;
import org.lenskit.api.ResultMap;
import org.lenskit.basic.AbstractItemScorer;
import org.lenskit.data.dao.DataAccessObject;
import org.lenskit.data.entities.CommonAttributes;
import org.lenskit.data.ratings.Rating;
import org.lenskit.results.BasicResult;
import org.lenskit.results.Results;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class VideoItemScorer extends AbstractItemScorer {
    private final DataAccessObject dao;
    private final VideoModel model;
    private final UserProfileBuilder profileBuilder;

    /**
     * Construct a new item scorer.  LensKit's dependency injector will call this constructor and
     * provide the appropriate parameters.
     *
     * @param dao The data access object, for looking up users' ratings.
     * @param m   The precomputed model containing the item tag vectors.
     * @param upb The user profile builder for building user tag profiles.
     */
    @Inject
    public VideoItemScorer(DataAccessObject dao, VideoModel m, UserProfileBuilder upb) {
        this.dao = dao;
        model = m;
        profileBuilder = upb;
    }

    /**
     * Generate item scores personalized for a particular user.  For the TFIDF scorer, this will
     * prepare a user profile and compare it to item tag vectors to produce the score.
     *
     * @param user   The user to score for.
     * @param items  A collection of item ids that should be scored.
     */
    @Nonnull
    @Override
    public ResultMap scoreWithDetails(long user, @Nonnull Collection<Long> items){
        // Get the user's ratings
        List<Rating> ratings = dao.query(Rating.class)
                                  .withAttribute(CommonAttributes.USER_ID, user)
                                  .get();

        if (ratings == null) {
            // the user doesn't exist, so return an empty ResultMap
            return Results.newResultMap();
        }

        // Create a place to store the results of our score computations
        List<Result> results = new ArrayList<>();

        // Get the user's profile, which is a vector with their 'like' for each tag
        Map<String, Double> userVector = profileBuilder.makeUserProfile(ratings);

        for (Long item: items) {

            Map<String, Double> iv = model.getItemVector(item);

            // TODO Compute the cosine of this item and the user's profile, store it in the output list
            // TODO And remove this exception to say you've implemented it
            // If the denominator of the cosine similarity is 0, skip the item

            Double mulUserItem = 0.0;

            for (Map.Entry<String, Double> e : iv.entrySet()) {

                if (userVector.containsKey(e.getKey()) == false) {
                    continue;
                }

                Double userPreference = userVector.get(e.getKey());
                mulUserItem += (e.getValue() * userPreference);
            }

            Double squareUser = 0.0;
            for(Map.Entry<String, Double> e : userVector.entrySet()) {
                squareUser += (e.getValue() * e.getValue());
            }
            squareUser = Math.sqrt(squareUser);

            Double squareItem = 0.0;
            for(Map.Entry<String, Double> e : iv.entrySet()) {
                squareItem += (e.getValue() * e.getValue());
            }

            squareItem = Math.sqrt(squareItem);

            if(squareUser.equals(0.0) || squareItem.equals(0.0)) {
                continue;
            }

            Double cs = mulUserItem / (squareItem * squareUser);

            results.add(new BasicResult(item, cs));
        }

        return Results.newResultMap(results);
    }
}
































































