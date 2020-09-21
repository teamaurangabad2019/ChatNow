package com.teammandroid.chatnow.activities;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (validateUser()) {
                    Utility.launchActivity(SplashActivity.this, HomeActivity.class, true);
                } else {
                    Utility.launchActivity(SplashActivity.this, OTPLoginActivity.class, true);
                }
            }
        },3000);

    }

    private boolean validateUser() {

        boolean result = false;

        try {
            SessionManager sessionManager=new SessionManager(SplashActivity.this);
            //UserResponse response = PrefHandler.getUserFromSharedPref(SplashActivity.this);

            UserModel response = sessionManager.getUserDetails();
            Log.e(TAG, "validateUser: "+response.toString());
            if (response.getUserid() > 0) {
                result = true;
            }
        } catch (Exception ex) {
            Log.e(TAG, "validateUser: ", ex);
        }
        return result;
    }
}
