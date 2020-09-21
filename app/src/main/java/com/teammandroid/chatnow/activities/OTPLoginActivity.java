package com.teammandroid.chatnow.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.chaos.view.PinView;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;
import com.teammandroid.chatnow.models.Response;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.network.OTPServices;
import com.teammandroid.chatnow.network.UserServices;
import com.teammandroid.chatnow.services.MyLocationService;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.PrefManager;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class OTPLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = OTPLoginActivity.class.getSimpleName();

    String OTP;
    String mobileNumber = " ";
    //ItemsSqlite itemSqlite;

    EditText txt_mobile_number;
    Button btn_get_otp, btn_confirm;
    RelativeLayout layoutOTP;
    // LinearLayout lyt_progress_reg;
    private ProgressDialog dialog;
    RelativeLayout layoutVerify;
    PinView txt_OTP;
    Button change_number;
    String token;

    //These are the objects needed
    //It is the verification id that will be sent to the user
    private String mVerificationId;
    private String email = " ";
    private UserModel user;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otplogin);
        try {
            //itemSqlite = getIntent().getParcelableExtra("itemSqlite");
            requestPermission();
        } catch (Exception e) {
            Utility.showErrorMessage(this, e.getMessage());
        }
        FacebookSdk.sdkInitialize(getApplicationContext());
        bindViews();
        getTokan();
        layoutOTP.setVisibility(View.VISIBLE);
        layoutVerify.setVisibility(View.GONE);
        listeners();

    }

    private void bindViews() {

        layoutOTP = findViewById(R.id.layoutOTP);
        layoutVerify = findViewById(R.id.layoutVerify);
        txt_mobile_number = findViewById(R.id.txt_mobile_number);
        btn_get_otp = findViewById(R.id.btn_get_otp);
        btn_confirm = findViewById(R.id.btn_confirm);
        txt_OTP = findViewById(R.id.txt_OTP);
        change_number = findViewById(R.id.change_number);
        //  lyt_progress_reg = findViewById(R.id.lyt_progress_reg);
        prefManager=new PrefManager(OTPLoginActivity.this);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading page, please wait.");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

    }
    public void disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();
            }
        }).executeAsync();
    }

    void listeners() {
        btn_get_otp.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        change_number.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_get_otp:
                mobileNumber = txt_mobile_number.getText().toString().trim();
                if (!mobileNumber.equals("")) {

                    mobileNumber = "91" + mobileNumber;
                    OTP = GenerateRandomNumber(6);
                    Log.e("OTP", OTP);

                    //Toast.makeText(OTPLoginActivity.this, OTP, Toast.LENGTH_SHORT).show();
                    String message = "Thank you for visiting Chat Now \n Your OTP :" + OTP;

                    if (Utility.isNetworkAvailable(getApplicationContext())) {
                        //sendOTP(mobileNumber, message);
                        Log.e(TAG, "onClick: here" + OTP);
                    } else {
                        Utility.showErrorMessage(this, "Could not connect to the internet", Snackbar.LENGTH_LONG);
                    }
                    layoutOTP.setVisibility(View.GONE);
                    layoutVerify.setVisibility(View.VISIBLE);
                    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                } else {
                    Log.e(TAG, "onClick: else here" + OTP);

                    Utility.showErrorMessage(this, " Enter Valid 10 digit Number", Snackbar.LENGTH_LONG);
                }

          /*      mobileNumber = txt_mobile_number.getText().toString().trim();
                if (!mobileNumber.equals("")) {

                    mobileNumber =mobileNumber;
                    //mobileNumber = "91" + mobileNumber;
                    if (Utility.isNetworkAvailable(getApplicationContext())) {
                        //sendOTP(mobileNumber, message);
                        sendVerificationCode(mobileNumber);
                        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        Log.e(TAG, "onClick: here" + OTP);
                    } else {
                        Utility.showErrorMessage(this, "Could not connect to the internet", Snackbar.LENGTH_LONG);
                    }
                    layoutOTP.setVisibility(View.GONE);
                    layoutVerify.setVisibility(View.VISIBLE);


                } else {
                    Log.e(TAG, "onClick: else here" + OTP);

                    Utility.showErrorMessage(this, " Enter Valid 10 digit Number", Snackbar.LENGTH_LONG);
                }*/
                break;

            case R.id.btn_confirm:

                String PinViewOTP = String.valueOf(txt_OTP.getText());
                if (PinViewOTP.isEmpty()) {

                    Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show();
                    //Utility.showErrorMessage(OTPLoginActivity.this,"Auth Error", Snackbar.LENGTH_SHORT);
                } else {
                    //if (txt_OTP.getText().toString().equals(OTP)) {
                    if (txt_OTP.getText().toString().equals("123456")) {

                        mobileNumber = txt_mobile_number.getText().toString().trim();
                        String deviceName = getDeviceName();
                        Log.e("BChkNumber", mobileNumber);
                        Log.e(TAG, "token " + token);

                        dialog.show();

                        CreateUserOnServer();


                    } else {
                        Toast.makeText(OTPLoginActivity.this, "Enter proper OTP", Toast.LENGTH_SHORT).show();
                    }
                }

               /* String code = txt_OTP.getText().toString().trim();

                if (code.isEmpty() || code.length() < 6) {
                    txt_OTP.setError("Enter valid code");
                    txt_OTP.requestFocus();
                    return;
                }
                else {
                    mobileNumber = txt_mobile_number.getText().toString().trim();
                    String deviceName = getDeviceName();
                    Log.e("BChkNumber", mobileNumber);
                    Log.e(TAG, "token " + token);

                    //CreateUserOnServer(1); // 1 if loging using otp
                    //verifying the code entered manually
                    verifyVerificationCode(code);
                }*/

                break;

            case R.id.change_number:
                layoutOTP.setVisibility(View.VISIBLE);
                layoutVerify.setVisibility(View.GONE);
                break;
        }
    }

    void sendOTP(String mobileNumber, String message) {
        OTPServices.getInstance(OTPLoginActivity.this).SendOTP(mobileNumber, message, new ApiStatusCallBack() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(OTPLoginActivity.this, "send message", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(ANError anError) {
                Toast.makeText(OTPLoginActivity.this, "Failed ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUnknownError(Exception e) {
                Toast.makeText(OTPLoginActivity.this, "Error ", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();
            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                txt_OTP.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        //signing the user
        // signInWithPhoneAuthCredential(credential);
    }

    private void GetUser(int UserId, final boolean is_new) {
        try {
            if (Utility.isNetworkAvailable(getApplicationContext())) {

                dialog.show();

                Log.e(TAG, "GetUser: " + UserId);
                UserServices.getInstance(getApplicationContext()).GetUserDetails(UserId, new ApiStatusCallBack<ArrayList<UserModel>>() {
                    @Override
                    public void onSuccess(ArrayList<UserModel> response) {
                        //   lyt_progress_reg.setVisibility(View.GONE);
                        dialog.dismiss();
                        user = response.get(0);
                        //PrefHandler.setUserInSharedPref(RegistrationActivity.this,user);
                        if (is_new) {
                            // SessionManager sessionManager = new SessionManager(OTPLoginActivity.this);
                            // sessionManager.createLoginSession(user);
                            Log.e(TAG, "isNew: " + user.getUserid());
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("user",user);
                            Utility.launchActivity(OTPLoginActivity.this, OtherLoginActivity.class, true,bundle);
                        } else {
                            Log.e(TAG, "olreadyExist: " + user.getUserid());
                            UpdateUser();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        //lyt_progress_reg.setVisibility(View.GONE);
                        dialog.dismiss();
                        Utility.showErrorMessage(OTPLoginActivity.this, "Invalid Credentials");
                    }

                    @Override
                    public void onUnknownError(Exception e) {
                        //lyt_progress_reg.setVisibility(View.GONE);
                        dialog.dismiss();
                        Utility.showErrorMessage(OTPLoginActivity.this, e.getMessage());
                    }
                });
            } else {
                Utility.showErrorMessage(OTPLoginActivity.this, "Could not connect to the internet");
            }
        } catch (Exception ex) {
            //lyt_progress_reg.setVisibility(View.GONE);
            dialog.dismiss();
            Utility.showErrorMessage(OTPLoginActivity.this, ex.getMessage());
        }
    }

    private void CreateUserOnServer() {
        try {
            //Creating a multi part request
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
            AndroidNetworking.upload(Constants.URL_LOGIN)
                    .addMultipartParameter("type", "1")
                    .addMultipartParameter("Action", "1")
                    .addMultipartParameter("Userid", "0")
                    .addMultipartParameter("Fullname"," ")
                    .addMultipartParameter("Roleid", "2")
                    .addMultipartParameter("Address", "")
                    .addMultipartParameter("Mobile", mobileNumber)
                    .addMultipartParameter("Token", token)
                    .addMultipartParameter("Email", email)
                    .addMultipartParameter("Profilepic", " ")
                    .addMultipartParameter("Latitute", "0.0")
                    .addMultipartParameter("Longitude", "0.0")
                    .addMultipartParameter("Device", Utility.getDeviceName())
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
                    /*.getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            Log.e(TAG, "onResponse: responce "+response );
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e(TAG, "onError: anEroor "+anError.getMessage() );
                        }
                    })*/
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(TAG, "onResponse: responce "+response.toString() );
                            TypeToken<Response> token = new TypeToken<Response>() {
                            };
                            Response response1 = new Gson().fromJson(response.toString(), token.getType());

                            Log.e(TAG, "onSuccess: " + response);
                            dialog.dismiss();

                            if (response1.getMessage().endsWith("already exist")) {
                                //get and update
                                GetUser(response1.getResult(), false);
                            } else {
                                //only get
                                GetUser(response1.getResult(), true);
                            }

                            Log.e(TAG, "uploadImage:onResponse: " + response);
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.e(TAG, "onError: ", error);
                            //lyt_progress_reg.setVisibility(View.GONE);
                            dialog.dismiss();
                            Utility.showErrorMessage(OTPLoginActivity.this, "Server Error", Snackbar.LENGTH_SHORT);

                        }
                    });
        } catch (Exception exc) {
            Log.e("getMessage", exc.getMessage());
            //  Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void UpdateUser() {
        if (Utility.isNetworkAvailable(getApplicationContext())) {
            try {
                //Creating a multi part request
                AndroidNetworking.upload(Constants.URL_LOGIN)
                        .addMultipartParameter("type", "1")
                        .addMultipartParameter("Action", "1")
                        .addMultipartParameter("Userid", String.valueOf(user.getUserid()))
                        .addMultipartParameter("Fullname",user.getFullname() )
                        .addMultipartParameter("Roleid", String.valueOf(user.getRoleid()))
                        .addMultipartParameter("Address", user.getAddress())
                        .addMultipartParameter("Mobile", user.getMobile())
                        .addMultipartParameter("Token", token)
                        .addMultipartParameter("Email", user.getEmail())
                        .addMultipartParameter("Profilepic", user.getProfilepic())
                        .addMultipartParameter("Latitute", String.valueOf(user.getLatitute()))
                        .addMultipartParameter("Longitude", String.valueOf(user.getLongitude()))
                        .addMultipartParameter("Device", Utility.getDeviceName())
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
                                TypeToken<Response> token = new TypeToken<Response>() {
                                };
                                Response response1 = new Gson().fromJson(response.toString(), token.getType());
                                Log.e(TAG, "uploadImage:onResponse: " + response);
                                //lyt_progress_reg.setVisibility(View.GONE);
                                SessionManager sessionManager = new SessionManager(OTPLoginActivity.this);
                                sessionManager.createLoginSession(user);
                                //mRegProgressDialog.dismiss();
                                Toast.makeText(OTPLoginActivity.this, "Login Successfully..", Toast.LENGTH_LONG).show();
                                   /* Intent intent=new Intent(OTPLoginActivity.this, OtherLoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();*/

                                Bundle bundle = new Bundle();
                                bundle.putParcelable("user",user);
                                Utility.launchActivity(OTPLoginActivity.this, OtherLoginActivity.class, true,bundle);

                                dialog.dismiss();
                            }

                            @Override
                            public void onError(ANError error) {
                                Log.e(TAG, "onError: ", error);
                                // lyt_progress_reg.setVisibility(View.GONE);
                                dialog.dismiss();
                                Utility.showErrorMessage(OTPLoginActivity.this, "Server Error", Snackbar.LENGTH_SHORT);

                            }
                        });
            } catch (Exception exc) {
                Log.e("getMessage", exc.getMessage());
                //  Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Utility.showErrorMessage(OTPLoginActivity.this, "Could not connect to the internet");
        }
    }



    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


    private void getTokan() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        token = task.getResult().getToken();
                        Log.e("token", token);

                    }
                });
    }

    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.SEND_SMS
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            startService(new Intent(OTPLoginActivity.this, MyLocationService.class));
                            //Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            // showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }


                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! " + error, Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    //

    private void UpdateUser(String firebaseUserUid, UserModel user) {
        if (Utility.isNetworkAvailable(getApplicationContext())) {
            try {
                //Creating a multi part request
                AndroidNetworking.upload(Constants.URL_LOGIN)
                        .addMultipartParameter("type", "1")
                        .addMultipartParameter("Action", "1")
                        .addMultipartParameter("Userid", String.valueOf(user.getUserid()))
                        .addMultipartParameter("Fullname", user.getFullname() )
                        .addMultipartParameter("Roleid", String.valueOf(user.getRoleid()))
                        .addMultipartParameter("Address", user.getAddress())
                        .addMultipartParameter("Mobile", user.getMobile())
                        .addMultipartParameter("Token", token)
                        .addMultipartParameter("Email", user.getEmail())
                        .addMultipartParameter("Profilepic", user.getProfilepic())
                        .addMultipartParameter("Latitute", String.valueOf(user.getLatitute()))
                        .addMultipartParameter("Longitude", String.valueOf(user.getLongitude()))
                        .addMultipartParameter("Device", Utility.getDeviceName())
                        .addMultipartParameter("Firebaseuserid", firebaseUserUid)
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
                        /*    .getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    Log.e(TAG, "onResponse: "+response );
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.e(TAG, "onError: "+anError );
                                }
                            });*/
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                TypeToken<Response> token = new TypeToken<Response>() {
                                };
                                Response response1 = new Gson().fromJson(response.toString(), token.getType());
                                Log.e(TAG, "uploadImage:onResponse: " + response);
                                updateUserOnFirebase(OTPLoginActivity.this.user.getUserid(),firebaseUserUid);

                                dialog.dismiss();
                            }

                            @Override
                            public void onError(ANError error) {
                                Log.e(TAG, "onError: ", error);
                                // lyt_progress_reg.setVisibility(View.GONE);
                                dialog.dismiss();
                                Utility.showErrorMessage(OTPLoginActivity.this, "Server Error", Snackbar.LENGTH_SHORT);

                            }
                        });
            } catch (Exception exc) {
                Log.e("getMessage", exc.getMessage());
                //  Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Utility.showErrorMessage(OTPLoginActivity.this, "Could not connect to the internet");
        }
    }

    //
    public void updateUserOnFirebase(int server_userId,String firebaseUserId){

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("onlineUserId",String.valueOf(server_userId));

        //update role in db
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        //ref.child(groupId).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
        ref.child(firebaseUserId).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                        SessionManager sessionManager = new SessionManager(OTPLoginActivity.this);
                        sessionManager.createLoginSession(user);
                        //mRegProgressDialog.dismiss();
                        Toast.makeText(OTPLoginActivity.this, "Register Successfully..", Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(OTPLoginActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog.dismiss();

                        Toast.makeText(OTPLoginActivity.this, "something went wrong ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    String GenerateRandomNumber(int charLength) {
        return String.valueOf(charLength < 1 ? 0 : new Random()
                .nextInt((9 * (int) Math.pow(10, charLength - 1)) - 1)
                + (int) Math.pow(10, charLength - 1));
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent data) {
        super.onActivityResult(requestCode, responseCode, data);

    }

}


/*
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.chaos.view.PinView;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;
import com.teammandroid.chatnow.models.Response;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.network.UserServices;
import com.teammandroid.chatnow.services.MyLocationService;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.PrefManager;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class OTPLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = OTPLoginActivity.class.getSimpleName();

    String OTP;
    String mobileNumber = " ";
    //ItemsSqlite itemSqlite;

    EditText txt_mobile_number;
    Button btn_get_otp, btn_confirm;
    RelativeLayout layoutOTP;
    // LinearLayout lyt_progress_reg;
    private ProgressDialog dialog;
    RelativeLayout layoutVerify;
    PinView txt_OTP;
    Button change_number;

    String token;
    private UserModel user;
    PrefManager prefManager;
    ImageView iv_emailPassword, iv_google, iv_facebook;
    FrameLayout rl_facebook;
    LoginButton btn_fbLogin;

    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    AccessTokenTracker accessTokenTracker;
    CallbackManager callbackManager;
    FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private String email = " ";
    private int serveruserId;
    private GoogleSignInClient googleSignInClient;
    private int RESULT_CODE_SINGIN=999;

    //These are the objects needed
    //It is the verification id that will be sent to the user
    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otplogin);
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        try {
            //itemSqlite = getIntent().getParcelableExtra("itemSqlite");
            requestPermission();
        } catch (Exception e) {
            Utility.showErrorMessage(this, e.getMessage());
        }
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        bindViews();
        getTokan();
        layoutOTP.setVisibility(View.VISIBLE);
        layoutVerify.setVisibility(View.GONE);
        listeners();

        btn_fbLogin.setReadPermissions("email", "public_profile", "user_friends");
        callbackManager=CallbackManager.Factory.create();
        btn_fbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG,"onSuccess"+loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                dialog.dismiss();
                Log.e(TAG,"onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                dialog.dismiss();
                Log.e(TAG,"onErrot "+error.getMessage());
            }
        });

        GoogleSignInOptions gso = new
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this,gso);

    }

    private void bindViews() {
        btn_fbLogin = findViewById(R.id.btn_fbLogin);
        rl_facebook = findViewById(R.id.rl_facebook);
        iv_facebook = findViewById(R.id.iv_facebook);
        iv_google = findViewById(R.id.iv_google);
        iv_emailPassword = findViewById(R.id.iv_emailPassword);
        layoutOTP = findViewById(R.id.layoutOTP);
        layoutVerify = findViewById(R.id.layoutVerify);
        txt_mobile_number = findViewById(R.id.txt_mobile_number);
        btn_get_otp = findViewById(R.id.btn_get_otp);
        btn_confirm = findViewById(R.id.btn_confirm);
        txt_OTP = findViewById(R.id.txt_OTP);
        change_number = findViewById(R.id.change_number);
        //  lyt_progress_reg = findViewById(R.id.lyt_progress_reg);
        prefManager=new PrefManager(OTPLoginActivity.this);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading page, please wait.");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

    }
    public void disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();
            }
        }).executeAsync();
    }

    void listeners() {
        iv_google.setOnClickListener(this);
        iv_emailPassword.setOnClickListener(this);
        rl_facebook.setOnClickListener(this);
        btn_get_otp.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        change_number.setOnClickListener(this);
        btn_fbLogin.setOnClickListener(this);
        iv_facebook.setOnClickListener(this);
        rl_facebook.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_get_otp:
                mobileNumber = txt_mobile_number.getText().toString().trim();
                if (!mobileNumber.equals("")) {

                    mobileNumber =mobileNumber;
                    //mobileNumber = "91" + mobileNumber;
                    if (Utility.isNetworkAvailable(getApplicationContext())) {
                        //sendOTP(mobileNumber, message);
                        sendVerificationCode(mobileNumber);
                        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        Log.e(TAG, "onClick: here" + OTP);
                    } else {
                        Utility.showErrorMessage(this, "Could not connect to the internet", Snackbar.LENGTH_LONG);
                    }
                    layoutOTP.setVisibility(View.GONE);
                    layoutVerify.setVisibility(View.VISIBLE);


                } else {
                    Log.e(TAG, "onClick: else here" + OTP);

                    Utility.showErrorMessage(this, " Enter Valid 10 digit Number", Snackbar.LENGTH_LONG);
                }
                break;

            case R.id.btn_confirm:

                String code = txt_OTP.getText().toString().trim();

                if (code.isEmpty() || code.length() < 6) {
                    txt_OTP.setError("Enter valid code");
                    txt_OTP.requestFocus();
                    return;
                }
                else {
                    mobileNumber = txt_mobile_number.getText().toString().trim();
                    String deviceName = getDeviceName();
                    Log.e("BChkNumber", mobileNumber);
                    Log.e(TAG, "token " + token);

                    //CreateUserOnServer(1); // 1 if loging using otp
                    //verifying the code entered manually
                    verifyVerificationCode(code);

                }



                break;

            case R.id.change_number:
                layoutOTP.setVisibility(View.VISIBLE);
                layoutVerify.setVisibility(View.GONE);
                break;

            case R.id.iv_facebook:
                Log.e(TAG, "onClick: fbrl" );
                dialog.show();
                btn_fbLogin.performClick();
                break;

            case R.id.iv_emailPassword:
                Bundle bundle = new Bundle();
                bundle.putString("token",token);
                Utility.launchActivity(OTPLoginActivity.this, LoginActivity.class,true,bundle);
                break;

            case R.id.iv_google:
                signInM();
                break;

            case R.id.btn_fbLogin:
                Log.e(TAG, "onClick: bfbtn" );
                //  btn_fbLogin.performClick();
                break;
        }
    }

    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();
            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                txt_OTP.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OTPLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //CreateUserOnServer(1,0,0);
                            saveMobileUser();
                        }
                        else {
                            Toast.makeText(OTPLoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void saveMobileUser() {
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        assert firebaseUser != null;
        final String firebaseUserId=firebaseUser.getUid();
        Log.e(TAG, "saveMobileUser: firebaseuid"+firebaseUserId );
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        //System.out.println( dateTime1.format(cal.getTime()) );//prints current date with time ex: 03/05/2020 11:30 PM
        String createdOn=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUserId);

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("id",firebaseUserId);
        hashMap.put("mobile",mobileNumber);
        hashMap.put("createdOn",createdOn);

        hashMap.put("status","offline");
        //Log.e(TAG, "onComplete: userId "+userId );
        hashMap.put("onlineUserId","");
        //hashMap.put("status","Hi there,i am using Chatnow app");
        hashMap.put("typingTo","noOne");//for typing status
        hashMap.put("imageUrl","default");
        hashMap.put("search","");
        hashMap.put("token",token);
        hashMap.put("about","Hey there! I am using Chatnow");


        hashMap.put("username","");
        hashMap.put("email","");
        hashMap.put("address","");

        //hashMap.put("imageUrl","default");
        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    CreateUserOnServer(firebaseUserId);

                    */
/*
                    SessionManager sessionManager = new SessionManager(OTPLoginActivity.this);
                    sessionManager.createLoginSession(user);
                    //mRegProgressDialog.dismiss();
                    Toast.makeText(OTPLoginActivity.this, "Register Successfully..", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(OTPLoginActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    *//*

                }
            }
        });

    }

    private void signInM() {
        Intent singInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(singInIntent,RESULT_CODE_SINGIN);
    }

    private void CreateUserOnServer(int isOtp, int isGoogle, int isfacebook) {
        try {
            //Creating a multi part request
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
            AndroidNetworking.upload(Constants.URL_LOGIN)
                    .addMultipartParameter("type", "1")
                    .addMultipartParameter("Action", "1")
                    .addMultipartParameter("Userid", "0")
                    .addMultipartParameter("Fullname"," ")
                    .addMultipartParameter("Roleid", "2")
                    .addMultipartParameter("Address", "")
                    .addMultipartParameter("Mobile", mobileNumber)
                    .addMultipartParameter("Token", token)
                    .addMultipartParameter("Email", email)
                    .addMultipartParameter("Profilepic", " ")
                    .addMultipartParameter("Latitute", "0.0")
                    .addMultipartParameter("Longitude", "0.0")
                    .addMultipartParameter("Device", Utility.getDeviceName())
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
                    */
/*.getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            Log.e(TAG, "onResponse: responce "+response );
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e(TAG, "onError: anEroor "+anError.getMessage() );
                        }
                    })*//*

                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(TAG, "onResponse: responce "+response.toString() );
                            TypeToken<Response> token = new TypeToken<Response>() {
                            };
                            Response response1 = new Gson().fromJson(response.toString(), token.getType());

                            Log.e(TAG, "onSuccess: " + response);
                            //imp
                            prefManager.setUSER_ID(response1.getResult());
                            // lyt_progress_reg.setVisibility(View.GONE);
                            dialog.dismiss();
                            serveruserId = response1.getResult();
                            Log.e(TAG, "onResponse: userId "+String.valueOf(serveruserId ));

                            if (response1.getMessage().endsWith("already exist")) {
                                //get and update
                                GetUser(response1.getResult(), false, isOtp,isGoogle,isfacebook);
                            } else {
                                //only get
                                GetUser(response1.getResult(), true,isOtp ,isGoogle,isfacebook);
                            }

                            Log.e(TAG, "uploadImage:onResponse: " + response);
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.e(TAG, "onError: ", error);
                            //lyt_progress_reg.setVisibility(View.GONE);
                            dialog.dismiss();
                            Utility.showErrorMessage(OTPLoginActivity.this, "Server Error", Snackbar.LENGTH_SHORT);

                        }
                    });
        } catch (Exception exc) {
            Log.e("getMessage", exc.getMessage());
            //  Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void UpdateUser(int isOtp, int isGoogle, int isFacebook) {
        if (Utility.isNetworkAvailable(getApplicationContext())) {
            try {
                //Creating a multi part request
                AndroidNetworking.upload(Constants.URL_LOGIN)
                        .addMultipartParameter("type", "1")
                        .addMultipartParameter("Action", "1")
                        .addMultipartParameter("Userid", String.valueOf(user.getUserid()))
                        .addMultipartParameter("Fullname",user.getFullname() )
                        .addMultipartParameter("Roleid", String.valueOf(user.getRoleid()))
                        .addMultipartParameter("Address", user.getAddress())
                        .addMultipartParameter("Mobile", user.getMobile())
                        .addMultipartParameter("Token", token)
                        .addMultipartParameter("Email", user.getEmail())
                        .addMultipartParameter("Profilepic", user.getProfilepic())
                        .addMultipartParameter("Latitute", String.valueOf(user.getLatitute()))
                        .addMultipartParameter("Longitude", String.valueOf(user.getLongitude()))
                        .addMultipartParameter("Device", Utility.getDeviceName())
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
                                TypeToken<Response> token = new TypeToken<Response>() {
                                };
                                Response response1 = new Gson().fromJson(response.toString(), token.getType());
                                Log.e(TAG, "uploadImage:onResponse: " + response);
                                //lyt_progress_reg.setVisibility(View.GONE);

                                if (isOtp == 1){
                                    saveMobileUser(user.getUserid());
                                }else if (isGoogle == 1){
                                    saveGoogleUser(user.getUserid());
                                }else if (isFacebook == 1){
                                    saveFaceBookUser(user.getUserid());
                                }

                                else {

                                    SessionManager sessionManager = new SessionManager(OTPLoginActivity.this);
                                    sessionManager.createLoginSession(user);
                                    //mRegProgressDialog.dismiss();
                                    Toast.makeText(OTPLoginActivity.this, "Register Successfully..", Toast.LENGTH_LONG).show();
                                    Intent intent=new Intent(OTPLoginActivity.this, HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                dialog.dismiss();
                            }

                            @Override
                            public void onError(ANError error) {
                                Log.e(TAG, "onError: ", error);
                                // lyt_progress_reg.setVisibility(View.GONE);
                                dialog.dismiss();
                                Utility.showErrorMessage(OTPLoginActivity.this, "Server Error", Snackbar.LENGTH_SHORT);

                            }
                        });
            } catch (Exception exc) {
                Log.e("getMessage", exc.getMessage());
                //  Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Utility.showErrorMessage(OTPLoginActivity.this, "Could not connect to the internet");
        }
    }

    private void GetUser(int UserId, final boolean is_new, int isOtp, int isGoogle, int isFacebook) {
        try {
            if (Utility.isNetworkAvailable(getApplicationContext())) {
               */
/* lyt_progress_reg.setVisibility(View.VISIBLE);
                lyt_progress_reg.setAlpha(1.0f);
*//*

                dialog.show();
                Log.e(TAG, "GetUser: " + UserId);
                UserServices.getInstance(getApplicationContext()).GetUserDetails(UserId, new ApiStatusCallBack<ArrayList<UserModel>>() {
                    @Override
                    public void onSuccess(ArrayList<UserModel> response) {
                        //   lyt_progress_reg.setVisibility(View.GONE);
                        dialog.dismiss();
                        user = response.get(0);
                        //PrefHandler.setUserInSharedPref(RegistrationActivity.this,user);
                        if (is_new ) {
                            if (isOtp == 1){
                                saveMobileUser(UserId);
                            }else if (isGoogle == 1){
                                saveGoogleUser(UserId);
                            }else if (isFacebook == 1){
                                saveFaceBookUser(UserId);
                            }
                            // CreateUserOnFirebase();
                        } else {
                            Log.e(TAG, "olreadyExist: " + user.getUserid());
                            UpdateUser(isOtp, isGoogle,isFacebook);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        //lyt_progress_reg.setVisibility(View.GONE);
                        dialog.dismiss();
                        Utility.showErrorMessage(OTPLoginActivity.this, "Invalid Credentials");
                    }

                    @Override
                    public void onUnknownError(Exception e) {
                        //lyt_progress_reg.setVisibility(View.GONE);
                        dialog.dismiss();
                        Utility.showErrorMessage(OTPLoginActivity.this, e.getMessage());
                    }
                });
            } else {
                Utility.showErrorMessage(OTPLoginActivity.this, "Could not connect to the internet");
            }
        } catch (Exception ex) {
            //lyt_progress_reg.setVisibility(View.GONE);
            dialog.dismiss();
            Utility.showErrorMessage(OTPLoginActivity.this, ex.getMessage());
        }
    }


    private void saveMobileUser(int userId) {
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        assert firebaseUser != null;
        final String firebaseUserId=firebaseUser.getUid();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        //System.out.println( dateTime1.format(cal.getTime()) );//prints current date with time ex: 03/05/2020 11:30 PM
        String createdOn=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUserId);

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("id",firebaseUserId);
        hashMap.put("mobile",mobileNumber);
        hashMap.put("createdOn",createdOn);

        hashMap.put("status","offline");
        Log.e(TAG, "onComplete: userId "+userId );
        hashMap.put("onlineUserId",String.valueOf(userId));
        //hashMap.put("status","Hi there,i am using Chatnow app");
        hashMap.put("typingTo","noOne");//for typing status
        hashMap.put("imageUrl","default");
        hashMap.put("search","");
        hashMap.put("token",token);
        hashMap.put("about","Hey there! I am using Chatnow");


        hashMap.put("username","");
        hashMap.put("email","");
        hashMap.put("address","");

        //hashMap.put("imageUrl","default");
        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    SessionManager sessionManager = new SessionManager(OTPLoginActivity.this);
                    sessionManager.createLoginSession(user);
                    //mRegProgressDialog.dismiss();
                    Toast.makeText(OTPLoginActivity.this, "Register Successfully..", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(OTPLoginActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    private void saveGoogleUser(int userId) {

        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        assert firebaseUser != null;
        String userId1=firebaseUser.getUid();


        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        //System.out.println( dateTime1.format(cal.getTime()) );//prints current date with time ex: 03/05/2020 11:30 PM

        String createdOn=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM

        reference= FirebaseDatabase.getInstance().getReference("Users").child(userId1);
        HashMap<String,String> hashMap=new HashMap<>();
        //getLastSignedInAccount returned the account
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account !=null){

            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            String photoUrl = String.valueOf(account.getPhotoUrl());

            hashMap.put("id",userId1);
            hashMap.put("username",personName);
            hashMap.put("email",personEmail);
            hashMap.put("password","***");
            hashMap.put("status","offline");
            hashMap.put("onlineUserId",String.valueOf(userId));
            //hashMap.put("status","Hi there,i am using Chatnow app");

            hashMap.put("typingTo","noOne");//for typing status

            hashMap.put("imageUrl",photoUrl);
            hashMap.put("search",personName.toLowerCase());

            hashMap.put("token",token);
            hashMap.put("about","Hey there! I am using Chatnow");
            hashMap.put("createdOn",createdOn);
            hashMap.put("mobile","");
            hashMap.put("address","");

            //Toast.makeText(FirebaseAuthenticationActivity.this,personName + "  " + personEmail,Toast.LENGTH_LONG).show();

            Log.e("AuthDetails",""+personName+" "+personEmail+" "+personId+" "+personGivenName+" "+photoUrl);
        }

        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    SessionManager sessionManager = new SessionManager(OTPLoginActivity.this);
                    sessionManager.createLoginSession(user);
                    //mRegProgressDialog.dismiss();
                    //Toast.makeText(FirebaseAuthenticationActivity.this, "Register Successfully..", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(OTPLoginActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }


    private void saveFaceBookUser(int userId) {
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        assert firebaseUser != null;
        String fuserId=firebaseUser.getUid();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        //System.out.println( dateTime1.format(cal.getTime()) );//prints current date with time ex: 03/05/2020 11:30 PM

        String createdOn=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM

        reference= FirebaseDatabase.getInstance().getReference("Users").child(fuserId);
        HashMap<String,String> hashMap=new HashMap<>();
        if (firebaseUser!=null){
            String username=firebaseUser.getDisplayName();
            String email=firebaseUser.getEmail();
            String photoUrl=firebaseUser.getPhotoUrl().toString();
            photoUrl=photoUrl+"?type=large";

            hashMap.put("id",fuserId);
            hashMap.put("username",username);
            hashMap.put("email",email);
            //hashMap.put("password","***");
            hashMap.put("status","offline");
            //hashMap.put("status","Hi there,i am using Chatnow app");
            hashMap.put("typingTo","noOne");//for typing status
            hashMap.put("imageUrl",photoUrl);
            hashMap.put("search",username.toLowerCase());
            hashMap.put("onlineUserId",String.valueOf(userId));
            Log.d(TAG,"AuthDetails"+username+" "+email+" "+photoUrl+" "+userId);

            hashMap.put("address","");
            hashMap.put("about","Hey there! I am using Chatnow");
            hashMap.put("token",token);
            hashMap.put("mobile","");
            hashMap.put("createdOn",createdOn);

        }

        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){


                    SessionManager sessionManager = new SessionManager(OTPLoginActivity.this);
                    sessionManager.createLoginSession(user);
                    //mRegProgressDialog.dismiss();
                    //Toast.makeText(FirebaseAuthenticationActivity.this, "Register Successfully..", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(OTPLoginActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void CreateUserOnFirebase() {
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        assert firebaseUser != null;
        String fuserId=firebaseUser.getUid();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(fuserId);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        //System.out.println( dateTime1.format(cal.getTime()) );//prints current date with time ex: 03/05/2020 11:30 PM

        String createdOn=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM

        HashMap<String,String> hashMap=new HashMap<>();
        if (firebaseUser!=null){
            String username=firebaseUser.getDisplayName();
            String email=firebaseUser.getEmail();
            String photoUrl=firebaseUser.getPhotoUrl().toString();
            photoUrl=photoUrl+"?type=large";

            hashMap.put("id",fuserId);
            hashMap.put("username",username);
            hashMap.put("email",email);
            //hashMap.put("password","***");
            hashMap.put("status","offline");
            //hashMap.put("status","Hi there,i am using Chatnow app");
            hashMap.put("typingTo","noOne");//for typing status
            hashMap.put("imageUrl",photoUrl);
            hashMap.put("search",username.toLowerCase());
            hashMap.put("onlineUserId",String.valueOf(serveruserId));

            hashMap.put("address","");
            hashMap.put("about","Hey there! I am using Chatnow");
            hashMap.put("token",token);
            hashMap.put("mobile","");
            hashMap.put("createdOn",createdOn);

            Log.e(TAG, "CreateUserOnFirebase: userId "+serveruserId );
            Log.d(TAG,"AuthDetails"+username+" "+email+" "+photoUrl+" "+serveruserId);
        }

        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //mRegProgressDialog.dismiss();
                    Toast.makeText(OTPLoginActivity.this, "Register Successfully..", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(OTPLoginActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else {

                }
            }
        });
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


    private void getTokan() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        token = task.getResult().getToken();
                        Log.e("token", token);

                    }
                });
    }

    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.SEND_SMS
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            startService(new Intent(OTPLoginActivity.this, MyLocationService.class));
                            //Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            // showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }


                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! " + error, Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void handleFacebookToken(AccessToken accessToken) {
        Log.d(TAG, "handleFacebookToken: "+accessToken);
        AuthCredential credential= FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    dialog.dismiss();

                    //CreateUserOnServer(0,0,1);    //  login without otp.

                    saveFaceBookUser();

                    Log.d(TAG, "sign in with creditial: successfull ");
                }
                else {
                    dialog.dismiss();
                    Log.d(TAG, "sign in with creditial: failure ",task.getException());
                    Toast.makeText(OTPLoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void saveFaceBookUser() {

        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        assert firebaseUser != null;
        String firebaseUserUid=firebaseUser.getUid();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        //System.out.println( dateTime1.format(cal.getTime()) );//prints current date with time ex: 03/05/2020 11:30 PM

        String createdOn=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM

        reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUserUid);
        HashMap<String,String> hashMap=new HashMap<>();
        if (firebaseUser!=null){
            String username=firebaseUser.getDisplayName();
            String email=firebaseUser.getEmail();
            String photoUrl=firebaseUser.getPhotoUrl().toString();
            photoUrl=photoUrl+"?type=large";

            hashMap.put("id",firebaseUserUid);
            hashMap.put("username",username);
            hashMap.put("email",email);
            //hashMap.put("password","***");
            hashMap.put("status","offline");
            //hashMap.put("status","Hi there,i am using Chatnow app");
            hashMap.put("typingTo","noOne");//for typing status
            hashMap.put("imageUrl",photoUrl);
            hashMap.put("search",username.toLowerCase());
            hashMap.put("onlineUserId","");
            Log.d(TAG,"AuthDetails"+username+" "+email+" "+photoUrl);

            hashMap.put("address","");
            hashMap.put("about","Hey there! I am using Chatnow");
            hashMap.put("token",token);
            hashMap.put("mobile","");
            hashMap.put("createdOn",createdOn);

        }

        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){


                    CreateUserOnServer(firebaseUserUid);


                    */
/*SessionManager sessionManager = new SessionManager(OTPLoginActivity.this);
                    sessionManager.createLoginSession(user);
                    //mRegProgressDialog.dismiss();
                    //Toast.makeText(FirebaseAuthenticationActivity.this, "Register Successfully..", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(OTPLoginActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    *//*

                }
            }
        });
    }


    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        //we use try catch block because of Exception.
        try {
            dialog.show();
            //signInButton.setVisibility(View.INVISIBLE);
            GoogleSignInAccount account = task.getResult(ApiException.class);
            //  Toast.makeText(OTPLoginActivity.this,"Signed In successfully",Toast.LENGTH_LONG).show();
            //SignIn successful now show authentication
            FirebaseGoogleAuth(account);

        } catch (ApiException e) {
            e.printStackTrace();
            dialog.dismiss();
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(OTPLoginActivity.this,"SignIn Failed!!!",Toast.LENGTH_LONG).show();
            //FirebaseGoogleAuth(null);
        }
    }


    private void FirebaseGoogleAuth(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        //here we are checking the Authentication Credential and checking the task is successful or not and display the message
        //based on that.
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    //CreateUserOnServer(0,1,0);

                    saveGoogleUser();


                    */
/* // Toast.makeText(OTPLoginActivity.this,"successful",Toast.LENGTH_LONG).show();
                     *//*

                }
                else {
                    Toast.makeText(OTPLoginActivity.this,"Failed!",Toast.LENGTH_LONG).show();
                    // UpdateUI(null);
                }
            }
        });
    }

    private void saveGoogleUser() {

        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        assert firebaseUser != null;
        String firebaseUserUid=firebaseUser.getUid();


        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        //System.out.println( dateTime1.format(cal.getTime()) );//prints current date with time ex: 03/05/2020 11:30 PM

        String createdOn=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM

        reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUserUid);
        HashMap<String,String> hashMap=new HashMap<>();
        //getLastSignedInAccount returned the account
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account !=null){

            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            String photoUrl = String.valueOf(account.getPhotoUrl());

            hashMap.put("id",firebaseUserUid);
            hashMap.put("username",personName);
            hashMap.put("email",personEmail);
            hashMap.put("password","***");
            hashMap.put("status","offline");
            hashMap.put("onlineUserId","");
            //hashMap.put("status","Hi there,i am using Chatnow app");

            hashMap.put("typingTo","noOne");//for typing status

            hashMap.put("imageUrl",photoUrl);
            hashMap.put("search",personName.toLowerCase());

            hashMap.put("token",token);
            hashMap.put("about","Hey there! I am using Chatnow");
            hashMap.put("createdOn",createdOn);
            hashMap.put("mobile","");
            hashMap.put("address","");

            //Toast.makeText(FirebaseAuthenticationActivity.this,personName + "  " + personEmail,Toast.LENGTH_LONG).show();

            Log.e("AuthDetails",""+personName+" "+personEmail+" "+personId+" "+personGivenName+" "+photoUrl);
        }

        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){


                    CreateUserOnServer(firebaseUserUid);


                    */
/*SessionManager sessionManager = new SessionManager(OTPLoginActivity.this);
                    sessionManager.createLoginSession(user);
                    //mRegProgressDialog.dismiss();
                    //Toast.makeText(FirebaseAuthenticationActivity.this, "Register Successfully..", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(OTPLoginActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    *//*

                }
            }
        });
    }

    //
    private void CreateUserOnServer(String firebaseUserUid) {
        try {
            //Creating a multi part request
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
            AndroidNetworking.upload(Constants.URL_LOGIN)
                    .addMultipartParameter("type", "1")
                    .addMultipartParameter("Action", "1")
                    .addMultipartParameter("Userid", "0")
                    .addMultipartParameter("Fullname"," ")
                    .addMultipartParameter("Roleid", "2")
                    .addMultipartParameter("Address", " ")
                    .addMultipartParameter("Mobile", mobileNumber)
                    .addMultipartParameter("Token", token)
                    .addMultipartParameter("Email", email)
                    .addMultipartParameter("Profilepic", " ")
                    .addMultipartParameter("Latitute", "0.0")
                    .addMultipartParameter("Longitude", "0.0")
                    .addMultipartParameter("Device", Utility.getDeviceName())
                    .addMultipartParameter("Firebaseuserid", firebaseUserUid)
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
                    */
/*  .getAsString(new StringRequestListener() {
                          @Override
                          public void onResponse(String response) {
                              Log.e(TAG, "onResponse: responce "+response );
                          }

                          @Override
                          public void onError(ANError anError) {
                              Log.e(TAG, "onError: anEroor "+anError.getMessage() );
                          }
                      });*//*

                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(TAG, "onResponse: responce "+response.toString() );
                            TypeToken<Response> token = new TypeToken<Response>() {
                            };
                            Response response1 = new Gson().fromJson(response.toString(), token.getType());

                            Log.e(TAG, "onSuccess: " + response);
                            //imp
                            prefManager.setUSER_ID(response1.getResult());
                            // lyt_progress_reg.setVisibility(View.GONE);
                            dialog.dismiss();
                            serveruserId = response1.getResult();
                            Log.e(TAG, "onResponse: userId "+String.valueOf(serveruserId ));

                            if (response1.getMessage().endsWith("already exist")) {
                                //get and update
                                GetUser(response1.getResult(), false,firebaseUserUid);
                            } else {
                                //only get
                                GetUser(response1.getResult(), true,firebaseUserUid);
                            }

                            Log.e(TAG, "uploadImage:onResponse: " + response);
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.e(TAG, "onError: ", error);
                            //lyt_progress_reg.setVisibility(View.GONE);
                            dialog.dismiss();
                            Utility.showErrorMessage(OTPLoginActivity.this, "Server Error", Snackbar.LENGTH_SHORT);

                        }
                    });

        } catch (Exception exc) {
            Log.e("getMessage", exc.getMessage());
            //  Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    //
    private void GetUser(int UserId,final boolean is_new,String firebaseUserUid) {
        try {
            if (Utility.isNetworkAvailable(getApplicationContext())) {
               */
/* lyt_progress_reg.setVisibility(View.VISIBLE);
                lyt_progress_reg.setAlpha(1.0f);
*//*

                dialog.show();
                Log.e(TAG, "GetUser: " + UserId);
                UserServices.getInstance(getApplicationContext()).GetUserDetails(UserId, new ApiStatusCallBack<ArrayList<UserModel>>() {
                    @Override
                    public void onSuccess(ArrayList<UserModel> response) {
                        //   lyt_progress_reg.setVisibility(View.GONE);
                        dialog.dismiss();
                        user = response.get(0);
                        //PrefHandler.setUserInSharedPref(RegistrationActivity.this,user);
                        if (is_new ) {

                            */
/*if (isOtp == 1){
                                saveMobileUser(UserId);
                            }else if (isGoogle == 1){
                                saveGoogleUser(UserId);
                            }else if (isFacebook == 1){
                                saveFaceBookUser(UserId);
                            }*//*

                            // CreateUserOnFirebase();

                            int server_userId=UserId;
                            updateUserOnFirebase(server_userId,firebaseUserUid);

                        } else {
                            Log.e(TAG, "olreadyExist: " + user.getUserid());

                            int server_userId=UserId;

                            UpdateUser(firebaseUserUid,user);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        //lyt_progress_reg.setVisibility(View.GONE);
                        dialog.dismiss();
                        Utility.showErrorMessage(OTPLoginActivity.this, "Invalid Credentials");
                    }

                    @Override
                    public void onUnknownError(Exception e) {
                        //lyt_progress_reg.setVisibility(View.GONE);
                        dialog.dismiss();
                        Utility.showErrorMessage(OTPLoginActivity.this, e.getMessage());
                    }
                });
            } else {
                Utility.showErrorMessage(OTPLoginActivity.this, "Could not connect to the internet");
            }
        } catch (Exception ex) {
            //lyt_progress_reg.setVisibility(View.GONE);
            dialog.dismiss();
            Utility.showErrorMessage(OTPLoginActivity.this, ex.getMessage());
        }
    }

    private void UpdateUser(String firebaseUserUid, UserModel user) {
        if (Utility.isNetworkAvailable(getApplicationContext())) {
            try {
                //Creating a multi part request
                AndroidNetworking.upload(Constants.URL_LOGIN)
                        .addMultipartParameter("type", "1")
                        .addMultipartParameter("Action", "1")
                        .addMultipartParameter("Userid", String.valueOf(user.getUserid()))
                        .addMultipartParameter("Fullname", user.getFullname() )
                        .addMultipartParameter("Roleid", String.valueOf(user.getRoleid()))
                        .addMultipartParameter("Address", user.getAddress())
                        .addMultipartParameter("Mobile", user.getMobile())
                        .addMultipartParameter("Token", token)
                        .addMultipartParameter("Email", user.getEmail())
                        .addMultipartParameter("Profilepic", user.getProfilepic())
                        .addMultipartParameter("Latitute", String.valueOf(user.getLatitute()))
                        .addMultipartParameter("Longitude", String.valueOf(user.getLongitude()))
                        .addMultipartParameter("Device", Utility.getDeviceName())
                        .addMultipartParameter("Firebaseuserid", firebaseUserUid)
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
                        */
/*    .getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    Log.e(TAG, "onResponse: "+response );
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.e(TAG, "onError: "+anError );
                                }
                            });*//*

                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                TypeToken<Response> token = new TypeToken<Response>() {
                                };
                                Response response1 = new Gson().fromJson(response.toString(), token.getType());
                                Log.e(TAG, "uploadImage:onResponse: " + response);
                                updateUserOnFirebase(OTPLoginActivity.this.user.getUserid(),firebaseUserUid);

                                dialog.dismiss();
                            }

                            @Override
                            public void onError(ANError error) {
                                Log.e(TAG, "onError: ", error);
                                // lyt_progress_reg.setVisibility(View.GONE);
                                dialog.dismiss();
                                Utility.showErrorMessage(OTPLoginActivity.this, "Server Error", Snackbar.LENGTH_SHORT);

                            }
                        });
            } catch (Exception exc) {
                Log.e("getMessage", exc.getMessage());
                //  Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Utility.showErrorMessage(OTPLoginActivity.this, "Could not connect to the internet");
        }
    }

    //
    public void updateUserOnFirebase(int server_userId,String firebaseUserId){

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("onlineUserId",String.valueOf(server_userId));

        //update role in db
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        //ref.child(groupId).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
        ref.child(firebaseUserId).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                        SessionManager sessionManager = new SessionManager(OTPLoginActivity.this);
                        sessionManager.createLoginSession(user);
                        //mRegProgressDialog.dismiss();
                        Toast.makeText(OTPLoginActivity.this, "Register Successfully..", Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(OTPLoginActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog.dismiss();

                        Toast.makeText(OTPLoginActivity.this, "something went wrong ", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);

        if (requestCode == RESULT_CODE_SINGIN) {        //just to verify the code
            //create a Task object and use GoogleSignInAccount from Intent and write a separate method to handle singIn Result.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

}
*/
