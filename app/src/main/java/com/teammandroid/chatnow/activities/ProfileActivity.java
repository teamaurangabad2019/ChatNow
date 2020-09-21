package com.teammandroid.chatnow.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.error.ANError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;
import com.teammandroid.chatnow.models.Response;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.network.UserServices;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ProfileActivity.class.getSimpleName();
    ImageView iv_backprofile;
    FloatingActionButton fab_edit_profile;
    TextView tv_name,tv_mobile,tv_email,tv_address,tv_setting,tv_aboutus,tv_logout;

    CircleImageView iv_male_avatar;

    Activity activity;
    private int REQUEST_FORM = 1;
    LinearLayout lyt_progress_reg;
    private UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        activity = ProfileActivity.this;

        SessionManager sessionManager = new SessionManager(ProfileActivity.this);
        user = sessionManager.getUserDetails();
        bindView();
        listener();

        GetUser(user.getUserid());
       // bindValues();

    }
    private void bindValues() {

        String profile = Constants.URL_USER_PROFILE_PIC+user.getProfilepic();
        Log.e(TAG, "bindValues: profile "+profile );
        Picasso.get().load(profile).placeholder(R.drawable.male_avatar).into(iv_male_avatar );

        tv_name.setText(user.getFullname());
        tv_mobile.setText(user.getMobile());
        tv_email.setText(user.getEmail());
        tv_address.setText(user.getAddress());

    }

    private void bindView() {
        fab_edit_profile= findViewById(R.id.fab_edit_profile);
        tv_name= findViewById(R.id.tv_name);
        tv_mobile= findViewById(R.id.tv_mobile);
        tv_email= findViewById(R.id.tv_email);
        tv_address= findViewById(R.id.tv_address);
        tv_setting= findViewById(R.id.tv_setting);
        tv_aboutus= findViewById(R.id.tv_aboutus);
        tv_logout= findViewById(R.id.tv_logout);
        iv_backprofile= findViewById(R.id.iv_backprofile);
        lyt_progress_reg = findViewById(R.id.lyt_progress_reg);
        iv_male_avatar = findViewById(R.id.iv_male_avatar);

    }

    private void listener() {
        fab_edit_profile.setOnClickListener(this);
        tv_setting.setOnClickListener(this);
        tv_aboutus.setOnClickListener(this);
        tv_logout.setOnClickListener(this);

        tv_logout.setVisibility(View.GONE);

        iv_backprofile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fab_edit_profile:
                Utility.launchActivityForResult(activity, ProfileEditActivity.class,REQUEST_FORM);
                break;

            case R.id.tv_setting:

                break;

            case R.id.tv_aboutus:

                break;

            case R.id.tv_logout:
                SessionManager sessionManager=new SessionManager(activity);
                sessionManager.logoutUser();
                activity.finish();
                break;

            case R.id.iv_backprofile:
                onBackPressed();
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == REQUEST_FORM) {
                Response response=data.getParcelableExtra("response");
                if (response!= null){
                    Utility.showErrorMessage(activity, response.getMessage());
                    GetUser(user.getUserid());
                }
            }
        }catch (Exception e){
            Log.e(TAG, "onActivityResult: e "+e.getMessage() );
        }

    }


    private void GetUser(int UserId) {
        try {
            if (Utility.isNetworkAvailable(getApplicationContext())) {

                lyt_progress_reg.setVisibility(View.VISIBLE);
                lyt_progress_reg.setAlpha(1.0f);

                Log.e(TAG, "GetUser: " + UserId);
                UserServices.getInstance(getApplicationContext()).GetUserDetails(UserId, new ApiStatusCallBack<ArrayList<UserModel>>() {
                    @Override
                    public void onSuccess(ArrayList<UserModel> response) {

                        lyt_progress_reg.setVisibility(View.GONE);
                        user = response.get(0);

                            //PrefHandler.setUserInSharedPref(RegistrationActivity.this,user);
                            SessionManager sessionManager = new SessionManager(ProfileActivity.this);
                            sessionManager.createLoginSession(user);

                            bindValues();
                            Log.e(TAG, "isNew: " + user.getUserid());
                    }

                    @Override
                    public void onError(ANError anError) {
                        lyt_progress_reg.setVisibility(View.GONE);
                        Utility.showErrorMessage(ProfileActivity.this, "Invalid Credentials");
                    }

                    @Override
                    public void onUnknownError(Exception e) {
                        lyt_progress_reg.setVisibility(View.GONE);
                        Utility.showErrorMessage(ProfileActivity.this, e.getMessage());
                    }
                });
            } else {
                Utility.showErrorMessage(ProfileActivity.this, "Could not connect to the internet");
            }
        } catch (Exception ex) {
            lyt_progress_reg.setVisibility(View.GONE);
            Utility.showErrorMessage(ProfileActivity.this, ex.getMessage());
        }
    }

}
