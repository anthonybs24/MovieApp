package com.movies.sulayman.moviefinder.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieContentProvider extends ContentProvider {
    private MovieDbHelper dbHelper;
    private static final int MOVIES = 100;
    private static final int SINGLE_MOVIE = 101;

    private static UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher () {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.MOVIES_PATH, MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.MOVIES_PATH + "/#", SINGLE_MOVIE);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new MovieDbHelper(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case MOVIES:
                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case SINGLE_MOVIE:
                String singleMovieId = uri.getPathSegments().get(1);
                String mSelection = "movieId=?";
                String[] mSelectionArgs = new String[]{singleMovieId};

                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME, projection, mSelection,
                        mSelectionArgs, null, null, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                // directory
                return "vnd.android.cursor.dir" + "/" + MovieContract.AUTHORITY + "/" + MovieContract.MOVIES_PATH;
            case SINGLE_MOVIE:
                // single item type
                return "vnd.android.cursor.item" + "/" + MovieContract.AUTHORITY + "/" + MovieContract.MOVIES_PATH;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri insertedUri;

        switch (match) {
            case MOVIES:
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    insertedUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                } else
                    throw new SQLiteException("Failed to insert row into " + uri);
                break;

            default:
                throw new UnsupportedOperationException("Unknow Uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return insertedUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        int numberOfDeleted = 0;

        switch (match) {
            case SINGLE_MOVIE:
                String singleMovieId = uri.getPathSegments().get(1);
                numberOfDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        "movieId=?", new String[]{singleMovieId});
                break;

            default:
                throw new UnsupportedOperationException("Unknow Uri: " + uri);
        }

        if (numberOfDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return numberOfDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
