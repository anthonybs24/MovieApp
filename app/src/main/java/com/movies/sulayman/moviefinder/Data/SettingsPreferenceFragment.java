package com.movies.sulayman.moviefinder.Data;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.movies.sulayman.moviefinder.R;

public class SettingsPreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference_visualizer);
    }
}
