package com.example.android.popularmoviesstage2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.adapters.MoviesAdapter;
import com.example.android.popularmoviesstage2.database.MovieEntry;
import com.example.android.popularmoviesstage2.utilities.MovieJsonUtils;
import com.example.android.popularmoviesstage2.utilities.NetworkUtils;
import com.facebook.stetho.Stetho;

import org.json.JSONException;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        MoviesAdapter.ItemClickListener,
        LoaderManager.LoaderCallbacks<List<MovieEntry>> {

    private TextView mErrorText;
    private ProgressBar mLoading;
    private RecyclerView mRecyclerView;
    private MoviesAdapter mAdapter;

    private static final int POPULAR_LOADER_ID = 1;
    private static final int TOP_RATED_LOADER_ID = 2;

    public static final int POPULAR = 3;
    public static final int TOP_RATED = 4;
    public static final int FAVORITES = 5;

    private final int NO_INTERNET = 1;
    private final int ERROR = 2;
    private final int EMPTY_FAVORITES = 3;

    private static final String SAVE = "save";

    private int mSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: Remove before submit
        Stetho.initializeWithDefaults(this);

        mErrorText = findViewById(R.id.tv_error_message);
        mLoading = findViewById(R.id.pb_loading_indicator);

        mRecyclerView = findViewById(R.id.rv_movies);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        mAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState != null)
            mSort = savedInstanceState.getInt(SAVE);
        loadMovies(mSort);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE, mSort);
    }

    private void loadMovies(int sort) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected && sort != FAVORITES) {
            showError(NO_INTERNET);
            return;
        }

        switch (sort) {
            case FAVORITES:
                mSort = FAVORITES;
                loadFavorites();
                break;
            case TOP_RATED:
                mSort = TOP_RATED;
                getSupportLoaderManager().initLoader(TOP_RATED_LOADER_ID, null, this);
                break;
            case POPULAR:
            default:
                mSort = POPULAR;
                getSupportLoaderManager().initLoader(POPULAR_LOADER_ID, null, this);
        }
    }

    private void loadFavorites() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getFavorites().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> favoriteEntries) {
                if (favoriteEntries != null) {
                    if (favoriteEntries.size() > 0) {
                        mLoading.setVisibility(View.INVISIBLE);
                        mAdapter.setMovies(favoriteEntries);
                        showMovies();
                    } else
                        showError(EMPTY_FAVORITES);
                }
            }
        });
    }

    @Override
    public void onItemClick(int click) {
        List<MovieEntry> movies = mAdapter.getMovies();
        MovieEntry currentMovie = movies.get(click);

        String id = String.valueOf(currentMovie.getMovieId());
        String title = currentMovie.getTitle();
        String poster = currentMovie.getPoster();
        String plot = currentMovie.getPlot();
        Double rating = currentMovie.getRating();
        String releaseDate = currentMovie.getReleaseDate();

        String[] movieDetail = {id, title, poster, plot, String.valueOf(rating), releaseDate};

        Intent intent = new Intent(this, MovieDetail.class);
        intent.putExtra(Intent.EXTRA_TEXT, movieDetail);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, currentMovie);
        startActivity(intent);
    }

    private void showError(int type) {
        String errorMessage;

        switch (type) {
            case EMPTY_FAVORITES:
                errorMessage = getString(R.string.empty_favorites);
                break;
            case NO_INTERNET:
                errorMessage = getString(R.string.no_internet);
                break;
            case ERROR:
            default:
                errorMessage = getString(R.string.error);
        }

        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorText.setVisibility(View.VISIBLE);
        mErrorText.setText(errorMessage);
    }

    private void showMovies() {
        mErrorText.setVisibility(View.INVISIBLE);
        mLoading.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    public Loader<List<MovieEntry>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<MovieEntry>>(this) {

            List<MovieEntry> mMovieData = null;

            @Override
            protected void onStartLoading() {
                if (mMovieData != null)
                    deliverResult(mMovieData);
                else {
                    mLoading.setVisibility(View.VISIBLE);
                    mErrorText.setVisibility(View.INVISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public List<MovieEntry> loadInBackground() {

                String networkResponse = NetworkUtils.getMovieList(mSort);

                List<MovieEntry> data = null;
                try {
                    data = MovieJsonUtils.getMovieDetails(networkResponse, MainActivity.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return data;
            }

            @Override
            public void deliverResult(@Nullable List<MovieEntry> data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished
            (@NonNull Loader<List<MovieEntry>> loader, List<MovieEntry> data) {
        if (data != null) {
            mLoading.setVisibility(View.INVISIBLE);
            mAdapter.setMovies(data);
            showMovies();
        } else
            showError(ERROR);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<MovieEntry>> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.sort_order, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.sort_popularity:
                loadMovies(POPULAR);
                break;
            case R.id.sort_top_rated:
                loadMovies(TOP_RATED);
                break;
            case R.id.show_favorites:
                loadMovies(FAVORITES);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSort == FAVORITES)
            loadFavorites();
    }
}