package com.example.android.popularmoviesstage2.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "favorite_movie")
public class FavoriteEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "movie_id")
    private int movieId;
    @ColumnInfo(name = "movie_title")
    private String movieTitle;

    @Ignore
    public FavoriteEntry(int movieId, String movieTitle) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
    }

    public FavoriteEntry(int id, int movieId, String movieTitle) {
        this.id = id;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }
}