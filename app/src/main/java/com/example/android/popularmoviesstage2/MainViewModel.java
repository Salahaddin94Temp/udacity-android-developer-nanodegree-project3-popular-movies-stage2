package com.example.android.popularmoviesstage2;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.android.popularmoviesstage2.database.AppDatabase;
import com.example.android.popularmoviesstage2.database.MovieEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<MovieEntry>> favorites;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getsInstance(this.getApplication());
        favorites = database.favoriteDao().loadAllFavorites();
    }

    public LiveData<List<MovieEntry>> getFavorites() {
        return favorites;
    }
}
