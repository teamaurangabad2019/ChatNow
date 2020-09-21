package com.teammandroid.chatnow.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;
import com.teammandroid.chatnow.models.ChatModel;
import com.teammandroid.chatnow.utils.Constants;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChattingService {

        private static final String TAG = ChattingService.class.getSimpleName();

        private Context context;

        public ChattingService(Context context) {
            this.context = context;
        }

        @SuppressLint("StaticFieldLeak")
        private static ChattingService instance;

        public static ChattingService getInstance(Context context) {
            if (instance == null) {
                instance = new ChattingService(context);
            }
            return instance;
        }

    public void GetMsg(int SendersId, int ReceiversId ,final ApiStatusCallBack apiStatusCallBack) {

        try {
            JSONObject jsonObject = new JSONObject();

            Log.e(TAG, "GetMsg: senderId"+SendersId );
            Log.e(TAG, "GetMsg: receiver"+ReceiversId );
            try {
                jsonObject.put("type", 4);
                jsonObject.put("Action", 1);
                jsonObject.put("Senderid", SendersId);
                jsonObject.put("Receiverid", ReceiversId);

            } catch (Exception e) {
                Log.e("JSONOBJECTerr", "" + e);
                apiStatusCallBack.onUnknownError(e);
            }

            AndroidNetworking.post(Constants.CHATTING)
                    .addJSONObjectBody(jsonObject)
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {

                            try {
                                TypeToken<ArrayList<ChatModel>> token = new TypeToken<ArrayList<ChatModel>>() {
                                };
                                ArrayList<ChatModel> chatPackages = new Gson().fromJson(response.toString(), token.getType());
                                Log.e("chatPkgs", "" + chatPackages.toString());
                                apiStatusCallBack.onSuccess(chatPackages);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("error", e.getMessage());
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("chatPkgs:anError", "" + anError);
                            Log.e("chatPkgs:anError", "" + anError.getErrorBody());
                            apiStatusCallBack.onError(anError);

                        }
                    });
        } catch (Exception ex) {
            Log.e("onUnknownError", "" + ex);
            apiStatusCallBack.onUnknownError(ex);
        }

    }


    public void GetMsgDetails(int chattingId ,final ApiStatusCallBack apiStatusCallBack) {

        try {
            JSONObject jsonObject = new JSONObject();

            Log.e(TAG, "GetMsg: chattingId"+chattingId );

            try {
                jsonObject.put("type", 2);
                jsonObject.put("Action", 1);
                jsonObject.put("Chattingid", chattingId);

            } catch (Exception e) {
                Log.e("JSONOBJECTerr", "" + e);
                apiStatusCallBack.onUnknownError(e);
            }

            AndroidNetworking.post(Constants.CHATTING)
                    .addJSONObjectBody(jsonObject)
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {

                            try {
                                TypeToken<ArrayList<ChatModel>> token = new TypeToken<ArrayList<ChatModel>>() {
                                };
                                ArrayList<ChatModel> chatPackages = new Gson().fromJson(response.toString(), token.getType());
                                Log.e("chatPkgs", "" + chatPackages.toString());
                                apiStatusCallBack.onSuccess(chatPackages);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("error", e.getMessage());
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("chatPkgs:anError", "" + anError);
                            Log.e("chatPkgs:anError", "" + anError.getErrorBody());
                            apiStatusCallBack.onError(anError);

                        }
                    });
        } catch (Exception ex) {
            Log.e("onUnknownError", "" + ex);
            apiStatusCallBack.onUnknownError(ex);
        }

    }

    public void GetNotification(JSONObject notification,final ApiStatusCallBack apiStatusCallBack) {

        try {
            Map<String, String> params = new HashMap<>();
            params.put("Authorization", Constants.GET_SERVER_KEY);
            params.put("Content-Type", "application/json");

            AndroidNetworking.post(Constants.FCM_API)
                    .addJSONObjectBody(notification)
                    .addHeaders(params)
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("notification responce",""+response.toString());
                            apiStatusCallBack.onSuccess(response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("notification:anError", "" + anError);
                            Log.e("notification:anError", "" + anError.getErrorBody());
                            apiStatusCallBack.onError(anError);

                        }
                    });
        } catch (Exception ex) {
            Log.e("onUnknownError", "" + ex);
            apiStatusCallBack.onUnknownError(ex);
        }

    }
}
