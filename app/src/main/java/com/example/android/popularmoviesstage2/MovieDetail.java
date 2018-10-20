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

import java.util.ArrayList;
import java.util.List;

public class MovieDetail extends AppCompatActivity implements
        TrailersAdapter.ItemClickListener,
        LoaderManager.LoaderCallbacks<List<String[][]>> {

    private TextView mTitle, mPlot, mRating, mReleaseDate, mTrailer, mReview;
    private ImageView mPoster;
    private View mTrailerLine, mReviewLine;
    private RecyclerView mTrailerList;
    private RecyclerView mReviewList;

    private static final int LOADER_ID = 10;

    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w342/";
    private static final String YOUTUBE_URL = "http://www.youtube.com/watch?v=";

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
        mTrailerLine = findViewById(R.id.v_trailer_line);
        mTrailer = findViewById(R.id.tv_trailers);
        mReviewLine = findViewById(R.id.v_review_line);
        mReview = findViewById(R.id.tv_review);

        mTrailerList = findViewById(R.id.rv_trailers);
        mTrailerList.setHasFixedSize(true);
        mTrailerList.setLayoutManager(new LinearLayoutManager(this));

        mReviewList = findViewById(R.id.rv_reviews);
        mReviewList.setHasFixedSize(true);
        mReviewList.setLayoutManager(new LinearLayoutManager(this));
        mReviewList.setNestedScrollingEnabled(false);

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
    public void onTrailerClick(int click) {

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
    public Loader<List<String[][]>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<String[][]>>(this) {

            List<String[][]> mData = null;

            @Override
            protected void onStartLoading() {
                if (mData != null)
                    deliverResult(mData);
                else
                    forceLoad();
            }

            @Nullable
            @Override
            public List<String[][]> loadInBackground() {
                String trailerResult = NetworkUtils.getTrailer(mMovieId);
                String reviewResult = NetworkUtils.getReview(mMovieId);

                List<String[][]> data = new ArrayList<>();
                try {
                    String[][] trailerData = MovieJsonUtils.getTrailers(trailerResult, MovieDetail.this);
                    String[][] reviewData = MovieJsonUtils.getReviews(reviewResult, MovieDetail.this);
                    data.add(0, trailerData);
                    data.add(1, reviewData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return data;
            }

            @Override
            public void deliverResult(@Nullable List<String[][]> data) {
                mData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<String[][]>> loader, List<String[][]> data) {
        if (data != null) {
            String[][] trailerData = data.get(0);
            String[][] reviewData = data.get(1);

            if (trailerData != null && trailerData.length > 0) {
                mTrailerData = trailerData;

                String[] trailerNames = new String[trailerData.length];
                for (int i = 0; i < trailerData.length; i++) {
                    String currentTrailer = trailerData[i][1];
                    trailerNames[i] = currentTrailer;
                }

                mTrailerLine.setVisibility(View.VISIBLE);
                mTrailer.setVisibility(View.VISIBLE);
                mTrailerList.setVisibility(View.VISIBLE);
                mTrailerList.setAdapter(new TrailersAdapter(trailerNames, MovieDetail.this));
            }

            if (reviewData != null && reviewData.length > 0) {
                mReviewLine.setVisibility(View.VISIBLE);
                mReview.setVisibility(View.VISIBLE);
                mReviewList.setVisibility(View.VISIBLE);
                mReviewList.setAdapter(new ReviewsAdapter(reviewData));
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<String[][]>> loader) {

    }
}