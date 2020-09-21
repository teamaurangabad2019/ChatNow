package com.teammandroid.chatnow.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;
import com.teammandroid.chatnow.models.Response;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.network.UserServices;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.PrefManager;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private EditText et_name;
    private TextInputEditText et_email;
    private TextInputEditText et_password;

    private Button btn_createAccount;
    TextView txt_alreadyHaveAnAcct;

    private FirebaseAuth mAuth;

    private Toolbar mToolbar;
    ImageView iv_back;
    PrefManager prefManager;

    private ProgressDialog mRegProgressDialog;

    //private FirebaseDatabase reference;

    private DatabaseReference reference;
    private String token;
    private int userId;
    private boolean isexist = false;

    private UserModel user;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user = getIntent().getParcelableExtra("user");
        token = getIntent().getStringExtra("token");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        bindView();
        btnListener();

    }

    private void bindView() {
        et_name =findViewById(R.id.et_name);
        et_email=findViewById(R.id.et_email);
        et_password=findViewById(R.id.et_password);
        btn_createAccount=findViewById(R.id.btn_createAccount);
        iv_back=findViewById(R.id.iv_back);
        txt_alreadyHaveAnAcct=findViewById(R.id.txt_alreadyHaveAnAcct);
        mRegProgressDialog =new ProgressDialog(this);

        prefManager=new PrefManager(RegisterActivity.this);
    }

    private void btnListener() {
        btn_createAccount.setOnClickListener(this);
        txt_alreadyHaveAnAcct.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    private void register(final String username, String email, String password){

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

                            assert firebaseUser != null;
                            String firebaseUserUid=firebaseUser.getUid();


                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                            //System.out.println( dateTime1.format(cal.getTime()) );//prints current date with time ex: 03/05/2020 11:30 PM

                            String createdOn=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM

                            reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUserUid);
                            HashMap<String,String> hashMap=new HashMap<>();
                            hashMap.put("id",firebaseUserUid);
                            hashMap.put("username",username);
                            hashMap.put("email",email);
                            hashMap.put("password",password);
                            //hashMap.put("status","Hi there,i am using Chatnow app");
                            hashMap.put("status","offline");//for online status
                            hashMap.put("typingTo","noOne");//for typing status
                            hashMap.put("imageUrl",user.getProfilepic());
                            hashMap.put("search",username.toLowerCase());
                            hashMap.put("onlineUserId", String.valueOf(user.getUserid()));
                            hashMap.put("address",user.getAddress());
                            hashMap.put("about","Hey there! I am using Chatnow");
                            hashMap.put("token",token);
                            hashMap.put("mobile",user.getMobile());
                            hashMap.put("createdOn",createdOn);

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        user.setFullname(username);
                                        user.setEmail(email);
                                        UpdateUser(firebaseUserUid);
                                    }
                                }
                            });
                        }
                        else {
                            mRegProgressDialog.hide();
                            Toast.makeText(RegisterActivity.this, "You can't register with this email and password", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    private void UpdateUser(String firebaseUserUid) {
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
                        .addMultipartParameter("Firebaseuserid", firebaseUser.getUid())
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

                                mRegProgressDialog.dismiss();
                              /*  SessionManager sessionManager = new SessionManager(RegisterActivity.this);
                                sessionManager.createLoginSession(user);*/
                                //mRegProgressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Register Successfully..", Toast.LENGTH_LONG).show();
                                //Intent intent=new Intent(RegisterActivity.this, HomeActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("user",user);
                                Utility.launchActivity(RegisterActivity.this,LoginActivity.class,false,bundle);
                                // updateUserOnFirebase(user.getUserid(),firebaseUserUid);
                                mRegProgressDialog.dismiss();
                            }

                            @Override
                            public void onError(ANError error) {
                                Log.e(TAG, "onError: ", error);
                                // lyt_progress_reg.setVisibility(View.GONE);
                                mRegProgressDialog.dismiss();
                                Utility.showErrorMessage(RegisterActivity.this, "Server Error", Snackbar.LENGTH_SHORT);

                            }
                        });
            } catch (Exception exc) {
                Log.e("getMessage", exc.getMessage());
                //  Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Utility.showErrorMessage(RegisterActivity.this, "Could not connect to the internet");
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            default:
                break;

            case R.id.btn_createAccount:


                String username= et_name.getText().toString();

                String email=et_email.getText().toString();

                String password=et_password.getText().toString();

                if(TextUtils.isEmpty(username)){
                    Toast.makeText(RegisterActivity.this, "Enter your name", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterActivity.this, "Enter your email id", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                }

                else if(password.length() < 6){
                    Toast.makeText(RegisterActivity.this, "password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                }
                else {
                    mRegProgressDialog.setTitle("Registering User");
                    mRegProgressDialog.setMessage("Please Wait while we create your account !");
                    mRegProgressDialog.setCanceledOnTouchOutside(false);
                    mRegProgressDialog.show();
                    register(username,email,password);
                }

                break;
            case R.id.txt_alreadyHaveAnAcct:

                Bundle bundle = new Bundle();
                bundle.putParcelable("user",user);
                Utility.launchActivity(RegisterActivity.this,LoginActivity.class,false,bundle);

                break;

            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

}


/*
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;
import com.teammandroid.chatnow.models.Response;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.network.UserServices;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.PrefManager;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private EditText et_name;
    private TextInputEditText et_email;
    private TextInputEditText et_password;

    private Button btn_createAccount;
    TextView txt_alreadyHaveAnAcct;

    private FirebaseAuth mAuth;

    private Toolbar mToolbar;
    ImageView iv_back;
    PrefManager prefManager;


    private ProgressDialog mRegProgressDialog;

    //private FirebaseDatabase reference;

    private DatabaseReference reference;
    private String token;
    private int userId;
    private boolean isexist = false;

    private UserModel user;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        token = getIntent().getStringExtra("token");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        bindView();
        btnListener();

    }

    private void bindView() {
        et_name =findViewById(R.id.et_name);
        et_email=findViewById(R.id.et_email);
        et_password=findViewById(R.id.et_password);
        btn_createAccount=findViewById(R.id.btn_createAccount);
        iv_back=findViewById(R.id.iv_back);
        txt_alreadyHaveAnAcct=findViewById(R.id.txt_alreadyHaveAnAcct);
        mRegProgressDialog =new ProgressDialog(this);

        prefManager=new PrefManager(RegisterActivity.this);

    }

    private void btnListener() {
        btn_createAccount.setOnClickListener(this);
        txt_alreadyHaveAnAcct.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    private void register(final String username, String email, String password){

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            //CreateUserOnServer(username, email, password);

                            //FirebaseUser firebaseUser=mAuth.getCurrentUser();


                            firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

                            assert firebaseUser != null;
                            String firebaseUserUid=firebaseUser.getUid();


                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                            //System.out.println( dateTime1.format(cal.getTime()) );//prints current date with time ex: 03/05/2020 11:30 PM

                            String createdOn=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM

                            reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUserUid);
                            HashMap<String,String> hashMap=new HashMap<>();
                            hashMap.put("id",firebaseUserUid);
                            hashMap.put("username",username);
                            hashMap.put("email",email);
                            hashMap.put("password",password);
                            //hashMap.put("status","Hi there,i am using Chatnow app");
                            hashMap.put("status","offline");//for online status
                            hashMap.put("typingTo","noOne");//for typing status
                            hashMap.put("imageUrl","default");
                            hashMap.put("search",username.toLowerCase());
                            hashMap.put("onlineUserId","");

                            hashMap.put("address","");
                            hashMap.put("about","Hey there! I am using Chatnow");
                            hashMap.put("token",token);
                            hashMap.put("mobile","");
                            hashMap.put("createdOn",createdOn);


                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){



                                        CreateUserOnServer(username, email, password,firebaseUserUid);


                                        */
/*mRegProgressDialog.dismiss();

                                        if (isexist){
                                            Toast.makeText(RegisterActivity.this, "Account Already Exist..", Toast.LENGTH_LONG).show();
                                            Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }else {
                                        Toast.makeText(RegisterActivity.this, "Register Successfully..", Toast.LENGTH_LONG).show();
                                        Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                        }
                                        *//*

                                    }
                                }
                            });
                        }
                        else {
                            mRegProgressDialog.hide();
                            Toast.makeText(RegisterActivity.this, "You can't register with this email and password", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }


    private void CreateUserOnServer(String username, String email, String password,String firebaseUserUid) {
        try {
            //Creating a multi part request
            mRegProgressDialog.show();
            mRegProgressDialog.setCanceledOnTouchOutside(true);
            AndroidNetworking.upload(Constants.URL_LOGIN)
                    .addMultipartParameter("type", "1")
                    .addMultipartParameter("Action", "1")
                    .addMultipartParameter("Userid", "0")
                    .addMultipartParameter("Fullname",username)
                    .addMultipartParameter("Roleid", "2")
                    .addMultipartParameter("Address", "")
                    .addMultipartParameter("Mobile", "1234567891")
                    .addMultipartParameter("Token", token)
                    .addMultipartParameter("Email", email)
                    .addMultipartParameter("Profilepic", " ")
                    .addMultipartParameter("Latitute", "0.0")
                    .addMultipartParameter("Longitude", "0.0")
                    .addMultipartParameter("Device", Utility.getDeviceName())
                    .addMultipartParameter("Firebaseuserid",firebaseUser.getUid())
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

                            Log.e(TAG, "onSuccess: " + response);
                            //imp
                            prefManager.setUSER_ID(response1.getResult());
                            // lyt_progress_reg.setVisibility(View.GONE);
                            mRegProgressDialog.dismiss();
                            userId = response1.getResult();


                            */
/*if (response1.getMessage().endsWith("already exist")) {
                                //get and update
                                isexist = true;

                                int server_userId=userId;

                                updateUserOnFirebase(server_userId,firebaseUserUid);

                                Toast.makeText(RegisterActivity.this,"This Account is already registered .",Toast.LENGTH_SHORT).show();

                            }else {
                                isexist = false;

                                int server_userId=userId;
                                updateUserOnFirebase(server_userId,firebaseUserUid);
                            }
                            *//*


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
                            mRegProgressDialog.dismiss();
                            Utility.showErrorMessage(RegisterActivity.this, "Server Error", Snackbar.LENGTH_SHORT);
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


               mRegProgressDialog.show();
                Log.e(TAG, "GetUser: " + UserId);
                UserServices.getInstance(getApplicationContext()).GetUserDetails(UserId, new ApiStatusCallBack<ArrayList<UserModel>>() {
                    @Override
                    public void onSuccess(ArrayList<UserModel> response) {
                        //   lyt_progress_reg.setVisibility(View.GONE);
                        mRegProgressDialog.dismiss();
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
                            //UpdateUser(server_userId);

                            UpdateUser(firebaseUserUid);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        //lyt_progress_reg.setVisibility(View.GONE);
                        mRegProgressDialog.dismiss();
                        Utility.showErrorMessage(RegisterActivity.this, "Invalid Credentials");
                    }

                    @Override
                    public void onUnknownError(Exception e) {
                        //lyt_progress_reg.setVisibility(View.GONE);
                        mRegProgressDialog.dismiss();
                        Utility.showErrorMessage(RegisterActivity.this, e.getMessage());
                    }
                });
            } else {
                Utility.showErrorMessage(RegisterActivity.this, "Could not connect to the internet");
            }
        } catch (Exception ex) {
            //lyt_progress_reg.setVisibility(View.GONE);
            mRegProgressDialog.dismiss();
            Utility.showErrorMessage(RegisterActivity.this, ex.getMessage());
        }
    }

    private void UpdateUser(String firebaseUserUid) {
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
                        .addMultipartParameter("Firebaseuserid", firebaseUser.getUid())
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


                                */
/*if (isOtp == 1){
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
                                }*//*


                                updateUserOnFirebase(user.getUserid(),firebaseUserUid);

                                mRegProgressDialog.dismiss();
                            }

                            @Override
                            public void onError(ANError error) {
                                Log.e(TAG, "onError: ", error);
                                // lyt_progress_reg.setVisibility(View.GONE);
                                mRegProgressDialog.dismiss();
                                Utility.showErrorMessage(RegisterActivity.this, "Server Error", Snackbar.LENGTH_SHORT);

                            }
                        });
            } catch (Exception exc) {
                Log.e("getMessage", exc.getMessage());
                //  Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Utility.showErrorMessage(RegisterActivity.this, "Could not connect to the internet");
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

                        mRegProgressDialog.dismiss();

                        SessionManager sessionManager = new SessionManager(RegisterActivity.this);
                        sessionManager.createLoginSession(user);
                        //mRegProgressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Register Successfully..", Toast.LENGTH_LONG).show();
                        //Intent intent=new Intent(RegisterActivity.this, HomeActivity.class);
                        Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        mRegProgressDialog.dismiss();

                        Toast.makeText(RegisterActivity.this, "something went wrong ", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            default:
                break;

            case R.id.btn_createAccount:


                String username= et_name.getText().toString();

                String email=et_email.getText().toString();

                String password=et_password.getText().toString();

                if(TextUtils.isEmpty(username)){
                    Toast.makeText(RegisterActivity.this, "Enter your name", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterActivity.this, "Enter your email id", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                }

                else if(password.length() < 6){
                    Toast.makeText(RegisterActivity.this, "password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                }
                else {
                    mRegProgressDialog.setTitle("Registering User");
                    mRegProgressDialog.setMessage("Please Wait while we create your account !");
                    mRegProgressDialog.setCanceledOnTouchOutside(false);
                    mRegProgressDialog.show();
                    register(username,email,password);
                }

                break;
            case R.id.txt_alreadyHaveAnAcct:

                Utility.launchActivity(RegisterActivity.this,LoginActivity.class,false);

                break;

            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

}
*/
