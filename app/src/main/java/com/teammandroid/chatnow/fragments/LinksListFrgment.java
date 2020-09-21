package com.teammandroid.chatnow.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.adapters.LinkListRecyclerAdapter;
import com.teammandroid.chatnow.models.NotificationModel;
import com.teammandroid.chatnow.models.OfflineUserModel;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseMsgModel;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;
import com.teammandroid.chatnow.offline.NotificationDatabaseHelper;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class LinksListFrgment extends Fragment implements View.OnClickListener {

    private static final String TAG = LinksListFrgment.class.getSimpleName() ;

    Activity context;

    RecyclerView rv_links;
    private ProgressDialog dialog;

    FirebaseUserModel chattingPartner;
    FirebaseUser firebaseUser;
    private int PartnerId;
    private int userId;

    private ArrayList<FirebaseMsgModel> firebaseChatModel;
    UserModel currentUser;
    Activity activity;

    private ArrayList<NotificationModel> offlineMsglist = new ArrayList<>();

    private LinkListRecyclerAdapter adapter;
    //private MediaListRecyclerAdapter adapter;


    public LinksListFrgment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        context = getActivity();

        //mContext=getActivity();

        View view = inflater.inflate(R.layout.fragment_link_list, container, false);

        Bundle bundle = getArguments();

        activity = getActivity();

        if (bundle != null) {
            chattingPartner = bundle.getParcelable("chattingPartner");
            Log.e(TAG, "onCreate:chattingPartner "+chattingPartner.toString());

        }

        SessionManager sessionManager = new SessionManager(getContext());
        currentUser = sessionManager.getUserDetails();

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        PartnerId = Integer.parseInt(chattingPartner.getOnlineUserId());
        userId = currentUser.getUserid();
        //SessionManager sessionManager = new SessionManager(activity);


        bindView(view);
        btnListeneres();

        dialog = new ProgressDialog(context);

        GetLinks();

        Log.e("loghere","log");

        return view;

    }

    private void GetLinks() {

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

                                                if (chat.getMediatype()==6 ) {

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



    private void btnListeneres() {

    }

    private void bindView(View view) {
        rv_links = view.findViewById(R.id.rv_links);
    }

    private void BindAnnoucements(final ArrayList<FirebaseMsgModel> arraylist) {
        try {

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            rv_links.setLayoutManager(mLayoutManager);
            rv_links.setItemAnimator(new DefaultItemAnimator());

            rv_links.addItemDecoration(new DividerItemDecoration(getContext(),
                    DividerItemDecoration.VERTICAL));

            dialog.dismiss();

            adapter = new LinkListRecyclerAdapter(getContext(), arraylist);
            //adapter = new LinkListRecyclerAdapter(getActivity(), linkList);

            rv_links.setAdapter(adapter);

            adapter.notifyDataSetChanged();

        } catch (Exception ex) {
            Utility.showErrorMessage(context, ex.getMessage(), Snackbar.LENGTH_LONG);
        }
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){

        }

    }

}
