package com.example.android.popularmoviesstage1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.popularmoviesstage1.utilities.MovieJsonUtils;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ItemClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private final int POPULAR = 1;
    private final int TOP_RATED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_movies);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        loadMovies(POPULAR);
    }

    private void loadMovies(int sort) {
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

    public class FetchMovie extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();

            //TODO: Add your API Key here
            String api = "";

            Request request = new Request.Builder()
                    .url("http://api.themoviedb.org/3/movie/" + strings[0] + "?api_key=" + api)
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
                mAdapter = new MoviesAdapter(strings, MainActivity.this);
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }
}