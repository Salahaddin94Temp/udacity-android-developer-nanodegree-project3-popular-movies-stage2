package com.example.android.popularmoviesstage1;

import android.content.Context;
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

import com.example.android.popularmoviesstage1.utilities.MovieJsonUtils;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ItemClickListener {

    private TextView mErrorText;
    private ProgressBar mLoading;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private final int POPULAR = 1;
    private final int TOP_RATED = 2;

    private final int NO_INTERNET = 1;
    private final int ERROR = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorText = findViewById(R.id.error_message);
        mLoading = findViewById(R.id.loading_indicator);

        mRecyclerView = findViewById(R.id.rv_movies);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        loadMovies(POPULAR);
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
                new FetchMovie().execute("top_rated");
                break;
            case POPULAR:
            default:
                new FetchMovie().execute("popular");
        }
    }

    @Override
    public void onItemClick(int click) {

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

    private class FetchMovie extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoading.setVisibility(View.VISIBLE);
            mErrorText.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String[] doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();

            // TODO: Add your API Key here
            String apiKey = "";

            Request request = new Request.Builder()
                    .url("http://api.themoviedb.org/3/movie/" + strings[0] + "?api_key=" + apiKey)
                    .get()
                    .build();

            String[] result = null;
            try {
                Response response = client.newCall(request).execute();
                result = MovieJsonUtils.getMovieThumbnail(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                mLoading.setVisibility(View.INVISIBLE);
                mAdapter = new MoviesAdapter(strings, MainActivity.this);
                mRecyclerView.setAdapter(mAdapter);
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