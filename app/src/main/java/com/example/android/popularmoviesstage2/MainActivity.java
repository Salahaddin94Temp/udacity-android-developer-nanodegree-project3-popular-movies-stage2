package com.example.android.popularmoviesstage2;

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

import com.example.android.popularmoviesstage2.utilities.MovieJsonUtils;
import com.example.android.popularmoviesstage2.utilities.NetworkUtils;

import org.json.JSONException;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        MoviesAdapter.ItemClickListener,
        LoaderManager.LoaderCallbacks<List<Movies>> {

    private TextView mErrorText;
    private ProgressBar mLoading;
    private RecyclerView mRecyclerView;

    private static final int POPULAR_LOADER_ID = 1;
    private static final int TOP_RATED_LOADER_ID = 2;

    public static final int POPULAR = 1;
    public static final int TOP_RATED = 2;
    private int mSort;
    private final String SAVE = "save";

    private final int NO_INTERNET = 1;
    private final int ERROR = 2;


    private List<Movies> mMoviesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorText = findViewById(R.id.error_message);
        mLoading = findViewById(R.id.loading_indicator);

        mRecyclerView = findViewById(R.id.rv_movies);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

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
        if (!isConnected) {
            showError(NO_INTERNET);
            return;
        }

        switch (sort) {
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

    @Override
    public void onItemClick(int click) {
        Movies currentMovie = mMoviesData.get(click);
        String title = currentMovie.getTitle();
        String poster = currentMovie.getPoster();
        String plot = currentMovie.getPlot();
        Double rating = currentMovie.getRating();
        String releaseDate = currentMovie.getReleaseDate();

        String[] movieDetail = {title, poster, plot, String.valueOf(rating), releaseDate};

        Intent intent = new Intent(this, MovieDetail.class);
        intent.putExtra(Intent.EXTRA_TEXT, movieDetail);
        startActivity(intent);
    }

    private void showError(int type) {
        String errorMessage;

        switch (type) {
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
    public Loader<List<Movies>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<Movies>>(this) {

            List<Movies> mMovieData = null;

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
            public List<Movies> loadInBackground() {

                String networkResponse = NetworkUtils.getMovieList(mSort);

                List<Movies> data = null;
                try {
                    data = MovieJsonUtils.getMovieThumbnail(networkResponse, MainActivity.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return data;
            }

            @Override
            public void deliverResult(@Nullable List<Movies> data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movies>> loader, List<Movies> data) {
        if (data != null) {
            mMoviesData = data;

            String[] posters = new String[data.size()];
            for (int i = 0; i < data.size(); i++) {
                Movies currentMovie = data.get(i);
                posters[i] = currentMovie.getPoster();
            }

            mLoading.setVisibility(View.INVISIBLE);
            mRecyclerView.setAdapter(new MoviesAdapter(posters, MainActivity.this));
            showMovies();
        } else
            showError(ERROR);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movies>> loader) {

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

        if (id == R.id.sort_popularity)
            loadMovies(POPULAR);
        else
            loadMovies(TOP_RATED);

        return super.onOptionsItemSelected(item);
    }
}