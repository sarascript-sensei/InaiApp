package com.example.inai;

import android.app.Application;

import com.cloudinary.android.MediaManager;

public class MainApplication extends Application {
    private static MainApplication _instance;

    public static MainApplication get() {
        return _instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        MediaManager.init(this);

        _instance = this;
    }
}
