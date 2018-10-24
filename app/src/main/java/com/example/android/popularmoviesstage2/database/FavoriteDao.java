package com.example.android.popularmoviesstage2.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM favorite_movie")
    LiveData<List<MovieEntry>> loadAllFavorites();

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void insertFavorite(MovieEntry movieEntry);

    @Delete
    void deleteFavorite(MovieEntry movieEntry);

    @Query("SELECT * FROM favorite_movie WHERE movie_id = :movieId")
    LiveData<MovieEntry> getFavorite(int movieId);
}