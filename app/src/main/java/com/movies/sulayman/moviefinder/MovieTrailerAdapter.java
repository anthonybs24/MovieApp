package com.movies.sulayman.moviefinder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.TrailerViewHolder> {

    private static final String TAG = MovieListAdapter.class.getSimpleName();
    private final MovieTrailerAdapterOnClickListener mOnClickListener;
    private String[] mTrailerKeys;

    public interface MovieTrailerAdapterOnClickListener {
        void onClick (String selectedTrailer);
    }

    public MovieTrailerAdapter(MovieTrailerAdapterOnClickListener itemClickListener) {
        mOnClickListener = itemClickListener;
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mTrailerImageView;
        private TextView mTrailerTitleTextView;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            mTrailerImageView = (ImageView) itemView.findViewById(R.id.iv_movie_trailer);
            mTrailerTitleTextView = (TextView) itemView.findViewById(R.id.tv_trailer_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            String selectedTrailer = mTrailerKeys[clickedPosition];
            mOnClickListener.onClick(selectedTrailer);
        }
    }

    @NonNull
    @Override
    public MovieTrailerAdapter.TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_trailer, parent, false);
        return new MovieTrailerAdapter.TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        String key = mTrailerKeys[position];
        holder.mTrailerTitleTextView.setText("Movie Trailer " + position);
        //Uri moviePosterUri = Uri.parse(NetworkUtils.getMoviePosterBaseUrl() + movie.getPosterPath());

    }

    @Override
    public int getItemCount() {
        if (mTrailerKeys == null)
            return 0;
        else
            return mTrailerKeys.length;
    }

    public void setMovieTrailers (String[] trailerKeys) {
        mTrailerKeys = trailerKeys;
        notifyDataSetChanged();
    }
}
