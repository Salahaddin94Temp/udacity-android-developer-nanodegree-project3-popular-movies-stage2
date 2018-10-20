package com.example.android.popularmoviesstage2;

public class Movies {
    private int id;
    private String title;
    private String poster;
    private String plot;
    private Double rating;
    private String releaseDate;

    public Movies(int id, String title, String poster, String plot, Double rating, String releaseDate) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.plot = plot;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return id;
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