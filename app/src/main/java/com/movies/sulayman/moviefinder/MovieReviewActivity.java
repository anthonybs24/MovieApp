package com.movies.sulayman.moviefinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.movies.sulayman.moviefinder.Model.Movie;
import com.movies.sulayman.moviefinder.Utilities.MovieJsonUtils;
import com.movies.sulayman.moviefinder.Utilities.NetworkUtils;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieReviewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String[]> {
    private Movie reviewedMovie;
    private MovieReviewAdapter mMovieReviewAdapter;
    public static final String TAG = "MovieReviewActivity";
    private static final int MOVIE_REVIEW_ID = 20;

    @BindView(R.id.tv_review_error_message) TextView mReviewErrorMessageView;
    @BindView(R.id.pb_reviews_loading_indicator) ProgressBar mReviewProgressBar;
    @BindView(R.id.rv_reviews) RecyclerView mMovieReviewsRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ButterKnife.bind(this);

        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this);
        mMovieReviewsRecyclerView.setLayoutManager(reviewsLayoutManager);
        mMovieReviewsRecyclerView.setHasFixedSize(true);

        mMovieReviewAdapter = new MovieReviewAdapter();
        mMovieReviewsRecyclerView.setAdapter(mMovieReviewAdapter);

        Intent parentIntent = getIntent();
        if (parentIntent != null) {
            if (parentIntent.hasExtra(Intent.EXTRA_TEXT)) {
                reviewedMovie = parentIntent.getParcelableExtra(Intent.EXTRA_TEXT);
            }
        }

        setTitle("Reviews");
        getSupportLoaderManager().initLoader(MOVIE_REVIEW_ID, null, MovieReviewActivity.this);
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {

            String[] reviews;

            @Override
            protected void onStartLoading() {
                if (NetworkUtils.hasNetworkConnection(getContext())) {
                    super.onStartLoading();
                    mReviewProgressBar.setVisibility(View.VISIBLE);
                    if (reviews != null)
                        deliverResult(reviews);
                    else
                        forceLoad();
                } else
                    showErrorMessage();
            }

            @Override
            public String[] loadInBackground() {
                if (NetworkUtils.hasNetworkConnection(getContext())) {
                    try {
                        URL movieReviewsUrl = NetworkUtils.buildUrl("reviews", String.valueOf(reviewedMovie.getMovieId()));
                        String response = NetworkUtils.getResponseFromHttpUrl(movieReviewsUrl);
                        reviews = MovieJsonUtils.getMovieReviewFromJson(MovieReviewActivity.this, response);

                        return reviews;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                } else
                    return null;
            }

            @Override
            public void deliverResult(String[] movieReviews) {
                reviews = movieReviews;
                super.deliverResult(reviews);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        mReviewProgressBar.setVisibility(View.INVISIBLE);
        if (data != null || data.length != 0) {
            showMovieReviewView();
            mMovieReviewAdapter.setReviewsData(data);
        }
        else {
            mMovieReviewsRecyclerView.setVisibility(View.INVISIBLE);
            mReviewErrorMessageView.setText("No reviews found");
            mReviewErrorMessageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    private void showMovieReviewView() {
        mReviewErrorMessageView.setVisibility(View.INVISIBLE);
        mMovieReviewsRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mMovieReviewsRecyclerView.setVisibility(View.INVISIBLE);
        mReviewErrorMessageView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra(Intent.EXTRA_TEXT, reviewedMovie);
            setResult(Activity.RESULT_OK, intent);
            NavUtils.navigateUpFromSameTask(this);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
