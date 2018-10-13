package com.example.android.popularmoviesstage2;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ItemClickListener {

    private TextView mErrorText;
    private ProgressBar mLoading;
    private RecyclerView mRecyclerView;

    private final int POPULAR = 1;
    private final int TOP_RATED = 2;
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
                new FetchMovie().execute("top_rated");
                break;
            case POPULAR:
                mSort = POPULAR;
            default:
                new FetchMovie().execute("popular");
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

    private class FetchMovie extends AsyncTask<String, Void, List<Movies>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoading.setVisibility(View.VISIBLE);
            mErrorText.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected List<Movies> doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();

            // TODO: Add your API Key here
            String apiKey = BuildConfig.THE_MOVIE_DB_API_KEY;

            Request request = new Request.Builder()
                    .url("http://api.themoviedb.org/3/movie/" + strings[0] + "?api_key=" + apiKey)
                    .get()
                    .build();

            List<Movies> result = null;
            try {
                Response response = client.newCall(request).execute();
                result = MovieJsonUtils.getMovieThumbnail(response.body().string(), MainActivity.this);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(List<Movies> movies) {
            if (movies != null) {
                mMoviesData = movies;

                String[] posters = new String[movies.size()];
                for (int i = 0; i < movies.size(); i++) {
                    Movies currentMovie = movies.get(i);
                    posters[i] = currentMovie.getPoster();
                }

                mLoading.setVisibility(View.INVISIBLE);
                mRecyclerView.setAdapter(new MoviesAdapter(posters, MainActivity.this));
                showMovies();
            } else
                showError(ERROR);
        }
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