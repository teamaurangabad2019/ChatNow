package com.teammandroid.chatnow.activities;

import android.app.Activity;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    Context mContext;

    Activity activity;

    private HashMap<String,String> getSingleNearByPlace(JSONObject googlePlaceJSON){

        HashMap<String,String> googleplaceMap=new HashMap<>();
        String NameOfPlace="";
        String vicinity="";
        String latitude="";
        String longtude="";
        String reference="";

        try {
            if (!googlePlaceJSON.isNull("name")){
                NameOfPlace=googlePlaceJSON.getString("name");
            }
            if (!googlePlaceJSON.isNull("vicinity")){
                NameOfPlace=googlePlaceJSON.getString("vicinity");
            }
            latitude=googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longtude=googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference=googlePlaceJSON.getString("reference");

            googleplaceMap.put("place_name",NameOfPlace);
            googleplaceMap.put("vicinity",vicinity);


            googleplaceMap.put("lat",latitude);
            googleplaceMap.put("lng",longtude);
            googleplaceMap.put("reference",reference);

        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return googleplaceMap;
    }

    private List<HashMap<String,String >> getAllNearByPlaces(JSONArray jsonArray){
        int counter=jsonArray.length();

        List<HashMap<String,String >> nearByPlacesList=new ArrayList<>();

        HashMap<String,String> nearByPlaceMap=null;
        for (int i=0;i<counter;i++){
            try {
                nearByPlaceMap=getSingleNearByPlace((JSONObject)jsonArray.get(i));
                nearByPlacesList.add(nearByPlaceMap);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return nearByPlacesList;
    }

    public List<HashMap<String,String >> parse(String jsonData){
        JSONArray jsonArray=null;
        JSONObject jsonObject;

        try {
            jsonObject=new JSONObject(jsonData);
            jsonArray=jsonObject.getJSONArray("results");
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return getAllNearByPlaces(jsonArray);

    }


}
