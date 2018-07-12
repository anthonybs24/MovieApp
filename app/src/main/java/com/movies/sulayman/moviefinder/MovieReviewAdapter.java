package com.movies.sulayman.moviefinder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewViewHolder>{
    private static final String TAG = MovieReviewAdapter.class.getSimpleName();
    private String[] reviews;

    public MovieReviewAdapter() {}

    public class MovieReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView mReviewTextView;

        public MovieReviewViewHolder(View itemView) {
            super(itemView);
            mReviewTextView = (TextView) itemView.findViewById(R.id.tv_movie_review);
        }

    }

    @NonNull
    @Override
    public MovieReviewAdapter.MovieReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_reviews, parent, false);
        return new MovieReviewAdapter.MovieReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewAdapter.MovieReviewViewHolder holder, int position) {
        String review = reviews[position];
        holder.mReviewTextView.setText(review);
    }

    @Override
    public int getItemCount() {
        if (reviews == null)
            return 0;
        else
            return reviews.length;
    }

    /**
     * This method is used to set the movie review on a MovieReviewAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new MovieReviewAdapter to display it.
     *
     * @param movieReviews The new movie reviews to be displayed.
     */
    public void setReviewsData(String[] movieReviews) {
        reviews = movieReviews;
        notifyDataSetChanged();
    }
}
