package com.movies.sulayman.moviefinder.Data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String AUTHORITY = "com.movies.sulayman.moviefinder";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String MOVIES_PATH = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(MOVIES_PATH).build();

        //Movie table and column names
        public static final String TABLE_NAME = "favoriteMovies";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_VIDEO = "video";

    }
}
