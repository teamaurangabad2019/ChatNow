package com.teammandroid.chatnow.services;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import androidx.core.app.NotificationCompat;


import com.androidnetworking.error.ANError;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.activities.HomeActivity;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;
import com.teammandroid.chatnow.models.Response;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.network.UserServices;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import java.util.Timer;
import java.util.TimerTask;


public class MyForgroundLocationService extends Service implements LocationListener {


    private static final String TAG = MyForgroundLocationService.class.getSimpleName() ;
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

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private UserModel user;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: in service called" );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SessionManager sessionManager = new SessionManager(getBaseContext());
        user = sessionManager.getUserDetails();

        String input = intent.getStringExtra("inputExtra");

        registerScreenStatusReceiver();  //

        createNotificationChannel();


        Intent notificationIntent = new Intent(this, HomeActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                //.setContentTitle("Foreground Service")
                .setContentTitle("Location Service")
                .setContentText(input)
                //.setSmallIcon(R.drawable.ic_stat_name)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY;
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
                            //  Log.e(TAG, "run: "+"1111" );
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

        // timer.schedule(doAsynchronousTask, 0, 5000);  // 5 seconds

        //timer.schedule(doAsynchronousTask, 0, 120000); // 2 minutes

        timer.schedule(doAsynchronousTask, 0, 300000); // 5 minutes


        Log.e( "test: ", "test");
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

                        Log.e("latitudeGgl",location.getLatitude()+"");
                        Log.e("longitudeGgl",location.getLongitude()+"");

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        Toast.makeText(this, "Cureent location :" +latitude+"\n"+longitude , Toast.LENGTH_LONG).show();
                        fn_update(latitude,longitude,user.getUserid());
                    }
                }

            }

            if (isGPSEnable){
                location = null;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    private void fn_update(final double latitute, double longitute, int Userid){

        Log.e(TAG, "fn_update: called" );
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        UserModel userModel = sessionManager.getUserDetails();

        userModel.setLatitute(latitude);
        userModel.setLongitude(longitude);

        sessionManager.createLoginSession(user);
       // sessionManager.setLocationDetails(latitute,longitute);

        try {
            if (Utility.isNetworkAvailable(getApplicationContext())) {

                UserServices.getInstance(getApplicationContext()).UpdateUserLocation(latitute, longitute, Userid,  new ApiStatusCallBack<Response>() {
                    @Override
                    public void onSuccess(Response response) {
                        Log.e("location onSuccess", "onSuccess: "+response.getMessage() );
                        SessionManager sessionManager = new SessionManager(getApplicationContext());
                        UserModel userModel = sessionManager.getUserDetails();

                        userModel.setLatitute(latitute);
                        userModel.setLongitude(longitude);

                        sessionManager.createLoginSession(user);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("location onError", "onError: "+anError.getMessage() );
                    }

                    @Override
                    public void onUnknownError(Exception e) {
                        Log.e("location onUnknownError", "onUnknownError: "+e.getMessage() );
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




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

