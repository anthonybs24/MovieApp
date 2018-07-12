package com.movies.sulayman.moviefinder;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.movies.sulayman.moviefinder.Data.MovieContract;
import com.movies.sulayman.moviefinder.Model.Movie;
import com.movies.sulayman.moviefinder.Utilities.MovieJsonUtils;
import com.movies.sulayman.moviefinder.Utilities.NetworkUtils;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String[]>
        , MovieTrailerAdapter.MovieTrailerAdapterOnClickListener {
    private Movie movie;
    private static final int MOVIE_DETAIL_REVIEW_REQUEST = 1;
    private static final String TAG = "MovieDetailActivity";
    private static final int MOVIE_DETAIL_ID = 50;

    private MovieTrailerAdapter mMovieTrailerAdapter;

    @BindView(R.id.iv_movie_poster_detail) ImageView mMoviePosterDetailView;
    @BindView(R.id.tv_movie_title_detail) TextView mTitleView;
    @BindView(R.id.tv_user_rating_detail_value) TextView mUserRatingView;
    @BindView(R.id.tv_release_date_detail_value) TextView mReleaseDetailView;
    @BindView(R.id.tv_synopsis) TextView mSynopsisView;
    @BindView(R.id.favorite_button) Button mFavoriteButton;
    @BindView(R.id.reviews_button) Button mReviewsButton;
    @BindView(R.id.pb_detail_loading_indicator) ProgressBar mDetailProgressBar;
    @BindView(R.id.rv_trailers) RecyclerView mMovieTrailersView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);

        ButterKnife.bind(this);

        Intent parentIntent = getIntent();
        if (parentIntent != null) {
            if (parentIntent.hasExtra(Intent.EXTRA_TEXT)) {
                movie = parentIntent.getParcelableExtra(Intent.EXTRA_TEXT);
                setMovieDetailView();
            }
        }

        mReviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class destinationClass = MovieReviewActivity.class;
                Intent intentToStartMovieReviewActivity = new Intent(MovieDetailActivity.this, destinationClass);
                intentToStartMovieReviewActivity.putExtra(Intent.EXTRA_TEXT, movie);
                startActivityForResult(intentToStartMovieReviewActivity, MOVIE_DETAIL_REVIEW_REQUEST);
            }
        });

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFavoriteButton.getText().equals("Add to favorite")) {
                    onClickAddFavoriteMovie(v);
                } else {
                    onClickRemoveFavoriteMovie(v);
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMovieTrailersView.setLayoutManager(layoutManager);
        mMovieTrailersView.setHasFixedSize(true);

        mMovieTrailerAdapter = new MovieTrailerAdapter(this);
        mMovieTrailersView.setAdapter(mMovieTrailerAdapter);

        setTitle(movie.getTitle());
        getSupportLoaderManager().initLoader(MOVIE_DETAIL_ID, null, MovieDetailActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MOVIE_DETAIL_REVIEW_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.hasExtra(Intent.EXTRA_TEXT)) {
                    movie = data.getParcelableExtra(Intent.EXTRA_TEXT);
                }
            }
        }
    }

    private void setMovieDetailView() {
        Uri moviePosterUri = Uri.parse(NetworkUtils.getMoviePosterBaseUrl() + movie.getPosterPath());
        Glide.with(this)
                .load(moviePosterUri)
                .into(mMoviePosterDetailView);

        mTitleView.setText(movie.getTitle());
        mUserRatingView.setText(String.valueOf(movie.getUserRating()));
        mReleaseDetailView.setText(movie.getReleaseDate());
        mSynopsisView.setText(movie.getSynopsis());
    }

    private void onClickAddFavoriteMovie(View view) {
        if (movie == null)
            return;

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getMovieId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, movie.getUserRating());
        contentValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, movie.getSynopsis());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO, MovieJsonUtils.buildTrailerJsonString(movie.getMovieTrailerKey()));
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER, movie.getPosterPath());

        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

        if (uri != null)
            mFavoriteButton.setText("Remove From Favorite");
    }

    private void onClickRemoveFavoriteMovie (View view) {
        int deletedMovie = 0;
        String deleteMovieId = String.valueOf(movie.getMovieId());
        Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(deleteMovieId).build();

        getContentResolver().delete(uri,null, null);
        mFavoriteButton.setText("Add to favorite");
    }

    @NonNull
    @Override
    public Loader<String[]> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {

            String[] trailerKeys;

            @Override
            protected void onStartLoading() {
                if (NetworkUtils.hasNetworkConnection(getContext())) {
                    super.onStartLoading();
                    mDetailProgressBar.setVisibility(View.VISIBLE);
                    if (trailerKeys != null)
                        deliverResult(trailerKeys);
                    else
                        forceLoad();
                }
            }

            @Override
            public String[] loadInBackground() {
                //Check if movie is already in the favorite list. If yes, change the "Add to favorite" button to "remove from favorite"
                String selectedMovieId = String.valueOf(movie.getMovieId());
                Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(selectedMovieId).build();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor.getCount() > 0)
                    mFavoriteButton.setText("Remove From Favorite");

                if (NetworkUtils.hasNetworkConnection(getContext())) {
                    try {
                        URL movieTrailerKeysUrl = NetworkUtils.buildUrl("trailer", String.valueOf(movie.getMovieId()));
                        String response = NetworkUtils.getResponseFromHttpUrl(movieTrailerKeysUrl);
                        trailerKeys = MovieJsonUtils.getMovieTrailerKeyFromJson(MovieDetailActivity.this, response);

                        return trailerKeys;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                } else
                    return null;
            }

            @Override
            public void deliverResult(String[] movieTrailerKeys) {
                trailerKeys = movieTrailerKeys;
                super.deliverResult(trailerKeys);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String[]> loader, String[] data) {
        mDetailProgressBar.setVisibility(View.INVISIBLE);
        if (data != null || data.length != 0) {
            mMovieTrailerAdapter.setMovieTrailers(data);
            movie.setMovieTrailerKey(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String[]> loader) {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void playTrailer (String key) {
        Uri appUri = Uri.parse("vnd.youtube:" + key);
        Uri webUri = Uri.parse("https://www.youtube.com/watch?v=" + key);
        Intent appIntent = new Intent(Intent.ACTION_VIEW, appUri);
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);

        if (appIntent.resolveActivity(getPackageManager()) != null)
            startActivity(appIntent);
        else if (webIntent.resolveActivity(getPackageManager()) != null)
            startActivity(webIntent);
    }

    @Override
    public void onClick(String selectedTrailer) {
        playTrailer(selectedTrailer);
    }
}
