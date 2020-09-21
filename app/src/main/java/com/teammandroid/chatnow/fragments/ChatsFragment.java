package com.teammandroid.chatnow.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.activities.ChattingActivity;
import com.teammandroid.chatnow.adapters.ChatsRecyclerAdapter;
import com.teammandroid.chatnow.offline.ChatDatabaseHelper;
import com.teammandroid.chatnow.models.MyChatModel;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.utils.MyDividerItemDecoration;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import java.util.ArrayList;
import java.util.Collections;

public class ChatsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ChatsFragment.class.getSimpleName();
    RecyclerView rv_chats;

    ChatsRecyclerAdapter chatsRecyclerAdapter;
    RelativeLayout originalToolbar;
    RelativeLayout replaceToolbar;
    ImageView viewReplaceClear;
    EditText toolbarEditText;
    ImageView viewSearch;
    ImageView viewDelete;

    private ArrayList<MyChatModel> mModel = new ArrayList<>();

    private Activity activity;

    private ProgressDialog dialog;

    private UserModel currentUser;

    FirebaseUser firebaseUser;

    ChatDatabaseHelper dbHelper;

    private boolean shouldRefreshOnResume = false;

    private DatabaseReference dref;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        dbHelper = new ChatDatabaseHelper(getContext());

        activity = getActivity();

        dialog = new ProgressDialog(activity);

        dialog.setMessage("Loading page, please wait.");

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        SessionManager sessionManager = new SessionManager(getContext());

        currentUser = sessionManager.getUserDetails();

        bindView(view);

        btnListeneres();

        dref = FirebaseDatabase.getInstance().getReference("Chatlist");
        dref.keepSynced(true);

        GetRecentChats();

        //region search bar
        toolbarEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rv_chats.setLayoutManager(mLayoutManager);
        rv_chats.setItemAnimator(new DefaultItemAnimator());

        //rv_firebaseGroups.addItemDecoration(new MyDividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL, 36));

        rv_chats.addItemDecoration(new MyDividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL, 80));

        // endregion
        return view;
    }

    private void GetRecentChats() {

        Log.e(TAG, "GetRecentChats: uid " + firebaseUser.getUid());
        dref.child(firebaseUser.getUid()).orderByChild("msgTime").limitToLast(100)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mModel.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.e(TAG, "onDataChange: " + snapshot.getValue());

                            MyChatModel model = snapshot.getValue(MyChatModel.class);

                            // SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
                            mModel.add(model);
                           /* try {
                                Date date1 = dateTime.parse(participantsModel.getClearDate());
                                Date date2 = dateTime.parse(model.getMsgTime());
                                if (date1.compareTo(date2)<0){
                                    //  if (!dateTime.parse(model.getMsgTime()).before(dateTime.parse("26/05/2020 09:21:00 AM")))   {
                                    groupChatList.add(model);
                                }
                            }catch (Exception e){
                                Log.e(TAG, "onDataChange: edate "+e.getMessage() );
                            }*/
                        }

                        bindAdapter(mModel);
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void btnListeneres() {
        viewSearch.setOnClickListener(this);
        viewReplaceClear.setOnClickListener(this);
        viewDelete.setOnClickListener(this);
    }

    private void bindAdapter(final ArrayList<MyChatModel> list) {
        Collections.reverse(list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        rv_chats.setLayoutManager(mLayoutManager);
        rv_chats.setItemAnimator(new DefaultItemAnimator());

        chatsRecyclerAdapter = new ChatsRecyclerAdapter(activity, list, currentUser, new ChatsRecyclerAdapter.ItemClickListener() {
            @Override
            public void onTrainerItemClick(View view, int position) {
                try {
                    Bundle bundle = new Bundle();
                    //bundle.putParcelable("chattingPartner", chattingPartner);
                    bundle.putInt("partnerId", Integer.valueOf(list.get(position).getPartnerid()));
                    bundle.putString("firebasePartnerId",list.get(position).getFirebasePartnerId());

                    Utility.launchActivity(getActivity(), ChattingActivity.class, false, bundle);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.e(TAG, "onItemLongClick: clicked" );
                MyChatModel model = list.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Do You want to delete this chat ? ");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Log.e(TAG, "GetRecentChats: uid " + firebaseUser.getUid());
                        dref.child(firebaseUser.getUid()).child(model.getPartnerid())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                            appleSnapshot.getRef().removeValue();
                                        }
                                        chatsRecyclerAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.e(TAG, "onCancelled", databaseError.toException());
                                    }
                                });

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.show();
                //Creatin)
            }
        });
        rv_chats.setAdapter(chatsRecyclerAdapter);
    }

    private void bindView(View view) {
        rv_chats = view.findViewById(R.id.rv_chats);
        viewReplaceClear = view.findViewById(R.id.viewReplaceClearA);
        originalToolbar = view.findViewById(R.id.originalToolbarA);
        replaceToolbar = view.findViewById(R.id.replaceToolbarA);
        toolbarEditText = view.findViewById(R.id.toolbarEditTextA);
        viewSearch = view.findViewById(R.id.viewSearchA);
        viewDelete = view.findViewById(R.id.viewDelete);
    }


    @Override
    public void onResume() {
        super.onResume();
        // Check should we need to refresh the fragment
        //   chatsRecyclerAdapter.notifyDataSetChanged();
        try {
            if (shouldRefreshOnResume) {
                // refresh fragment
                chatsRecyclerAdapter.notifyDataSetChanged();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }


    @Override
    public void onStop() {
        super.onStop();
        //mModel=getChatList();
        //  chatsRecyclerAdapter.notifyDataSetChanged();
        shouldRefreshOnResume = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewSearchA:
                originalToolbar.setVisibility(View.GONE);
                replaceToolbar.setVisibility(View.VISIBLE);
                break;

            case R.id.viewReplaceClearA:
                if (toolbarEditText.getText().toString().equals("")) {
                    originalToolbar.setVisibility(View.VISIBLE);
                    replaceToolbar.setVisibility(View.GONE);
                } else {
                    toolbarEditText.setText("");
                    chatsRecyclerAdapter.filterList(mModel);
                }
                chatsRecyclerAdapter.filterList(mModel);
                break;
        }
    }

    private void filter(String text) {
        try {
            //new array list that will hold the filtered data
            ArrayList<MyChatModel> filterdNames = new ArrayList<>();
            //looping through existing elements
            for (MyChatModel s : mModel) {
                //if the existing elements contains the search input
                if (s.getPartnerName() != "") {
                    if (s.getPartnerName().toLowerCase().contains(text.toLowerCase())) {
                        //adding the element to filtered list
                        filterdNames.add(s);
                    }
                }
            }
            //calling a method of the adapter class and passing the filtered list
            chatsRecyclerAdapter.filterList(filterdNames);
        } catch (Exception e) {
            Log.e(TAG, "filter: e " + e.getMessage());
        }
    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);

        // Refresh tab data:

        if (getFragmentManager() != null) {

            getFragmentManager()
                    .beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }
}
