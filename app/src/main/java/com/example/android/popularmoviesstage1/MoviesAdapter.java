package com.example.android.popularmoviesstage1;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder> {

    private final ItemClickListener mOnClickListener;

    public interface ItemClickListener {
        void onItemClick(int click);
    }

    public MoviesAdapter(ItemClickListener clickListener) {
        mOnClickListener = clickListener;
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView movieThumbnail;

        public MovieHolder(View itemView) {
            super(itemView);

            movieThumbnail = itemView.findViewById(R.id.iv_movie_main_thumbnail);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onItemClick(getAdapterPosition());
        }
    }
}
