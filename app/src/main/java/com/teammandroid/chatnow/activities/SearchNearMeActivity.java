package com.teammandroid.chatnow.activities;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.androidnetworking.error.ANError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.ion.Ion;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.dialoge.SearchDialogActivity;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.network.UserServices;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

//import android.location.LocationListener;

public class SearchNearMeActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        View.OnClickListener{

    private static final String TAG = SearchNearMeActivity.class.getSimpleName();
    private GoogleMap mMap;

    private GoogleApiClient googleApiClient;

    private LocationRequest locationRequest;

    private Location lastLocation;
    private Marker currentUserLocationMarker;

    private static final int Request_User_Location_Code=99;

    private ProgressDialog dialog;
    
    ImageButton btnSearchAddress;

    double latitude,longitude;

    private int ProximityRadius=10000;

    ImageView imgBack;
    //private UserModel[] userNearMeList;

    ImageView img_search;
    private Integer distance_range = 30;
    private ArrayList<UserModel> userNearMeList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_near_me);

        bindView();

        btnListener();

        btnSearchAddress=findViewById(R.id.search_address);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            checkUserLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    private void bindView() {
        imgBack=(ImageView) findViewById(R.id.img_back);
        img_search=(ImageView) findViewById(R.id.img_search);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading page, please wait.");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void btnListener() {
        imgBack.setOnClickListener(this);
        img_search.setOnClickListener(this);
    }

    private void getNearByFireStations() {

        final String hospital="hospital",school="school",restaurant="restaurant",fireStation="fire_station";

        Object transferData[]=new Object[2];

        GetNearByPlaces getNearByPlaces=new GetNearByPlaces();

        mMap.clear();

        String url3=getUrl(latitude,longitude,fireStation);

        transferData[0]=mMap;
        transferData[1]=url3;

        getNearByPlaces.execute(transferData);

        Toast.makeText(this, "Searching for nearby fire station", Toast.LENGTH_SHORT).show();

        Toast.makeText(this, "showing nearby fire station", Toast.LENGTH_SHORT).show();



    }

    public void onClick(View view){

        switch (view.getId()){

            case R.id.img_back:
                onBackPressed();
                break;

            case R.id.img_search:
                SearchDialogActivity dialog = new SearchDialogActivity(SearchNearMeActivity.this);
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

            case R.id.search_address:

                EditText addressField=findViewById(R.id.location_search);
                String address=addressField.getText().toString();
                List<Address> addressList=null;

                MarkerOptions userMarkerOptions=new MarkerOptions();

                if (!TextUtils.isEmpty(address)){

                    Geocoder geocoder=new Geocoder(this);
                    try
                    {
                        addressList=geocoder.getFromLocationName(address,3);

                        if (addressList != null){
                            for (int i=0;i<addressList.size();i++){
                                Address userAddress=addressList.get(i);
                                LatLng latLng=new LatLng(userAddress.getLatitude(),userAddress.getLongitude());
                                userMarkerOptions.position(latLng);
                                userMarkerOptions.title(address);
                                userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                mMap.addMarker(userMarkerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));


                            }
                        }
                        else {
                            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    Toast.makeText(this, "please enter any location name", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private String getUrl(double latitude,double longitude,String nearByPlace){

        StringBuilder googleURL=new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location="+latitude+","+longitude);

        googleURL.append("&radius="+ProximityRadius);
        googleURL.append("&type="+nearByPlace);
        googleURL.append("&sensor=true");
        //googleURL.append("&key="+"AIzaSyARo9yMFMxt88qQzqCOz460ZLubdkGt4Fo");
        //googleURL.append("&key="+"AIzaSyBFaSTVF_W5_UGTP2DICet1UmdrFxzLR6o");
        googleURL.append("&key="+"AIzaSyA8CS32BzdEx81qCxZnZvUR84Xt5Bw6iYM");

        //ArrayList<String> arrayList=new ArrayList<>();

        //Log.d("googlemapsactivity","locations="+location);

        //Log.d("googlemapsactivity","url="+googleURL.toString());

        return googleURL.toString();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    public boolean checkUserLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case Request_User_Location_Code:
                if (grantResults.length>0&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        if (googleApiClient==null){
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else {
                    Toast.makeText(this, "Permisssion denied..", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    protected synchronized void buildGoogleApiClient(){
        googleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest=new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        latitude=location.getLatitude();
        longitude=location.getLongitude();

        lastLocation=location;

        if (currentUserLocationMarker!=null){
            currentUserLocationMarker.remove();
        }
        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("my location");

        //Toast.makeText(this, "your location : "+latLng, Toast.LENGTH_SHORT).show();

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        currentUserLocationMarker=mMap.addMarker(markerOptions);


        //Toast.makeText(this, currentUserLocationMarker.getId(), Toast.LENGTH_SHORT).show();

        //Log.d("user location : ",currentUserLocationMarker.getTitle());


        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(12));

       // getNearByFireStations();

        GetUserNearMe();

        if (googleApiClient!=null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }

    }


    private void GetUserNearMe() {
        Log.e(TAG, "GetUserNearMe: called" );
        try {
            if (Utility.isNetworkAvailable(getApplicationContext())) {
                dialog.show();
                //  Log.e(TAG, "GetUser: " + UserId);
                // UserServices.getInstance(getApplicationContext()).GetUserNearMe(user.getUserid(),latitude,longitude,distance_range, user.getRoleid(),new ApiStatusCallBack<ArrayList<UserModel>>() {
                UserServices.getInstance(getApplicationContext()).GetAllUser(new ApiStatusCallBack<ArrayList<UserModel>>() {
                    @Override
                    public void onSuccess(ArrayList<UserModel> response) {

                        dialog.dismiss();
                        userNearMeList = response;
                        Log.e(TAG, "onSuccess: list "+userNearMeList.toString() );
                        bindListToMap(response);
                        Toast.makeText(SearchNearMeActivity.this,userNearMeList.size()+" User Found",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        Utility.showErrorMessage(SearchNearMeActivity.this, "Network "+anError.getMessage());
                    }

                    @Override
                    public void onUnknownError(Exception e) {
                        dialog.dismiss();
                        Utility.showErrorMessage(SearchNearMeActivity.this, e.getMessage());
                    }
                });
            } else {
                Utility.showErrorMessage(SearchNearMeActivity.this, "Could not connect to the internet");
            }
        } catch (Exception ex) {
            dialog.dismiss();
            Utility.showErrorMessage(SearchNearMeActivity.this, ex.getMessage());
        }
    }

    private void bindListToMap(final ArrayList<UserModel> response) {
        Log.e(TAG, "bindListToMap: called" );
        for (UserModel item : response
        ) {
            createMarker(item.getLatitute(), item.getLongitude(), item.getFullname(), item.getAddress(), item.getProfilepic());
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //  mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 250, null);
                for (UserModel item : userNearMeList) {
                    if (item.getFullname().equals(marker.getTitle()) && item.getLatitute() == marker.getPosition().latitude && item.getLongitude() == marker.getPosition().longitude) {

                        Bundle bundle = new Bundle();
                        bundle.putParcelable("chattingPartner", item);
                        Utility.launchActivity(SearchNearMeActivity.this, PeopleProfileActivity.class, false, bundle);
                       // Utility.launchActivity(SearchNearMeActivity.this, ChattingActivity.class, false, bundle);
                    }
                }
                Toast.makeText(SearchNearMeActivity.this, marker.getTitle() + " Ckicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void createMarker(double latitude, double longitude, String title, String snippet, String path) {

        Log.e(TAG, "createMarker: called");
        try {
             Bitmap bmImg = null;
             if (path.length()>0) {

                bmImg = Ion.with(this)
                        .load(Constants.URL_USER_PROFILE_PIC + path).asBitmap().get();

                Log.e(TAG, "createMarker:Path " + Constants.URL_USER_PROFILE_PIC + path);

                 int height = 100;
                 int width = 100;

                 Bitmap smallMarker = Bitmap.createScaledBitmap(bmImg, width, height, false);


             /*   float ratio = Math.min(
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
*/
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .anchor(0.5f, 0.5f)
                        .title(title)
                        .snippet(snippet))
                        .setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));

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

}


/*

public class SearchFireStationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_fire_station);
    }
}
*/
