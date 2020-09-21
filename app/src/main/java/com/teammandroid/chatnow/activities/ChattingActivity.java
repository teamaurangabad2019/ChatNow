package com.teammandroid.chatnow.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.teammandroid.chatnow.BuildConfig;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.activities.preview_activity.CameraPreviewActivity;
import com.teammandroid.chatnow.activities.preview_activity.ContactPreviewActivity;
import com.teammandroid.chatnow.activities.preview_activity.SentContactPreviewActivity;
import com.teammandroid.chatnow.fragments.BottomSheetFileOptionsFragment;
import com.teammandroid.chatnow.interfaces.MessageSwipeController;
import com.teammandroid.chatnow.interfaces.SwipeControllerActions;
import com.teammandroid.chatnow.models.firebase.FirebaseContactResult;
import com.teammandroid.chatnow.models.firebase.FirebaseMsgModel;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;
import com.teammandroid.chatnow.offline.ChatDatabaseHelper;
import com.teammandroid.chatnow.offline.NotificationDatabaseHelper;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.pagination.OfflineChatTimeLineAdapter;
import com.teammandroid.chatnow.pagination.PaginationListener;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.teammandroid.chatnow.pagination.PaginationListener.PAGE_START;

public class ChattingActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener  {

    ImageView viewMenuIconBack;
    CircleImageView civ_profile;
    ImageView iv_back, iv_copy, iv_reply, iv_delete, iv_forward;

    @BindView(R.id.rv_chatDay)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    private OfflineChatTimeLineAdapter adapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    int itemCount = 0;

    String myMsg = "";
    // Get clipboard manager object.
    Object clipboardService;
    ClipboardManager clipboardManager;

    TextView txtTitleName;
    RecyclerView rv_chatDay;
    EditText et_msg;
    ImageView iv_send, iv_attachment;
    Bundle bundle;

    private int PICK_IMAGE_REQUEST = 1;

    final String TAG = ChattingActivity.class.getSimpleName();
    ChatDatabaseHelper dbHelper;

    private static final int CONTACT_PICKER_REQUEST = 9 ;
    private static final int IMAGE_PICK_CAMERA_CODE = 300;

    int UserId; //receiver when fetch // sender when sending msg
    int partnerId;  //sender when fetch // receiver when sending msg

    Toolbar toolbar, toolbar1;
    private ProgressDialog dialog;
    Activity activity;
    // private OfflineUserModel offchattingPartner;
    private UserModel onchattingPartner;
    private UserModel currentUser;
    private ArrayList<FirebaseMsgModel> multiSelectedList = new ArrayList<>();

    private ArrayList<FirebaseMsgModel> selectedList = new ArrayList<>();

   // private OfflineChatTimeLineAdapter mofflineAdapter;

    ValueEventListener seenListener;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    FirebaseUserModel firebasePartnerModel;

    RelativeLayout rl_reply_layout;
    TextView txtQuotedMsg, txtQuotedMsgType;
    ImageView cancelButton, iv_preview;
    TextView txtTitleBar;
    private String replyToMsg = "";
    private FirebaseMsgModel replyToMsgModel;

    TextView txtStatus;
    private ArrayList<FirebaseMsgModel> firebaseChatModel = new ArrayList<>();

    Toolbar toolbar_search;
    private String firebasePartnerId = "";
    private NotificationDatabaseHelper notificationSqliteOperations;
    String partnerFirebaseUserId;

    int blockFlag=0;
    public int amIBlockByPartner=0;
    public boolean amIBlockByPartnerFlag=false;
    int result=0;
    ImageView viewMenuIconBack2, search_clear;
    EditText et_search_view;
    private ArrayList<FirebaseMsgModel> groupSearchChatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        activity = ChattingActivity.this;

        ButterKnife.bind(this);
        swipeRefresh.setOnRefreshListener(this);

        dbHelper = new ChatDatabaseHelper(this);
        //here client is sender and trainer is receiver
        bindView();
        initListener();

        // Get clipboard manager object.
        clipboardService = getSystemService(CLIPBOARD_SERVICE);
        clipboardManager = (ClipboardManager) clipboardService;

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading page, please wait.");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        //  dialog.show();

        bundle = getIntent().getExtras();

        if (bundle != null) {
            partnerId = bundle.getInt("partnerId");
            firebasePartnerId = bundle.getString("firebasePartnerId");
            GetPartnerDetails(partnerId);
            multiSelectedList = bundle.getParcelableArrayList("multiSelectedList");

            Log.e(TAG, "onCreate:user " + currentUser.toString());
            UserId = currentUser.getUserid();
            GetOfflineMsg(UserId, partnerId);
            updateFirebaseMsgStatus(UserId);

            if (multiSelectedList != null) {
                if (firebaseChatModel != null){
                    for (FirebaseMsgModel model : multiSelectedList) {
                        forwordMessage(UserId,partnerId,model);
                    }
                }
            }

        }
        else {
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

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

    }

    public void searchMessages(String text) {
        try {
            ArrayList<FirebaseMsgModel> filterdNames = new ArrayList<>();
            for (FirebaseMsgModel member : firebaseChatModel) {
                String Topicname = member.getMessage().toLowerCase();
                if (Topicname.contains(text.toLowerCase()))
                    filterdNames.add(member);
            }
            adapter.setFilter(filterdNames);
        }catch (Exception e){

        }
    }

    private void updateFirebaseMsgStatus(int userId) {
        // int notification_count = notificationSqliteOperations.GetUnReadNotificationsfromUserId(Integer.valueOf(item.getPartnerid())).size();
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    FirebaseMsgModel chatModel=snapshot.getValue(FirebaseMsgModel.class);
                    if (chatModel.getReceiverid() == userId && chatModel.getSenderid()==partnerId){
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("msgStatus","seen");
                        snapshot.getRef().updateChildren(hashMap);
                        // }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void GetOfflineMsg(int PartnerId, int userId) {
        //final MyChatModel[] chatModel = new MyChatModel[1];

        final String[] clearDate = {""};

        reference = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(String.valueOf(partnerId));

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
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

        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.keepSynced(true);
        reference.orderByChild("msgTime").limitToLast(100)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        firebaseChatModel.clear();
                        for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                            try {
                                FirebaseMsgModel chat=snapshot.getValue(FirebaseMsgModel.class);
                                if (chat.getReceiverid() == PartnerId && chat.getSenderid() == userId ||
                                        chat.getReceiverid() == userId && chat.getSenderid() == PartnerId
                                ){
                                    /*if ( chat.getIsdeleted() != 1){

                         SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");

                         if (!clearDate[0].equals("")){
                             Date date1 = dateTime.parse(clearDate[0].toString());
                             Date date2 = dateTime.parse(chat.getMsgtime());
                             if (date1.compareTo(date2)<=0){
                                 firebaseChatModel.add(chat);
                             }

                         }else {
                             firebaseChatModel.add(chat);
                         }
                        }*/
                                    SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");

                                    if (!clearDate[0].equals("")){
                                        Date date1 = dateTime.parse(clearDate[0].toString());
                                        Date date2 = dateTime.parse(chat.getMsgtime());
                                        if (date1.compareTo(date2)<=0){
                                            firebaseChatModel.add(chat);
                                        }

                                    }else {
                                        firebaseChatModel.add(chat);
                                    }

                                }
                            }catch (Exception e){
                                Log.e(TAG, "onDataChange: e "+e.getMessage() );
                            }
                        }

                        BindOfflineData(firebaseChatModel, PartnerId, userId);

                        // add user to chat fragment
                        if (firebaseChatModel.size()>0) {
                            FirebaseMsgModel lastMsgItem = firebaseChatModel.get(firebaseChatModel.size() - 1);
                            FirebaseMsgModel firstMsgItem = firebaseChatModel.get(0);

                            final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                                    .child(firebaseUser.getUid())
                                    .child(String.valueOf(partnerId));

                            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        chatRef.child("senderid").setValue(String.valueOf(lastMsgItem.getSenderid()));
                                        chatRef.child("partnerid").setValue(String.valueOf(partnerId));
                                        //chatRef.child("messagess").setValue(String.valueOf(lastMsgItem.getMessage()));
                                        chatRef.child("message").setValue(String.valueOf(lastMsgItem.getMessage()));
                                        chatRef.child("partnerName").setValue(String.valueOf(firebasePartnerModel.getUsername()));
                                        chatRef.child("profilepic").setValue(firebasePartnerModel.getImageUrl());
                                        chatRef.child("msgTime").setValue(lastMsgItem.getMsgtime());
                                        chatRef.child("firebasePartnerId").setValue(firebasePartnerId);
                                        chatRef.child("clerchatdate").setValue(firstMsgItem.getMsgtime());

                                        chatRef.child("mediaType").setValue(lastMsgItem.getMediatype());

                                        chatRef.child("isdeleted").setValue(lastMsgItem.getIsdeleted());

                                    }else {
                                        chatRef.child("message").setValue(String.valueOf(lastMsgItem.getMessage()));
                                        //chatRef.child("messagess").setValue(String.valueOf(lastMsgItem.getMessage()));
                                        chatRef.child("msgTime").setValue(lastMsgItem.getMsgtime());
                                        chatRef.child("profilepic").setValue(firebasePartnerModel.getImageUrl());
                                        chatRef.child("partnerName").setValue(String.valueOf(firebasePartnerModel.getUsername()));

                                        chatRef.child("mediaType").setValue(lastMsgItem.getMediatype());
                                        chatRef.child("isdeleted").setValue(lastMsgItem.getIsdeleted());

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

    private void initListener() {
        iv_send.setOnClickListener(this);
        viewMenuIconBack.setOnClickListener(this);
        iv_attachment.setOnClickListener(this);
        viewMenuIconBack2.setOnClickListener(this);
        search_clear.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_copy.setOnClickListener(this);
        iv_delete.setOnClickListener(this);
        iv_reply.setOnClickListener(this);
        iv_forward.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

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
                    UpdatetypingStatus(String.valueOf(partnerId)); //userId of receiver
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void bindView() {
        notificationSqliteOperations = new NotificationDatabaseHelper(activity);
        SessionManager sessionManager = new SessionManager(this);
        currentUser = sessionManager.getUserDetails();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        et_search_view = findViewById(R.id.et_search_view);
        search_clear = findViewById(R.id.search_clear);
        viewMenuIconBack2 = findViewById(R.id.viewMenuIconBack2);
        toolbar_search = findViewById(R.id.toolbar_search);
        txtTitleBar = findViewById(R.id.txtTitleBar);
        iv_preview = findViewById(R.id.iv_preview);
        txtQuotedMsgType = findViewById(R.id.txtQuotedMsgType);
        cancelButton = findViewById(R.id.cancelButton);
        txtQuotedMsg = findViewById(R.id.txtQuotedMsg);
        rl_reply_layout = findViewById(R.id.rl_reply_layout);
        viewMenuIconBack = findViewById(R.id.viewMenuIconBack);
        txtTitleName = findViewById(R.id.txtTitleName);
        rv_chatDay = findViewById(R.id.rv_chatDay);
        et_msg = findViewById(R.id.et_msg);
        iv_send = findViewById(R.id.iv_send);
        iv_attachment = findViewById(R.id.iv_attachment);
        civ_profile = findViewById(R.id.civ_profile);
        // iv_more = findViewById(R.id.iv_more);
        toolbar1 = findViewById(R.id.toolbar1);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        iv_back = findViewById(R.id.iv_back);
        iv_copy = findViewById(R.id.iv_copy);
        iv_delete = findViewById(R.id.iv_delete);
        iv_reply = findViewById(R.id.iv_reply);
        iv_forward = findViewById(R.id.iv_forward);
        txtStatus = findViewById(R.id.txtStatus);
        iv_delete = findViewById(R.id.iv_delete);

        cancelAllNotifications();
    }

    private void GetPartnerDetails(final int partnerId) {
        try {
            Query query = reference.orderByChild("onlineUserId").equalTo(String.valueOf(partnerId));
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.e(TAG, "onDataChange: firebaseUserModel "+dataSnapshot.toString() );
                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                        Log.e(TAG, "onDataChange: firebaseUserModel "+childSnapshot.toString() );
                        firebasePartnerModel=childSnapshot.getValue(FirebaseUserModel.class);

                    }
                    // Log.e(TAG, "onDataChange: firebaseUserModel "+firebasePartnerModel.toString() );
                    isBlockedOrNot(firebasePartnerModel,firebaseUser.getUid());
                    setUpPartner();
                    partnerFirebaseUserId=firebasePartnerModel.getId();

                    Log.e("partnerFirebaseUserId",""+partnerFirebaseUserId);

                    if (firebasePartnerModel.getTypingTo().equals(String.valueOf(currentUser.getUserid()))){
                        txtStatus.setText("typing..");
                    }
                    else {
                        //checks online status of user
                        if (firebasePartnerModel.getStatus().equals("online")){
                            txtStatus.setText("online");
                        }
                        else {
                            String lastSeenTime=firebasePartnerModel.getStatus();
                            txtStatus.setText("Last Seen at : "+lastSeenTime);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled: firebaseUserModel "+databaseError.getMessage() );

                }
            });

            txtTitleName.setText(firebasePartnerModel.getUsername());
            String media = Constants.URL_USER_PROFILE_PIC + firebasePartnerModel.getImageUrl();
            Log.e(TAG, "setUpPartner: media "+media );
            Picasso.get().load(media).placeholder(R.drawable.ic_account_circle).into(civ_profile);

        }catch (Exception e){
            Log.e(TAG, "GetPartnerDetails:firebaseUserModel "+e.getMessage() );
        }

    }

    //public void isBlockedOrNot(FirebaseUserModel model,String currentUserId){
    public void isBlockedOrNot(FirebaseUserModel model,String currentUserId){
        //first check if sender(current user) is blocked by receiver or not
        //logic: if uid of the sender(current user) exists in 'BlockedUsers' of receiver then sender(current user) is blocked,otherwise not
        //if blocked then just display a message e.g. You're blocked by that user,can't send message
        //if not blocked then simply start the chat activity

        partnerFirebaseUserId=model.getId();

        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(partnerFirebaseUserId).child("BlockedUsers").orderByChild("uid").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if (snapshot.exists()){
                                //block,don't show profile pic

                                //Toast.makeText(activity, "You're blocked by this user,you can't send message", Toast.LENGTH_SHORT).show();

                                civ_profile.setImageResource(R.drawable.ic_male);

                                amIBlockByPartner=1;

                                amIBlockByPartnerFlag=true;


                                //bindResult(amIBlockByPartner);
                                bindResult(amIBlockByPartner,amIBlockByPartnerFlag);
                            }
                            else {

                                String media = Constants.URL_USER_PROFILE_PIC + firebasePartnerModel.getImageUrl();

                                Picasso.get().load(media).placeholder(R.drawable.ic_male).into(civ_profile);

                                amIBlockByPartner=0;

                                amIBlockByPartnerFlag=false;

                                //bindResult(amIBlockByPartner);
                                bindResult(amIBlockByPartner,amIBlockByPartnerFlag);


                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void bindResult(int amIBlockByPartner,boolean amIBlockByPartnerFlag){

        if (amIBlockByPartner == 1){
            Toast.makeText(activity, "You are blocked by this user", Toast.LENGTH_SHORT).show();

            result=amIBlockByPartner;

            //Log.e("amIBlockByPartner",""+amIBlockByPartner);
            Log.e("amIBlockByPartner",""+amIBlockByPartner+""+amIBlockByPartnerFlag);
        }
        if (amIBlockByPartner == 0){
            Toast.makeText(activity, "You are not blocked by this user", Toast.LENGTH_SHORT).show();

            result=amIBlockByPartner;

            //Log.e("amIBlockByPartner",""+amIBlockByPartner);
            Log.e("amIBlockByPartner",""+amIBlockByPartner+""+amIBlockByPartnerFlag);
        }

    }

    private void setUpPartner() {
        Log.e(TAG, "setUpPartner: partner "+firebasePartnerModel.toString() );
        txtTitleName.setText(firebasePartnerModel.getUsername());

        String media = Constants.URL_USER_PROFILE_PIC + firebasePartnerModel.getImageUrl();
        Log.e(TAG, "setUpPartner: media "+media );
        //Picasso.get().load(media).placeholderR.drawable.ic_male).into(imageView);
        Picasso.get().load(media).placeholder(R.drawable.ic_account_circle).into(civ_profile);


       /* Picasso.get()
                .load(media)
                .into(civ_profile, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        // Try again online if cache failed
                        Picasso.get()
                                .load(media)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.male_avatar)
                                .error(R.drawable.male_avatar)
                                .into(civ_profile);
                    }
                });*/


        Log.e(TAG, "offchattingPartner " + firebasePartnerModel.toString());

        NotificationDatabaseHelper notificationSqliteOperations = new NotificationDatabaseHelper(activity);
        long updateResult = notificationSqliteOperations.readAllNotificationsForUser(partnerId);
        Log.e("updateResult", "" + updateResult);
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.on_clicked));

        switch (v.getId()) {
            case R.id.iv_send:
                Utility.hideKeyboard(activity);
                rl_reply_layout.setVisibility(View.GONE);
                //sendMsg();
                checkIsBlocked(partnerFirebaseUserId);
                if (blockFlag==1){
                    AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                    //builder.setTitle("Delete");
                    builder.setMessage("Unblock this user to send message");
                    //delete button
                    builder.setPositiveButton("UNBLOCK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            unBlockUser(partnerFirebaseUserId);
                        }
                    });

                    //cancel button
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss dialog
                            dialog.dismiss();
                        }
                    });

                    builder.create().show();
                }
                else {
                    sendMsg();
                }

                break;

            case R.id.viewMenuIconBack:
                onBackPressed();
                break;

            case R.id.iv_attachment:
                Bundle bundle = new Bundle();
                bundle.putParcelable("offchattingPartner", firebasePartnerModel);
                BottomSheetFileOptionsFragment bottomSheetFragment1 = new BottomSheetFileOptionsFragment();
                bottomSheetFragment1.show(getSupportFragmentManager(), bottomSheetFragment1.TAG);
                bottomSheetFragment1.setArguments(bundle);
                break;


            case R.id.iv_back:
                //onBackPressed();
                toolbar1.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                setSupportActionBar(toolbar);
                BindOfflineData(firebaseChatModel, UserId, partnerId);
                selectedList.clear();
                break;

            case R.id.search_clear:
                et_search_view.setText("");
                break;

            case R.id.viewMenuIconBack2:
                toolbar1.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                setSupportActionBar(toolbar);
                BindOfflineData(firebaseChatModel, UserId, partnerId);
                break;

            case R.id.iv_forward:
                Bundle bundle1 = new Bundle();
                if (selectedList.size() > 0) {
                    bundle1.putParcelableArrayList("multiSelectedList", selectedList);
                    Utility.launchActivity(activity, MessageForwardActivity.class, true, bundle1);
                } else {
                    Toast.makeText(activity, "No Message Selected", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.iv_copy:
                // to copy some text to clipboard
                // Create a new ClipData.
                String text = "";

                if (selectedList.size() > 0) {
                    for (FirebaseMsgModel msgModel: selectedList)  {
                        text = text+msgModel.getMsgtime()+" "+msgModel.getMessage()+"\n";
                    }
                }

                ClipData clipData = ClipData.newPlainText("Source Text", text);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(this, "text copied : " , Toast.LENGTH_SHORT).show();

                break;
            case R.id.iv_reply:
                et_msg.setText(myMsg);
                break;

            case R.id.cancelButton:
                rl_reply_layout.setVisibility(View.GONE);
                replyToMsg = "";
                replyToMsgModel = null;
                break;

            case R.id.iv_delete:
                if (selectedList.size() > 0) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                    builder.setTitle("Delete");
                    builder.setMessage("Do you want to delete this message?");
                    //delete button
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //deleteMessage(selectedList);
                            for (FirebaseMsgModel  model : selectedList){
                                deleteMessage(model);
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

                } else {
                    Toast.makeText(activity, "No Message Selected", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    //private void deleteMessage(ArrayList<FirebaseMsgModel> selectedList) {
    private void deleteMessage(FirebaseMsgModel model) {

        reference = FirebaseDatabase.getInstance().getReference("Chats");

        String msgUid=model.getMessageid();

        Query query = reference.orderByChild("messageid").equalTo(msgUid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    HashMap<String, Object> hashMap = new HashMap<>();

                    hashMap.put("message", "This message was deleted...");
                    hashMap.put("isdeleted", 1);
                    snapshot.getRef().updateChildren(hashMap);

                    HashMap<String,Object> chatListhashMap=new HashMap<>();
                    //chatListhashMap.put("messagess", "This message was deleted...");
                    chatListhashMap.put("message", "This message was deleted...");
                    chatListhashMap.put("isdeleted",1);

                    DatabaseReference chatListdbRef=FirebaseDatabase.getInstance().getReference("Chatlist");

                    chatListdbRef.child(firebaseUser.getUid()).child(String.valueOf(model.getReceiverid())).updateChildren(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(activity, "message deleted", Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(activity, "message deleted failed", Toast.LENGTH_SHORT).show();

                                }
                            });

                    //Toast.makeText(activity, "message deleted", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void sendMsg() {
        if (et_msg.getText().toString().equals("")) {
            Utility.showErrorMessage(activity, "Type Message", Snackbar.LENGTH_LONG);
        } else {
            String msg = et_msg.getText().toString().trim();
            if (Utility.isNetworkAvailable(activity)) {
                sendMessage(UserId,partnerId,msg);
                et_msg.setText("");
            } else {
                Utility.showErrorMessage(activity, "Could not connect to the internet", Snackbar.LENGTH_LONG);
            }
        }
    }

    public void sendMessage(int sender,int receiver,String message){
        String replyToMsgId;
        int replyToMsgSenderId;
        String replyToMsgText;
        int replyToMsgTextMediaType;

        int mediatype = 7;
        if (Utility.containsURL(message)){
            mediatype = 6;
        }

        if (replyToMsg == ""){
            replyToMsgId = "-1";
            replyToMsgSenderId = -1;
            replyToMsgText = " ";
            replyToMsgTextMediaType = -1;

        }else{
            replyToMsgId = replyToMsg;
            replyToMsgSenderId = replyToMsgModel.getSenderid();
            replyToMsgText = replyToMsgModel.getMessage();
            replyToMsgTextMediaType = replyToMsgModel.getMediatype();
        }

        reference =  FirebaseDatabase.getInstance().getReference();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
        //System.out.println( dateTime1.format(cal.getTime()) );//prints current date with time ex: 03/05/2020 11:30 PM
        String msgTime=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM
        HashMap<String,Object> hashMap=new HashMap<>();
        //to generate unique random message uid
        // Get the size n
        int n = 7;
        String msgUid=firebaseUser.getUid()+getAlphaNumericString(n);
        Log.e("msgUid",""+msgUid);
        hashMap.put("messageid",msgUid);
        hashMap.put("senderid",sender);
        hashMap.put("receiverid",receiver);
        hashMap.put("message",message);
        hashMap.put("msgtime",msgTime);// message time
        hashMap.put("isread",0);
        hashMap.put("isdownloaded",1);
        hashMap.put("mediatype",mediatype);
        hashMap.put("type",0);
        hashMap.put("isdeleted",0);
        hashMap.put("replyToMsgId",replyToMsgId);
        hashMap.put("replyToMsgSenderId",replyToMsgSenderId);
        hashMap.put("replyToMsgText",replyToMsgText);
        hashMap.put("replyToMsgTextMediaType",replyToMsgTextMediaType);
        hashMap.put("isForward",0);
        hashMap.put("msgStatus","sent");

        reference.child("Chats").push().setValue(hashMap);
        // databaseReference.child("Chats").child(msgUid).setValue(hashMap);
        sendTextNotification(msgUid, firebasePartnerModel,mediatype, message,msgTime, replyToMsgId,replyToMsgSenderId,replyToMsgText, replyToMsgTextMediaType);
    }

    private void forwordMessage(int userId, int partnerId, FirebaseMsgModel chatModel) {

        String replyToMsgId = "-1";
        int replyToMsgSenderId = -1;
        String replyToMsgText = " ";
        int replyToMsgTextMediaType = -1;

        reference = FirebaseDatabase.getInstance().getReference();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
        //System.out.println( dateTime1.format(cal.getTime()) );//prints current date with time ex: 03/05/2020 11:30 PM
        String msgTime=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM
        HashMap<String,Object> hashMap=new HashMap<>();
        //to generate unique random message uid
        // Get the size n
        int n = 7;
        String msgUid=firebaseUser.getUid()+getAlphaNumericString(n);
        Log.e("msgUid",""+msgUid);
        hashMap.put("messageid",msgUid);
        hashMap.put("senderid",userId);
        hashMap.put("receiverid",partnerId);
        hashMap.put("message",chatModel.getMessage());
        hashMap.put("msgtime",msgTime);// message time
        hashMap.put("isread",0);
        hashMap.put("isdownloaded",1);
        hashMap.put("mediatype",chatModel.getMediatype());
        hashMap.put("type",0);
        hashMap.put("isdeleted",0);
        hashMap.put("replyToMsgId",replyToMsgId);
        hashMap.put("replyToMsgSenderId",replyToMsgSenderId);
        hashMap.put("replyToMsgText",replyToMsgText);
        hashMap.put("replyToMsgTextMediaType",replyToMsgTextMediaType);
        hashMap.put("isForward",1);
        hashMap.put("msgStatus","sent");
        //databaseReference.child("Chats").child(msgUid).setValue(hashMap);
        reference.child("Chats").push().setValue(hashMap);
        sendTextNotification(msgUid,firebasePartnerModel,chatModel.getMediatype(), chatModel.getMessage(),msgTime, replyToMsgId,replyToMsgSenderId,replyToMsgText, replyToMsgTextMediaType);
        saveFilesToSd(chatModel);
    }

    private void saveFilesToSd(FirebaseMsgModel chatModel) {

        Log.e("file", "file not exists 2 "+chatModel.getMessage());

        String path = "" ;

        //1 for image   //2 for gif  //3 for audio  //4 for video  //5 for documents  //6 for url  //7 for text //8 contacts

        switch (chatModel.getMediatype()){
            case 1:
                path = Constants.OFFLINE_IMAGE_PATH + "Sent/" + chatModel.getMessage();
                break;

            case 2:
                path = Constants.OFFLINE_GIF_PATH + "Sent/" + chatModel.getMessage();
                break;

            case 3:
                path = Constants.OFFLINE_AUDIO_PATH + "Sent/" + chatModel.getMessage();
                break;

            case 4:
                path = Constants.OFFLINE_VIDEO_PATH + "Sent/" + chatModel.getMessage();
                break;

            case 5:
                path = Constants.OFFLINE_DOCUMENT_PATH + "Sent/" + chatModel.getMessage();
                break;
        }

        try {

            Log.e("mforward","mforward 1");

            File source = new File(GetLocalFilePath(chatModel));

            Log.e(TAG, "saveFilesToSd: chat "+chatModel.getSenderid()+" file "+source.getPath() );
            File destination = new File(Environment.getExternalStorageDirectory() + "/" + path);
            if (source.exists()) {

                Log.e("mforward","mforward 2");

                FileChannel src = new FileInputStream(source).getChannel();
                FileChannel dst = new FileOutputStream(destination).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        } catch (Exception e) {
            //Log.e(TAG, "saveNotificationToSqlite: e "+e.getMessage() );

            Log.e("mforward","mforward 3"+e.getMessage());

        }



    }

    private void sendTextNotification(String msgUid, FirebaseUserModel firebasePartnerModel1, int mediatype, String message, String msgTime, String replyToMsgId, int replyToMsgSenderId, String replyToMsgText, int replyToMsgTextMediaType) {

        String to = "";
        if (firebasePartnerModel1 !=null){
            to = this.firebasePartnerModel.getToken();
        }else if (onchattingPartner!=null){
            to = onchattingPartner.getToken();
        }
        //  String to = "ftU0PTXttbk:APA91bFHUGKhBdDABO-cLoeI-2bNcqRg58yO79po2132Rkx0UpSAQiZMI5_zYBPDkllNe4skr0YxjdJp8ayD5haZen1-YAASnbJM8nyTbhWyAXhkXbjK7DKV9Twpl_XmDuknV2Bkdnc6";
        Log.e(TAG, "sendTextNotification: " + to);
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();

        try {
            notifcationBody.put("isFromGroup", 0);
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
            String finalTo = to;
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
                            Log.e("notification to", "" + finalTo);
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("notification:anError", "" + anError);
                            Log.e("notification:anError", "" + anError.getErrorBody());
                        }

                    });
            updatelastMessageToPartnerChat( msgUid,  firebasePartnerModel1,  mediatype,  message,  msgTime);
            replyToMsg = "";
            replyToMsgModel = null;
        } catch (Exception ex) {
            Log.e("onUnknownError", "" + ex);
        }
    }

    private void updatelastMessageToPartnerChat(String msgUid, FirebaseUserModel firebasePartnerModel1, int mediatype, String message, String msgTime) {
        // add partner to chat fragment
            final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                    .child(firebasePartnerModel.getId())
                    .child(String.valueOf(currentUser.getUserid()));

            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        chatRef.child("senderid").setValue(String.valueOf(currentUser.getUserid()));
                        chatRef.child("partnerid").setValue(String.valueOf(currentUser.getUserid()));
                        //chatRef.child("messagess").setValue(String.valueOf(lastMsgItem.getMessage()));
                        chatRef.child("message").setValue(message);
                        chatRef.child("partnerName").setValue(currentUser.getFullname());
                        chatRef.child("profilepic").setValue(currentUser.getProfilepic());
                        chatRef.child("msgTime").setValue(msgTime);
                        chatRef.child("firebasePartnerId").setValue(firebasePartnerId);
                        chatRef.child("clerchatdate").setValue(msgTime);
                        chatRef.child("mediaType").setValue(mediatype);
                        chatRef.child("isdeleted").setValue(0);

                    }else {
                        chatRef.child("message").setValue(String.valueOf(message));
                        //chatRef.child("messagess").setValue(String.valueOf(lastMsgItem.getMessage()));
                        chatRef.child("msgTime").setValue(msgTime);
                        chatRef.child("profilepic").setValue(currentUser.getProfilepic());
                        chatRef.child("partnerName").setValue(String.valueOf(firebasePartnerModel.getUsername()));
                        chatRef.child("mediaType").setValue(mediatype);
                        chatRef.child("isdeleted").setValue(0);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

    private void BindOfflineData(final ArrayList<FirebaseMsgModel> arraylist, int senderId, int receiverId) {
        try {
            Log.e(TAG, "BindOfflineData: " + arraylist.toString());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setStackFromEnd(true);
            rv_chatDay.setLayoutManager(linearLayoutManager);
            rv_chatDay.setItemAnimator(new DefaultItemAnimator());
            rv_chatDay.setHasFixedSize(true);


            MessageSwipeController messageSwipeController = new MessageSwipeController(this, new SwipeControllerActions() {
                @Override
                public void showReplyUI(int position) {
                    try {
                        FirebaseMsgModel replyModel = arraylist.get(position);
                        showQuotedMessage(replyModel);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            },0, ItemTouchHelper.RIGHT);

            new ItemTouchHelper(messageSwipeController).attachToRecyclerView(rv_chatDay);
            adapter = new OfflineChatTimeLineAdapter(activity, arraylist, senderId, receiverId, firebasePartnerModel, new
                    OfflineChatTimeLineAdapter.ItemClickListener() {

                    @Override
                    public void onItemClick(View v, int adapterPosition) {
                        v.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.on_clicked));
                        Log.e(TAG, "onItemClick: clicked");
                        final FirebaseMsgModel model = arraylist.get(adapterPosition);

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
                                if (model.getSenderid() == UserId) {
                                    bundle.putString("sender_name", "You");
                                } else if (model.getSenderid() == partnerId) {
                                    bundle.putString("sender_name", firebasePartnerModel.getUsername());
                                }
                                Utility.launchActivity(ChattingActivity.this, DetailsChattingActivity.class, false, bundle);
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
                              /*  v.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.on_clicked));
                                String FINAL_URL = Constants.URL_CHATTING_MEDIA + model.getMessage();
                                Log.e(TAG, "onMessageClick: pdf" + FINAL_URL);
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(FINAL_URL));
                                startActivity(browserIntent);*/
                            }
                        }
                        else if (model.getMediatype() == 8){
                            Log.e(TAG, "onItemClick: contact clled" );
                            OpenContactList(model);
                        }
                    }

                    @Override
                    public void onMessageClick(View v, int adapterPosition) {
                        FirebaseMsgModel model = arraylist.get(adapterPosition);
                        Log.e(TAG, "onMessageClick: clicked");
                    }

                        @Override
                        public void onMessageLongClick(View v, int adapterPosition) {

                        }

                        @Override
                        public void onTrainerItemClick(View view, int position) {

                        }
            });
             mRecyclerView.setAdapter(adapter);

            mRecyclerView.addOnScrollListener(new PaginationListener(linearLayoutManager) {
                @Override
                protected void loadMoreItems() {
                    isLoading = true;
                    currentPage++;
                    doApiCall();
                }
                @Override
                public boolean isLastPage() {
                    return isLastPage;
                }
                @Override
                public boolean isLoading() {
                    return isLoading;
                }
            });

        } catch (Exception ex) {
            Log.e(TAG, "BindOfflineData: ex " + ex.getMessage());
            Utility.showErrorMessage(this, ex.getMessage(), Snackbar.LENGTH_LONG);
        }
    }

    private void OpenContactList(FirebaseMsgModel model) {

        ArrayList<FirebaseContactResult> results = new ArrayList<>();
        ArrayList<FirebaseMsgModel> msgresults = new ArrayList<>();
        results.clear();

        reference =  FirebaseDatabase.getInstance().getReference("Chats");
        reference.keepSynced(true);
        //dref.child(model.getMessageid()).child("contacts")
        reference.orderByChild("messageid").equalTo(model.getMessageid()).limitToFirst(10)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.e(TAG, "onDataChange: snapshot "+snapshot.getValue() );
                            FirebaseMsgModel model = snapshot.getValue(FirebaseMsgModel.class);
                            // FirebaseContactResult model = snapshot.getValue(FirebaseContactResult.class);
                            results.addAll(model.getContacts());
                            Log.e(TAG, "onDataChange: model "+results.toString() );
                        }

                        Log.e("MyTag", results.toString());
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("offchattingPartner", firebasePartnerModel);
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

    private void showQuotedMessage(FirebaseMsgModel replyModel) {
        et_msg.requestFocus();
        replyToMsg = replyModel.getMessageid();
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(et_msg, InputMethodManager.SHOW_IMPLICIT);

        replyToMsgModel = replyModel;
        if (replyModel.getType()==1){
            txtQuotedMsg.setText("You");
        }else {
            txtQuotedMsg.setText(firebasePartnerModel.getUsername());
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
    }

    private String GetLocalFilePath(FirebaseMsgModel model) {

        String filename = "";

        //1 img //2 gif //3 audio //4 video //5 document //6 url // 7 text
        switch (model.getMediatype()) {
            case 1:
                if (model.getSenderid() == currentUser.getUserid()) {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_IMAGE_PATH + "Sent/" + model.getMessage();

                    // filename="Chatnow/Media/Chatnow Images/"+model.getMessage();
                } else {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_IMAGE_PATH + model.getMessage();

                }
                break;

            case 2:

                //if (model.getType() == 2) {

                if (model.getSenderid() == currentUser.getUserid()) {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_GIF_PATH + "Sent/" + model.getMessage();

                } else {

                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_GIF_PATH + model.getMessage();

                }
                break;

            case 3:
                //if (model.getType() == 2) {

                if (model.getSenderid() == currentUser.getUserid()) {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_AUDIO_PATH + "Sent/" + model.getMessage();

                    //filename="Chatnow/Media/Chatnow Audio/"+model.getMessage();
                } else {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_AUDIO_PATH + model.getMessage();

                }
                break;

            case 4:
                //if (model.getType() == 2) {

                if (model.getSenderid() == currentUser.getUserid()) {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_VIDEO_PATH + "Sent/" + model.getMessage();

                    //filename="Chatnow/Media/Chatnow Videos/"+model.getMessage();
                } else {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_VIDEO_PATH + model.getMessage();

                }
                break;

            case 5:
                //if (model.getType() == 2) {
                if (model.getSenderid() == currentUser.getUserid()) {
                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_DOCUMENT_PATH + "Sent/" + model.getMessage();

                    //filename="Chatnow/Media/Chatnow Videos/"+model.getMessage();
                } else {

                    filename = Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_DOCUMENT_PATH + model.getMessage();

                }
                break;
        }
        return filename;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        checkIsBlocked(partnerFirebaseUserId);

        return true;
    }

    //method used to hide or show menu items
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (blockFlag==1){
            MenuItem menu1 = menu.findItem(R.id.itemBlock);
            menu1.setVisible(false);

            MenuItem menu2 = menu.findItem(R.id.itemUnBlock);
            menu2.setVisible(true);
        }
        else {
            MenuItem menu1 = menu.findItem(R.id.itemBlock);
            menu1.setVisible(true);

            MenuItem menu2 = menu.findItem(R.id.itemUnBlock);
            menu2.setVisible(false);
        }

        return true;

    }

    private void checkIsBlocked(String partnerFirebaseUserId) {

        //check partner is blocked or not
        //if uid of the partner exists under current user's 'BlockedUsers' node then that user is blocked,otherwise not

        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseUser.getUid()).child("BlockedUsers").orderByChild("uid").equalTo(partnerFirebaseUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.exists()){

                                blockFlag=1;

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Bundle bundle = new Bundle();

        bundle.putParcelable("chattingPartner", firebasePartnerModel);

        int id = item.getItemId();
        switch (id) {
            case R.id.itemProfile:
                Utility.launchActivity(ChattingActivity.this, ChatProfileDetailsActivity.class, false, bundle);
                return true;

            case R.id.itemMedia:
                Utility.launchActivity(ChattingActivity.this, MediaListActivity.class, false, bundle);
                return true;

            case R.id.itemSearch:
                toolbar.setVisibility(View.GONE);
                toolbar1.setVisibility(View.GONE);
                toolbar_search.setVisibility(View.VISIBLE);
                setSupportActionBar(toolbar_search);

                return true;

            case R.id.itemReport:
                return true;

            case R.id.itemBlock:

                AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                //builder.setTitle("Delete");
                builder.setMessage("Block this user ? Blocked contacts will no longer be able to send you messages");
                //delete button
                builder.setPositiveButton("BLOCK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        blockUser(partnerFirebaseUserId);

                    }
                });

                //cancel button
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss dialog
                        dialog.dismiss();
                    }
                });

                builder.create().show();



                return true;

            case R.id.itemUnBlock:

                unBlockUser(partnerFirebaseUserId);

                return true;

            case R.id.itemClearChat:
                reference = FirebaseDatabase.getInstance().getReference("Chatlist")
                        .child(firebaseUser.getUid())
                        .child(String.valueOf(partnerId));
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
                            //System.out.println( dateTime1.format(cal.getTime()) );//prints current date with time ex: 03/05/2020 11:30 PM
                            String time=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM
                            reference.child("clerchatdate").setValue(time);
                        }

                        GetOfflineMsg(UserId, partnerId);


                        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                                .child(firebaseUser.getUid())
                                .child(String.valueOf(partnerId));

                        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    chatRef.child("message").setValue(String.valueOf(""));
                                    //chatRef.child("messagess").setValue(String.valueOf(lastMsgItem.getMessage()));
                                    chatRef.child("msgTime").setValue("");
                                    chatRef.child("profilepic").setValue(firebasePartnerModel.getImageUrl());
                                    chatRef.child("partnerName").setValue(String.valueOf(firebasePartnerModel.getUsername()));
                                    chatRef.child("mediaType").setValue(6);
                                    chatRef.child("isdeleted").setValue(0);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void blockUser(String partnerFirebaseUserId) {

        //block the user,by adding partnerFirebaseUserId of partner to current user's 'BlockedUsers' node

        //put values
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("uid",partnerFirebaseUserId);

        reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(firebaseUser.getUid()).child("BlockedUsers").child(partnerFirebaseUserId).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //blocked succeesfully

                        Toast.makeText(activity, "User Blocked Successfully", Toast.LENGTH_SHORT).show();

                        blockFlag=1;

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed to block

                        Toast.makeText(activity, "Failed to block user", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void unBlockUser(String partnerFirebaseUserId) {
        //unblock the user,by removing from current user's "BlockedUsers" node
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseUser.getUid()).child("BlockedUsers").orderByChild("uid").equalTo(partnerFirebaseUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if (snapshot.exists()){
                                //remove blocked user data from current user's 'BlockUsers' list
                                snapshot.getRef().removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.e("hi","hi...12");
                                                //unblocked Successfully
                                                blockFlag=0;
                                                Toast.makeText(activity, "User Unblocked Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed to unblock
                                                Toast.makeText(activity, "failed to unblock user", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void UpdatetypingStatus(String typing){
        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("typingTo",typing);
        reference.updateChildren(hashMap);
    }

    public void UpdateOnlineStatus(String status){
        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        UpdateOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //reference.removeEventListener(seenListener);
        //UpdateOnlineStatus("offline");
        // to get current date with time
        SimpleDateFormat dform = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date obj = new Date();
        UpdateOnlineStatus(dform.format(obj));
        reference.removeEventListener(seenListener);

    }

    @Override
    protected void onResume() {
        UpdateOnlineStatus("online");
        cancelAllNotifications();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    int currentItem = 0;
                    while (currentItem < count) {
                        Uri imageUri = data.getClipData().getItemAt(currentItem).getUri();
                        Log.e(TAG, "onActivityResult: uri " + imageUri);
                        //do something with the image (save it to some directory or whatever you need to do with it here)
                        currentItem = currentItem + 1;
                    }
                } else if (data.getData() != null) {
                    String imagePath = data.getData().getPath();
                    //do something with the image (save it to some directory or whatever you need to do with it here)
                }
            }
        } else if (requestCode == 2) {
            GetOfflineMsg(UserId, partnerId);
        }
        else if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                ArrayList<ContactResult> results = MultiContactPicker.obtainResult(data);
                Log.d("MyTag", results.get(0).getDisplayName());
                Bundle bundle = new Bundle();
                bundle.putParcelable("offchattingPartner", firebasePartnerModel);
                bundle.putParcelableArrayList("contacts", results);
                bundle.putInt("mediaType", 8);
                bundle.putInt("isSent", 0);
                Utility.launchActivityForResult(activity, ContactPreviewActivity.class, bundle, 2);
            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("User closed the picker without selecting items.");
            }
        } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
            //was picked from camera
            //set to imageview
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            // circleImageView.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();

            try {
                Log.e(TAG, "onActivityResult: data "+data.getExtras().get("data") );
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Uri tempUri = getImageUri(getApplicationContext(), photo);
                Bundle bundle = new Bundle();
                bundle.putParcelable("offchattingPartner",firebasePartnerModel);
                bundle.putString("files",tempUri.getPath());
                bundle.putInt("mediaType",5);
                Utility.launchActivityForResult(activity, CameraPreviewActivity.class,bundle,2);
            }catch (Exception e){
                Log.e(TAG, "onActivityResult: e "+e.getMessage() );
            }
        }

    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File( Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_IMAGE_PATH );
        if (!wallpaperDirectory.exists()) {  // have the object build the directory structure, if needed.
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::---&gt;" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000,true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reference.removeEventListener(seenListener);
        reference = null;
    }

    private void cancelAllNotifications() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }


    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        //adapter.clear();
        doApiCall();
    }


    private void doApiCall() {
        final ArrayList<FirebaseMsgModel> items = new ArrayList<>();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               /* for (int i = 0; i < 10; i++) {
                    itemCount++;
                    FirebaseMsgModel postItem = new FirebaseMsgModel();
                    postItem.setMessage(getString(R.string.text_title) + itemCount);
                    postItem.setMsgStatus(getString(R.string.text_description));
                    items.add(postItem);
                }*/ 
                /**
                 * manage progress view
                 */
                if (currentPage != PAGE_START) adapter.removeLoading();
                adapter.addItems(items);
                swipeRefresh.setRefreshing(false);
                // check weather is last page or not
                if (currentPage < totalPage) {
                    adapter.addLoading();
                } else {
                    isLastPage = true;
                }
                isLoading = false;
            }
        }, 1500);
    }
}
