package com.example.android.popularmoviesstage1;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder> {

    private String[] mMovieData;
    private final ItemClickListener mOnClickListener;

    public interface ItemClickListener {
        void onItemClick(int click);
    }

    public MoviesAdapter(String[] movieData, ItemClickListener clickListener) {
        mMovieData = movieData;
        mOnClickListener = clickListener;
    }

    @NonNull
    @Override
    public MoviesAdapter.MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.movie_list_item, parent, false);

        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {

        final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185/";

        String currentThumbnail = BASE_IMAGE_URL + mMovieData[position];
        Picasso.get().load(currentThumbnail).into(holder.mMovieThumbnail);
    }

    @Override
    public int getItemCount() {
        return mMovieData.length;
    }

    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mMovieThumbnail;

        public MovieHolder(View itemView) {
            super(itemView);

            mMovieThumbnail = itemView.findViewById(R.id.iv_movie_main_thumbnail);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onItemClick(getAdapterPosition());
        }
    }
}
