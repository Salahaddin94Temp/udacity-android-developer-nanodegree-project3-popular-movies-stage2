package com.example.android.popularmoviesstage2.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "favorite_movie")
public class MovieEntry implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "movie_id")
    private int movieId;
    private String title;
    private String poster;
    private String plot;
    private Double rating;
    @ColumnInfo(name = "release_date")
    private String releaseDate;

    @Ignore
    public MovieEntry(int movieId, String title, String poster, String plot, Double rating, String releaseDate) {
        this.movieId = movieId;
        this.title = title;
        this.poster = poster;
        this.plot = plot;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    public MovieEntry(int id, int movieId, String title, String poster, String plot, Double rating, String releaseDate) {
        this.id = id;
        this.movieId = movieId;
        this.title = title;
        this.poster = poster;
        this.plot = plot;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return id;
    }

    public int getMovieId() {
        return movieId;
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