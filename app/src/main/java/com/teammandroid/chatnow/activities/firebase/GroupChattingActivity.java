package com.teammandroid.chatnow.activities.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.squareup.picasso.Picasso;
import com.teammandroid.chatnow.BuildConfig;
import com.teammandroid.chatnow.activities.GroupMediaListActivity;
import com.teammandroid.chatnow.R;

import com.teammandroid.chatnow.activities.GroupInfoActivity;
//import com.teammandroid.chatnow.activities.GroupMessageForwardActivity;
import com.teammandroid.chatnow.activities.GrpDetailsChattingActivity;
import com.teammandroid.chatnow.activities.group_preview.GrpContactPreviewActivity;
import com.teammandroid.chatnow.activities.preview_activity.SentContactPreviewActivity;
import com.teammandroid.chatnow.adapters.firebase.GroupChatAdapter;
//import com.teammandroid.chatnow.grouping.DataGroupingActivity;
import com.teammandroid.chatnow.fragments.BottomSheetGroupFileOptionsFragment;
import com.teammandroid.chatnow.interfaces.MessageSwipeController;
import com.teammandroid.chatnow.interfaces.SwipeControllerActions;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseContactResult;
import com.teammandroid.chatnow.models.firebase.FirebaseGroupChatModel;
import com.teammandroid.chatnow.models.firebase.FirebaseMsgModel;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;
import com.teammandroid.chatnow.models.firebase.GroupChatList;
import com.teammandroid.chatnow.models.firebase.ParticipantsModel;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GroupChattingActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private static final String TAG = GroupChattingActivity.class.getSimpleName();
    private static final int CONTACT_PICKER_REQUEST = 9;

    RecyclerView rv_groupChat;
    LinearLayout ll_groupName;
    ImageView viewMenuIconBack, iv_more, iv_cprofile_pic;

    Intent intent;
    String groupId, myGroupRole;
    FirebaseUser firebaseUser;

    EditText et_msg;
    ImageView iv_attachment, iv_send;
    TextView tv_groupName, txt_members, txtTitleBar;

    RelativeLayout rl_reply_layout;
    TextView txtQuotedMsg, txtQuotedMsgType;
    ImageView cancelButton, iv_preview;

    MessageSwipeController messageSwipeController;
    //private int replyToMsg = -1;
    //private String replyToMsg = null;

    private String replyToMsg = "";

    FirebaseAuth firebaseAuth;

    GroupChatList groupChatModel;

    private ArrayList<FirebaseGroupChatModel> groupChatList;

    private GroupChatAdapter groupChatAdapter;

    private ParticipantsModel participantsModel;

    private FirebaseGroupChatModel replyToMsgModel;

    private ArrayList<FirebaseGroupChatModel> selectedList = new ArrayList<>();

    private ArrayList<FirebaseGroupChatModel> groupSearchChatList;

    Activity activity;
    UserModel currentUser;

    Toolbar toolbar, toolbar1, toolbar_search;

    ImageView iv_back, iv_copy, iv_reply, iv_delete, iv_forward;

    private GroupChatAdapter.ItemClickListener itemClickListener;

    Bundle bundle;

    private ArrayList<FirebaseGroupChatModel> multiSelectedList = new ArrayList<>();

    String SenderId;

    String myGroupId;

    // Get clipboard manager object.
    Object clipboardService;
    ClipboardManager clipboardManager;


    ArrayList<String> stringArrayList = new ArrayList<>();

    ImageView search_clear, viewMenuIconBack2;
    EditText et_search_view;
    private ArrayList<ParticipantsModel> participantsList = new ArrayList<>();

    FirebaseUserModel firebasePartnerModel;

    String chatClearDate;


    int count = 0;

    ArrayList<MediaFile> files = new ArrayList<>();

    MediaFile media;

    String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chatting);

        bindView();
        btnListener();

        activity = GroupChattingActivity.this;
        SessionManager sessionManager = new SessionManager(activity);
        currentUser = sessionManager.getUserDetails();
        try {
            intent = getIntent();
            groupId = intent.getStringExtra("groupId");
            Log.e("groupId", groupId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bundle = getIntent().getExtras();
        try {
            if (bundle != null) {
                myGroupId = bundle.getString("groupId");
                multiSelectedList = bundle.getParcelableArrayList("multiSelectedList");
                Log.e(TAG, "onCreate:user " + currentUser.toString());
                Log.e("msgforward: ", "msgforward 4" + multiSelectedList.toString());

                if (multiSelectedList != null) {
                    for (FirebaseGroupChatModel model : multiSelectedList) {

                        Log.e("msgforward","msgforward 5"+model.getMessage());

                        forwardMessage(model,myGroupId,1);

                        //sendMessage(model.getMessage(), model.getSender(), myGroupId,1);

                    }
                }

                loadGroupMessages();

            } else {
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        rv_groupChat.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        rv_groupChat.setLayoutManager(linearLayoutManager);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();

        loadGroupInfo(groupId);
        //groupChatList=new ArrayList<>();
        loadMyGroupRole();

        loadParticipants();

        loadGroupMessages();

        // Get clipboard manager object.
        clipboardService = getSystemService(CLIPBOARD_SERVICE);
        clipboardManager = (ClipboardManager) clipboardService;


        et_search_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchMessages(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //filter(s.toString());
            }
        });

        et_msg.requestFocus();

    }

    public void searchMessages(String s) {

        groupSearchChatList = new ArrayList<>();

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Groups").child(groupId).child("Messages").orderByChild("message")
                .startAt(s)
                .endAt(s + "\uf0ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupSearchChatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FirebaseGroupChatModel model = snapshot.getValue(FirebaseGroupChatModel.class);

                    groupSearchChatList.add(model);
                }


                bindList(groupSearchChatList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadMyGroupRole() {

        Log.e("curfuserid", "curfuserid" + currentUser.getFirebaseuserid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants")
                .orderByChild("uid").equalTo(firebaseUser.getUid())
                //.orderByChild("uid").equalTo(String.valueOf(currentUser.getUserid()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            participantsModel = snapshot.getValue(ParticipantsModel.class);


                            myGroupRole = "" + snapshot.child("role").getValue();

                            //chatClearDate=participantsModel.getClearDate();

                            chatClearDate="" + snapshot.child("clearDate").getValue();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadParticipants() {

        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Groups");
        dref.child(groupId).child("Participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //get uid from Group > Participants
                    ParticipantsModel participantsModel = snapshot.getValue(ParticipantsModel.class);
                    if (!participantsModel.getUid().equals(firebaseUser.getUid())){
                        participantsList.add(participantsModel);
                    }

                    String uid = "" + snapshot.child("uid").getValue();
                    Log.e("mygroupId", "memberid : " + uid);

                    et_msg.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.toString().trim().length()==0){
                                UpdatetypingStatus("noOne");
                            }
                            else {

                                UpdatetypingStatus(uid); //userId of receiver
                                //UpdatetypingStatus(userModel.getId()); //userId of receiver
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });


                    //get info of user using uid
                    reference.orderByChild("id").equalTo(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                firebasePartnerModel = ds.getValue(FirebaseUserModel.class);
                                //FirebaseUserModel firebasePartnerModel = ds.getValue(FirebaseUserModel.class);

                                String name = (String) ds.child("username").getValue();

                                Log.e("mygroupId", "2" + name);

                                if (stringArrayList.contains(name)){
                                }
                                else {
                                    stringArrayList.add(name);
                                }


                                //stringArrayList.add(name);

                            }

                            Log.e("strList", "strList" + stringArrayList);
                            StringBuilder stringBuilder = new StringBuilder();
                            //sort array list
                            Collections.sort(stringArrayList);

                            Log.e("strList", "strList" + stringArrayList);

                            for (String ele : stringArrayList) {

                                stringBuilder.append(ele + ",");

                            }
                            String str = String.valueOf(stringBuilder);
                            if (str.endsWith(",")) {
                                str = str.substring(0, str.length() - 1);
                            }

                            if (firebasePartnerModel.getTypingTo().equals(firebaseUser.getUid())){

                                //txt_members.setText(userModel.getUsername()+" is typing..");

                                if (firebasePartnerModel.getId().equals(firebaseUser.getUid())){
                                    txt_members.setText(str);
                                    //txt_members.setText("");
                                }
                                else {
                                    str="";
                                    txt_members.setText(firebasePartnerModel.getUsername()+" is typing..");
                                }

                            }



                            //txt_members.setText(str);
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

    private void loadGroupMessages() {

        groupChatList = new ArrayList<>();

        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Groups");
        dref.keepSynced(true);

        dref.child(groupId).child("Messages").orderByChild("msgTime").limitToLast(100)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.e(TAG, "onDataChange: " + dataSnapshot.getValue());
                        groupChatList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                FirebaseGroupChatModel model = snapshot.getValue(FirebaseGroupChatModel.class);
                                SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");

                                //Log.e(TAG, "onDataChange: date1 "+participantsModel.getClearDate() );
                                Log.e(TAG, "onDataChange: date1 "+chatClearDate);
                                Log.e(TAG, "onDataChange: date2 "+model.getMsgTime() );

                            try {
                                //Date date1 = dateTime.parse(participantsModel.getClearDate());
                                Date date1 = dateTime.parse(chatClearDate);
                                Date date2 = dateTime.parse(model.getMsgTime());

                                if (date1.compareTo(date2)<0){
                                    // if (!dateTime.parse(model.getMsgTime()).before(dateTime.parse("26/05/2020 09:21:00 AM")))   {
                                    groupChatList.add(model);
                                }

                                //groupChatList.add(model);

                            } catch (Exception e) {
                                Log.e(TAG, "onDataChange: edate " + e.getMessage());
                            }
                        }
                        bindList(groupChatList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void bindList(ArrayList<FirebaseGroupChatModel> list) {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(GroupChattingActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        rv_groupChat.setLayoutManager(linearLayoutManager);
        rv_groupChat.setItemAnimator(new DefaultItemAnimator());
        rv_groupChat.setHasFixedSize(true);

        try {
            messageSwipeController = new MessageSwipeController(GroupChattingActivity.this, new SwipeControllerActions() {
                @Override
                public void showReplyUI(int position) {
                    try {
                        FirebaseGroupChatModel replyModel = list.get(position);
                        showQuotedMessage(replyModel);
                        Log.e("replymodel", "replymodel" + replyModel.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, ItemTouchHelper.RIGHT);

            new ItemTouchHelper(messageSwipeController).attachToRecyclerView(rv_groupChat);

            //groupChatAdapter=new GroupChatAdapter(GroupChattingActivity.this,groupChatList,groupId);

            groupChatAdapter = new GroupChatAdapter(GroupChattingActivity.this, list, groupId, new GroupChatAdapter.ItemClickListener() {

                @Override
                public void onItemClick(View v, int adapterPosition) {

                    v.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.on_clicked));
                    Log.e(TAG, "onItemClick: clicked");
                    final FirebaseGroupChatModel model = list.get(adapterPosition);
                    // if media is not downloaded and media type is not link or text
                    File file = new File(GetLocalFilePath(model));
                    Log.e(TAG, "onItemClick: file "+file.exists()+" "+file);
                    if (file.exists()) {
                        //show file
                        if (model.getMediatype() == 1 ||
                                model.getMediatype() == 2 ||
                                model.getMediatype() == 3 ||
                                model.getMediatype() == 4) {
                            Log.e(TAG, "onMessageClick: " + model.getMessage());
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("chatModel", model);
                            if (model.getSender() == firebaseAuth.getUid()) {
                                bundle.putString("sender_name", "You");
                            } else  {
                                bundle.putString("sender_name", model.getSenderName());
                            }
                            Utility.launchActivity(GroupChattingActivity.this, GrpDetailsChattingActivity.class, false, bundle);
                        }

                        // else if (model.getMessage().endsWith(".pdf")) {
                        else {
                            try {
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
                        }
                    }
                    else if (model.getMediatype() == 8){
                        Log.e(TAG, "onItemClick: contact clled" );
                        OpenContactList(model);
                    }
                }

                @Override
                public void onMessageClick(View v, int adapterPosition) {
                    FirebaseGroupChatModel model = list.get(adapterPosition);
                    //FirebaseGroupChatModel model = groupChatList.get(adapterPosition);
                    Log.e(TAG, "onMessageClick: clicked");
                }

                @Override
                public void onMessageLongClick(View v, int adapterPosition) {

                    FirebaseGroupChatModel model = list.get(adapterPosition);
                    model.setSelected(!model.isSelected());

                    if (model.isSelected()) {
                        //selected
                        if ((model.getMediatype() != 6 && model.getMediatype() != 7) && model.getMediatype() != 8) {
                        //if (model.getMediatype() != 6 && model.getMediatype() != 7) {
                            File checkFile = new File(GetLocalFilePath(model));

                            iv_copy.setVisibility(View.GONE);
                            Log.e("checkfile","checkfile"+checkFile.getPath());

                            if (checkFile.exists()) {
                                v.setBackgroundColor(Color.CYAN);
                                selectedList.add(model);
                                txtTitleBar.setText(""+selectedList.size());
                                //groupChatList.add(model);
                                Log.e(TAG, "onMessageLongClick: selected " + model.getMessage());
                            } else {
                                Toast.makeText(activity, "media Not exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if (model.getMediatype() == 8){
                            iv_copy.setVisibility(View.GONE);

                            v.setBackgroundColor(Color.CYAN);
                            selectedList.add(model);
                            txtTitleBar.setText(""+selectedList.size());
                            //groupChatList.add(model);
                            Log.e(TAG, "onMessageLongClick: selected " + model.getMessage());
                        }
                        else {
                            v.setBackgroundColor(Color.CYAN);
                            selectedList.add(model);
                            txtTitleBar.setText("" + selectedList.size());
                            Log.e(TAG, "onMessageLongClick: selected " + model.getMessage());
                        }

                    } else {
                        //not selected
                        v.setBackgroundColor(Color.TRANSPARENT);
                        selectedList.remove(model);
                        txtTitleBar.setText("" + selectedList.size());
                        Log.e(TAG, "onMessageLongClick: removed " + model.getMessage());
                    }
                    toolbar.setVisibility(View.GONE);
                    toolbar1.setVisibility(View.VISIBLE);
                    setSupportActionBar(toolbar1);
                }
            });

            //groupChatAdapter=new GroupChatAdapter(GroupChattingActivity.this,groupChatList);

            rv_groupChat.setAdapter(groupChatAdapter);

            Log.e("groupchatlist", "" + list.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //private String GetMyLocalFilePath(FirebaseGroupChatModel model) {
    private String GetMyLocalFilePath(int mediaType,String senderId,int type,String message) {
        String filename = "";

        //1 img //2 gif //3 audio //4 video //5 document //6 url // 7 text
        //switch (model.getMediatype()) {
        switch (mediaType) {
            case 1:
                //if (model.getSender() == firebaseUser.getUid()) {
                //if (senderId == firebaseUser.getUid()) {
                if (senderId.equals(firebaseUser.getUid())) {
                    //filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_IMAGE_PATH + "Sent/" + model.getMessage();
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_IMAGE_PATH + "Sent/" + message;

                    // filename="Chatnow/Media/Chatnow Images/"+model.getMessage();
                } else {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_IMAGE_PATH + message;
                }
                break;

            case 2:
                //if (model.getType() == 2) {
                //if (type == 2) {

                if (senderId.equals(firebaseUser.getUid())) {

                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_GIF_PATH + "Sent/" + message;

                }
                else {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_GIF_PATH + message;

                }
                break;

            case 3:
                if (senderId.equals(firebaseUser.getUid())) {
                //if (model.getType() == 2) {
                //if (type == 2) {

                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_AUDIO_PATH + "Sent/" + message;

                    //filename="Chatnow/Media/Chatnow Audio/"+model.getMessage();
                }
                else {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_AUDIO_PATH + message;

                }
                break;

            case 4:
                if (senderId.equals(firebaseUser.getUid())) {
                //if (model.getType() == 2) {
                //if (type == 2) {

                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_VIDEO_PATH + "Sent/" + message;


                    //filename="Chatnow/Media/Chatnow Videos/"+model.getMessage();
                }
                else {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_VIDEO_PATH + message;

                }
                break;

            case 5:
                if (senderId.equals(firebaseUser.getUid())) {
                //if (model.getType() == 2) {
                //if (type == 2) {

                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_DOCUMENT_PATH + "Sent/" + message;

                    //filename="Chatnow/Media/Chatnow Videos/"+model.getMessage();
                } else {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_DOCUMENT_PATH + message;

                }
                break;
        }
        return filename;
    }


    private String GetLocalFilePath(FirebaseGroupChatModel model) {

        String filename = "";

        //1 img //2 gif //3 audio //4 video //5 document //6 url // 7 text
        switch (model.getMediatype()) {
            case 1:
                //if (model.getSender() == firebaseUser.getUid()) {
                if (model.getSender().equals(firebaseUser.getUid())) {

                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_IMAGE_PATH + "Sent/" + model.getMessage();

                    // filename="Chatnow/Media/Chatnow Images/"+model.getMessage();
                } else {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_IMAGE_PATH + model.getMessage();
                }
                break;

            case 2:
                if (model.getSender().equals(firebaseUser.getUid())) {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_GIF_PATH + "Sent/" + model.getMessage();

                } else {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_GIF_PATH + model.getMessage();

                }
                break;

            case 3:
                if (model.getSender().equals(firebaseUser.getUid())) {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_AUDIO_PATH + "Sent/" + model.getMessage();
                    //filename="Chatnow/Media/Chatnow Audio/"+model.getMessage();
                } else {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_AUDIO_PATH + model.getMessage();
                }
                break;

            case 4:
                if (model.getSender().equals(firebaseUser.getUid())) {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_VIDEO_PATH + "Sent/" + model.getMessage();
                    //filename="Chatnow/Media/Chatnow Videos/"+model.getMessage();
                } else {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_VIDEO_PATH + model.getMessage();
                }
                break;

            case 5:
                if (model.getSender().equals(firebaseUser.getUid())) {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_DOCUMENT_PATH + "Sent/" + model.getMessage();

                    //filename="Chatnow/Media/Chatnow Videos/"+model.getMessage();
                } else {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_DOCUMENT_PATH + model.getMessage();
                }
                break;
        }
        return filename;
    }


    private void showQuotedMessage(FirebaseGroupChatModel replyModel) {

        et_msg.requestFocus();
        replyToMsg = replyModel.getMsgUid();
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(et_msg, InputMethodManager.SHOW_IMPLICIT);

        replyToMsgModel = replyModel;
        if (replyModel.getSender()==firebaseUser.getUid()){
            txtQuotedMsg.setText("You");
        }else {
            //txtQuotedMsg.setText(firebasePartnerModel.getUsername());

            DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Users");
            dref.keepSynced(true);

            dref.orderByChild("id").equalTo(replyModel.getSender())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                FirebaseUserModel userModel = snapshot1.getValue(FirebaseUserModel.class);
                                Log.e("sendername", "" + userModel.getUsername());
                                //holder.tv_senderName.setText(userModel.getFullname());
                                txtQuotedMsg.setText(userModel.getUsername());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

        }
        rl_reply_layout.setVisibility(View.VISIBLE);

        //1 img //2 gif //3 audio //4 video //5 document //6 url // 7 text
        String media = Constants.URL_CHATTING_MEDIA + replyModel.getMessage();
        Log.e(TAG, "showQuotedMessage: media "+media );
        switch (replyModel.getMediatype()){
            case 1:
                iv_preview.setVisibility(View.VISIBLE);
                txtQuotedMsgType.setText(" Image");
                txtQuotedMsgType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_image_small, 0, 0, 0);
                Glide.with(activity).load(media).centerCrop().placeholder(R.drawable.ic_gallery).dontAnimate().into(iv_preview);
                break;

            case 2:
                iv_preview.setVisibility(View.VISIBLE);
                txtQuotedMsgType.setText(" GIF");
                txtQuotedMsgType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_image_small, 0, 0, 0);
                Glide.with(activity).load(media).centerCrop().placeholder(R.drawable.ic_video).dontAnimate().into(iv_preview);
                break;

            case 3:
                iv_preview.setVisibility(View.GONE);
                txtQuotedMsgType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_audio_small, 0, 0, 0);
                txtQuotedMsgType.setText(" Audio");
                break;

            case 4:
                iv_preview.setVisibility(View.VISIBLE);
                txtQuotedMsgType.setText(" Video");
                txtQuotedMsgType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_video_small, 0, 0, 0);
                Glide.with(activity).load(media).centerCrop().placeholder(R.drawable.ic_video).dontAnimate().into(iv_preview);
                break;

            case 5:
                txtQuotedMsgType.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                iv_preview.setVisibility(View.GONE);
                txtQuotedMsgType.setText(replyModel.getMessage());
                break;

            case 6:
                iv_preview.setVisibility(View.GONE);
                txtQuotedMsgType.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                txtQuotedMsgType.setText(replyModel.getMessage());
                break;

            case 7:
                iv_preview.setVisibility(View.GONE);
                txtQuotedMsgType.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                txtQuotedMsgType.setText(replyModel.getMessage());
                break;

            case 8:
                iv_preview.setVisibility(View.GONE);
                txtQuotedMsgType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_contact_small, 0, 0, 0);
                txtQuotedMsgType.setText("Contact : "+replyModel.getMessage());
                break;
        }

/*
        et_msg.requestFocus();

        replyToMsg = replyModel.getMsgUid();

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(et_msg, InputMethodManager.SHOW_IMPLICIT);

        replyToMsgModel = replyModel;
        if (replyModel.getType() == 1) {
            txtQuotedMsg.setText("You");
        } else {

            //txtQuotedMsg.setText(replyModel.getSender());

            //get info of sender of last message
            DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Users");
            dref.keepSynced(true);

            dref.orderByChild("id").equalTo(replyModel.getSender())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {

                                FirebaseUserModel userModel = snapshot1.getValue(FirebaseUserModel.class);
                                //UserModel userModel=snapshot1.getValue(UserModel.class);

                                //Log.e("sendername",""+userModel.getFullname());
                                Log.e("sendername", "" + userModel.getUsername());

                                //holder.tv_senderName.setText(userModel.getFullname());
                                txtQuotedMsg.setText(userModel.getUsername());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

            //txtQuotedMsg.setText("some name");
        }

        rl_reply_layout.setVisibility(View.VISIBLE);

        //1 img //2 gif //3 audio //4 video //5 document //6 url // 7 text
        String media = replyModel.getMessage();

        Log.e(TAG, "showQuotedMessage: media " + media);

        switch (replyModel.getMediatype()) {
            case 1:
                iv_preview.setVisibility(View.VISIBLE);
                txtQuotedMsgType.setText(" Image");
                txtQuotedMsgType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_image_small, 0, 0, 0);
                Glide.with(activity).load(media).centerCrop().placeholder(R.drawable.ic_gallery).dontAnimate().into(iv_preview);
                break;

            case 2:
                iv_preview.setVisibility(View.VISIBLE);
                txtQuotedMsgType.setText(" GIF");
                txtQuotedMsgType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_image_small, 0, 0, 0);
                Glide.with(activity).load(media).centerCrop().placeholder(R.drawable.ic_video).dontAnimate().into(iv_preview);
                break;

            case 3:
                iv_preview.setVisibility(View.GONE);
                txtQuotedMsgType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_audio_small, 0, 0, 0);
                txtQuotedMsgType.setText(" Audio");
                break;

            case 4:
                iv_preview.setVisibility(View.VISIBLE);
                txtQuotedMsgType.setText(" Video");
                txtQuotedMsgType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_video_small, 0, 0, 0);
                Glide.with(activity).load(media).centerCrop().placeholder(R.drawable.ic_video).dontAnimate().into(iv_preview);
                break;

            case 5:
                txtQuotedMsgType.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                iv_preview.setVisibility(View.GONE);
                txtQuotedMsgType.setText(replyModel.getMessage());
                break;

            case 6:
                iv_preview.setVisibility(View.GONE);
                txtQuotedMsgType.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                txtQuotedMsgType.setText(replyModel.getMessage());
                break;

            case 7:
                iv_preview.setVisibility(View.GONE);
                txtQuotedMsgType.setVisibility(View.VISIBLE);
                txtQuotedMsgType.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                txtQuotedMsgType.setText(replyModel.getMessage());
                Log.e(TAG, "showQuotedMessage: media " + replyModel.getMessage());

                //Log.e("groupchatlist","groupchatlist"+replyModel.getMessage());
                break;
        }
*/

    }

    private void loadGroupInfo(String groupId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {
                    groupChatModel = dataSnapshot.getValue(GroupChatList.class);

                    groupName=groupChatModel.getGroupTitle();

                    tv_groupName.setText(groupChatModel.getGroupTitle());

                    Log.e("groupId", groupChatModel.getGroupTitle());
                    String groupIcon = groupChatModel.getGroupIcon();

                    if (groupIcon.equals("default")) {
                        iv_cprofile_pic.setImageResource(R.drawable.ic_male);
                    } else {
                        Picasso.get().load(groupIcon).placeholder(R.drawable.ic_male).into(iv_cprofile_pic);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void btnListener() {

        ll_groupName.setOnClickListener(this);
        viewMenuIconBack.setOnClickListener(this);
        iv_send.setOnClickListener(this);
        iv_more.setOnClickListener(this);
        iv_attachment.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_copy.setOnClickListener(this);
        iv_delete.setOnClickListener(this);
        iv_reply.setOnClickListener(this);
        iv_forward.setOnClickListener(this);

        search_clear.setOnClickListener(this);
        viewMenuIconBack2.setOnClickListener(this);

    }

    private void bindView() {
        ll_groupName = findViewById(R.id.ll_groupName);
        rv_groupChat = findViewById(R.id.rv_groupChat);
        iv_attachment = findViewById(R.id.iv_attachment);

        viewMenuIconBack = findViewById(R.id.viewMenuIconBack);

        txtTitleBar = findViewById(R.id.txtTitleBar);

        iv_preview = findViewById(R.id.iv_preview);
        txtQuotedMsgType = findViewById(R.id.txtQuotedMsgType);
        cancelButton = findViewById(R.id.cancelButton);
        txtQuotedMsg = findViewById(R.id.txtQuotedMsg);
        rl_reply_layout = findViewById(R.id.rl_reply_layout);

        et_msg = findViewById(R.id.et_msg);
        iv_attachment = findViewById(R.id.iv_attachment);
        iv_send = findViewById(R.id.iv_send);
        tv_groupName = findViewById(R.id.tv_groupName);
        iv_more = findViewById(R.id.iv_more);
        iv_cprofile_pic = findViewById(R.id.iv_cprofile_pic);
        txt_members = findViewById(R.id.txt_members);
        viewMenuIconBack2 = findViewById(R.id.viewMenuIconBack2);

        et_search_view = findViewById(R.id.et_search_view);
        search_clear = findViewById(R.id.search_clear);

        toolbar_search = findViewById(R.id.toolbar_search);

        toolbar1 = findViewById(R.id.toolbar1);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        iv_back = findViewById(R.id.iv_back);
        iv_copy = findViewById(R.id.iv_copy);
        iv_delete = findViewById(R.id.iv_delete);
        iv_reply = findViewById(R.id.iv_reply);
        iv_forward = findViewById(R.id.iv_forward);
    }

    @Override
    public void onClick(View v) {

        v.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.on_clicked));

        switch (v.getId()) {

            case R.id.ll_groupName:
                //Utility.launchActivity(this, GroupInfoActivity.class,false);
                /*Intent intent = new Intent(this, GroupInfoActivity.class);
                intent.putExtra("groupId", groupId);
                startActivity(intent);*/

                Bundle chatBundle = new Bundle();

                chatBundle.putString("groupId",groupId);

                Log.e("groupInfo","groupInfo chat"+groupId);


                Utility.launchActivity(GroupChattingActivity.this, GroupInfoActivity.class, false, chatBundle);

                break;

            case R.id.viewMenuIconBack:
                onBackPressed();
                break;

            case R.id.iv_send:
                rl_reply_layout.setVisibility(View.GONE);
                String message = et_msg.getText().toString().trim();
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(this, "can't send empty message", Toast.LENGTH_SHORT).show();
                } else {
                    SenderId = firebaseUser.getUid();
                    sendMessage(message, SenderId, groupId,0);
                }
                break;

            case R.id.iv_more:
                showMenu(v);
                break;

            case R.id.cancelButton:
                rl_reply_layout.setVisibility(View.GONE);
                //replyToMsg = null;
                replyToMsg = "";
                replyToMsgModel = null;
                break;

            case R.id.iv_forward:
                Bundle bundle1 = new Bundle();
                if (selectedList.size() > 0) {
                    bundle1.putParcelableArrayList("multiSelectedList", selectedList);
                    Log.e("msgforward", "msgforward 1" + selectedList);
                    Utility.launchActivity(activity, GroupMessageForwardActivity.class, true, bundle1);

                } else {
                    Toast.makeText(activity, "No Message Selected", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.iv_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Delete");
                builder.setMessage("Do you want to delete this message?");
                //delete button
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (selectedList != null) {

                            for (FirebaseGroupChatModel model : selectedList) {
                                deleteMessage(model, groupId);
                            }
                        }
                    }
                });

                //cancel button
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss dialog
                        dialog.dismiss();
                    }
                });

                builder.create().show();

                break;

            case R.id.iv_copy:

                String text = "";

                if (selectedList.size() > 0) {
                    for (FirebaseGroupChatModel msgModel : selectedList) {
                        //text = text+msgModel.getMessage();
                        text = text + msgModel.getMsgTime() + " " + msgModel.getMessage() +"\n";
                    }
                }

                ClipData clipData = ClipData.newPlainText("Source Text", text);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(this, "text copied", Toast.LENGTH_SHORT).show();

                /*
                if (selectedList != null) {

                    for (FirebaseGroupChatModel model : selectedList) {

                        //sendMessage(model.getMessage(),model.getSender(),myGroupId);

                        ClipData clipData = ClipData.newPlainText("Source Text",model.getMessage());
                        clipboardManager.setPrimaryClip(clipData);
                    }
                    Log.e("copiedmessages","copiedmessages"+selectedList);

                    Toast.makeText(this, "message copied", Toast.LENGTH_SHORT).show();
                    toolbar1.setVisibility(View.GONE);
                    toolbar.setVisibility(View.VISIBLE);
                    setSupportActionBar(toolbar);

                }*/

                break;


            case R.id.viewMenuIconBack2:
                toolbar_search.setVisibility(View.GONE);
                toolbar1.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                setSupportActionBar(toolbar);
                break;

            case R.id.search_clear:
                et_search_view.setText("");
                et_search_view.requestFocus();
                break;

            case R.id.iv_attachment:
                Bundle bundle = new Bundle();
                bundle.putString("myGroupId", myGroupId);
                bundle.putParcelable("groupChatModel", groupChatModel);
                BottomSheetGroupFileOptionsFragment bottomSheetFragment1 = new BottomSheetGroupFileOptionsFragment();
                bottomSheetFragment1.show(getSupportFragmentManager(), bottomSheetFragment1.TAG);
                bottomSheetFragment1.setArguments(bundle);
                break;

            default:
                break;

        }

    }

    private void deleteMessage(FirebaseGroupChatModel model, String groupId) {

        String msgUid = model.getMsgUid();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Groups");

        Query query = dbRef.child(groupId).child("Messages").orderByChild("msgUid")
                .equalTo(msgUid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    HashMap<String, Object> hashMap = new HashMap<>();

                    hashMap.put("message", "This message was deleted...");
                    hashMap.put("isDeleted", 1);
                    snapshot.getRef().updateChildren(hashMap);

                    Toast.makeText(activity, "message deleted", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);// to implement on click event on items of menu
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_group, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.createGroup:

                startActivity(new Intent(GroupChattingActivity.this, CreateNewGroupActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                return true;
            case R.id.addParticipant:
                //startActivity(new Intent(GroupChattingActivity.this,AddGroupParticipantActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Intent intent = new Intent(this, AddGroupParticipantActivity.class);
                intent.putExtra("groupId", groupId);
                startActivity(intent);

                return true;
            case R.id.groupInfo:

                //startActivity(new Intent(GroupChattingActivity.this,AddGroupParticipantActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                /*Intent groupInfoIntent = new Intent(this, GroupInfoActivity.class);
                groupInfoIntent.putExtra("groupId", groupId);
                startActivity(groupInfoIntent);*/

                //Bundle chatBundle = new Bundle();

                //chatBundle.putParcelable("groupChatModel",groupChatModel);
                //Utility.launchActivity(GroupChattingActivity.this, GroupInfoActivity.class, false, chatBundle);


                Bundle chatBundle = new Bundle();

                chatBundle.putString("groupId",groupId);

                Log.e("groupInfo","groupInfo chat"+groupId);


                Utility.launchActivity(GroupChattingActivity.this, GroupInfoActivity.class, false, chatBundle);


                return true;
            case R.id.clearGroupChat:

                clearChat(groupId);

                //Utility.launchActivity(this, DataGroupingActivity.class,true);

                return true;


            case R.id.searchMessage:

                toolbar.setVisibility(View.GONE);
                toolbar1.setVisibility(View.GONE);
                toolbar_search.setVisibility(View.VISIBLE);
                setSupportActionBar(toolbar_search);

                return true;

            case R.id.itemMedia:

                Bundle bundle = new Bundle();
                bundle.putString("groupId",groupId);

                bundle.putParcelable("groupChatModel",groupChatModel);
                Utility.launchActivity(GroupChattingActivity.this, GroupMediaListActivity.class, false, bundle);

                return true;
        }
        return false;
    }

    private void clearChat(String groupId) {


        String timeStamp;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm a");
        SimpleDateFormat clearTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
        String clearTimeStamp=clearTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("clearDate",clearTimeStamp);

        //update role in db
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        //ref.child(groupId).child("Participants").child(participantsModel.getUid()).updateChildren(hashMap)
        ref.child(groupId).child("Participants").child(firebaseUser.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //make admin
                        Toast.makeText(GroupChattingActivity.this, "chat Clear", Toast.LENGTH_SHORT).show();
                        loadGroupMessages();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed making admin
                        Toast.makeText(GroupChattingActivity.this, "chat Clear failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    //private void sendMessage(String message, String SenderId, String groupId, int forward) {

    private void forwardMessage(FirebaseGroupChatModel model, String groupId, int forward) {

        Log.e("msgforward","msgforward 6"+model.toString());

        String replyToMsgId;
        String replyToMsgSenderId;
        String replyToMsgText;
        int replyToMsgTextMediaType;


        int mediatype = model.getMediatype();

        Log.e("msgforward","msgforward 6.1"+model.toString());

        /*
        if (Utility.containsURL(message)) {
            mediatype = 6;
        }
        */

        replyToMsgId = "-1";
        replyToMsgSenderId = "";
        replyToMsgText = " ";
        replyToMsgTextMediaType = -1;


        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        String msgTime = dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30:20 PM
        String msgDate = date.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30:20 PM

        Log.e("msgforward","msgforward 6.2"+model.toString());


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",currentUser.getFirebaseuserid());
        //hashMap.put("sender", firebaseUser.getUid());
        hashMap.put("message", model.getMessage());
        hashMap.put("msgTime", msgTime);    // message time
        hashMap.put("msgDate", msgDate);
        //to generate unique random message uid
        // Get the size n
        int n = 10;

        Log.e("msgforward","msgforward 6.3"+model.toString());

        String msgUid = currentUser.getFirebaseuserid()+ getAlphaNumericString(n);
        //String msgUid = firebaseUser.getUid()+ getAlphaNumericString(n);
        //String msgUid=firebaseAuth.getUid()+getAlphaNumericString(n);
        //String msgUid=String.valueOf(currentUser.getUserid())+getAlphaNumericString(n);
        Log.e("msgUid", "" + msgUid);

        hashMap.put("msgUid", msgUid);
        hashMap.put("msgType","text");
        hashMap.put("isread", 0);
        hashMap.put("isdownloaded", 1);
        hashMap.put("mediatype", mediatype);

        int type=1;
        hashMap.put("type",type);
        //hashMap.put("type", 1);
        Log.e("msgforward","msgforward 6.4"+model.toString());

        hashMap.put("isDeleted", 0);

        hashMap.put("isForward", forward);
        hashMap.put("msgStatus", "sent");
        hashMap.put("senderName",currentUser.getFullname());
        Log.e("msgforward","msgforward 6.5"+model.toString());

        //hashMap.put("senderName", firebaseUser.getDisplayName());
        hashMap.put("replyToMsgId", replyToMsgId);
        hashMap.put("replyToMsgSenderId", replyToMsgSenderId);
        hashMap.put("replyToMsgText", replyToMsgText);
        hashMap.put("replyToMsgTextMediaType", replyToMsgTextMediaType);

        Log.e("msgforward","msgforward 7");

        //ref.child("Groups").child(groupId).child("Messages").child(msgUid).push().setValue(hashMap);

        //Log.e("msgforward","msgforward 8"+model.toString());

        //sendTextNotification(msgUid, model.getMediatype(),model.getMessage(), msgTime, replyToMsgId, replyToMsgSenderId, replyToMsgText, replyToMsgTextMediaType);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Messages").child(msgUid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //message sent
                        //Toast.makeText(GroupChattingActivity.this, "Message Sent Successfully..", Toast.LENGTH_SHORT).show();

                        et_msg.setText("");

                        //replyToMsg = null;
                        replyToMsg = "";
                        replyToMsgModel = null;

                        Log.e("msgforward","msgforward 8"+model.getMessage());

                        //sendTextNotification(msgUid, model.getMediatype(),model.getMessage(), msgTime, replyToMsgId, replyToMsgSenderId, replyToMsgText, replyToMsgTextMediaType);
                        sendTextNotification(msgUid, model.getMediatype(),model.getMessage(), msgTime, replyToMsgId, replyToMsgSenderId, replyToMsgText, replyToMsgTextMediaType,type);

                        //sendTextNotification(msgUid, model.getMediatype(),model.getMessage(), msgTime, replyToMsgId, replyToMsgSenderId, replyToMsgText, replyToMsgTextMediaType);

                        saveFilesToSd(model);

                        groupChatAdapter.notifyDataSetChanged();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //message sending failed

                        Log.e("msgforward","msgforward 8.1");

                        Toast.makeText(GroupChattingActivity.this, "message sending failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //private void saveFilesToSd(int mediatype, String message, MediaPreviewActivity trainerChatActivity, MediaFile media) {
    private void saveFilesToSd(FirebaseGroupChatModel model) {

        Log.e("file", "file not exists 2 "+model.getMessage());

        String path = "" ;

        switch (model.getMediatype()){
            case 1:
                path = Constants.OFFLINE_IMAGE_PATH + "Sent/" + model.getMessage();
                break;

            case 2:
                path = Constants.OFFLINE_GIF_PATH + "Sent/" + model.getMessage();
                break;
            case 3:
                path = Constants.OFFLINE_AUDIO_PATH + "Sent/" + model.getMessage();
                break;

            case 4:
                path = Constants.OFFLINE_VIDEO_PATH + "Sent/" + model.getMessage();
                break;

            case 5:
                path = Constants.OFFLINE_DOCUMENT_PATH + "Sent/" + model.getMessage();
                break;

        }

        try {

            File source = new File(GetLocalFilePath(model));

            File destination = new File(Environment.getExternalStorageDirectory() + "/" + path);
            if (source.exists()) {
                FileChannel src = new FileInputStream(source).getChannel();
                FileChannel dst = new FileOutputStream(destination).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "saveNotificationToSqlite: e "+e.getMessage() );
        }

        /*
        if (count >= files.size() - 1) {
            SystemClock.sleep(3000);
            Log.e(TAG, "run: caalled ");
            Intent intent1 = new Intent();
            setResult(2, intent1);
            finish();
        }
        */
    }


    private void sendMessage(String message, String SenderId, String groupId, int forward) {

        String replyToMsgId;
        String replyToMsgSenderId;
        String replyToMsgText;
        int replyToMsgTextMediaType;

        int mediatype = 7;

        if (Utility.containsURL(message)) {
            mediatype = 6;
        }


        if (replyToMsg == "") {
            replyToMsgId = "-1";
            replyToMsgSenderId = "-1";
            replyToMsgText = " ";
            replyToMsgTextMediaType = -1;

        } else {
            replyToMsgId = replyToMsg;
            replyToMsgSenderId = replyToMsgModel.getSender();
            replyToMsgText = replyToMsgModel.getMessage();
            replyToMsgTextMediaType = replyToMsgModel.getMediatype();
        }

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        String msgTime = dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30:20 PM
        String msgDate = date.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30:20 PM

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", SenderId);
        hashMap.put("message", message);
        hashMap.put("msgTime", msgTime);    // message time
        hashMap.put("msgDate", msgDate);
        //to generate unique random message uid
        // Get the size n
        int n = 10;

        String msgUid = SenderId + getAlphaNumericString(n);
        //String msgUid=firebaseAuth.getUid()+getAlphaNumericString(n);
        //String msgUid=String.valueOf(currentUser.getUserid())+getAlphaNumericString(n);
        Log.e("msgUid", "" + msgUid);

        hashMap.put("msgUid", msgUid);
        hashMap.put("msgType", "text");
        hashMap.put("isread", 0);
        hashMap.put("isdownloaded", 1);

        hashMap.put("isDeleted", 0);

        hashMap.put("mediatype", mediatype);

        int type=1;

        hashMap.put("type", type);
        hashMap.put("isForward", forward);
        hashMap.put("msgStatus", "sent");
        hashMap.put("senderName",currentUser.getFullname());
        //hashMap.put("senderName", firebaseUser.getDisplayName());
        hashMap.put("replyToMsgId", replyToMsgId);
        hashMap.put("replyToMsgSenderId", replyToMsgSenderId);
        hashMap.put("replyToMsgText", replyToMsgText);
        hashMap.put("replyToMsgTextMediaType", replyToMsgTextMediaType);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Messages").child(msgUid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //message sent
                        //Toast.makeText(GroupChattingActivity.this, "Message Sent Successfully..", Toast.LENGTH_SHORT).show();
                        et_msg.setText("");
                        replyToMsg = "";
                        replyToMsgModel = null;

                        //sendTextNotification(msgUid, 7, message, msgTime, replyToMsgId, replyToMsgSenderId, replyToMsgText, replyToMsgTextMediaType);
                        sendTextNotification(msgUid, 7, message, msgTime, replyToMsgId, replyToMsgSenderId, replyToMsgText, replyToMsgTextMediaType,type);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //message sending failed
                        Toast.makeText(GroupChattingActivity.this, "message sending failed", Toast.LENGTH_SHORT).show();
                    }
                });

        et_msg.setText("");
        replyToMsg = "";
        replyToMsgModel = null;
    }

    static String getAlphaNumericString(int n) {
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
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    public void UpdateOnlineStatus(String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    //private void sendTextNotification(String msgUid, int mediatype, String message, String msgTime, String replyToMsgId, String replyToMsgSenderId, String replyToMsgText, int replyToMsgTextMediaType) {
    private void sendTextNotification(String msgUid, int mediatype, String message, String msgTime, String replyToMsgId, String replyToMsgSenderId, String replyToMsgText, int replyToMsgTextMediaType,int type) {

        Log.e("msgforward","msgforward 9");

        String to = "";
        if (participantsList != null) {
            for (ParticipantsModel participant : participantsList) {
                to = participant.getToken();
                Log.e(TAG, "sendTextNotification: " + to);
                JSONObject notification = new JSONObject();
                JSONObject notifcationBody = new JSONObject();

                try {
                    notifcationBody.put("isFromGroup", 1);
                    notifcationBody.put("groupId", myGroupId);
                    notifcationBody.put("title", currentUser.getFullname());
                    notifcationBody.put("message", message);
                    notifcationBody.put("senderid", currentUser.getUserid());
                    notifcationBody.put("mediatype", mediatype);
                    notifcationBody.put("msgUid", msgUid);
                    notifcationBody.put("created", msgTime);
                    notifcationBody.put("replyToMsgId", replyToMsgId);
                    notifcationBody.put("replyToMsgSenderId", replyToMsgSenderId);
                    notifcationBody.put("replyToMsgText", replyToMsgText);
                    notifcationBody.put("replyToMsgTextMediaType", replyToMsgTextMediaType);
                    notification.put("to", to);
                    notification.put("data", notifcationBody);

                } catch (JSONException e) {
                    Log.e(TAG, "onCreate: " + e.getMessage());
                }
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", Constants.GET_SERVER_KEY);
                    params.put("Content-Type", "application/json");

                    String finalReplyToMsgId = replyToMsgId;
                    AndroidNetworking.post(Constants.FCM_API)
                            .addJSONObjectBody(notification)
                            .addHeaders(params)
                            .setTag("test")
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.e("notification responce", "" + response.toString());

                                    Log.e("msgforward","msgforward 10");

                                    /*
                                    File checkFile = new File(GetMyLocalFilePath(mediatype,currentUser.getFirebaseuserid(),type,message));

                                    if (checkFile.exists()){

                                        Log.e("file", "file exists ");

                                    }else {
                                        Log.e("file", "file not exists ");

                                        //saveFilesToSd(finalMediatype, chatModel.getMessagess(),MediaPreviewActivity.this, media);
                                        //saveFilesToSd(model.getMediatype(), model.getMessage(),GroupChattingActivity.this);
                                        saveFilesToSd(mediatype, message,GroupChattingActivity.this);

                                    }
                                    */
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.e("notification:anError", "" + anError);
                                    Log.e("notification:anError", "" + anError.getErrorBody());
                                }

                            });

                    replyToMsg = "";
                    replyToMsgModel = null;

                } catch (Exception ex) {
                    Log.e("onUnknownError", "" + ex);
                }
            }
        }
        //  String to = "ftU0PTXttbk:APA91bFHUGKhBdDABO-cLoeI-2bNcqRg58yO79po2132Rkx0UpSAQiZMI5_zYBPDkllNe4skr0YxjdJp8ayD5haZen1-YAASnbJM8nyTbhWyAXhkXbjK7DKV9Twpl_XmDuknV2Bkdnc6";
    }

    private void OpenContactList(FirebaseGroupChatModel model) {
        ArrayList<FirebaseContactResult> results = new ArrayList<>();
        ArrayList<FirebaseMsgModel> msgresults = new ArrayList<>();
        results.clear();

        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Groups");
        dref.child(groupId).child("Messages").orderByChild("msgUid").equalTo(model.getMsgUid()).limitToFirst(10)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.e(TAG, "onDataChange: snapshot "+snapshot.getValue() );
                            FirebaseGroupChatModel model = snapshot.getValue(FirebaseGroupChatModel.class);
                            Log.e(TAG, "FirebaseGroupChatModel: "+model );
                            results.addAll(model.getContacts());
                            Log.e(TAG, "onDataChange: model "+results.toString() );
                        }
                        Log.e("MyTag", results.toString());
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("offchattingPartner", null);
                        bundle.putParcelableArrayList("contacts", results);
                        bundle.putInt("mediaType", 8);
                        bundle.putInt("isSent", 1);
                        Utility.launchActivityForResult(activity, SentContactPreviewActivity.class, bundle, 2);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onStart() {
        UpdateOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();


        UpdatetypingStatus("noOne");

        //reference.removeEventListener(seenListener);
        // to get current date with time
        SimpleDateFormat dform = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date obj = new Date();
        UpdateOnlineStatus(dform.format(obj));
    }

    @Override
    protected void onResume() {
        UpdateOnlineStatus("online");
        super.onResume();
        cancelAllNotifications();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            loadGroupMessages();
        } else if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                ArrayList<ContactResult> results = MultiContactPicker.obtainResult(data);
                Log.d("MyTag", results.get(0).getDisplayName());
                Bundle bundle = new Bundle();
                bundle.putString("myGroupId", myGroupId);
                bundle.putParcelableArrayList("contacts", results);
                bundle.putInt("mediaType", 8);
                bundle.putInt("isSent", 0);
                bundle.putParcelable("groupChatModel", groupChatModel);
                Utility.launchActivityForResult(activity, GrpContactPreviewActivity.class, bundle, 2);
            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("User closed the picker without selecting items.");
            }
        }
    }

    public void UpdatetypingStatus(String typing){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("typingTo",typing);
        reference.updateChildren(hashMap);
    }

    // url = file path or whatever suitable URL you want.
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private void cancelAllNotifications() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
