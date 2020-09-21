package com.teammandroid.chatnow.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.adapters.GroupDocListRecyclerAdapter;
import com.teammandroid.chatnow.adapters.GroupMediaListRecyclerAdapter;
import com.teammandroid.chatnow.models.NotificationModel;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseGroupChatModel;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;
import com.teammandroid.chatnow.models.firebase.GroupChatList;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GroupDocListFragment extends Fragment {


    private static final String TAG = GroupMediaListFragment.class.getSimpleName() ;

    Activity context;
    //   GalleryRecyclerAdapter adapter;
    // ArrayList<GalleryModel>  imgList = new ArrayList<>();

    RecyclerView rv_gallery;
    private ProgressDialog dialog;
    FirebaseUserModel chattingPartner;
    FirebaseUser firebaseUser;
    private int PartnerId;
    private int userId;

    private ArrayList<FirebaseGroupChatModel> firebaseChatModel;

    UserModel currentUser;
    Activity activity;
    private ArrayList<NotificationModel> offlineMsglist = new ArrayList<>();
    private GroupDocListRecyclerAdapter adapter;

    GroupChatList groupChatList;

    String groupId;
    String chatClearDate;

    ArrayList<FirebaseGroupChatModel> groupChatModel = new ArrayList<>();

    public GroupDocListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_group_doc_list, container, false);

        context = getActivity();

        Bundle bundle = getArguments();

        activity = getActivity();


        if (bundle != null) {
            groupChatList = bundle.getParcelable("groupChatList");
            Log.e("groupMedia", "groupMedia frag "+groupChatList.getGroupTitle());
        }

        SessionManager sessionManager = new SessionManager(activity);
        currentUser = sessionManager.getUserDetails();

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        bindView(view);

        dialog = new ProgressDialog(context);

        GetMediaFiles();


        return view;
    }

    private void bindView(View view) {
        rv_gallery = view.findViewById(R.id.rv_gallery);
    }

    private void GetMediaFiles() {

        dialog.show();
        dialog.setMessage("Loading Page, please wait.");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        try {

            //final String[] clearDate = {""};

            groupId=groupChatList.getGroupId();

            Log.e("groupMedia", "groupMedia frag groupId "+groupChatList.getGroupId());

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
            reference.child(groupId).child("Participants")
                    .orderByChild("uid").equalTo(firebaseUser.getUid())
                    //.orderByChild("uid").equalTo(String.valueOf(currentUser.getUserid()))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                //participantsModel = snapshot.getValue(ParticipantsModel.class);


                                //myGroupRole = "" + snapshot.child("role").getValue();

                                //chatClearDate=participantsModel.getClearDate();

                                chatClearDate="" + snapshot.child("clearDate").getValue();

                                Log.e("groupMedia", "groupMedia frag clearDate"+chatClearDate);


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


                                    if ( !(chatClearDate.equals("")) ){
                                        if (date1.compareTo(date2)<0){

                                            Log.e("groupMedia", "groupMedia 4");

                                            if (model.getMediatype()== 5) {

                                                Log.e("groupMedia", "groupMedia 5");

                                                groupChatModel.add(model);

                                                Log.e("groupMedia", "groupMedia frag media"+model.getMediatype());

                                            }
                                        }
                                    }
                                    else {
                                        firebaseChatModel.add(model);
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

            rv_gallery.setLayoutManager(new LinearLayoutManager(activity));
            rv_gallery.setItemAnimator(new DefaultItemAnimator());
            rv_gallery.setHasFixedSize(true);
            rv_gallery.addItemDecoration(new DividerItemDecoration(activity,
                    DividerItemDecoration.VERTICAL));

            dialog.dismiss();
            adapter = new GroupDocListRecyclerAdapter(context, arraylist, new GroupDocListRecyclerAdapter.ItemClickListener() {
                @Override
                public void onClick(View v, int position) {
                    /*FirebaseGroupChatModel model = arraylist.get(position);
                    Log.e(TAG, "onMessageClick: " + model.toString());
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("chatModel", model);
                    if (model.getType()==1) {
                        bundle.putString("sender_name", "You");
                    } else if (model.getType()== 2) {
                        bundle.putString("sender_name", chattingPartner.getUsername());
                    }
                    Utility.launchActivity(activity, DetailsChattingActivity.class, false, bundle);*/
                }
            });
            rv_gallery.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (Exception ex) {
            Utility.showErrorMessage(context, ex.getMessage(), Snackbar.LENGTH_LONG);
        }
    }

}
