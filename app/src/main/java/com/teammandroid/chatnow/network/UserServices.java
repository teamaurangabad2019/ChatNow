package com.teammandroid.chatnow.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;
import com.teammandroid.chatnow.models.Response;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserServices {

    private static final String TAG = UserServices.class.getSimpleName();

    private Context context;

    public UserServices(Context context) {
        this.context = context;
    }

    @SuppressLint("StaticFieldLeak")
    private static UserServices instance;

    public static UserServices getInstance(Context context) {
        if (instance == null) {
            instance = new UserServices(context);
        }
        return instance;
    }

    public void InsertUserDetails(String Mobile, String Token, String deviceName, final ApiStatusCallBack apiStatusCallBack) {

        try {
            //Creating a multi part request
            AndroidNetworking.upload(Constants.URL_LOGIN)
                    .addMultipartParameter("type", "1")
                    .addMultipartParameter("Action", "1")
                    .addMultipartParameter("Userid","0")
                    .addMultipartParameter("Fullname","" )
                    .addMultipartParameter("Roleid","2")
                    .addMultipartParameter("Address", "")
                    .addMultipartParameter("Mobile", Mobile)
                    .addMultipartParameter("Token", Token)
                    .addMultipartParameter("Email", "")
                    .addMultipartParameter("Profilepic", "")
                    .addMultipartParameter("Emergencyno1", "")
                    .addMultipartParameter("Emergencyno2", "")
                    .addMultipartParameter("Emergencyno3", "")
                    .addMultipartParameter("Latitute", "0")
                    .addMultipartParameter("Longitude", "0")
                    .addMultipartParameter("Emergencyemail1", "")
                    .addMultipartParameter("Emergencyemail2", "")
                    .addMultipartParameter("Emergencyemail3", "")
                    .addMultipartParameter("Device", deviceName)
                    .addMultipartParameter("LogedinUserId", "0")
                    //.setNotificationConfig(new UploadNotificationConfig())
                    .setTag("uploadTest")
                    .setPriority(Priority.HIGH)
                    .build()
                    .setUploadProgressListener(new UploadProgressListener() {
                        @Override
                        public void onProgress(long bytesUploaded, long totalBytes) {
                            Log.e(TAG, "uploadImage: totalBytes: " + totalBytes);
                            Log.e(TAG, "uploadImage: bytesUploaded: " + bytesUploaded);
                        }
                    })
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                          //  Toast.makeText(activity,"Updated Successfully",Toast.LENGTH_SHORT).show();
                            TypeToken<Response> token = new TypeToken<Response>() {
                            };
                            Response response1 = new Gson().fromJson(response.toString(), token.getType());

                            Log.e(TAG, "uploadImage:onResponse: " + response);
                            apiStatusCallBack.onSuccess(response1);
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.e(TAG, "onError: ", error);
                            apiStatusCallBack.onError(error);
                        }
                    });
        } catch (Exception exc) {
            Log.e("getMessage", exc.getMessage());
          //  Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
        
    }

    public void GetUserDetails(int AppUserId, final ApiStatusCallBack apiStatusCallBack) {

       // {type":2,"Action":1,"AppUserId":1}
        try {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type", 2);
                jsonObject.put("Action", 2);
                jsonObject.put("Userid", AppUserId);
            } catch (Exception e) {
                Log.e("JSONOBJECTerr", "" + e);
                apiStatusCallBack.onUnknownError(e);
            }

            AndroidNetworking.post(Constants.URL_LOGIN)
                    .addJSONObjectBody(jsonObject)
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {

                            try {
                                TypeToken<ArrayList<UserModel>> token = new TypeToken<ArrayList<UserModel>>() {
                                };
                                ArrayList<UserModel> notesPackages = new Gson().fromJson(response.toString(), token.getType());
                                Log.e("UserModel", "" + notesPackages);
                                apiStatusCallBack.onSuccess(notesPackages);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("error", e.getMessage());
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("GetUser:anError", "" + anError);
                            Log.e("GetUser:anError", "" + anError.getErrorBody());
                            apiStatusCallBack.onError(anError);
                        }
                    });

        } catch (Exception ex) {
            Log.e("onUnknownError", "" + ex);
            apiStatusCallBack.onUnknownError(ex);
        }
    }

    public void GetAllUser( final ApiStatusCallBack apiStatusCallBack) {
        // {type":2,"Action":1,"AppUserId":1}
        try {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type", 2);
                jsonObject.put("Action", 1);
            } catch (Exception e) {
                Log.e("JSONOBJECTerr", "" + e);
                apiStatusCallBack.onUnknownError(e);
            }

            AndroidNetworking.post(Constants.URL_LOGIN)
                    .addJSONObjectBody(jsonObject)
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {

                            try {

                                TypeToken<ArrayList<UserModel>> token = new TypeToken<ArrayList<UserModel>>() {
                                };
                                ArrayList<UserModel> notesPackages = new Gson().fromJson(response.toString(), token.getType());
                                Log.e("UserModel", "" + notesPackages);
                                apiStatusCallBack.onSuccess(notesPackages);


                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("error", e.getMessage());
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("GetUser:anError", "" + anError);
                            Log.e("GetUser:anError", "" + anError.getErrorBody());
                            apiStatusCallBack.onError(anError);
                        }
                    });

        } catch (Exception ex) {
            Log.e("onUnknownError", "" + ex);
            apiStatusCallBack.onUnknownError(ex);
        }
    }


    public void UpdateUserDetails(UserModel user, final ApiStatusCallBack apiStatusCallBack) {

       // {"type":1,"Action":1,"AppUserId":1,"Fullname":1,"Mobile":1,"Email":1,"Address":1}
        try {
            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("type", 1);
                jsonObject.put("Action", 1);
                jsonObject.put("Userid", user.getUserid());
                jsonObject.put("Fullname", user.getFullname());
                jsonObject.put("Roleid", user.getRoleid());
                jsonObject.put("Address", user.getAddress());
                jsonObject.put("Mobile", user.getMobile());
                jsonObject.put("Token", user.getToken());
                jsonObject.put("Email", user.getEmail());
                jsonObject.put("Profilepic", user.getProfilepic());
                jsonObject.put("Latitute", user.getLatitute());
                jsonObject.put("Longitude", user.getLongitude());
                jsonObject.put("Device", user.getDevice());
                jsonObject.put("LogedinUserId", user.getUserid());
                Log.e(TAG, "UpdateUserDetails: "+user.toString() );

            } catch (Exception e) {
                Log.e("JSONOBJECTerr", "" + e);
                apiStatusCallBack.onUnknownError(e);
            }

            AndroidNetworking.post(Constants.URL_LOGIN)
                    .addJSONObjectBody(jsonObject)
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                TypeToken<Response> token = new TypeToken<Response>() {
                                };
                                Response notesPackages = new Gson().fromJson(response.toString(), token.getType());
                                Log.e("UserModel", "" + notesPackages);
                                apiStatusCallBack.onSuccess(notesPackages);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                Log.e("error", e.getMessage());
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            Log.e("UpdateUser:anError", "" + anError);
                            Log.e("UpdateUser:anError", "" + anError.getErrorBody());
                            apiStatusCallBack.onError(anError);
                        }
                    });

        } catch (Exception ex) {
            Log.e("onUnknownError", "" + ex);
            apiStatusCallBack.onUnknownError(ex);
        }
    }

    public void UpdateUserLocation(double latitute, double longitute, int Userid, final ApiStatusCallBack apiStatusCallBack) {

        // {"type":1,"Action":1,"AppUserId":1,"Fullname":1,"Mobile":1,"Email":1,"Address":1}
        try {
            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("type", 3);
                jsonObject.put("Action", 1);
                jsonObject.put("Userid", Userid);
                jsonObject.put("Latitute", latitute);
                jsonObject.put("Longitude", longitute);
            } catch (Exception e) {
                Log.e("JSONOBJECTerr", "" + e);
                apiStatusCallBack.onUnknownError(e);
            }

            AndroidNetworking.post(Constants.URL_LOGIN)
                    .addJSONObjectBody(jsonObject)
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                TypeToken<Response> token = new TypeToken<Response>() {
                                };
                                Response notesPackages = new Gson().fromJson(response.toString(), token.getType());
         //                       Log.e("UpdateLocation", "" + notesPackages);
                                apiStatusCallBack.onSuccess(notesPackages);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                Log.e("error", e.getMessage());
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            Log.e("UpdateLocation:anError", "" + anError);
                            Log.e("UpdateLocation:anError", "" + anError.getErrorBody());
                            apiStatusCallBack.onError(anError);
                        }
                    });

        } catch (Exception ex) {
            Log.e("onUnknownError", "" + ex);
            apiStatusCallBack.onUnknownError(ex);
        }
    }

    public void GetUserNearMe(int userid, double latitute,   double longitude, int range, int roleid, final ApiStatusCallBack apiStatusCallBack) {

        // {type":2,"Action":1,"AppUserId":1}
        try {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type", 2);
                jsonObject.put("Action", 3);
                jsonObject.put("Userid", userid);
                jsonObject.put("Roleid", roleid);
                jsonObject.put("Latitute", latitute);
                jsonObject.put("Longitude", longitude);
                jsonObject.put("Range", range);
            } catch (Exception e) {
                Log.e("JSONOBJECTerr", "" + e);
                apiStatusCallBack.onUnknownError(e);
            }

            AndroidNetworking.post(Constants.URL_LOGIN)
                    .addJSONObjectBody(jsonObject)
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {

                            try {
                                TypeToken<ArrayList<UserModel>> token = new TypeToken<ArrayList<UserModel>>() {
                                };
                                ArrayList<UserModel> notesPackages = new Gson().fromJson(response.toString(), token.getType());
                                Log.e("UserModel", "" + notesPackages);
                                apiStatusCallBack.onSuccess(notesPackages);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("error", e.getMessage());
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("GetUser:anError", "" + anError);
                            Log.e("GetUser:anError", "" + anError.getErrorBody());
                            apiStatusCallBack.onError(anError);
                        }
                    });

        } catch (Exception ex) {
            Log.e("onUnknownError", "" + ex);
            apiStatusCallBack.onUnknownError(ex);
        }
    }
}
