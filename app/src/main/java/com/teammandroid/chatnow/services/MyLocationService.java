package com.teammandroid.chatnow.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.androidnetworking.error.ANError;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;
import com.teammandroid.chatnow.models.Response;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.network.UserServices;
import com.teammandroid.chatnow.utils.PrefManager;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;


import java.util.Timer;
import java.util.TimerTask;

public  class MyLocationService extends  Service implements  LocationListener {


    private static final String TAG = MyLocationService.class.getSimpleName() ;
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 10000;
    public static String str_receiver = "servicetutorial.service.receiver";
    Intent intent;
    //PrefManager prefManager;
    private UserModel user;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //  prefManager=new PrefManager(getApplicationContext());
        SessionManager  sessionManager  = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();
        registerScreenStatusReceiver();
    }

    @Override
    public void onDestroy() {
        unregisterScreenStatusReceiver();
    }

    private void registerScreenStatusReceiver() {

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {
                            //              Log.e(TAG, "run: "+"1111" );
                            Toast.makeText(getApplicationContext(), "1111", Toast.LENGTH_SHORT).show();
                            fn_getlocation();
                        }
                        catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5000);

        Log.e( "test: ", "test");
    }

    private void unregisterScreenStatusReceiver() {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void fn_getlocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {

        } else {

            if (isNetworkEnable) {
                location = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location!=null){

                        //  Log.e("latitudeGgl",location.getLatitude()+"");
                        //  Log.e("longitudeGgl",location.getLongitude()+"");

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fn_update(latitude,longitude,user.getUserid());
                    }
                }

            }

            if (isGPSEnable){
                location = null;

                try{

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);
                    if (locationManager!=null){
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location!=null){
                            // Log.e("latitude_service",location.getLatitude()+"");
                            // Log.e("longitude_service",location.getLongitude()+"");
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            fn_update(latitude,longitude,user.getUserid());
                        }
                    }

                }catch (Exception e){
                    Log.e(TAG, "fn_getlocation: "+e.getMessage() );
                }
            }

        }
    }

    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    fn_getlocation();
                }
            });

        }
    }

    private void fn_update(double latitute, double longitute, int Userid){
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        //sessionManager.setLocationDetails(latitute,longitute);

        try {
            if (Utility.isNetworkAvailable(getApplicationContext())) {

                UserServices.getInstance(getApplicationContext()).UpdateUserLocation(latitute, longitute, Userid,  new ApiStatusCallBack<Response>() {
                    @Override
                    public void onSuccess(Response response) {
                        // Log.e("onSuccess", "onSuccess: "+response.getMessage() );
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("onError", "onError: "+anError.getMessage() );
                    }

                    @Override
                    public void onUnknownError(Exception e) {
                        Log.e("onUnknownError", "onUnknownError: "+e.getMessage() );
                        //Utility.showErrorMessage(OTPLoginActivity.this, e.getMessage());
                    }
                });
            } else {
                //Utility.showErrorMessage(OTPLoginActivity.this, "Could not connect to the internet");
            }
        } catch (Exception ex) {
            Log.e("fn_update","fn_update: "+ex.getMessage() );
            // Utility.showErrorMessage(OTPLoginActivity.this, ex.getMessage());
        }

//        intent.putExtra("latutide",location.getLatitude()+"");
        //     intent.putExtra("longitude",location.getLongitude()+"");
        //  sendBroadcast(intent);
    }

}

