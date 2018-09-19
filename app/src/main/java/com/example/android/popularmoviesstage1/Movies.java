package com.example.android.popularmoviesstage1;

public class Movies {
    private String title;
    private String poster;
    private String plot;
    private Double rating;
    private String releaseDate;

    public Movies(String title, String poster, String plot, Double rating, String releaseDate) {
        this.title = title;
        this.poster = poster;
        this.plot = plot;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster() {
        return poster;
    }

    public String getPlot() {
        return plot;
    }

    public Double getRating() {
        return rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}