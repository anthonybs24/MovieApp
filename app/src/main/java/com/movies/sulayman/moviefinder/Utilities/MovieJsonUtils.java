package com.movies.sulayman.moviefinder.Utilities;

import android.content.Context;

import com.movies.sulayman.moviefinder.Model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Utility functions to handle MovieDB Json Data
public class MovieJsonUtils {
    private static final String MOVIE_RESULTS = "results";
    private static final String MOVIE_TITLE = "title";
    private static final String MOVIE_POSTER = "poster_path";
    private static final String USER_RATING = "vote_average";
    private static final String MOVIE_SYNOPSIS = "overview";
    private static final String RELEASE_DATE = "release_date";
    private static final String STATUS_CODE = "status_code";
    private static final String STATUS_MESSAGE = "status_message";
    private static final String MOVIE_ID = "id";
    private static final String MOVIE_TRAILER_KEY = "key";
    private static final String MOVIE_REVIEW_AUTHOR = "author";
    private static final String MOVIE_REVIEW_CONTENT = "content";

    private static final String TAG = "MovieJsonUtils";

    public static String jsonErrorMessage = null;
    /* @param movieJsonStr JSON response from server
     * @return Array of Strings describing movie data
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static Movie[] getMovieContentsFromJson(Context context, String movieJsonStr)
            throws JSONException {

        JSONObject movieJson = new JSONObject(movieJsonStr);

        if (movieJson.has(STATUS_CODE)) {
            jsonErrorMessage = movieJson.getString(STATUS_MESSAGE);
            return null;
        }

        JSONArray jsonMoviesArray = movieJson.getJSONArray(MOVIE_RESULTS);
        Movie[] movies = new Movie[jsonMoviesArray.length()];

        for (int i = 0; i < jsonMoviesArray.length(); i++) {

            JSONObject jsonMovie = jsonMoviesArray.getJSONObject(i);
            String title = jsonMovie.getString(MOVIE_TITLE);
            String poster = jsonMovie.getString(MOVIE_POSTER);
            String synopsis = jsonMovie.getString(MOVIE_SYNOPSIS);
            String releaseDateString = jsonMovie.getString(RELEASE_DATE);
            String releaseDate = new Movie().convertDateToNewFormat(releaseDateString);
            long movieId = jsonMovie.getLong(MOVIE_ID);

            double rating = jsonMovie.getDouble(USER_RATING);

            Movie movie = new Movie(title, poster, synopsis, releaseDate, rating, movieId);
            movies[i] = movie;
        }

        return movies;
    }

    public static String[] getMovieTrailerKeyFromJson (Context context, String movieVideosJsonStr)
        throws JSONException {

        JSONObject movieVideoJson = new JSONObject(movieVideosJsonStr);

        if (movieVideoJson.has(STATUS_CODE)) {
            jsonErrorMessage = movieVideoJson.getString(STATUS_MESSAGE);
            return null;
        }

        JSONArray jsonMovieVideoArray = movieVideoJson.getJSONArray(MOVIE_RESULTS);
        String[] movieVideoKeys = new String[jsonMovieVideoArray.length()];

        for (int j = 0; j < jsonMovieVideoArray.length(); j++) {
            JSONObject movieVideo = jsonMovieVideoArray.getJSONObject(j);
            String movieKey = movieVideo.getString(MOVIE_TRAILER_KEY);
            movieVideoKeys[j] = movieKey;
        }

        return movieVideoKeys;
    }

    public static String[] getMovieReviewFromJson (Context context, String movieReviewJsonStr)
        throws JSONException {

        JSONObject movieReviewJson = new JSONObject(movieReviewJsonStr);

        if (movieReviewJson.has(STATUS_CODE)) {
            jsonErrorMessage = movieReviewJson.getString(STATUS_MESSAGE);
            return null;
        }

        JSONArray jsonMovieReviewArray = movieReviewJson.getJSONArray(MOVIE_RESULTS);
        String[] movieReviews = new String[jsonMovieReviewArray.length()];

        for (int r = 0; r < jsonMovieReviewArray.length(); r++) {
            JSONObject movieReview = jsonMovieReviewArray.getJSONObject(r);
            String movieReviewContent = movieReview.getString(MOVIE_REVIEW_CONTENT);
            String movieReviewAuthor = movieReview.getString(MOVIE_REVIEW_AUTHOR);

            movieReviews[r] = movieReviewContent + " - by " + movieReviewAuthor;
        }

        return movieReviews;
    }

    public static String buildTrailerJsonString(String[] data) {
        String result = "{results: [";

        for (int i = 0; i < data.length; i++) {
            result += "{ " + MOVIE_TRAILER_KEY + " : " + data[i] + " }";
            if (i < data.length - 1)
                result += ", ";
        }
        result += "]}";

        return result;
    }
}
