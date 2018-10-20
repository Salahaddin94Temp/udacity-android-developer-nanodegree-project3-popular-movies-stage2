package com.example.android.popularmoviesstage2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.utilities.MovieJsonUtils;
import com.example.android.popularmoviesstage2.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

public class MovieDetail extends AppCompatActivity implements
        TrailersAdapter.ItemClickListener,
        LoaderManager.LoaderCallbacks<String[][]> {

    private TextView mTitle, mPlot, mRating, mReleaseDate, mTrailer;
    private ImageView mPoster;
    private RecyclerView mRecyclerView;

    private static final int LOADER_ID = 10;

    final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w342/";
    final String YOUTUBE_URL = "http://www.youtube.com/watch?v=";

    private String mMovieId;
    private String[][] mTrailerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mTitle = findViewById(R.id.tv_movie_title);
        mPoster = findViewById(R.id.iv_movie_poster);
        mPlot = findViewById(R.id.tv_plot_synopsis);
        mRating = findViewById(R.id.tv_average_rating);
        mReleaseDate = findViewById(R.id.tv_release_date);
        mTrailer = findViewById(R.id.tv_trailers);

        mRecyclerView = findViewById(R.id.rv_trailers);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        if (intent != null) {
            String[] movieDetail = intent.getStringArrayExtra(Intent.EXTRA_TEXT);

            mMovieId = movieDetail[0];
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);

            mTitle.setText(movieDetail[1]);

            String posterUrl = BASE_IMAGE_URL + movieDetail[2];
            Picasso.get().load(posterUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(mPoster);

            mPlot.setText(movieDetail[3]);

            if (!movieDetail[4].equals("-1.0"))
                mRating.setText(movieDetail[4]);
            else
                mRating.setText("N/A");

            mReleaseDate.setText(movieDetail[5]);
        }
    }

    @Override
    public void onItemClick(int click) {

        String currentTrailer = mTrailerData[click][0];

        Uri uri = Uri.parse(YOUTUBE_URL + currentTrailer);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @NonNull
    @Override
    public Loader<String[][]> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<String[][]>(this) {

            String[][] mTrailersData = null;

            @Override
            protected void onStartLoading() {
                if (mTrailersData != null)
                    deliverResult(mTrailersData);
                else
                    forceLoad();
            }

            @Nullable
            @Override
            public String[][] loadInBackground() {
                String networkResult = NetworkUtils.getTrailer(mMovieId);

                String[][] data = null;
                try {
                    data = MovieJsonUtils.getTrailers(networkResult, MovieDetail.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return data;
            }

            @Override
            public void deliverResult(@Nullable String[][] data) {
                mTrailersData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String[][]> loader, String[][] data) {
        if (data != null && data.length > 0) {
            mTrailerData = data;

            String[] trailerNames = new String[data.length];
            for (int i = 0; i < data.length; i++) {
                String currentTrailer = data[i][1];
                trailerNames[i] = currentTrailer;
            }

            mTrailer.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.setAdapter(new TrailersAdapter(trailerNames, MovieDetail.this));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String[][]> loader) {

    }
}