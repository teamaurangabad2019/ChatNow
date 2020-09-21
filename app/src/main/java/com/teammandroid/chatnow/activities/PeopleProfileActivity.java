package com.teammandroid.chatnow.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.error.ANError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;
import com.teammandroid.chatnow.models.OfflineUserModel;
import com.teammandroid.chatnow.models.Response;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.network.UserServices;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PeopleProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = PeopleProfileActivity.class.getSimpleName();
    ImageView iv_backprofile;
    FloatingActionButton fab_chat;
    TextView tv_name,tv_mobile,tv_email,tv_address;

    CircleImageView iv_male_avatar;

    Activity activity;
    private int REQUEST_FORM = 1;

    LinearLayout lyt_progress_reg;

    private UserModel user;
    private UserModel chattingPartner;
    private Bundle bundle;

    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_profile);
        activity = PeopleProfileActivity.this;

        SessionManager sessionManager = new SessionManager(PeopleProfileActivity.this);
        user = sessionManager.getUserDetails();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        bindView();
        listener();

        bundle = getIntent().getExtras();
        if (bundle != null) {
            chattingPartner = bundle.getParcelable("chattingPartner");
            Log.e(TAG, "onCreate:chattingPartner "+chattingPartner.toString());
            Log.e(TAG, "onCreate:user "+user.toString());
        }

      //  GetUser(user.getUserid());
        bindValues();
    }
    private void bindValues() {

        //String profile = Constants.URL_USER_PROFILE_PIC+chattingPartner.getProfilepic();
        //Log.e(TAG, "bindValues: profile "+profile );
        //Picasso.get().load(profile).placeholder(R.drawable.male_avatar).into(iv_male_avatar );


        isBlockedOrNot(chattingPartner,firebaseUser.getUid(),iv_male_avatar);


        tv_name.setText(chattingPartner.getFullname());
        tv_mobile.setText(chattingPartner.getMobile());
        tv_email.setText(chattingPartner.getEmail());
        tv_address.setText(chattingPartner.getAddress());
    }

    public void isBlockedOrNot(UserModel chattingPartnerModel, String currentUserId,ImageView imageView){

        //first check if sender(current user) is blocked by receiver or not
        //logic: if uid of the sender(current user) exists in 'BlockedUsers' of receiver then sender(current user) is blocked,otherwise not
        //if blocked then just display a message e.g. You're blocked by that user,can't send message
        //if not blocked then simply start the chat activity


        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(chattingPartnerModel.getFirebaseuserid()).child("BlockedUsers").orderByChild("uid").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if (snapshot.exists()){
                                //block,don't show profile pic

                                //Toast.makeText(activity, "You're blocked by this user,you can't send message", Toast.LENGTH_SHORT).show();

                                imageView.setImageResource(R.drawable.ic_male);

                                Log.e("ok","ok");


                                return;
                            }


                        }

                        String media = Constants.URL_USER_PROFILE_PIC + chattingPartnerModel.getProfilepic();

                        Picasso.get().load(media).placeholder(R.drawable.ic_male).into(imageView);

                        Log.e("ok","notok");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    private void bindView() {
        fab_chat= findViewById(R.id.fab_chat);
        tv_name= findViewById(R.id.tv_name);
        tv_mobile= findViewById(R.id.tv_mobile);
        tv_email= findViewById(R.id.tv_email);
        tv_address= findViewById(R.id.tv_address);
     /*   tv_setting= findViewById(R.id.tv_setting);
        tv_aboutus= findViewById(R.id.tv_aboutus);
        tv_logout= findViewById(R.id.tv_logout);*/
        iv_backprofile= findViewById(R.id.iv_backprofile);
        lyt_progress_reg = findViewById(R.id.lyt_progress_reg);
        iv_male_avatar = findViewById(R.id.iv_male_avatar);
    }

    private void listener() {
        fab_chat.setOnClickListener(this);
       /* tv_setting.setOnClickListener(this);
        tv_aboutus.setOnClickListener(this);
        tv_logout.setOnClickListener(this);*/
        iv_backprofile.setOnClickListener(this);
        iv_male_avatar.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fab_chat:
                Bundle bundle = new Bundle();
                //bundle.putParcelable("chattingPartner", chattingPartner);
                bundle.putInt("partnerId",chattingPartner.getUserid());
                bundle.putString("firebasePartnerId",chattingPartner.getFirebaseuserid());
                Utility.launchActivity(PeopleProfileActivity.this, ChattingActivity.class, false, bundle);                break;

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

            case R.id.iv_male_avatar:
                Bundle bundle1 = new Bundle();
                bundle1.putParcelable("onlineChattingPartner",chattingPartner);
                Utility.launchActivity(activity,ProfilePicDetailsActivity.class,false,bundle1);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FORM) {
            Response response=data.getParcelableExtra("response");
            if (response!= null){
                Utility.showErrorMessage(activity, response.getMessage());
                GetUser(user.getUserid());
            }
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
                            SessionManager sessionManager = new SessionManager(PeopleProfileActivity.this);
                            sessionManager.createLoginSession(user);

                            bindValues();
                            Log.e(TAG, "isNew: " + user.getUserid());
                    }

                    @Override
                    public void onError(ANError anError) {
                        lyt_progress_reg.setVisibility(View.GONE);
                        Utility.showErrorMessage(PeopleProfileActivity.this, "Invalid Credentials");
                    }

                    @Override
                    public void onUnknownError(Exception e) {
                        lyt_progress_reg.setVisibility(View.GONE);
                        Utility.showErrorMessage(PeopleProfileActivity.this, e.getMessage());
                    }
                });
            } else {
                Utility.showErrorMessage(PeopleProfileActivity.this, "Could not connect to the internet");
            }
        } catch (Exception ex) {
            lyt_progress_reg.setVisibility(View.GONE);
            Utility.showErrorMessage(PeopleProfileActivity.this, ex.getMessage());
        }
    }

}
