package com.movies.sulayman.moviefinder.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    final static String MOVIE_LIST_BASE_URL = "https://api.themoviedb.org/3/movie/";
    final static String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";
    final static String API_KEY_PARAM = "api_key";
    private final static String apiKey = "";
    final static String sortByMostPopular = "popular";
    final static String sortByTopRated = "top_rated";
    final static String movieTrailer = "videos";
    final static String movieReviews = "reviews";

    /**
     * Builds the URL used to query MovieDB.
     *
     * @param queryBy Movie result that will be sorted by.
     * @param movieId Movie Id that will be used to query from MovieDB
     * @return The URL to use to query the MovieDB.
     */
    public static URL buildUrl(String queryBy, String movieId) {
        Uri builtUri = null;

        switch (queryBy){
            case "top_rated": {
                builtUri = Uri.parse(MOVIE_LIST_BASE_URL).buildUpon()
                        .appendPath(sortByTopRated)
                        .appendQueryParameter(API_KEY_PARAM, apiKey)
                        .build();
                break;
            }
            case "most_popular": {
                builtUri = Uri.parse(MOVIE_LIST_BASE_URL).buildUpon()
                        .appendPath(sortByMostPopular)
                        .appendQueryParameter(API_KEY_PARAM, apiKey)
                        .build();
                break;
            }

            case "trailer": {
                builtUri = Uri.parse(MOVIE_LIST_BASE_URL).buildUpon()
                        .appendPath(movieId)
                        .appendPath(movieTrailer)
                        .appendQueryParameter(API_KEY_PARAM, apiKey)
                        .build();
                break;
            }

            case "reviews": {
                builtUri = Uri.parse(MOVIE_LIST_BASE_URL).buildUpon()
                        .appendPath(movieId)
                        .appendPath(movieReviews)
                        .appendQueryParameter(API_KEY_PARAM, apiKey)
                        .build();
                break;
            }

            default:
                break;
        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String getMoviePosterBaseUrl () {
        return MOVIE_POSTER_BASE_URL;
    }

    public static boolean hasNetworkConnection(Context context) {
        boolean hasNetwork = false;
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        hasNetwork = networkInfo != null && networkInfo.isConnected();
        return hasNetwork;
    }
}
