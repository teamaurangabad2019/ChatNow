package com.teammandroid.chatnow.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.androidnetworking.error.ANError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.dialoge.SearchDialogActivity;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;
import com.teammandroid.chatnow.models.MarkerData;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.network.UserServices;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private LocationManager locationManager;
    boolean isGPSEnable = false;
    ImageView img_search;

    boolean isNetworkEnable = false;
    double latitude, longitude;
    Location location;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 10000;
    Intent intent;
    ArrayList<MarkerData> markersArray = new ArrayList<MarkerData>();

    private Location mCurrentLocation;
    LinearLayout lyt_progress_reg;
    private ArrayList<UserModel> userNearMeList = new ArrayList<>();

    int distance_range = 30;
    private Bitmap image;
    private Bitmap bmImg;
    private UserModel user;

    private Marker currentUserLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.g_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

        bindView();
        btnListener();

        SessionManager sessionManager = new SessionManager(this);
        user = sessionManager.getUserDetails();

      // fn_getlocation();
        GetUserNearMe();
    }

    private void btnListener() {
        img_search.setOnClickListener(this);
    }

    private void bindView() {
        img_search = findViewById(R.id.img_search);
        lyt_progress_reg = findViewById(R.id.lyt_progress_reg);
    }

    private void GetUserNearMe() {
        Log.e(TAG, "GetUserNearMe: called" );
        try {
            if (Utility.isNetworkAvailable(getApplicationContext())) {
                lyt_progress_reg.setVisibility(View.VISIBLE);
                lyt_progress_reg.setAlpha(1.0f);

                //  Log.e(TAG, "GetUser: " + UserId);
               // UserServices.getInstance(getApplicationContext()).GetUserNearMe(user.getUserid(),latitude,longitude,distance_range, user.getRoleid(),new ApiStatusCallBack<ArrayList<UserModel>>() {
                UserServices.getInstance(getApplicationContext()).GetAllUser(new ApiStatusCallBack<ArrayList<UserModel>>() {
                    @Override
                    public void onSuccess(ArrayList<UserModel> response) {

                        lyt_progress_reg.setVisibility(View.GONE);
                        userNearMeList = response;
                        Log.e(TAG, "onSuccess: list "+userNearMeList.toString() );
                        bindListToMap();
                        Toast.makeText(MapsActivity.this,userNearMeList.size()+" User Found",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        lyt_progress_reg.setVisibility(View.GONE);
                        Utility.showErrorMessage(MapsActivity.this, "Network "+anError.getMessage());
                    }

                    @Override
                    public void onUnknownError(Exception e) {
                        lyt_progress_reg.setVisibility(View.GONE);
                        Utility.showErrorMessage(MapsActivity.this, e.getMessage());
                    }
                });
            } else {
                Utility.showErrorMessage(MapsActivity.this, "Could not connect to the internet");
            }
        } catch (Exception ex) {
            lyt_progress_reg.setVisibility(View.GONE);
            Utility.showErrorMessage(MapsActivity.this, ex.getMessage());
        }
    }

    private void bindListToMap() {
        Log.e(TAG, "bindListToMap: called" );
        for (UserModel item : userNearMeList
        ) {
            createMarker(item.getLatitute(), item.getLongitude(), item.getFullname(), item.getAddress(), item.getProfilepic());
        }
    }

    protected void createMarker(double latitude, double longitude, String title, String snippet, String path) {

        Log.e(TAG, "createMarker: called");
        try {
        // Bitmap bmImg = null;
            if (path.length()>0) {

                bmImg = Ion.with(this)
                        .load(Constants.URL_USER_PROFILE_PIC + path).asBitmap().get();

                Log.e(TAG, "createMarker:Path " + Constants.URL_USER_PROFILE_PIC + path);

                float ratio = Math.min(
                        (float) 100 / bmImg.getWidth(),
                        (float) 100 / bmImg.getHeight());
                int width = Math.round((float) ratio * bmImg.getWidth());
                int height = Math.round((float) ratio * bmImg.getHeight());

                Bitmap newBitmap = Bitmap.createScaledBitmap(bmImg, width,
                        height, true);

                Canvas canvas = new Canvas(newBitmap);

                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setColor(0xff424242);

                Rect rect = new Rect(0, 0, width, height);
                RectF rectF = new RectF(rect);

                canvas.drawARGB(0, 0, 0, 0);
                canvas.drawCircle(rectF.left + (rectF.width() / 2), rectF.top + (rectF.height() / 2), 19 / 2, paint);

                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(newBitmap, rect, rect, paint);

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .anchor(0.5f, 0.5f)
                        .title(title)
                        .snippet(snippet))
                        .setIcon(BitmapDescriptorFactory.fromBitmap(newBitmap));

            }else {
                int height = 100;
                int width = 100;
                BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.male_avatar);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .anchor(0.5f, 0.5f)
                        .title(title)
                        .snippet(snippet))
                        //.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_account_circle));
                        .setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            }



        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.e(TAG, "createMarker: error" + e.getMessage());

        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "createMarker: error" + e.getMessage());

        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
      //  fillData();

       // fn_getlocation();
        GetUserNearMe();

           /* LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());

            Log.e(TAG, "onMapReady: lat+long" + latitude + " " + longitude);
            mMap.addMarker(new MarkerOptions().position(sydney).title("My Location"));
            //  mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            float zoomLevel = 16.0f; //This goes up to 21
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel));
            */

            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                mMap.setMyLocationEnabled(true);
            }

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    //  mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 250, null);
                    for (UserModel item : userNearMeList) {
                        if (item.getFullname().equals(marker.getTitle()) && item.getLatitute() == marker.getPosition().latitude && item.getLongitude() == marker.getPosition().longitude) {
                            Bundle bundle = new Bundle();
                            /*bundle.putParcelable("chattingPartner", item);
                            Utility.launchActivity(MapsActivity.this, ChattingActivity.class, false, bundle);*/
                            bundle.putParcelable("chattingPartner", item);
                            Utility.launchActivity(MapsActivity.this, PeopleProfileActivity.class, false, bundle);
                        }
                    }
                    Toast.makeText(MapsActivity.this, marker.getTitle() + " Ckicked", Toast.LENGTH_SHORT).show();
                }
            });

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        Log.e(TAG, "onClick: called" );
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
        alert.setCancelable(true);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude=location.getLatitude();
        longitude=location.getLongitude();

        location=location;

        if (currentUserLocationMarker!=null){
            currentUserLocationMarker.remove();
        }
        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("user current location");

        //Toast.makeText(this, "your location : "+latLng, Toast.LENGTH_SHORT).show();

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        currentUserLocationMarker=mMap.addMarker(markerOptions);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_search:
                SearchDialogActivity dialog = new SearchDialogActivity(MapsActivity.this);
                dialog.show();

                dialog.setDialogResult(new SearchDialogActivity.OnMyDialogResult() {
                    @Override
                    public void finish(Integer selectedDistance, int interestType) {
                        distance_range = selectedDistance;
                        Log.e(TAG, "finish: distance" );
                        GetUserNearMe();
                    }
                });

                break;
        }
    }

}
