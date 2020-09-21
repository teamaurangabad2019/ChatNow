package com.teammandroid.chatnow.activities.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.activities.ChattingActivity;
import com.teammandroid.chatnow.activities.PeopleProfileActivity;
import com.teammandroid.chatnow.adapters.PeopleRecyclerAdapter;
import com.teammandroid.chatnow.adapters.firebase.AddParticipantAdapter;
import com.teammandroid.chatnow.fragments.FindPeopleFragment;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;
import com.teammandroid.chatnow.network.UserServices;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import java.util.ArrayList;

public class AddGroupParticipantActivity extends AppCompatActivity {

    private static final String TAG =AddGroupParticipantActivity.class.getSimpleName();

    RecyclerView rv_addParticipant;

    FirebaseAuth firebaseAuth;

    TextView txt_groupTitle,txt_role;

    String groupId;

    private String myGroupRole;

    private ArrayList<FirebaseUserModel> userList;

    private AddParticipantAdapter addParticipantAdapter;

    Activity activity;
    private ProgressDialog dialog;

    private UserModel currentUser;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_participant);

        activity = AddGroupParticipantActivity.this;

        bindView();

        dialog = new ProgressDialog(activity);
        dialog.setMessage("Loading page, please wait.");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        SessionManager sessionManager = new SessionManager(activity);
        currentUser = sessionManager.getUserDetails();

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        rv_addParticipant.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rv_addParticipant.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        rv_addParticipant.setLayoutManager(linearLayoutManager);

        firebaseAuth=FirebaseAuth.getInstance();

        groupId=getIntent().getStringExtra("groupId");

        loadGroupInfo();

        //getAllUsers();

        GetUserNearMe();

    }


    private void GetUserNearMe() {
        Log.e(TAG, "GetUserNearMe: called" );
        try {
            if (Utility.isNetworkAvailable(activity)) {

                dialog.show();
                //  Log.e(TAG, "GetUser: " + UserId);
                // UserServices.getInstance(getApplicationContext()).GetUserNearMe(user.getUserid(),latitude,longitude,distance_range, user.getRoleid(),new ApiStatusCallBack<ArrayList<UserModel>>() {
                UserServices.getInstance(activity).GetAllUser(new ApiStatusCallBack<ArrayList<UserModel>>() {
                    @Override
                    public void onSuccess(ArrayList<UserModel> response) {

                        dialog.dismiss();
                        ArrayList<UserModel> userNearMeList = response;

                        Log.e(TAG, "onSuccess: list "+userNearMeList.toString() );
                        bindList(response);
                        Toast.makeText(activity,userNearMeList.size()+" User Found",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        Utility.showErrorMessage(activity, "Network "+anError.getMessage());
                    }

                    @Override
                    public void onUnknownError(Exception e) {
                        dialog.dismiss();
                        Utility.showErrorMessage(activity, e.getMessage());
                    }
                });
            } else {
                Utility.showErrorMessage(activity, "Could not connect to the internet");
            }
        } catch (Exception ex) {
            dialog.dismiss();
            Utility.showErrorMessage(activity, ex.getMessage());
        }
    }


    /*
    private void getAllUsers() {

        //init list
        userList=new ArrayList<>();
        //load users from db
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    FirebaseUserModel modelUser=snapshot.getValue(FirebaseUserModel.class);

                    //get all users except currently signin
                    if (!firebaseAuth.getUid().equals(modelUser.getId())){
                        //not mu uid
                        userList.add(modelUser);
                    }
                    //setup adapter
                    addParticipantAdapter=new AddParticipantAdapter(AddGroupParticipantActivity.this,userList,""+groupId,""+myGroupRole);
                    //set adapter to recyclerview
                    rv_addParticipant.setAdapter(addParticipantAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    */


    //private void bindList(final ArrayList<UserModel> response) {

    private void bindList(final ArrayList<UserModel> response) {

        rv_addParticipant.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_addParticipant.setLayoutManager(mLayoutManager);
        rv_addParticipant.setItemAnimator(new DefaultItemAnimator());

        rv_addParticipant.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));


        addParticipantAdapter=new AddParticipantAdapter(activity, response,groupId,myGroupRole);

        Log.e("nearmelist",""+response.toString());

        /*addParticipantAdapter=new AddParticipantAdapter(activity, response,currentUser,firebaseUser, reference, new AddParticipantAdapter.ItemClickListener() {
            @Override
            public void onTrainerItemClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("chattingPartner", response.get(position));
                Utility.launchActivity(getActivity(), PeopleProfileActivity.class, false, bundle);

                //pageViewModel.setIsUpdate("1");
            }
        });
        */

        rv_addParticipant.setAdapter(addParticipantAdapter);
    }


    private void loadGroupInfo() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String groupId=""+snapshot.child("groupId").getValue();
                    String groupTitle=""+snapshot.child("groupTitle").getValue();
                    String groupDescription=""+snapshot.child("groupDescription").getValue();
                    String groupIcon=""+snapshot.child("groupIcon").getValue();
                    String createdBy=""+snapshot.child("createdBy").getValue();
                    String timeStamp=""+snapshot.child("timeStamp").getValue();

                    ref.child(groupId).child("Participants").child(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        myGroupRole=""+dataSnapshot.child("role").getValue();

                                        txt_groupTitle.setText(groupTitle);
                                        txt_role.setText("("+myGroupRole+")");

                                        //getAllUsers();
                                        GetUserNearMe();
                                    }
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

    private void bindView() {
        rv_addParticipant=findViewById(R.id.rv_addParticipant);
        txt_groupTitle=findViewById(R.id.txt_groupTitle);
        txt_role=findViewById(R.id.txt_role);

    }
}
