package com.movies.sulayman.moviefinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.movies.sulayman.moviefinder.Data.MovieContract;
import com.movies.sulayman.moviefinder.Model.Movie;
import com.movies.sulayman.moviefinder.Utilities.MovieJsonUtils;
import com.movies.sulayman.moviefinder.Utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieListAdapter.MovieListAdapterOnClickListener,
        LoaderManager.LoaderCallbacks<Movie[]>, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = "MainActivity";
    private static final int SPAN_COUNT = 2;
    private static final int MOVIE_LOADER_ID = 24;
    private static String mMovieSortUrlQuery = null;

    private MovieListAdapter mMovieAdapter;

    @BindView(R.id.rv_movies) RecyclerView mMovieListView;
    @BindView(R.id.tv_error_message_display) TextView mErrorMessageView;
    @BindView(R.id.pb_loading_indicator) ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            mMovieListView.setLayoutManager (new GridLayoutManager(this, SPAN_COUNT));
        else
            mMovieListView.setLayoutManager (new GridLayoutManager(this, 4));

        mMovieListView.setHasFixedSize(true);

        mMovieAdapter = new MovieListAdapter(this);
        mMovieListView.setAdapter(mMovieAdapter);

        setupMoviePreferences();
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
    }

    // Create Setting option on the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_setting) {
            Intent settingActivityIntent = new Intent(this, SettingActivity.class);
            startActivity(settingActivityIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupMoviePreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mMovieSortUrlQuery = loadMoviesSortFromPreference(sharedPreferences);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private String loadMoviesSortFromPreference (SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(getString(R.string.pref_sort_movie_key),
                getString(R.string.pref_most_popular_value));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sort_movie_key)))
            loadMoviesSortFromPreference(sharedPreferences);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public Loader<Movie[]> onCreateLoader(int id, final Bundle bundle) {
        return new AsyncTaskLoader<Movie[]>(this) {

            public Movie[] movies;

            @Override
            protected void onStartLoading() {
                if (NetworkUtils.hasNetworkConnection(getContext())) {
                    super.onStartLoading();
                    mProgressBar.setVisibility(View.VISIBLE);
                    if (movies != null)
                        deliverResult(movies);
                    else
                        forceLoad();
                }
                else
                    showErrorMessage();
            }

            @Override
            public Movie[] loadInBackground() {
                List<Movie> tempMovies = new ArrayList<Movie>();
                setupMoviePreferences();
                if (NetworkUtils.hasNetworkConnection(getContext())) {
                    if (!mMovieSortUrlQuery.equals(getString(R.string.pref_favorite_value))) {
                        try {
                            URL movieListUrl = NetworkUtils.buildUrl(mMovieSortUrlQuery, null);
                            String response = NetworkUtils.getResponseFromHttpUrl(movieListUrl);
                            movies = MovieJsonUtils.getMovieContentsFromJson(MainActivity.this, response);

                            return movies;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                    else {
                        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                                null, null, null, null);
                        if (cursor.getCount() != 0) {
                            if (cursor.moveToFirst()) {
                                do {
                                    Movie movie = new Movie();
                                    movie.setTitle(cursor.getString(1));
                                    movie.setMovieId(cursor.getLong(2));
                                    movie.setSynopsis(cursor.getString(3));
                                    movie.setUserRating(cursor.getDouble(4));
                                    movie.setReleaseDate(cursor.getString(5));
                                    movie.setPosterPath(cursor.getString(6));
                                    String trailers = cursor.getString(7);
                                    if (trailers != null) {
                                        try {
                                            movie.setMovieTrailerKey(MovieJsonUtils.
                                                    getMovieTrailerKeyFromJson(MainActivity.this, trailers));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            return null;
                                        }
                                    }
                                    tempMovies.add(movie);
                                } while (cursor.moveToNext());
                            }
                            movies = tempMovies.toArray(new Movie[tempMovies.size()]);
                            return movies;
                        }
                        else {
                            mErrorMessageView.setText(getString(R.string.no_favorite_movie_message));
                            return null;
                        }
                    }
                }
                else
                    return null;
            }

            @Override
            public void deliverResult(Movie[] data) {
                movies = data;
                super.deliverResult(data);
            }
        };
    }

    private void showMovieDataView() {
        mErrorMessageView.setVisibility(View.INVISIBLE);
        mMovieListView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mMovieListView.setVisibility(View.INVISIBLE);
        mErrorMessageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadFinished(Loader<Movie[]> loader, Movie[] data) {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (data != null) {
            showMovieDataView();
            mMovieAdapter.setMovieData(data);
        }
        else
            showErrorMessage();
    }

    @Override
    public void onLoaderReset(Loader<Movie[]> loader) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onClick(Movie selectedMovie) {
        Class destinationClass = MovieDetailActivity.class;
        Intent intentToStartMovieDetailActivity = new Intent(this, destinationClass);
        intentToStartMovieDetailActivity.putExtra(Intent.EXTRA_TEXT, selectedMovie);
        startActivity(intentToStartMovieDetailActivity);
    }
}
