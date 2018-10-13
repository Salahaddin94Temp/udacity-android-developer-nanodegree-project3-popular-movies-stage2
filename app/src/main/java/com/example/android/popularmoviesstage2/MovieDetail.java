package com.example.android.popularmoviesstage2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetail extends AppCompatActivity {

    TextView mTitle, mPlot, mRating, mReleaseDate;
    ImageView mPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mTitle = findViewById(R.id.movie_title);
        mPoster = findViewById(R.id.movie_poster);
        mPlot = findViewById(R.id.plot_synopsis);
        mRating = findViewById(R.id.average_rating);
        mReleaseDate = findViewById(R.id.release_date);

        Intent intent = getIntent();
        if (intent != null) {
            String[] movieDetail = intent.getStringArrayExtra(Intent.EXTRA_TEXT);

            mTitle.setText(movieDetail[0]);

            final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w342/";
            String posterUrl = BASE_IMAGE_URL + movieDetail[1];
            Picasso.get().load(posterUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(mPoster);

            mPlot.setText(movieDetail[2]);

            if (!movieDetail[3].equals("-1.0"))
                mRating.setText(movieDetail[3]);
            else
                mRating.setText("N/A");

            mReleaseDate.setText(movieDetail[4]);
        }
    }
}
