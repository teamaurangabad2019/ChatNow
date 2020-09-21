package com.teammandroid.chatnow.activities.group_preview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.adapters.ContactRecyclerAdapter;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;
import com.teammandroid.chatnow.models.firebase.GroupChatList;
import com.teammandroid.chatnow.models.firebase.ParticipantsModel;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.SessionManager;
import com.wafflecopter.multicontactpicker.ContactResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GrpContactPreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = GrpContactPreviewActivity.class.getSimpleName();
    Activity activity;
    private UserModel currentUser;
    Bundle bundle;
 //   private FirebaseUserModel offchattingPartner;
    ArrayList<ContactResult> contacts = new ArrayList<>();
    private int mediaType;

    ImageView viewMenuIconBack;
    TextView tv_toolbar;
    RecyclerView rv_contacts;
    FloatingActionButton fab_send;
    private ContactRecyclerAdapter contactRecyclerAdapter;
    private int isSent;
    private FirebaseUser firebaseUser;
    private String myGroupId;
    private GroupChatList groupChatModel;
    private ArrayList<ParticipantsModel> participantsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_preview);

        activity = GrpContactPreviewActivity.this;
        bindView();
        btnListener();

        SessionManager sessionManager = new SessionManager(activity);
        currentUser = sessionManager.getUserDetails();

        bundle = getIntent().getExtras();
        if (bundle != null) {
            groupChatModel = bundle.getParcelable("groupChatModel");
            myGroupId = bundle.getString("myGroupId");
            contacts = bundle.getParcelableArrayList("contacts");
            mediaType = bundle.getInt("mediaType");
            isSent = bundle.getInt("isSent");
            BindContactList();
            loadParticipants();
        } else {
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void BindContactList() {
        rv_contacts.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        rv_contacts.setLayoutManager(mLayoutManager);
        rv_contacts.setItemAnimator(new DefaultItemAnimator());
        rv_contacts.addItemDecoration(new DividerItemDecoration(activity,
                DividerItemDecoration.VERTICAL));
        contactRecyclerAdapter=new ContactRecyclerAdapter(activity, contacts,isSent, new ContactRecyclerAdapter.ItemClickListener() {
            @Override
            public void onAddItemClick(View view, int position) {

            }
        });
        rv_contacts.setAdapter(contactRecyclerAdapter);
    }

    private void btnListener() {
        viewMenuIconBack.setOnClickListener(this);
        fab_send.setOnClickListener(this);
    }

    private void bindView() {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        viewMenuIconBack = findViewById(R.id.viewMenuIconBack);
        tv_toolbar = findViewById(R.id.tv_toolbar);
        rv_contacts = findViewById(R.id.rv_contacts);
        fab_send = findViewById(R.id.fab_send);

        if (isSent == 1){
            fab_send.setVisibility(View.GONE);
        }else {
            fab_send.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_send:
                sendNotification(contacts);
                break;

            case R.id.viewMenuIconBack:
                onBackPressed();
                break;
        }
    }

    private void sendNotification(ArrayList<ContactResult> contacts) {

        int mediatype = 8;
        int isDownloaded = 0;
        String msgString = "";
        if (contacts.size()<=1){
            msgString = contacts.get(0).getDisplayName();
        }else {
            msgString =  contacts.get(0).getDisplayName()+" and "+(contacts.size()-1)+"  More";
        }

        String SenderId = firebaseUser.getUid();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        String msgTime=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30:20 PM
        String msgDate=date.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30:20 PM
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",SenderId);
        hashMap.put("message",msgString);
        hashMap.put("msgTime",msgTime);// message time
        hashMap.put("msgDate",msgDate);
        //to generate unique random message uid
        // Get the size n
        int n = 10;
        String msgUid=SenderId+getAlphaNumericString(n);
        //String msgUid=firebaseAuth.getUid()+getAlphaNumericString(n);
        //String msgUid=String.valueOf(currentUser.getUserid())+getAlphaNumericString(n);
        Log.e("msgUid",""+msgUid);
        hashMap.put("msgUid",msgUid);
        hashMap.put("msgType","text");
        hashMap.put("isread",0);
        hashMap.put("isdownloaded",1);
        hashMap.put("mediatype",mediatype);
        hashMap.put("type",1);
        hashMap.put("replyToMsgId","-1");
        hashMap.put("replyToMsgSenderId","");
        hashMap.put("replyToMsgText","");
        hashMap.put("replyToMsgTextMediaType",-1);
        hashMap.put("isForward",0);
        hashMap.put("msgStatus","sent");
        hashMap.put("senderName", currentUser.getFullname());
        hashMap.put("contacts",contacts);
        //hashMap.put("senderName", firebaseUser.getDisplayName());
        hashMap.put("isDeleted", 0);

               //hashMap.put("type",0);
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(myGroupId).child("Messages").child(msgUid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //message sent
                        Toast.makeText(activity, "Message Sent Successfully..", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //message sending failed
                        Toast.makeText(activity, "message sending failed", Toast.LENGTH_SHORT).show();
                    }
                });

//        databaseReference.child("Chats").push().setValue(hashMap);

        if (participantsList != null) {
            for (ParticipantsModel participant : participantsList) {
                String to = participant.getToken();
                Log.e(TAG, "onResponse to: " + to);

                JSONObject notification = new JSONObject();
                JSONObject notifcationBody = new JSONObject();

                try {
                    notifcationBody.put("isFromGroup", 1);
                    notifcationBody.put("title", currentUser.getFullname());
                    notifcationBody.put("message", msgString);
                    notifcationBody.put("senderid", currentUser.getUserid());
                    notifcationBody.put("mediatype", mediatype);
                    notifcationBody.put("msgUid", msgUid);
                    notifcationBody.put("created", dateTime);
                    notifcationBody.put("replyToMsgId", -1);
                    notifcationBody.put("replyToMsgSenderId", -1);
                    notifcationBody.put("replyToMsgText", " ");
                    notifcationBody.put("replyToMsgTextMediaType", -1);
                    notification.put("to", to);
                    notification.put("data", notifcationBody);

                } catch (JSONException e) {
                    Log.e(TAG, "onCreate: " + e.getMessage());
                }

                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", Constants.GET_SERVER_KEY);
                    params.put("Content-Type", "application/json");

                    final int finalMediatype = mediatype;
                    Log.e(TAG, "sendNotification: mediatype" + mediatype);
                    final int finalIsDownloaded = isDownloaded;
                    AndroidNetworking.post(Constants.FCM_API)
                            .addJSONObjectBody(notification)
                            .addHeaders(params)
                            .setTag("test")
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {

                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.e("notification:anError", "" + anError);
                                    Log.e("notification:anError", "" + anError.getErrorBody());
                                }

                            });
                } catch (Exception ex) {
                    Log.e("onUnknownError", "" + ex);
                }

            }
        }

        Intent intent1 = new Intent();
        setResult(2, intent1);
        finish();
    }

    static String getAlphaNumericString(int n)
    {
        // chose a Character random from this String    "baQfVCfUSzS0XHNUPtGtqWmlPFt1jmyhkh3"
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    private void loadParticipants() {

        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Groups");
        dref.child(myGroupId).child("Participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //get uid from Group > Participants
                    ParticipantsModel participantsModel = snapshot.getValue(ParticipantsModel.class);
                    if (!participantsModel.getUid().equals(firebaseUser.getUid())) {
                        participantsList.add(participantsModel);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
