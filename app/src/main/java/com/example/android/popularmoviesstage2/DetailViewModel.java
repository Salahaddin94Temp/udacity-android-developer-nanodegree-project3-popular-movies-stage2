package com.example.android.popularmoviesstage2;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.popularmoviesstage2.database.AppDatabase;
import com.example.android.popularmoviesstage2.database.MovieEntry;

public class DetailViewModel extends ViewModel {

    private LiveData<MovieEntry> movieDetail;

    public DetailViewModel(AppDatabase database, int movieId) {
        movieDetail = database.favoriteDao().getFavorite(movieId);
    }

    public LiveData<MovieEntry> getMovieDetail() {
        return movieDetail;
    }
}
