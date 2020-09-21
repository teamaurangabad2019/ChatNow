package com.teammandroid.chatnow.activities.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.DateFormatSymbols;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;
import com.teammandroid.chatnow.utils.Utility;

import org.joda.time.format.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class FirebaseChattingActivity extends AppCompatActivity implements View.OnClickListener {

    CircleImageView iv_cprofile_pic;
    TextView tv_cUsername,txt_status;
    ImageButton btn_sendMsg;
    EditText et_message;
    //LinearLayout
    //ImageView iv_more;
    FirebaseUser firebaseUser;

    DatabaseReference reference;

    FirebaseUserModel firebaseUserModel;

    Intent intent;

    String userId;

  //  FirebaseMessageAdapter firebaseMessageAdapter;
   // List<FirebaseChatModel> firebaseChatModel;
    RecyclerView rv_firebaseChat;

    ValueEventListener seenListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_firebase_chatting);

        bindView();

        btnListener();
        intent=getIntent();
        userId=intent.getStringExtra("userId");

        rv_firebaseChat.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rv_firebaseChat.setLayoutManager(linearLayoutManager);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        reference= FirebaseDatabase.getInstance().getReference("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                firebaseUserModel=dataSnapshot.getValue(FirebaseUserModel.class);
                //FirebaseUserModel firebaseUserModel=dataSnapshot.getValue(FirebaseUserModel.class);

                tv_cUsername.setText(firebaseUserModel.getUsername());

                if (firebaseUserModel.getImageUrl().equals("default")){

                    iv_cprofile_pic.setImageResource(R.drawable.ic_male);

                    //iv_cprofile_pic.setImageResource(R.drawable.male_avatar);
                    //iv_cprofile_pic.setImageResource(R.mipmap.ic_launcher_round);

                }
                else {
                    Glide.with(getApplicationContext()).load(firebaseUserModel.getImageUrl()).into(iv_cprofile_pic);
                    //Glide.with(FirebaseChattingActivity.this).load(firebaseUserModel.getImageUrl()).into(iv_cprofile_pic);
                }

                readMessages(firebaseUser.getUid(),userId);
                //readMessages(firebaseUser.getUid(),userId,firebaseUserModel.getImageUrl());

                //checks whether user is online or typing or offline
                if (firebaseUserModel.getTypingTo().equals(firebaseUser.getUid())){
                    txt_status.setText("typing..");
                }
                else {
                    //checks online status of user
                    if (firebaseUserModel.getStatus().equals("online")){
                        txt_status.setText("online");
                    }
                    else {

                        String lastSeenTime=firebaseUserModel.getStatus();
                        txt_status.setText("Last Seen at : "+lastSeenTime);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userId);


        // to check if user is typing or not
        et_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length()==0){
                    typingStatus("noOne");
                }
                else {
                    typingStatus(userId); //userId of receiver
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void btnListener() {
        btn_sendMsg.setOnClickListener(this);
    }

    private void bindView() {
        iv_cprofile_pic=findViewById(R.id.iv_cprofile_pic);
      /*  tv_cUsername=findViewById(R.id.tv_cUsername);

        et_message=findViewById(R.id.et_message);
        btn_sendMsg=findViewById(R.id.btn_sendMsg);

        txt_status=findViewById(R.id.txt_status);

        rv_firebaseChat=findViewById(R.id.rv_firebaseChat);*/
        //iv_more=findViewById(R.id.iv_more);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                break;

           /* case R.id.btn_sendMsg:

                String msg=et_message.getText().toString();

                if (!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),userId,msg);
                }
                else {
                    Toast.makeText(this,"You can't send empty message" , Toast.LENGTH_SHORT).show();
                }

                et_message.setText("");
                //Toast.makeText(this, et_message.getText().toString(), Toast.LENGTH_SHORT).show();

                break;*/

        }
    }

    public void seenMessage(String userId){


        String chatId=firebaseUser.getUid()+"_"+userId;

        //reference=FirebaseDatabase.getInstance().getReference("Chats").child(chatId);
        reference=FirebaseDatabase.getInstance().getReference("Chats");

        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                  //  FirebaseChatModel chatModel=snapshot.getValue(FirebaseChatModel.class);
                 //   if (chatModel.getReceiver().equals(firebaseUser.getUid()) && chatModel.getSender().equals(userId)){
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                   // }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void sendMessage(String sender,String receiver,String message){

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        // to get current date with time
        /*SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date obj = new Date();
        String msgTime=dateTime.format(obj); //prints current date with time : 03/05/2020 23:22:58
        */

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        //System.out.println( dateTime1.format(cal.getTime()) );//prints current date with time ex: 03/05/2020 11:30 PM

        String msgTime=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM

        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("msgTime",msgTime);// message time
        hashMap.put("isseen",false);

        //to generate unique random message uid
        // Get the size n
        int n = 7;
        String msgUid=firebaseUser.getUid()+getAlphaNumericString(n);
        Log.e("msgUid",""+msgUid);

        hashMap.put("msgUid",msgUid);

        /*String chatId=firebaseUser.getUid()+"_"+userId;

        hashMap.put("chatId",chatId); //chatId

        databaseReference.child("Chats").child(chatId).push().setValue(hashMap);
        */

        databaseReference.child("Chats").push().setValue(hashMap);

        // add user to chat fragment
        final DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(userId);


        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()){
                    chatRef.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // to add user to chat fragment
        final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(userId)
                .child(firebaseUser.getUid());
        chatRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef1.child("id").setValue(firebaseUser.getUid());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    //private void readMessages(final String myid, final String userid, final String imageUrl){
    private void readMessages(final String myid, final String userid){
     //   firebaseChatModel=new ArrayList<>();
        String chatId=myid+"_"+userid;
        //reference=FirebaseDatabase.getInstance().getReference("Chats").child(chatId);
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             //   firebaseChatModel.clear();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                   // FirebaseChatModel chat=snapshot.getValue(FirebaseChatModel.class);

                 /*   if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid) ){
                        firebaseChatModel.add(chat);
                    }
                    firebaseMessageAdapter=new FirebaseMessageAdapter(FirebaseChattingActivity.this,firebaseChatModel);
                    //firebaseMessageAdapter=new FirebaseMessageAdapter(FirebaseChattingActivity.this,firebaseChatModel,imageUrl);
                    rv_firebaseChat.setAdapter(firebaseMessageAdapter);
*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // to generate random alphanumeric string
    // function to generate a random string of length n
    static String getAlphaNumericString(int n)
    {
        // chose a Character random from this String
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

    public void status(String status){

        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("status",status);
        reference.updateChildren(hashMap);

    }

    public void typingStatus(String typing){

        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("typingTo",typing);
        reference.updateChildren(hashMap);

    }

    @Override
    protected void onStart() {
        status("online");
        super.onStart();
    }

    @Override
    protected void onPause() {

        super.onPause();


        reference.removeEventListener(seenListener);

        typingStatus("noOne");

        // to get current date with time
        SimpleDateFormat dform = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date obj = new Date();
        status(dform.format(obj));


        typingStatus("noOne");

    }

    @Override
    protected void onResume() {

        status("online");

        super.onResume();


    }

}




/*
public class FirebaseChattingActivity extends AppCompatActivity implements View.OnClickListener {

    CircleImageView iv_cprofile_pic;
    TextView tv_cUsername,txt_status;
    ImageButton btn_sendMsg;
    EditText et_message;

    //LinearLayout

    //ImageView iv_more;

    FirebaseUser firebaseUser;

    DatabaseReference reference;

    FirebaseUserModel firebaseUserModel;

    Intent intent;

    String userId;

    FirebaseMessageAdapter firebaseMessageAdapter;
    List<FirebaseChatModel> firebaseChatModel;
    RecyclerView rv_firebaseChat;

    ValueEventListener seenListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_chatting);

        bindView();

        btnListener();
        intent=getIntent();
        userId=intent.getStringExtra("userId");

        rv_firebaseChat.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rv_firebaseChat.setLayoutManager(linearLayoutManager);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        reference= FirebaseDatabase.getInstance().getReference("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                firebaseUserModel=dataSnapshot.getValue(FirebaseUserModel.class);
                //FirebaseUserModel firebaseUserModel=dataSnapshot.getValue(FirebaseUserModel.class);

                tv_cUsername.setText(firebaseUserModel.getUsername());

                if (firebaseUserModel.getImageUrl().equals("default")){

                    iv_cprofile_pic.setImageResource(R.drawable.ic_male);

                    //iv_cprofile_pic.setImageResource(R.drawable.male_avatar);
                    //iv_cprofile_pic.setImageResource(R.mipmap.ic_launcher_round);

                }
                else {
                    Glide.with(getApplicationContext()).load(firebaseUserModel.getImageUrl()).into(iv_cprofile_pic);
                    //Glide.with(FirebaseChattingActivity.this).load(firebaseUserModel.getImageUrl()).into(iv_cprofile_pic);
                }

                readMessages(firebaseUser.getUid(),userId);
                //readMessages(firebaseUser.getUid(),userId,firebaseUserModel.getImageUrl());

                //checks whether user is online or typing or offline
                if (firebaseUserModel.getTypingTo().equals(firebaseUser.getUid())){
                    txt_status.setText("typing..");
                }
                else {
                    //checks online status of user
                    if (firebaseUserModel.getStatus().equals("online")){
                        txt_status.setText("online");
                    }
                    else {

                        String lastSeenTime=firebaseUserModel.getStatus();
                        txt_status.setText("Last Seen at : "+lastSeenTime);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userId);


        // to check if user is typing or not
        et_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length()==0){
                    typingStatus("noOne");
                }
                else {
                    typingStatus(userId); //userId of receiver
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void btnListener() {
        btn_sendMsg.setOnClickListener(this);
    }

    private void bindView() {
        iv_cprofile_pic=findViewById(R.id.iv_cprofile_pic);
        tv_cUsername=findViewById(R.id.tv_cUsername);

        et_message=findViewById(R.id.et_message);
        btn_sendMsg=findViewById(R.id.btn_sendMsg);

        txt_status=findViewById(R.id.txt_status);

        rv_firebaseChat=findViewById(R.id.rv_firebaseChat);
        //iv_more=findViewById(R.id.iv_more);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                break;

            case R.id.btn_sendMsg:

                String msg=et_message.getText().toString();

                if (!msg.equals("")){
                  sendMessage(firebaseUser.getUid(),userId,msg);
                }
                else {
                    Toast.makeText(this,"You can't send empty message" , Toast.LENGTH_SHORT).show();
                }

                et_message.setText("");
                //Toast.makeText(this, et_message.getText().toString(), Toast.LENGTH_SHORT).show();

                break;

        }
    }

    public void seenMessage(String userId){

        reference=FirebaseDatabase.getInstance().getReference("Chats");

        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    FirebaseChatModel chatModel=snapshot.getValue(FirebaseChatModel.class);
                    if (chatModel.getReceiver().equals(firebaseUser.getUid()) && chatModel.getSender().equals(userId)){
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void sendMessage(String sender,String receiver,String message){

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();

        // to get current date with time
        */
/*SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date obj = new Date();
        String msgTime=dateTime.format(obj); //prints current date with time : 03/05/2020 23:22:58
        *//*


        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        //System.out.println( dateTime1.format(cal.getTime()) );//prints current date with time ex: 03/05/2020 11:30 PM

        String msgTime=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM

        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("msgTime",msgTime);// message time
        hashMap.put("isseen",false);

        //to generate unique random message uid
        // Get the size n
        int n = 7;
        String msgUid=firebaseUser.getUid()+getAlphaNumericString(n);
        Log.e("msgUid",""+msgUid);

        hashMap.put("msgUid",msgUid);

        databaseReference.child("Chats").push().setValue(hashMap);

        // add user to chat fragment
        final DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(userId);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()){
                    chatRef.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // to add user to chat fragment
        final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(userId)
                .child(firebaseUser.getUid());
        chatRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef1.child("id").setValue(firebaseUser.getUid());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    //private void readMessages(final String myid, final String userid, final String imageUrl){
    private void readMessages(final String myid, final String userid){

        firebaseChatModel=new ArrayList<>();

        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                firebaseChatModel.clear();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    FirebaseChatModel chat=snapshot.getValue(FirebaseChatModel.class);

                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid) ){

                        firebaseChatModel.add(chat);

                    }

                    firebaseMessageAdapter=new FirebaseMessageAdapter(FirebaseChattingActivity.this,firebaseChatModel);
                    //firebaseMessageAdapter=new FirebaseMessageAdapter(FirebaseChattingActivity.this,firebaseChatModel,imageUrl);
                    rv_firebaseChat.setAdapter(firebaseMessageAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // to generate random alphanumeric string
    // function to generate a random string of length n
    static String getAlphaNumericString(int n)
    {
        // chose a Character random from this String
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

    public void status(String status){

        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("status",status);
        reference.updateChildren(hashMap);

    }

    public void typingStatus(String typing){

        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("typingTo",typing);
        reference.updateChildren(hashMap);

    }

    @Override
    protected void onStart() {
        status("online");
        super.onStart();
    }

    @Override
    protected void onPause() {

        super.onPause();


        reference.removeEventListener(seenListener);

        typingStatus("noOne");

        // to get current date with time
        SimpleDateFormat dform = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date obj = new Date();
        status(dform.format(obj));


        typingStatus("noOne");

    }

    @Override
    protected void onResume() {

        status("online");

        super.onResume();


    }

}
*/
