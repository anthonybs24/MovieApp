package com.movies.sulayman.moviefinder.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Movie implements Parcelable{
    private String mTitle;
    private String mPosterPath;
    private String mSynopsis;
    private String mReleaseDate;
    private double mUserRating;
    private long mId;
    private String[] mTrailerKeys;
    private String[] mReviews;

    public Movie () {}

    public Movie (String title, String posterPath, String synopsis, String releaseDate,
                  double rating, long id) {
        mTitle = title;
        mPosterPath = posterPath;
        mSynopsis = synopsis;
        mReleaseDate = releaseDate;
        mUserRating = rating;
        mId = id;
    }

    protected Movie(Parcel in) {
        mTitle = in.readString();
        mPosterPath = in.readString();
        mSynopsis = in.readString();
        mReleaseDate = in.readString();
        mUserRating = in.readDouble();
        mId = in.readLong();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getTitle () { return mTitle; }

    public void setTitle (String movieTitle) { mTitle = movieTitle; }

    public String getPosterPath () { return mPosterPath; }

    public void setPosterPath (String poster) { mPosterPath = poster; }

    public String getSynopsis () { return mSynopsis; }

    public void setSynopsis (String synopsis) { mSynopsis = synopsis; }

    public String getReleaseDate () { return mReleaseDate; }

    public void setReleaseDate (String releaseDate) { mReleaseDate = releaseDate; }

    public double getUserRating () { return mUserRating; }

    public void setUserRating (double rating) { mUserRating = rating; }

    public long getMovieId () { return mId; }

    public void setMovieId (long id) { mId = id; }

    public String[] getMovieTrailerKey () { return mTrailerKeys; }

    public void setMovieTrailerKey (String[] trailerKey) { mTrailerKeys = trailerKey; }

    public String[] getMovieReviews () { return mReviews; }

    public void setMovieReviews (String[] movieReviews) { mReviews = movieReviews; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mSynopsis);
        dest.writeString(mReleaseDate);
        dest.writeDouble(mUserRating);
        dest.writeLong(mId);
    }

    public String convertDateToNewFormat (String dateString) {
        String newDate;
        try {
            DateFormat currentFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = currentFormat.parse(dateString);
            DateFormat newFormat = new SimpleDateFormat("MMMM dd, yyyy");
            newDate = newFormat.format(date);
            return newDate;
        } catch (ParseException pe) {
            pe.printStackTrace();
            return null;
        }
    }
}
