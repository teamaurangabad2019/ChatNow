package com.teammandroid.chatnow.utils;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MyFirebaseApp extends Application {

    public MyFirebaseApp() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }

}

