package com.teammandroid.chatnow.activities;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearByPlaces extends AsyncTask<Object,String,String> {

    private String googleplacedata,url;
    private GoogleMap mMap;

    Context mContext;

    @Override
    protected String doInBackground(Object... objects) {
        mMap=(GoogleMap)objects[0];
        url=(String)objects[1];

        DownloadUrl downloadUrl=new DownloadUrl();
        try {
            googleplacedata=downloadUrl.ReadTheURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleplacedata;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String,String >> nearByPlacesList=null;

        DataParser dataParser=new DataParser();
        nearByPlacesList=dataParser.parse(s);

        //Toast.makeText(mContext, "nearbyplacelist"+nearByPlacesList.toString(), Toast.LENGTH_SHORT).show();

        DisplayNearByPlaces(nearByPlacesList);

    }

    private void DisplayNearByPlaces(List<HashMap<String,String >> nearByPlaceList){

        for (int i=0;i<nearByPlaceList.size();i++){
            MarkerOptions markerOptions=new MarkerOptions();

            HashMap<String,String> googleNearByPlace=nearByPlaceList.get(i);
            String nameOfPlace=googleNearByPlace.get("place_name");
            String vicinity=googleNearByPlace.get("vicinity");
            double lat=Double.parseDouble(googleNearByPlace.get("lat"));
            double lng=Double.parseDouble(googleNearByPlace.get("lng"));
            String reference=googleNearByPlace.get("reference");

            LatLng latLng=new LatLng(lat,lng);
            markerOptions.position(latLng);
            markerOptions.title(nameOfPlace+" : "+vicinity);

            //Toast.makeText(mContext, "Your location : "+vicinity, Toast.LENGTH_SHORT).show();

            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));


        }
    }
}
