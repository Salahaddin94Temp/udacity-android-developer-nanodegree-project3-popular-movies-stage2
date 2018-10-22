package com.example.android.popularmoviesstage2.utilities;

import com.example.android.popularmoviesstage2.database.MovieEntry;
import com.example.android.popularmoviesstage2.utilities.pojos.MovieDetailPojo;
import com.example.android.popularmoviesstage2.utilities.pojos.MovieDetailPojo.Result;
import com.example.android.popularmoviesstage2.utilities.pojos.ReviewPojo;
import com.example.android.popularmoviesstage2.utilities.pojos.TrailerPojo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public final class MovieJsonUtils {

    public static List<MovieEntry> getMovieDetails(String mainJson) {

        List<Result> results = new Gson().fromJson(mainJson, MovieDetailPojo.class).getResults();
        if (results == null)
            return null;

        List<MovieEntry> movies = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            Result result = results.get(i);

            int id = result.getId();
            String title = result.getOriginalTitle();
            String poster = result.getPosterPath();
            String plot = result.getOverview();
            Double rating = result.getVoteAverage();
            String releaseDate = result.getReleaseDate();

            movies.add(new MovieEntry(id, title, poster, plot, rating, releaseDate));
        }

        return movies;
    }

    public static String[][] getTrailers(String mainJson) {

        List<TrailerPojo.Result> results = new Gson().fromJson(mainJson, TrailerPojo.class).getResults();
        if (results == null)
            return null;

        String[][] data = new String[results.size()][2];
        for (int i = 0; i < results.size(); i++) {
            TrailerPojo.Result currentResult = results.get(i);

            String key = currentResult.getKey();
            data[i][0] = key;

            String name = currentResult.getName();
            data[i][1] = name;
        }

        return data;
    }

    public static String[][] getReviews(String mainJson) {

        List<ReviewPojo.Result> results = new Gson().fromJson(mainJson, ReviewPojo.class).getResults();
        if (results == null)
            return null;

        String[][] data = new String[results.size()][2];
        for (int i = 0; i < results.size(); i++) {
            ReviewPojo.Result currentResult = results.get(i);

            String author = currentResult.getAuthor();
            data[i][0] = author;

            String content = currentResult.getContent();
            data[i][1] = content;
        }

        return data;
    }
}