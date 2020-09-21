package com.teammandroid.chatnow.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
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
import com.teammandroid.chatnow.BuildConfig;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.activities.DetailsChattingActivity;
import com.teammandroid.chatnow.adapters.DocumentListRecyclerAdapter;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
*/


public class DocumentsListFrgment extends Fragment implements View.OnClickListener {

    private static final String TAG = DocumentsListFrgment.class.getSimpleName() ;

    FirebaseUserModel chattingPartner;
    FirebaseUser firebaseUser;

    private ArrayList<FirebaseMsgModel> firebaseChatModel;

    Activity activity;
 //   GalleryRecyclerAdapter adapter;
   // ArrayList<GalleryModel>  imgList = new ArrayList<>();
    RecyclerView rv_gallery;
    private ProgressDialog dialog;
    private ArrayList<NotificationModel> offlineMsglist = new ArrayList<>();
    private Bundle bundle;
    private UserModel currentUser;
    private DocumentListRecyclerAdapter adapter;
    private int PartnerId;
    private int userId;

    public DocumentsListFrgment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        Bundle bundle = getArguments();

        View view = inflater.inflate(R.layout.fragment_media_list_fragment, container, false);
        bindView(view);
        btnListeneres();

        if (bundle != null) {
            chattingPartner = bundle.getParcelable("chattingPartner");
            PartnerId = Integer.parseInt(chattingPartner.getOnlineUserId());
            Log.e(TAG, "onCreate:chattingPartner "+chattingPartner.toString());
        }

        SessionManager sessionManager = new SessionManager(activity);
        currentUser = sessionManager.getUserDetails();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUserid();

        dialog = new ProgressDialog(activity);
        GetMediaFiles();
        return view;
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

                                                if (chat.getMediatype()== 5 ) {
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

            rv_gallery.setLayoutManager(new LinearLayoutManager(activity));
            rv_gallery.setItemAnimator(new DefaultItemAnimator());
            rv_gallery.setHasFixedSize(true);
            rv_gallery.addItemDecoration(new DividerItemDecoration(activity,
                    DividerItemDecoration.VERTICAL));

            dialog.dismiss();
            adapter = new DocumentListRecyclerAdapter(activity, arraylist, new DocumentListRecyclerAdapter.ItemClickListener() {
                @Override
                public void onClick(View v, int position) {
                    FirebaseMsgModel model = arraylist.get(position);
                    Log.e(TAG, "onMessageClick: " + model.toString());
                    try {

                        String filename = "";
                        if (model.getType() == 2) {
                            filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_DOCUMENT_PATH + model.getMessage();
                            //filename="Chatnow/Media/Chatnow Videos/"+model.getMessage();
                        } else {
                            filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_DOCUMENT_PATH + "Sent/" + model.getMessage();
                        }
                        File file = new File(filename);

                        Uri data = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", file);
                        //Uri data = Uri.fromFile(file);
                        Intent intent = new Intent();
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);
                        if (data.toString().contains(".doc") || data.toString().contains(".docx")) {
                            // Word document
                            intent.setDataAndType(data, "application/msword");
                        } else if (data.toString().contains(".pdf")) {
                            // PDF file
                            intent.setDataAndType(data, "application/pdf");
                        } else if (data.toString().contains(".ppt") || data.toString().contains(".pptx")) {
                            // Powerpoint file
                            intent.setDataAndType(data, "application/vnd.ms-powerpoint");
                        } else if (data.toString().contains(".xls") || data.toString().contains(".xlsx")) {
                            // Excel file
                            intent.setDataAndType(data, "application/vnd.ms-excel");
                        } else if (data.toString().contains(".zip") || data.toString().contains(".rar")) {
                            // WAV audio file
                            intent.setDataAndType(data, "application/zip");
                        } else if (data.toString().contains(".rtf")) {
                            // RTF file
                            intent.setDataAndType(data, "application/rtf");
                        } else if (data.toString().contains(".wav") || data.toString().contains(".mp3")) {
                            // WAV audio file
                            intent.setDataAndType(data, "audio/x-wav");
                        } else if (data.toString().contains(".gif")) {
                            // GIF file
                            intent.setDataAndType(data, "image/gif");
                        } else if (data.toString().contains(".jpg") || data.toString().contains(".jpeg") || data.toString().contains(".png")) {
                            // JPG file
                            intent.setDataAndType(data, "image/jpeg");
                        } else if (data.toString().contains(".txt")) {
                            // Text file
                            intent.setDataAndType(data, "text/plain");
                        } else {
                            //if you want you can also define the intent type for any other file
                            //additionally use else clause below, to manage other unknown extensions
                            //in this case, Android will show all applications installed on the device
                            //so you can choose which application to use
                            intent.setDataAndType(data, "*/*");
                        }
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(activity, "No Application Availabe to open file.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onItemClick: except " + e.getMessage());
                    }
                    //Utility.launchActivity(activity, DetailsChattingActivity.class, false, bundle);
                }
            });
            rv_gallery.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (Exception ex) {
            Utility.showErrorMessage(activity, ex.getMessage(), Snackbar.LENGTH_LONG);
        }
    }

    private void btnListeneres() {

    }

    private void bindView(View view) {
        rv_gallery = view.findViewById(R.id.rv_gallery);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
           /* case R.id.rl_date:
                // Create the DatePickerDialog instance
                DatePickerDialog datePicker = new DatePickerDialog(context,
                        R.style.AppBlackTheme,
                        datePickerListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setCancelable(false);
                datePicker.setTitle("Select the date");
                datePicker.show();
                break;*/
        }

    }

}
