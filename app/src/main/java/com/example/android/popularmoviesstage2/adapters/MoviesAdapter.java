package com.example.android.popularmoviesstage2.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmoviesstage2.R;
import com.example.android.popularmoviesstage2.database.MovieEntry;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder> {

    private final ItemClickListener mOnClickListener;
    private List<MovieEntry> mMovieEntries;

    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185/";

    public interface ItemClickListener {
        void onItemClick(int click);
    }

    public MoviesAdapter(ItemClickListener clickListener) {
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

        MovieEntry currentMovie = mMovieEntries.get(position);

        String currentThumbnail = BASE_IMAGE_URL + currentMovie.getPoster();
        Picasso.get().load(currentThumbnail)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.mMovieThumbnail);
    }

    @Override
    public int getItemCount() {
        if (mMovieEntries == null)
            return 0;
        return mMovieEntries.size();
    }

    public List<MovieEntry> getMovies() {
        return mMovieEntries;
    }

    public void setMovies(List<MovieEntry> movieEntries) {
        mMovieEntries = movieEntries;
        notifyDataSetChanged();
    }

    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mMovieThumbnail;

        MovieHolder(View itemView) {
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
