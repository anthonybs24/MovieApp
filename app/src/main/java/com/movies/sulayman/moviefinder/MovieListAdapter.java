package com.movies.sulayman.moviefinder;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.movies.sulayman.moviefinder.Model.Movie;
import com.movies.sulayman.moviefinder.Utilities.NetworkUtils;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {

    private static final String TAG = MovieListAdapter.class.getSimpleName();
    private final MovieListAdapterOnClickListener mOnClickListener;
    private Movie[] mMovies;

    public interface MovieListAdapterOnClickListener {
        void onClick (Movie selectedMovie);
    }

    public MovieListAdapter(MovieListAdapterOnClickListener itemClickListener) {
            mOnClickListener = itemClickListener;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mThumbnailView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mThumbnailView = (ImageView) itemView.findViewById(R.id.iv_thumbnail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Movie selectedMovie = mMovies[clickedPosition];
            mOnClickListener.onClick(selectedMovie);
        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_list, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = mMovies[position];

        Uri moviePosterUri = Uri.parse(NetworkUtils.getMoviePosterBaseUrl() + movie.getPosterPath());
        Glide.with(holder.mThumbnailView.getContext())
                .load(moviePosterUri)
                .into(holder.mThumbnailView);
    }

    @Override
    public int getItemCount() {
        if (mMovies == null)
            return 0;
        else
            return mMovies.length;
    }

    public void setMovieData (Movie[] movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }
}
