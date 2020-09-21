package com.teammandroid.chatnow.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
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

public class OtherLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = OtherLoginActivity.class.getSimpleName();
    ImageView iv_emailPassword, iv_google, iv_facebook;
    FrameLayout rl_facebook;
    LoginButton btn_fbLogin;
    FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    CallbackManager callbackManager;
    private ProgressDialog dialog;
    private GoogleSignInClient googleSignInClient;
    private String token;
    private Activity activity;
    private int RESULT_CODE_SINGIN=999;
    private DatabaseReference reference;
    private String mobileNumber;
    private UserModel user;
    private String email;
    PrefManager prefManager;
    private int serveruserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_login);
        activity = OtherLoginActivity.this;

        user = getIntent().getParcelableExtra("user");
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());

        bindview();
        getTokan();

        listeners();
        btn_fbLogin.setReadPermissions("email", "public_profile", "user_friends");
        callbackManager= CallbackManager.Factory.create();
        btn_fbLogin.setPermissions("email", "public_profile");
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

    private void listeners() {
        iv_google.setOnClickListener(this);
        iv_emailPassword.setOnClickListener(this);
        rl_facebook.setOnClickListener(this);
        btn_fbLogin.setOnClickListener(this);
        iv_facebook.setOnClickListener(this);
        rl_facebook.setOnClickListener(this);
    }

    private void bindview() {
        btn_fbLogin = findViewById(R.id.btn_fbLogin);
        rl_facebook = findViewById(R.id.rl_facebook);
        iv_facebook = findViewById(R.id.iv_facebook);
        iv_google = findViewById(R.id.iv_google);
        iv_emailPassword = findViewById(R.id.iv_emailPassword);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading page, please wait.");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prefManager=new PrefManager(activity);
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

    private void signInM() {
        Intent singInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(singInIntent,RESULT_CODE_SINGIN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_facebook:
                Log.e(TAG, "onClick: fbrl" );
                dialog.show();
                btn_fbLogin.performClick();
                break;

            case R.id.iv_emailPassword:
                Bundle bundle = new Bundle();
                user.setToken(token);
                bundle.putParcelable("user",user);
                Utility.launchActivity(activity, LoginActivity.class,true,bundle);
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
            String email = " ";
            if (firebaseUser.getEmail()!=null){
                email =firebaseUser.getEmail();
            }
            String photoUrl=firebaseUser.getPhotoUrl().toString();
            photoUrl=photoUrl+"?type=large";
            user.setEmail(email);
            user.setFullname(username);

            Log.e(TAG, "saveFaceBookUser: fb_email "+email );
            hashMap.put("id",firebaseUserUid);
            hashMap.put("username",username);
            hashMap.put("email",email);
            //hashMap.put("password","***");
            hashMap.put("status","offline");
            //hashMap.put("status","Hi there,i am using Chatnow app");
            hashMap.put("typingTo","noOne");//for typing status
            hashMap.put("imageUrl",user.getProfilepic());
            hashMap.put("search",username.toLowerCase());
            hashMap.put("onlineUserId", String.valueOf(user.getUserid()));
            Log.d(TAG,"AuthDetails"+username+" "+email+" "+photoUrl);
            hashMap.put("address",user.getAddress());
            hashMap.put("about","Hey there! I am using Chatnow");
            hashMap.put("token",token);
            hashMap.put("mobile",user.getMobile());
            hashMap.put("createdOn",createdOn);

        }

        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    UpdateUser(firebaseUserUid,user);
                }
            }
        });
    }

    private void handleFacebookToken(AccessToken accessToken) {
        Log.d(TAG, "handleFacebookToken: "+accessToken);
        AuthCredential credential= FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    dialog.dismiss();
                    saveFaceBookUser();
                    Log.d(TAG, "sign in with creditial: successfull ");
                }
                else {
                    dialog.dismiss();
                    Log.d(TAG, "sign in with creditial: failure ",task.getException());
                    Toast.makeText(activity, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        //here we are checking the Authentication Credential and checking the task is successful or not and display the message
        //based on that.
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    saveGoogleUser();
                }
                else {
                    Toast.makeText(activity,"Failed!",Toast.LENGTH_LONG).show();
                    // UpdateUI(null);
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
            Toast.makeText(activity,"SignIn Failed!!!",Toast.LENGTH_LONG).show();
            //FirebaseGoogleAuth(null);
        }
    }

    private void saveGoogleUser() {

        Log.e(TAG, "saveGoogleUser: without userid" );
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

            user.setFullname(personName);
            user.setEmail(personEmail);

            hashMap.put("id",firebaseUserUid);
            hashMap.put("username",personName);
            hashMap.put("email",personEmail);
            hashMap.put("password","***");
            hashMap.put("status","offline");
            hashMap.put("onlineUserId", String.valueOf(user.getUserid()));
            //hashMap.put("status","Hi there,i am using Chatnow app");
            hashMap.put("typingTo","noOne");//for typing status
            hashMap.put("imageUrl",user.getProfilepic());
            hashMap.put("search",personName.toLowerCase());

            hashMap.put("token",token);
            hashMap.put("about","Hey there! I am using Chatnow");
            hashMap.put("createdOn",createdOn);
            hashMap.put("mobile",user.getMobile());
            hashMap.put("address",user.getAddress());

            //Toast.makeText(FirebaseAuthenticationActivity.this,personName + "  " + personEmail,Toast.LENGTH_LONG).show();

            Log.e("AuthDetails",""+personName+" "+personEmail+" "+personId+" "+personGivenName+" "+photoUrl);
        }

        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    //  CreateUserOnServer(firebaseUserUid);
                    UpdateUser(firebaseUserUid,user);
                    /*SessionManager sessionManager = new SessionManager(OTPLoginActivity.this);
                    sessionManager.createLoginSession(user);
                    //mRegProgressDialog.dismiss();
                    //Toast.makeText(FirebaseAuthenticationActivity.this, "Register Successfully..", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(OTPLoginActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    */
                }
            }
        });
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
                                SessionManager sessionManager = new SessionManager(activity);
                                sessionManager.createLoginSession(user);
                                //mRegProgressDialog.dismiss();
                                Toast.makeText(activity, "Register Successfully..", Toast.LENGTH_LONG).show();
                                Intent intent=new Intent(activity, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                                //  updateUserOnFirebase(user.getUserid(),firebaseUserUid);
                                dialog.dismiss();
                            }

                            @Override
                            public void onError(ANError error) {
                                Log.e(TAG, "onError: ", error);
                                // lyt_progress_reg.setVisibility(View.GONE);
                                dialog.dismiss();
                                Utility.showErrorMessage(activity, "Server Error", Snackbar.LENGTH_SHORT);

                            }
                        });
            } catch (Exception exc) {
                Log.e("getMessage", exc.getMessage());
                //  Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Utility.showErrorMessage(activity, "Could not connect to the internet");
        }
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