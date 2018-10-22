package com.example.android.popularmoviesstage2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.adapters.ReviewsAdapter;
import com.example.android.popularmoviesstage2.adapters.TrailersAdapter;
import com.example.android.popularmoviesstage2.database.AppDatabase;
import com.example.android.popularmoviesstage2.database.MovieEntry;
import com.example.android.popularmoviesstage2.utilities.MovieJsonUtils;
import com.example.android.popularmoviesstage2.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieDetail extends AppCompatActivity implements
        TrailersAdapter.ItemClickListener,
        LoaderManager.LoaderCallbacks<List<String[][]>> {

    private TextView mTitle, mPlot, mRating, mReleaseDate, mTrailer, mReview;
    private ImageView mPoster;
    private CheckBox mFavorite;
    private View mTrailerLine, mReviewLine;
    private RecyclerView mTrailerList;
    private RecyclerView mReviewList;

    private static final int LOADER_ID = 10;

    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w342/";
    private static final String YOUTUBE_URL = "http://www.youtube.com/watch?v=";

    private String mMovieId;
    private String[][] mTrailerData;
    private boolean mIsFavorite;

    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mDb = AppDatabase.getsInstance(this);

        initializeUi();

        final Intent intent = getIntent();
        if (intent != null) {
            String[] movieDetail = intent.getStringArrayExtra(Intent.EXTRA_TEXT);

            mMovieId = movieDetail[0];

            populateUI(movieDetail);
            loadOnlineDetails();

            final MovieEntry movieEntry = (MovieEntry) intent.getSerializableExtra(Intent.EXTRA_LOCAL_ONLY);
            mFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                Cursor cursor = mDb.favoriteDao().getFavorite(Integer.parseInt(mMovieId));
                                if (cursor.getCount() > 0)
                                    return;

                                mDb.favoriteDao().insertFavorite(movieEntry);
                            }
                        });
                    } else {
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                mDb.favoriteDao().deleteFavorite(movieEntry);
                            }
                        });
                    }
                }
            });
        }
    }

    private void populateUI(String[] movieDetail) {
        final String title = movieDetail[1];
        mTitle.setText(title);

        final String poster = movieDetail[2];
        String posterUrl = BASE_IMAGE_URL + poster;
        Picasso.get().load(posterUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(mPoster);

        final String plot = movieDetail[3];
        mPlot.setText(plot);

        final String rating = movieDetail[4];
        if (!rating.equals("-1.0"))
            mRating.setText(rating);
        else
            mRating.setText("N/A");

        final String releaseDate = movieDetail[5];
        mReleaseDate.setText(releaseDate);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = mDb.favoriteDao().getFavorite(Integer.parseInt(mMovieId));
                if (cursor.getCount() > 0) {
                    mIsFavorite = true;
                } else
                    mIsFavorite = false;

                mFavorite.setChecked(mIsFavorite);
            }
        });
    }

    private void initializeUi() {
        mTitle = findViewById(R.id.tv_movie_title);
        mPoster = findViewById(R.id.iv_movie_poster);
        mPlot = findViewById(R.id.tv_plot_synopsis);
        mRating = findViewById(R.id.tv_average_rating);
        mReleaseDate = findViewById(R.id.tv_release_date);
        mFavorite = findViewById(R.id.cb_favorite);
        mTrailerLine = findViewById(R.id.v_trailer_line);
        mTrailer = findViewById(R.id.tv_trailers);
        mReviewLine = findViewById(R.id.v_review_line);
        mReview = findViewById(R.id.tv_review);

        mTrailerList = findViewById(R.id.rv_trailers);
        mTrailerList.setHasFixedSize(true);
        mTrailerList.setLayoutManager(new LinearLayoutManager(this));
        mTrailerList.setNestedScrollingEnabled(false);

        mReviewList = findViewById(R.id.rv_reviews);
        mReviewList.setHasFixedSize(true);
        mReviewList.setLayoutManager(new LinearLayoutManager(this));
        mReviewList.setNestedScrollingEnabled(false);
    }

    private void loadOnlineDetails() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected)
            return;

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
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

                String[][] trailerData = MovieJsonUtils.getTrailers(trailerResult);
                String[][] reviewData = MovieJsonUtils.getReviews(reviewResult);

                data.add(0, trailerData);
                data.add(1, reviewData);

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