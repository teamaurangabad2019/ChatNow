package com.teammandroid.chatnow.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;

public class OTPServices {
    private static final String TAG = OTPServices.class.getSimpleName();

    private Context context;

    public OTPServices(Context context) {
        this.context = context;
    }

    @SuppressLint("StaticFieldLeak")
    private static OTPServices instance;

    public static OTPServices getInstance(Context context) {
        if (instance == null) {
            instance = new OTPServices(context);
        }
        return instance;
    }

    public void SendOTP(String mobile,String message,final ApiStatusCallBack apiStatusCallBack) {
        try {

            AndroidNetworking.post("http://sms.fastsmsindia.com/api/sendhttp.php?authkey=32981AWaHeFqP7b5d5686ec&mobiles="+mobile+"&message="+message+"&sender=GLODIV&route=6&country=0")
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            apiStatusCallBack.onSuccess(response);
                            Log.e(TAG, "onResponse: "+response );
                        }

                        @Override
                        public void onError(ANError anError) {
                            apiStatusCallBack.onError(anError);
                        }
                    });
        } catch (Exception ex) {
            Log.e("onUnknownError", "" + ex);
            apiStatusCallBack.onUnknownError(ex);
        }
    }
}

