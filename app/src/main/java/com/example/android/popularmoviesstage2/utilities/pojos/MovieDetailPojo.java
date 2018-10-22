package com.example.android.popularmoviesstage2.utilities.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieDetailPojo {

    private List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public class Result {

        private int id;
        @SerializedName("vote_average")
        private Double voteAverage;
        @SerializedName("poster_path")
        private String posterPath;
        @SerializedName("original_title")
        private String originalTitle;
        private String overview;
        @SerializedName("release_date")
        private String releaseDate;

        public int getId() {
            return id;
        }

        public Double getVoteAverage() {
            return voteAverage;
        }

        public String getPosterPath() {
            return posterPath;
        }

        public String getOriginalTitle() {
            return originalTitle;
        }

        public String getOverview() {
            return overview;
        }

        public String getReleaseDate() {
            return releaseDate;
        }
    }
}