package com.rootminusone8004.bazarnote;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class BazarnoteApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // The app is dark-only; set it once instead of in every activity.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
}
