package com.teammandroid.chatnow.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.models.OfflineUserModel;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.utils.Constants;

public class ProfilePicDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ProfilePicDetailsActivity.class.getSimpleName();
    ImageView viewMenuIconBack;
    TextView tv_name;
    ImageView iv_profilePic;
    Activity  activity;
    private Bundle bundle;
    OfflineUserModel offlineChattingPartner;
    UserModel onlineChattingPartner;
    ZoomageView myZoomageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pic_details);
        activity = ProfilePicDetailsActivity.this;

        bindView();
        btnListener();

        bundle = getIntent().getExtras();
        if (bundle != null) {
            offlineChattingPartner = bundle.getParcelable("offlineChattingPartner");
            onlineChattingPartner = bundle.getParcelable("onlineChattingPartner");

            if (offlineChattingPartner!=null){
                tv_name.setText(offlineChattingPartner.getFullname());
                String media = Constants.URL_USER_PROFILE_PIC + offlineChattingPartner.getProfilepic();
                Log.e(TAG, "onCreate: media "+media );
                Picasso.get()
                        .load(media)
                        .into(myZoomageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }
                            @Override
                            public void onError(Exception e) {
                                // Try again online if cache failed
                                Picasso.get()
                                        .load(media)
                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                        .placeholder(R.drawable.male_avatar)
                                        .error(R.drawable.male_avatar)
                                        .into(myZoomageView);
                            }
                        });
                //imageView.setImage(ImageSource.bitmap(bitmap));

            }else if (onlineChattingPartner!=null){
                String profile = Constants.URL_USER_PROFILE_PIC+onlineChattingPartner.getProfilepic();
                tv_name.setText(onlineChattingPartner.getFullname());
                 Log.e(TAG, "bindValues: profile "+profile );

                Picasso.get()
                        .load(profile)
                        .into(myZoomageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                // Try again online if cache failed
                                Picasso.get()
                                        .load(profile)
                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                        .placeholder(R.drawable.male_avatar)
                                        .error(R.drawable.male_avatar)
                                        .into(myZoomageView);
                            }
                        });
               // Picasso.get().load(profile).placeholder(R.drawable.male_avatar).into(myZoomageView);
            }

        }else {
            Toast.makeText(activity,"Something went wrong",Toast.LENGTH_SHORT).show();
        }

    }

    private void btnListener() {
        viewMenuIconBack.setOnClickListener(this);
    }

    private void bindView() {
        viewMenuIconBack = findViewById(R.id.viewMenuIconBack);
        tv_name = findViewById(R.id.tv_name);
        iv_profilePic = findViewById(R.id.iv_profilePic);
        myZoomageView = findViewById(R.id.myZoomageView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.viewMenuIconBack:
                onBackPressed();
                break;
        }
    }
}
