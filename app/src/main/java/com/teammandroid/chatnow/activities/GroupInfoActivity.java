package com.teammandroid.chatnow.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.activities.firebase.AddGroupParticipantActivity;
import com.teammandroid.chatnow.activities.firebase.GroupEditActivity;
import com.teammandroid.chatnow.adapters.GroupInfoRecyclerAdapter;
import com.teammandroid.chatnow.adapters.GroupMediaListHorizontalRecyclerAdapter;
import com.teammandroid.chatnow.adapters.firebase.AddParticipantAdapter;
import com.teammandroid.chatnow.adapters.firebase.GroupInfoAdapter;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseGroupChatModel;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;
import com.teammandroid.chatnow.models.firebase.GroupChatList;
import com.teammandroid.chatnow.models.firebase.ParticipantsModel;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GroupInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = GroupInfoActivity.class.getSimpleName() ;

    RecyclerView rv_groupMembers;
    ImageView iv_group_icon,iv_editGroupInfo;
    TextView tv_groupDescription,tv_createdBy,tv_group_name,tv_addParticipant,tv_leaveGroup,tv_participants;

    ParticipantsModel participantsModel;

    String groupId;

    String myGroupRole="";

    private FirebaseAuth firebaseAuth;

    FirebaseUser firebaseUser;

    GroupChatList groupChatList;

    FirebaseUserModel firebaseUserModel;
    UserModel currentUser;

    //GroupInfoRecyclerAdapter groupInfoRecyclerAdapter;
    //private ArrayList<TrainerModel> mModel;

    AddParticipantAdapter addParticipantAdapter;
    GroupInfoAdapter groupInfoAdapter;

    private ArrayList<FirebaseUserModel> userList;

    private ArrayList<UserModel> userModelList;

    GroupInfoRecyclerAdapter groupInfoRecyclerAdapter;


    Activity activity;

    Intent intent;

    Bundle bundle;

    ProgressDialog dialog;
    String chatClearDate;

    ArrayList<FirebaseGroupChatModel> groupChatModel = new ArrayList<>();

    RecyclerView rv_media;
    TextView tv_mediaCount;
    private GroupMediaListHorizontalRecyclerAdapter adapter;

    ImageView iv_moreMedia;

    LinearLayout ll_participant;

    TextView tv_participantCount;


    ArrayList<String> stringArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        activity = GroupInfoActivity.this;

        bindView();
        btnListener();


        firebaseAuth=FirebaseAuth.getInstance();

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();


        SessionManager sessionManager = new SessionManager(activity);
        currentUser = sessionManager.getUserDetails();

        bundle = getIntent().getExtras();

        try {

            if (bundle != null) {

                //groupChatList = bundle.getParcelable("groupChatModel");

                //groupId=groupChatList.getGroupId();

                groupId=bundle.getString("groupId");

                Log.e("groupInfo","groupInfo list"+groupId);

                GetMediaFiles();
            }
            else {
                Toast.makeText(activity,"Something went wrong",Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }




        /*
        intent=getIntent();
        groupId=intent.getStringExtra("groupId");
        Log.e("mygroupId",groupId);*/





        Log.e("muname","muname"+currentUser.getFullname()+currentUser.getFirebaseuserid()+currentUser.getUserid());

        loadGroupInfo();

        loadMyGroupRole();

        rv_groupMembers.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv_groupMembers.setLayoutManager(mLayoutManager);
        rv_groupMembers.setItemAnimator(new DefaultItemAnimator());

        rv_groupMembers.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

    }


    private void GetMediaFiles() {

        dialog.setMessage("Loading Page, please wait.");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        try {

            //final String[] clearDate = {""};

            //groupId=groupChatList.getGroupId();

            //Log.e("groupInfo","groupInfo list"+groupChatList.getGroupId());

            Log.e("groupInfo","groupInfo mo"+groupId);


            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
            reference.child(groupId).child("Participants")
                    .orderByChild("uid").equalTo(firebaseUser.getUid())
                    //.orderByChild("uid").equalTo(String.valueOf(currentUser.getUserid()))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                participantsModel = snapshot.getValue(ParticipantsModel.class);

                                Log.e("groupInfo","groupInfo 00");


                                //myGroupRole = "" + snapshot.child("role").getValue();

                                //chatClearDate=participantsModel.getClearDate();

                                Log.e("groupInfo","groupInfo 01"+participantsModel.getClearDate());

                                chatClearDate="" + snapshot.child("clearDate").getValue();

                                Log.e("groupInfo","groupInfo chatClearDate"+chatClearDate);


                                Log.e("groupInfo","groupInfo 02");

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });




            Log.e("groupMedia", "groupMedia 1");


            groupChatModel = new ArrayList<>();

            DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Groups");
            dref.keepSynced(true);

            dref.child(groupId).child("Messages").orderByChild("msgTime").limitToLast(100)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.e(TAG, "onDataChange: " + dataSnapshot.getValue());
                            groupChatModel.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                Log.e("groupMedia", "groupMedia 2");

                                FirebaseGroupChatModel model = snapshot.getValue(FirebaseGroupChatModel.class);
                                SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");

                                //Log.e(TAG, "onDataChange: date1 "+participantsModel.getClearDate() );
                                Log.e(TAG, "onDataChange: date1 "+chatClearDate);
                                Log.e(TAG, "onDataChange: date2 "+model.getMsgTime() );

                                try {
                                    //Date date1 = dateTime.parse(participantsModel.getClearDate());
                                    Date date1 = dateTime.parse(chatClearDate);
                                    Date date2 = dateTime.parse(model.getMsgTime());

                                    Log.e("groupMedia", "groupMedia 3");


                                    if (!(chatClearDate.equals(""))){
                                        if (date1.compareTo(date2)<0){

                                            Log.e("groupMedia", "groupMedia 4");

                                            if (model.getMediatype()==1 ||
                                                    model.getMediatype() == 2 ||
                                                    model.getMediatype() == 4 ) {

                                                Log.e("groupMedia", "groupMedia 5");

                                                groupChatModel.add(model);

                                                Log.e("groupMedia", "groupMedia frag media"+model.getMediatype());

                                            }
                                        }
                                    }
                                    else {
                                        groupChatModel.add(model);
                                    }

                                    Log.e("groupMedia", "groupMedia 6");

                                    //groupChatList.add(model);

                                } catch (Exception e) {
                                    Log.e(TAG, "onDataChange: edate " + e.getMessage());
                                }
                            }
                            BindAnnoucements(groupChatModel);
                            Log.e("groupMedia","groupMedia files"+groupChatModel.toString());
                            Log.e("groupMedia", "groupMedia 7");


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


    private void BindAnnoucements(final ArrayList<FirebaseGroupChatModel> arraylist) {
        try {

            tv_mediaCount.setText(String.valueOf(arraylist.size()));

            rv_media.setLayoutManager(new LinearLayoutManager(activity,RecyclerView.HORIZONTAL,false));
            rv_media.setItemAnimator(new DefaultItemAnimator());
            rv_media.setHasFixedSize(true);

            dialog.dismiss();
            adapter = new GroupMediaListHorizontalRecyclerAdapter(activity, arraylist, new GroupMediaListHorizontalRecyclerAdapter.ItemClickListener() {
                @Override
                public void onClick(View v, int position) {

                    //FirebaseGroupChatModel model = arraylist.get(position);

                    //Log.e(TAG, "onMessageClick: " + model.toString());

                    Bundle bundle = new Bundle();

                    bundle.putParcelable("groupChatModel", groupChatList);

                    Utility.launchActivity(activity, GroupMediaListActivity.class, false, bundle);
                }
            });
            rv_media.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (Exception ex) {
            Utility.showErrorMessage(activity, ex.getMessage(), Snackbar.LENGTH_LONG);
        }
    }

    private void loadGroupInfo() {

        try {
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        //get group info

                        groupChatList=dataSnapshot.getValue(GroupChatList.class);

                        Log.e("mygroupId",groupChatList.getGroupTitle());

                        tv_group_name.setText(groupChatList.getGroupTitle());
                        tv_groupDescription.setText(groupChatList.getGroupDescription());

                        String groupIcon=groupChatList.getGroupIcon();

                        if (groupIcon.equals("default")){
                            iv_group_icon.setImageResource(R.drawable.ic_male);
                        }else {
                            Picasso.get().load(groupIcon).placeholder(R.drawable.ic_male).into(iv_group_icon);
                        }

                        //tv_createdBy.setText("Created by : "+groupChatModel.getCreatedBy());

                        String groupCreator=groupChatList.getCreatedBy();
                        String timeStamp=groupChatList.getTimeStamp();

                        loadCreatorInfo(groupCreator,timeStamp);


                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    private void loadCreatorInfo(String groupCreator,String timeStamp) {

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(groupCreator); //

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot :dataSnapshot.getChildren()){

                    firebaseUserModel=dataSnapshot.getValue(FirebaseUserModel.class);
                    tv_createdBy.setText("Created by : "+firebaseUserModel.getUsername()+" on :"+timeStamp);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void loadMyGroupRole() {

        try {
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
            ref.child(groupId).child("Participants").orderByChild("uid")
                    //.equalTo(firebaseUser.getUid())
                    //.equalTo(String.valueOf(currentUser.getFirebasePartnerId()))
                    .equalTo(firebaseAuth.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                                try {

                                    participantsModel=dataSnapshot.getValue(ParticipantsModel.class);

                                    myGroupRole=""+snapshot.child("role").getValue();

                                    Log.e("mygroupId",""+myGroupRole);


                                    Log.e("role",""+myGroupRole);

                                    if (myGroupRole.equals("participant")){

                                        ll_participant.setVisibility(View.GONE);

                                        iv_editGroupInfo.setVisibility(View.GONE);
                                        tv_group_name.setVisibility(View.VISIBLE);
                                        tv_addParticipant.setVisibility(View.GONE);
                                        tv_leaveGroup.setText("Leave Group");
                                    }
                                    else if (myGroupRole.equals("admin")){

                                        ll_participant.setVisibility(View.VISIBLE);

                                        iv_editGroupInfo.setVisibility(View.VISIBLE);
                                        tv_group_name.setVisibility(View.VISIBLE);
                                        tv_addParticipant.setVisibility(View.VISIBLE);
                                        tv_leaveGroup.setText("Leave Group");
                                    }
                                    else if (myGroupRole.equals("creator")){

                                        ll_participant.setVisibility(View.VISIBLE);

                                        iv_editGroupInfo.setVisibility(View.VISIBLE);
                                        tv_group_name.setVisibility(View.VISIBLE);
                                        tv_addParticipant.setVisibility(View.VISIBLE);
                                        tv_leaveGroup.setText("Leave Group");
                                    }

                                    /*else if (myGroupRole.equals("admin")){
                                        tv_group_name.setVisibility(View.VISIBLE);
                                        tv_addParticipant.setVisibility(View.VISIBLE);
                                        tv_leaveGroup.setText("Leave Group");
                                    }
                                    else if (myGroupRole.equals("creator")){
                                        tv_group_name.setVisibility(View.VISIBLE);
                                        tv_addParticipant.setVisibility(View.VISIBLE);
                                        tv_leaveGroup.setText("Delete Group");
                                    }*/

                                }

                                catch (Exception e){
                                    e.printStackTrace();
                                }


                            /*
                            participantsModel=dataSnapshot.getValue(ParticipantsModel.class);

                            //myGroupRole=""+snapshot.child("role").getValue();

                            myGroupRole=participantsModel.getRole();

                            Log.e("role",""+myGroupRole);


                            if (myGroupRole.equals("participant")){
                                iv_editGroupInfo.setVisibility(View.GONE);
                                tv_group_name.setVisibility(View.VISIBLE);
                                tv_addParticipant.setVisibility(View.GONE);
                                tv_leaveGroup.setText("Leave Group");
                            }
                            else if (myGroupRole.equals("admin")){
                                tv_group_name.setVisibility(View.VISIBLE);
                                tv_addParticipant.setVisibility(View.VISIBLE);
                                tv_leaveGroup.setText("Leave Group");
                            }
                            else if (myGroupRole.equals("creator")){

                                tv_group_name.setVisibility(View.VISIBLE);
                                tv_addParticipant.setVisibility(View.VISIBLE);
                                tv_leaveGroup.setText("Delete Group");

                            }*/

                            }

                            loadParticipants();


                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadParticipants() {

        try {

            userList=new ArrayList<>();

            //userModelList=new ArrayList<>();

            DatabaseReference dref=FirebaseDatabase.getInstance().getReference("Groups");
            //dref.child(groupId).child("Participants").addValueEventListener(new ValueEventListener() {
            dref.child(groupId).child("Participants").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userList.clear();

                    //userModelList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        //get uid from Group > Participants

                        String uid=""+snapshot.child("uid").getValue();

                        Log.e("mygroupId","memberid : "+uid);


                        //stringArrayList.add(uid);

                        if (stringArrayList.contains(uid)){

                        }
                        else {
                            stringArrayList.add(uid);
                        }


                        //bindUserList();


                        //get info of user using uid
                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
                        //reference.orderByChild("id").equalTo(uid).addValueEventListener(new ValueEventListener() {
                        reference.orderByChild("id").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot ds : dataSnapshot.getChildren()){

                                    FirebaseUserModel userModel=ds.getValue(FirebaseUserModel.class);



                                    //UserModel userModel=ds.getValue(UserModel.class);

                                    //userModelList.add(userModel);

                                    //userList.add(userModel);


                                    if (userList.contains(userModel)){

                                    }
                                    else {
                                        userList.add(userModel);
                                    }


                                    String id = (String) ds.child("uid").getValue();

                                    String onlineUserId = (String) ds.child("onlineUserId").getValue();

                                    String name= (String) ds.child("username").getValue();

                                    Log.e("groupInfo",""+id+onlineUserId+name);


                                    Log.e("mygroupId","2");

                                }

                                bindList(userList);


                                /*
                                //addParticipantAdapter=new AddParticipantAdapter(GroupInfoActivity.this,userModelList,groupId,myGroupRole);
                                groupInfoAdapter=new GroupInfoAdapter(GroupInfoActivity.this,userList,groupId,myGroupRole);

                                //Log.e("mulist",""+userModelList.toString());
                                Log.e("mulist",""+userList.toString());


                                //rv_groupMembers.setAdapter(addParticipantAdapter);
                                rv_groupMembers.setAdapter(groupInfoAdapter);
                                //tv_participants.setText("Participants ("+userList.size()+")");

                                tv_participantCount.setText(userList.size()+" Participants");
                                //tv_participants.setText("Participants ("+userModelList.size()+")");*/


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }



/*    private void loadParticipants() {

        try {

            userList=new ArrayList<>();

            //userModelList=new ArrayList<>();

            DatabaseReference dref=FirebaseDatabase.getInstance().getReference("Groups");
            dref.child(groupId).child("Participants").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userList.clear();

                    //userModelList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        //get uid from Group > Participants

                        String uid=""+snapshot.child("uid").getValue();

                        Log.e("mygroupId","memberid : "+uid);


                        //stringArrayList.add(uid);

                        if (stringArrayList.contains(uid)){

                        }
                        else {
                            stringArrayList.add(uid);
                        }


                        //bindUserList();


                        //get info of user using uid
                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
                        reference.orderByChild("id").equalTo(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot ds : dataSnapshot.getChildren()){

                                    FirebaseUserModel userModel=ds.getValue(FirebaseUserModel.class);



                                    //UserModel userModel=ds.getValue(UserModel.class);

                                    //userModelList.add(userModel);

                                    //userList.add(userModel);


                                    if (userList.contains(userModel)){

                                    }
                                    else {
                                        userList.add(userModel);
                                    }


                                    String id = (String) ds.child("uid").getValue();

                                    String onlineUserId = (String) ds.child("onlineUserId").getValue();

                                    String name= (String) ds.child("username").getValue();

                                    Log.e("groupInfo",""+id+onlineUserId+name);


                                    Log.e("mygroupId","2");

                                }

                                bindList(userList);


                                *//*
                                //addParticipantAdapter=new AddParticipantAdapter(GroupInfoActivity.this,userModelList,groupId,myGroupRole);
                                groupInfoAdapter=new GroupInfoAdapter(GroupInfoActivity.this,userList,groupId,myGroupRole);

                                //Log.e("mulist",""+userModelList.toString());
                                Log.e("mulist",""+userList.toString());


                                //rv_groupMembers.setAdapter(addParticipantAdapter);
                                rv_groupMembers.setAdapter(groupInfoAdapter);
                                //tv_participants.setText("Participants ("+userList.size()+")");

                                tv_participantCount.setText(userList.size()+" Participants");
                                //tv_participants.setText("Participants ("+userModelList.size()+")");*//*


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }*/


    /*public void bindUserList(){

        for(String uid : stringArrayList){
            //get info of user using uid
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
            reference.orderByChild("id").equalTo(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()){

                        FirebaseUserModel userModel=ds.getValue(FirebaseUserModel.class);

                        //UserModel userModel=ds.getValue(UserModel.class);

                        //userModelList.add(userModel);


                        //userList.add(userModel);

                        if (userList.contains(userModel)){

                        }
                        else {
                            userList.add(userModel);
                        }

                    }
                    bindList(userList);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }*/



    public void bindList(ArrayList<FirebaseUserModel>  arrayList){

        //addParticipantAdapter=new AddParticipantAdapter(GroupInfoActivity.this,userModelList,groupId,myGroupRole);

        groupInfoAdapter=new GroupInfoAdapter(GroupInfoActivity.this,arrayList,groupId,myGroupRole);

        //Log.e("mulist",""+userModelList.toString());
        Log.e("mulist",""+arrayList.toString());

        groupInfoAdapter.notifyDataSetChanged();

        //rv_groupMembers.setAdapter(addParticipantAdapter);
        rv_groupMembers.setAdapter(groupInfoAdapter);
        //tv_participants.setText("Participants ("+userList.size()+")");

        tv_participantCount.setText(arrayList.size()+" Participants");
        //tv_participants.setText("Participants ("+userModelList.size()+")");





    }

    private void btnListener() {
        tv_addParticipant.setOnClickListener(this);
        tv_leaveGroup.setOnClickListener(this);

        //tv_group_name.setOnClickListener(this);

        iv_editGroupInfo.setOnClickListener(this);

        tv_mediaCount.setOnClickListener(this);
        iv_moreMedia.setOnClickListener(this);

    }


    private void bindView() {

        rv_groupMembers=findViewById(R.id.rv_groupMembers);

        tv_groupDescription=findViewById(R.id.tv_groupDescription);
        tv_createdBy=findViewById(R.id.tv_createdBy);
        tv_group_name=findViewById(R.id.tv_group_name);
        tv_addParticipant=findViewById(R.id.tv_addParticipant);
        tv_leaveGroup=findViewById(R.id.tv_leaveGroup);

        //tv_participants=findViewById(R.id.tv_participants);

        iv_group_icon=findViewById(R.id.iv_group_icon);
        iv_editGroupInfo=findViewById(R.id.iv_editGroupInfo);

        rv_media  = findViewById(R.id.rv_media);
        tv_mediaCount  = findViewById(R.id.tv_mediaCount);
        dialog = new ProgressDialog(activity);

        iv_moreMedia=findViewById(R.id.iv_moreMedia);

        ll_participant=findViewById(R.id.ll_participant);

        tv_participantCount=findViewById(R.id.tv_participantCount);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                break;

            case R.id.iv_moreMedia:

                Bundle bundle = new Bundle();

                bundle.putParcelable("groupChatModel", groupChatList);

                Utility.launchActivity(activity, GroupMediaListActivity.class, false, bundle);

                break;

            case R.id.tv_addParticipant:

                Intent addIntent=new Intent(this, AddGroupParticipantActivity.class);
                addIntent.putExtra("groupId",groupId);
                startActivity(addIntent);

                break;

            case R.id.tv_group_name:

                Intent intent=new Intent(GroupInfoActivity.this, GroupEditActivity.class);
                intent.putExtra("groupId",groupId);
                startActivity(intent);

                break;
            case R.id.iv_editGroupInfo:

                Intent intent1=new Intent(GroupInfoActivity.this, GroupEditActivity.class);
                intent1.putExtra("groupId",groupId);
                startActivity(intent1);

                break;

            case R.id.tv_leaveGroup:

                String dialogTitle="";
                String dialogDescription="";
                String positiveButton="";

                if (myGroupRole.equals("creator")){
                    dialogTitle="Exit Group";
                    dialogDescription="Do you want to exit from this group permanantly?";
                    positiveButton="Exit";
                }
                else if(myGroupRole.equals("admin")){
                    dialogTitle="Exit Group";
                    dialogDescription="Do you want to exit from this group permanantly?";
                    positiveButton="Exit";
                }
                else {
                    dialogTitle="Exit Group";
                    dialogDescription="Do you want to exit from this group permanantly?";
                    positiveButton="Exit";
                }


                AlertDialog.Builder builder=new AlertDialog.Builder(GroupInfoActivity.this);
                builder.setTitle(dialogTitle)
                        .setMessage(dialogDescription)
                        .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {

                                if (myGroupRole.equals("creator")){
                                    //in creator of group : delete group
                                    Log.e("delete","1");
                                    //deleteGroup();

                                    leaveGroup();

                                }
                                else {
                                    // in participant/admin leave group
                                    leaveGroup();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

                break;
        }
    }

    private void leaveGroup() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(firebaseUser.getUid())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //group left successfully
                        Toast.makeText(GroupInfoActivity.this, "Group left successfully", Toast.LENGTH_SHORT).show();
                        Utility.launchActivity(GroupInfoActivity.this,HomeActivity.class,true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed to leave group
                        Toast.makeText(GroupInfoActivity.this, "failed to leave group", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteGroup() {

        Log.e("delete","2");

        try {

            DatabaseReference dref=FirebaseDatabase.getInstance().getReference("Groups");
            dref.child(groupId)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            //group deleted successfully..
                            Log.e("delete","3");

                            Toast.makeText(GroupInfoActivity.this, "Group deleted successfully..", Toast.LENGTH_SHORT).show();

                            Log.e("delete","4");

                            Utility.launchActivity(GroupInfoActivity.this, HomeActivity.class,true);

                            //startActivity(new Intent(GroupInfoActivity.this,FirebaseHomeActivity.class));
                            //finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed to delete group
                            Toast.makeText(GroupInfoActivity.this, "failed to delete group", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}


