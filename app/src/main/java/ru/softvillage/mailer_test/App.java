package ru.softvillage.mailer_test;

import android.app.Application;

import lombok.Getter;

public class App extends Application {
    public static final String TAG = "ru.softvillage.mailer_test";

    @Getter
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
