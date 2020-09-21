package com.teammandroid.chatnow.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.SubtitleCollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.adapters.MediaListHorizontalRecyclerAdapter;
import com.teammandroid.chatnow.adapters.MediaListRecyclerAdapter;
import com.teammandroid.chatnow.models.NotificationModel;
import com.teammandroid.chatnow.models.OfflineUserModel;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseMsgModel;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;
import com.teammandroid.chatnow.offline.NotificationDatabaseHelper;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatProfileDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ChatProfileDetailsActivity.class.getSimpleName();
    ImageView iv_profile;
    RecyclerView rv_media;
    ImageView iv_moreMedia;
    Switch switchNotification;
    TextView tv_customNotification;
    TextView tv_mediaCount;
    TextView tv_mediaVisibility;
    TextView tv_about;
    RelativeLayout rl_encryption;
    LinearLayout ll_block;
    LinearLayout ll_report;
    SubtitleCollapsingToolbarLayout subtitlecollapsingtoolbarlayout;
    private Activity activity;
    private ProgressDialog dialog;
    private UserModel currentUser;
    private Bundle bundle;
    FirebaseUserModel chattingPartner;
   // private ArrayList<NotificationModel> offlineMsglist;
    private MediaListHorizontalRecyclerAdapter adapter;

    private ArrayList<FirebaseMsgModel> firebaseChatModel;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_profile_details);
        activity = ChatProfileDetailsActivity.this;
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        bindView();
        btnListener();

        SessionManager sessionManager = new SessionManager(activity);
        currentUser = sessionManager.getUserDetails();

        bundle = getIntent().getExtras();
        if (bundle != null) {
            chattingPartner = bundle.getParcelable("chattingPartner");

            Log.e(TAG, "onCreate:chattingPartner "+chattingPartner.toString());

            subtitlecollapsingtoolbarlayout.setTitle(chattingPartner.getUsername());
            tv_about.setText(chattingPartner.getAbout());
            String media = Constants.URL_USER_PROFILE_PIC + chattingPartner.getImageUrl();
            Log.e(TAG, "onCreate: media "+media );
            Picasso.get()
                    .load(media)
                    .into(iv_profile, new Callback() {
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
                                    .into(iv_profile);
                        }
                    });

            GetMediaFiles();
        }else {
            Toast.makeText(activity,"Something went wrong",Toast.LENGTH_SHORT).show();
        }
    }

    private void bindView() {
        iv_profile  = findViewById(R.id.iv_profile);
        rv_media  = findViewById(R.id.rv_media);
        iv_moreMedia  = findViewById(R.id.iv_moreMedia);
        switchNotification  = findViewById(R.id.switchNotification);
        tv_customNotification  = findViewById(R.id.tv_customNotification);
        tv_mediaVisibility  = findViewById(R.id.tv_mediaVisibility);
        rl_encryption  = findViewById(R.id.rl_encryption);
        ll_block  = findViewById(R.id.ll_block);
        ll_report  = findViewById(R.id.ll_report);
        tv_mediaCount  = findViewById(R.id.tv_mediaCount);
        tv_about  = findViewById(R.id.tv_about);
        subtitlecollapsingtoolbarlayout  = findViewById(R.id.subtitlecollapsingtoolbarlayout);
        dialog = new ProgressDialog(activity);
    }

    private void btnListener() {
        iv_moreMedia.setOnClickListener(this);
        tv_mediaCount.setOnClickListener(this);
        ll_block.setOnClickListener(this);
        ll_report.setOnClickListener(this);
        iv_profile.setOnClickListener(this);
    }

    private void GetMediaFiles() {

        dialog.show();
        dialog.setMessage("Loading Page, please wait.");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        try {

            final String[] clearDate = {""};

            final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                    .child(firebaseUser.getUid())
                    .child(String.valueOf(chattingPartner.getOnlineUserId()));

            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        clearDate[0] = dataSnapshot.child("clerchatdate").getValue().toString();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            int userId = currentUser.getUserid();
            int PartnerId   = Integer.parseInt(chattingPartner.getOnlineUserId());

            firebaseChatModel=new ArrayList<>();
            DatabaseReference dref=FirebaseDatabase.getInstance().getReference("Chats");
            dref.keepSynced(true);
            dref.orderByChild("msgTime").limitToLast(100)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            firebaseChatModel.clear();
                            for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                                try {
                                    FirebaseMsgModel chat=snapshot.getValue(FirebaseMsgModel.class);
                                    if (chat.getReceiverid() == PartnerId && chat.getSenderid() == userId ||
                                            chat.getReceiverid() == userId && chat.getSenderid() == PartnerId ){

                                        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");

                                        if (!clearDate[0].equals("")){
                                            Date date1 = dateTime.parse(clearDate[0].toString());
                                            Date date2 = dateTime.parse(chat.getMsgtime());
                                            if (date1.compareTo(date2)<0){

                                                if (chat.getMediatype()==1 ||
                                                        chat.getMediatype() == 2 ||
                                                        chat.getMediatype() == 4 ) {

                                                    firebaseChatModel.add(chat);
                                                }
                                            }
                                        }else {
                                            firebaseChatModel.add(chat);
                                        }

                                    }
                                }catch (Exception e){
                                    Log.e(TAG, "onDataChange: e "+e.getMessage() );
                                }
                            }
                            BindAnnoucements(firebaseChatModel);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        }catch (Exception e){
            dialog.dismiss();
            Log.e(TAG, "GetMediaFiles: "+e.getMessage() );
            Toast.makeText(activity,"Something went wrong !",Toast.LENGTH_SHORT).show();
        }
    }


    private void BindAnnoucements(final ArrayList<FirebaseMsgModel> arraylist) {
        try {

            tv_mediaCount.setText(String.valueOf(arraylist.size()));
            rv_media.setLayoutManager(new LinearLayoutManager(activity,RecyclerView.HORIZONTAL,false));
            rv_media.setItemAnimator(new DefaultItemAnimator());
            rv_media.setHasFixedSize(true);

            dialog.dismiss();
            adapter = new MediaListHorizontalRecyclerAdapter(activity, arraylist, new MediaListHorizontalRecyclerAdapter.ItemClickListener() {
                @Override
                public void onClick(View v, int position) {
                    Log.e(TAG, "onClick: "+arraylist.get(position) );
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("chattingPartner", chattingPartner);
                    Utility.launchActivity(activity, MediaListActivity.class, false, bundle);
                }
            });
            rv_media.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (Exception ex) {
            Utility.showErrorMessage(activity, ex.getMessage(), Snackbar.LENGTH_LONG);
        }
    }


    @Override
    public void onClick(View v) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("chattingPartner", chattingPartner);

        switch (v.getId()){
            case R.id.iv_moreMedia:
                Utility.launchActivity(activity, MediaListActivity.class, false, bundle);
                break;

            case R.id.tv_mediaCount:
                Utility.launchActivity(activity, MediaListActivity.class, false, bundle);
                break;

            case R.id.ll_block:
                break;

            case R.id.ll_report:
                break;

            case R.id.iv_profile:
                Bundle bundle1 = new Bundle();
                bundle1.putParcelable("offlineChattingPartner",chattingPartner);
                Utility.launchActivity(activity,ProfilePicDetailsActivity.class,false,bundle1);
                break;
        }
    }
}
