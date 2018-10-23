package com.example.android.popularmoviesstage2.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.R;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {

    private String[][] mReviewData;

    public ReviewsAdapter(String[][] reviewData) {
        mReviewData = reviewData;
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.review_list_item, parent, false);

        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        int pos = holder.getAdapterPosition();

        String author = mReviewData[pos][0];
        String content = mReviewData[pos][1];

        holder.mAuthor.setText(author);
        holder.mContent.setText(content);
    }

    @Override
    public int getItemCount() {
        return mReviewData.length;
    }

    class ReviewHolder extends RecyclerView.ViewHolder {

        TextView mAuthor, mContent;

        ReviewHolder(View itemView) {
            super(itemView);

            mAuthor = itemView.findViewById(R.id.tv_author);
            mContent = itemView.findViewById(R.id.tv_content);
        }
    }
}