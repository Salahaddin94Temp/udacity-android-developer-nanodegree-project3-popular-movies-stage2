package com.example.android.popularmoviesstage2.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.R;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerHolder> {

    private String[] mTrailerData;
    private final ItemClickListener mOnClickListener;

    public interface ItemClickListener {
        void onTrailerClick(int click);
    }

    public TrailersAdapter(String[] trailerData, ItemClickListener clickListener) {
        mTrailerData = trailerData;
        mOnClickListener = clickListener;
    }

    @NonNull
    @Override
    public TrailerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.trailer_list_item, parent, false);

        return new TrailerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerHolder holder, int position) {
        String name = mTrailerData[holder.getAdapterPosition()];
        holder.mTrailerName.setText(name);
    }

    @Override
    public int getItemCount() {
        return mTrailerData.length;
    }

    class TrailerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTrailerName;

        TrailerHolder(View itemView) {
            super(itemView);

            mTrailerName = itemView.findViewById(R.id.tv_trailer_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onTrailerClick(getAdapterPosition());
        }
    }
}
