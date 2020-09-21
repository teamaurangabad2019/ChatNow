package com.teammandroid.chatnow.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;
import com.teammandroid.chatnow.models.Response;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;
import com.teammandroid.chatnow.network.UserServices;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = LoginActivity.class.getSimpleName();
    private TextInputEditText edt_email;
    private TextInputEditText edt_password;
    private Button btn_login;
    ImageView iv_back;
    String  token;
    TextView forgot_password,txt_notHaveAnAcct;
    //private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ProgressDialog mLoginProgressDialog;
    private UserModel userHolder;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userHolder = getIntent().getParcelableExtra("user");
        // token = getIntent().getStringExtra("token");
        token = userHolder.getToken();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        bindView();
        btnListener();
    }

    private void bindView() {
        edt_email=findViewById(R.id.edt_email);
        edt_password=findViewById(R.id.edt_password);
        btn_login=findViewById(R.id.btn_login);
        forgot_password=findViewById(R.id.forgot_password);
        txt_notHaveAnAcct=findViewById(R.id.txt_notHaveAnAcct);
        iv_back=findViewById(R.id.iv_back);
        //progressBar = findViewById(R.id.progressBar);
        mLoginProgressDialog=new ProgressDialog(this);
    }

    private void btnListener() {
        btn_login.setOnClickListener(this);
        forgot_password.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        txt_notHaveAnAcct.setOnClickListener(this);
    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    mLoginProgressDialog.dismiss();
                    firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                    reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                if (snapshot.exists()) {
                                    FirebaseUserModel user=dataSnapshot.getValue(FirebaseUserModel.class);
                                    //saveEmailUser(user);
                                }
                            }
                            // GetUser(Integer.parseInt(user.getOnlineUserId()));

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    UpdateUser(userHolder);
                }
                else {
                    mLoginProgressDialog.hide();
                    Toast.makeText(LoginActivity.this, "incorrect email or password", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            default:
                break;
            case R.id.btn_login:

                String email=edt_email.getText().toString();
                String password=edt_password.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    mLoginProgressDialog.setTitle("Logging In");
                    mLoginProgressDialog.setMessage("Please wait while we check your login credentials");
                    mLoginProgressDialog.setCanceledOnTouchOutside(false);
                    mLoginProgressDialog.show();
                    loginUser(email,password);
                }

                break;
            case R.id.forgot_password:

                //startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));

                break;
            case R.id.txt_notHaveAnAcct:
                Bundle bundle = new Bundle();
                bundle.putParcelable("user",userHolder);
                bundle.putString("token",token);
                Utility.launchActivity(LoginActivity.this,RegisterActivity.class,false,bundle);
                break;

            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

    private void UpdateUser(UserModel user) {
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
                                mLoginProgressDialog.dismiss();
                                SessionManager sessionManager = new SessionManager(LoginActivity.this);
                                sessionManager.createLoginSession(LoginActivity.this.userHolder);
                                Log.e(TAG, "onSuccess in Update: " + LoginActivity.this.userHolder.getUserid());
                                Utility.launchActivity(LoginActivity.this, HomeActivity.class, true);
                            }

                            @Override
                            public void onError(ANError error) {
                                Log.e(TAG, "onError: ", error);
                                // lyt_progress_reg.setVisibility(View.GONE);
                                mLoginProgressDialog.dismiss();
                                Utility.showErrorMessage(LoginActivity.this, "Server Error", Snackbar.LENGTH_SHORT);

                            }
                        });
            } catch (Exception exc) {
                Log.e("getMessage", exc.getMessage());
                //  Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Utility.showErrorMessage(LoginActivity.this, "Could not connect to the internet");
        }
    }

}
