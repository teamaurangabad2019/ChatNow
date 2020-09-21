package com.teammandroid.chatnow.fragments.firebase;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.adapters.firebase.GroupChatsFragmentAdapter;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.GroupChatList;
import com.teammandroid.chatnow.utils.MyDividerItemDecoration;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */

public class GroupChatsFragment extends Fragment {

    private RecyclerView rv_firebaseGroups;

    private FirebaseAuth firebaseAuth;

    private ArrayList<GroupChatList> groupChatLists;

    private GroupChatsFragmentAdapter groupChatsFragmentAdapter;

    private ProgressDialog dialog;

    //private FragmentActivity activity;

    private Activity activity;

    UserModel currentUser;

    DatabaseReference reference;


    FirebaseUser firebaseUser;

    public GroupChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_group_chats, container, false);

        bindView(view);

        activity = getActivity();

        SessionManager sessionManager = new SessionManager(activity);
        currentUser = sessionManager.getUserDetails();

        firebaseAuth=FirebaseAuth.getInstance();

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        Log.e("curuname","curuname"+currentUser.getUserid()+""+currentUser.getFirebaseuserid()+""+currentUser.getFullname()+""+firebaseAuth.getUid());

        loadGroupChatList();

        return view;
    }

    private void bindView(View view) {

        activity = getActivity();
        dialog = new ProgressDialog(activity);
        dialog.setMessage("Loading page, please wait.");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        rv_firebaseGroups=view.findViewById(R.id.rv_firebaseGroups);

        /*rv_firebaseGroups.setHasFixedSize(true);

        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DataGroupingActivity.this,RecyclerView.VERTICAL,true);
        //rv_firebaseGroups.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,true));

        rv_firebaseGroups.setLayoutManager(new LinearLayoutManager(getContext()));
        */

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rv_firebaseGroups.setLayoutManager(mLayoutManager);
        rv_firebaseGroups.setItemAnimator(new DefaultItemAnimator());

        //rv_firebaseGroups.addItemDecoration(new MyDividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL, 36));

        rv_firebaseGroups.addItemDecoration(new MyDividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL, 80));

    }

    private void loadGroupChatList(){

        // if (Utility.isNetworkAvailable(activity)){
        //groupChatLists=new ArrayList<>();

        groupChatLists=new ArrayList<>();

        dialog.show();

        reference= FirebaseDatabase.getInstance().getReference("Groups");

        //reference.keepSynced(true);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                groupChatLists.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    //if (snapshot.child("Participants").child(firebaseAuth.getUid()).exists()){
                    //if (snapshot.child("Participants").child(firebaseAuth.getUid()).exists()){

                    //if (snapshot.child("Participants").child(String.valueOf(currentUser.getFirebasePartnerId())).exists()){

                    Log.e("curuserserid","curuserserid"+currentUser.getUserid());

                    //if (snapshot.child("Participants").child(currentUser.getFirebasePartnerId()).exists()){
                    //if (snapshot.child("Participants").child(firebaseAuth.getUid()).exists()){

                    if (snapshot.child("Participants").child(firebaseUser.getUid()).exists()){
                        GroupChatList model=snapshot.getValue(GroupChatList.class);
                        groupChatLists.add(model);
                    }
                }

                dialog.dismiss();
                groupChatsFragmentAdapter=new GroupChatsFragmentAdapter(getContext(),groupChatLists,reference);
                rv_firebaseGroups.setAdapter(groupChatsFragmentAdapter);

                Log.e("groups",""+groupChatLists.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
            }
        });

        /*}else {

        }*/

    }

}
