package com.example.android.popularmoviesstage1.utilities;

import com.example.android.popularmoviesstage1.Movies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class MovieJsonUtils {

    private static final String ERROR = "status_code";

    public static List<Movies> getMovieThumbnail(String mainJson) throws JSONException {

        final String RESULTS = "results";
        final String TITLE = "original_title";
        final String POSTER = "poster_path";
        final String PLOT = "overview";
        final String RATING = "vote_average";
        final String RELEASE_DATE = "release_date";

        JSONObject root = new JSONObject(mainJson);

        if (root.has(ERROR)) {
            return null;
        }

        JSONArray results = root.getJSONArray(RESULTS);
        List<Movies> movies = new ArrayList<>();

        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            String title = result.getString(TITLE);
            String poster = result.getString(POSTER);
            String plot = result.getString(PLOT);
            Double rating = result.getDouble(RATING);
            String releaseDate = result.getString(RELEASE_DATE);

            movies.add(new Movies(title, poster, plot, rating, releaseDate));
        }

        return movies;
    }
}