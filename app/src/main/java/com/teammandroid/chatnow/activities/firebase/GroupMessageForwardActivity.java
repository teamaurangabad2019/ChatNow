package com.teammandroid.chatnow.activities.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.adapters.firebase.GroupMessageForwardAdapter;
//import com.teammandroid.chatnow.grouping.ListItem;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseGroupChatModel;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;
import com.teammandroid.chatnow.models.firebase.GroupChatList;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import java.util.ArrayList;

public class GroupMessageForwardActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = GroupMessageForwardActivity.class.getSimpleName();

    RecyclerView rv_group_forwardMsg;

    GroupMessageForwardAdapter groupMessageForwardAdapter;

    private FirebaseAuth firebaseAuth;

    private ArrayList<GroupChatList> groupChatLists;


    UserModel currentUser;
    DatabaseReference groupReference;

    private Activity activity;
    ImageView iv_back;
    private ProgressDialog dialog;

    Bundle bundle;

    ArrayList<FirebaseGroupChatModel> multiSelectedList = new ArrayList<>();

    //ArrayList<ListItem> newMultiSelectedList = new ArrayList<>();

    private FirebaseUserModel firebaseUserModel;


    GroupChatList currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message_forward);

        activity = GroupMessageForwardActivity.this;

        bindView();

        btnListener();

        SessionManager sessionManager = new SessionManager(activity);
        currentUser = sessionManager.getUserDetails();

        dialog = new ProgressDialog(activity);
        dialog.setMessage("Loading page, please wait.");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        try {
            bundle=getIntent().getExtras();
            if (bundle != null) {

                multiSelectedList = bundle.getParcelableArrayList("multiSelectedList");

                //newMultiSelectedList = bundle.getParcelableArrayList("multiSelectedList");

                Log.e("msgforward: ","msgforward 2"+multiSelectedList.toString());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


        firebaseAuth=FirebaseAuth.getInstance();

        Log.e("curuname","curuname"+currentUser.getUserid()+""+currentUser.getFirebaseuserid()+""+currentUser.getFullname()+""+firebaseAuth.getUid());

        groupChatLists=new ArrayList<>();

        loadGroupList();
    }

    private void loadGroupList() {

        dialog.show();

        groupReference= FirebaseDatabase.getInstance().getReference("Groups");

        //reference.keepSynced(true);

        groupReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                groupChatLists.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Log.e("curuserserid","curuserserid"+currentUser.getUserid());

                    if (snapshot.child("Participants").child(firebaseAuth.getUid()).exists()){
                        GroupChatList model=snapshot.getValue(GroupChatList.class);
                        groupChatLists.add(model);
                    }
                }

                dialog.dismiss();


                groupMessageForwardAdapter=new GroupMessageForwardAdapter(activity, groupChatLists, currentGroup, groupReference, new GroupMessageForwardAdapter.ItemClickListener() {
                    @Override
                    public void onGroupClick(View view, int position) {


                        GroupChatList selectedGroup = groupChatLists.get(position);

                        Toast.makeText(activity, ""+selectedGroup.getGroupTitle(), Toast.LENGTH_SHORT).show();

                        try {

                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList("multiSelectedList",multiSelectedList);
                            //bundle.putParcelableArrayList("multiSelectedList",newMultiSelectedList);

                            bundle.putString("groupId",selectedGroup.getGroupId());

                            Log.e("msgforward: ","msgforward 3"+multiSelectedList.toString());


                            Utility.launchActivity(activity, GroupChattingActivity.class,true,bundle);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                });


                rv_group_forwardMsg.setAdapter(groupMessageForwardAdapter);

                Log.e("groups",""+groupChatLists.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
            }
        });

    }

    private void btnListener() {
        iv_back.setOnClickListener(this);
    }

    private void bindView() {
        rv_group_forwardMsg=findViewById(R.id.rv_group_forwardMsg);
        iv_back=findViewById(R.id.viewMenuIconBack);

        rv_group_forwardMsg.setHasFixedSize(true);
        rv_group_forwardMsg.setLayoutManager(new LinearLayoutManager(activity));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                break;

            case R.id.viewMenuIconBack:
                onBackPressed();
                break;
        }
    }
}
