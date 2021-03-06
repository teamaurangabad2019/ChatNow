package com.teammandroid.chatnow.activities.group_preview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.adapters.CustomMusicAdapter;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;
import com.teammandroid.chatnow.models.ChatModel;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;
import com.teammandroid.chatnow.models.firebase.GroupChatList;
import com.teammandroid.chatnow.models.firebase.ParticipantsModel;
import com.teammandroid.chatnow.network.ChattingService;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.RealPathUtil;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GrpAudioPreviewListActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView songList;
    Activity activity;

    private static final String TAG = GrpAudioPreviewListActivity.class.getSimpleName();
    RecyclerView rv_media;
    FloatingActionButton fab_send;
    ImageView viewMenuIconBack;
    private UserModel currentUser;
    Bundle bundle;
  //  private FirebaseUserModel offchattingPartner;
    ArrayList<MediaFile> files = new ArrayList<>();
    private int mediaType;
    private CustomMusicAdapter adapter;
    Uri uriData;
    private ProgressDialog dialog;
    TextView tv_name;
    private int count = 0;
    FirebaseUser firebaseUser;
    private ArrayList<ParticipantsModel> participantsList = new ArrayList<>();

    TextView text;
    RelativeLayout rl_progress;
    private ProgressBar progress;
    private String myGroupId;
    private GroupChatList groupChatModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_preview_list);

        activity = GrpAudioPreviewListActivity.this;
        bindView();
        btnListener();

        SessionManager sessionManager = new SessionManager(activity);
        currentUser = sessionManager.getUserDetails();

        bundle = getIntent().getExtras();
        if (bundle != null) {
            //  filePathString  = bundle.getString("filePath");
            groupChatModel = bundle.getParcelable("groupChatModel");
            myGroupId = bundle.getString("myGroupId");
            files = bundle.getParcelableArrayList("files");
            mediaType = bundle.getInt("mediaType");
            //1 img //3 audio //4 video //5 document
            loadParticipants();

            slider(files);
        } else {
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
        }    }

    private void slider(ArrayList<MediaFile> files) {
        tv_name.setText(groupChatModel.getGroupTitle());
        Log.e(TAG, "bindValues: mediatype " + files.toString());

        adapter = new CustomMusicAdapter(this, R.layout.custom_music_item, files);
        songList.setAdapter(adapter);
       /* adapter = new VideoPreviewListRecyclerAdapter(activity, files, mediaType);
        // dialog.dismiss();
        rv_media.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        rv_media.setAdapter(adapter);
        rv_media.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //int position = getCurrentItem();
                    // selectedPage = getCurrentItem();
                    // Log.e(TAG, "onPageSelected: selectedPage " + selectedPage);
                }
            }
        });*/
    }

    private void bindView() {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        songList = (ListView) findViewById(R.id.songList);
        fab_send = findViewById(R.id.fab_send);
        viewMenuIconBack = findViewById(R.id.viewMenuIconBack);
        tv_name = findViewById(R.id.tv_name);
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        text = findViewById(R.id.textView1);
        rl_progress = findViewById(R.id.rl_progress);
        progress = (ProgressBar) findViewById(R.id.progressBar1);
    }

    private void btnListener() {
        fab_send.setOnClickListener(this);
        viewMenuIconBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_send:
                startProgress();
                break;

            case R.id.viewMenuIconBack:
                onBackPressed();
                break;
        }
    }

    public void startProgress() {
        // do something long
        rl_progress.setVisibility(View.VISIBLE);
        fab_send.setVisibility(View.GONE);
        progress.setMax(files.size());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < files.size(); i++) {
                    final int value = i;
                    InsertMessageWithFile(files.get(i));
                    progress.post(new Runnable() {
                        @Override
                        public void run() {
                            text.setText("Sending " + files.get(value).getName() + " . ");
                            progress.setProgress(value);
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
        // this.finish();
    }

    private void InsertMessageWithFile(MediaFile media) {
        try {
            uriData = media.getUri();
            File path = new File(RealPathUtil.getRealPath(activity, uriData));
            Log.e(TAG, "uploadWithFilePath: " + path.toString());
            int UserId = currentUser.getUserid();
            AndroidNetworking.upload(Constants.CHATTING)
                    // .addFileToUpload("", "certificate") //Adding file
                    .addMultipartParameter("type", "1")
                    .addMultipartParameter("Action", "1")
                    .addMultipartParameter("ChattingId", "0")
                    .addMultipartParameter("SenderId", String.valueOf(UserId))
                    .addMultipartParameter("ReceiverId", String.valueOf(UserId))
                    .addMultipartFile("Messages", path)
                    .addMultipartParameter("LogedinUserId", "1")
                    .setTag("uploadTest")
                    .setPriority(Priority.HIGH)
                    .build()
                    .setUploadProgressListener(new UploadProgressListener() {
                        @Override
                        public void onProgress(long bytesUploaded, long totalBytes) {
                           //  Log.e(TAG, "uploadImage: totalBytes: " + totalBytes);
                           // Log.e(TAG, "uploadImage: bytesUploaded: " + bytesUploaded);
                            dialog.setMessage("Sending File, please wait.");
                            dialog.show();
                        }
                    })

                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            dialog.hide();
                            try {
                                GetMsgDetails(Integer.parseInt(response.getString("Result")),media);
                                Log.e(TAG, "onResponse: " + response.toString());
                                Toast.makeText(activity, response.getString("Message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Log.e(TAG, "onResponse: " + e.getMessage());
                                e.printStackTrace();
                            }
                            try {
                                Toast.makeText(activity, response.getString("Message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.e(TAG, "onError: ", error);
                            dialog.hide();
                        }
                    });

        } catch (Exception exc) {
            Log.e(TAG, "InsertMessageWithPdf: " + exc.getMessage());
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void GetMsgDetails(final int chattingId, MediaFile media) {  //(sender , receiver)
        try {
            if (Utility.isNetworkAvailable(activity)) {
                dialog.show();
                Log.e("CheckPackage", "Called");
                //(sender , receiver)
                ChattingService.getInstance(activity).GetMsgDetails(chattingId, new ApiStatusCallBack<ArrayList<ChatModel>>() {
                    @Override
                    public void onSuccess(ArrayList<ChatModel> arrayList) {
                        //  lyt_progress_employees.setVisibility(View.GONE);
                        dialog.dismiss();
                        Log.e(TAG, "onSuccess: getMsg " + arrayList.toString());
                        sendNotification(arrayList.get(0),media);
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        //  lyt_progress_employees.setVisibility(View.GONE);
                        Log.e(TAG, "onError: anError " + anError.getMessage());
                        Utility.showErrorMessage(activity, "No Messages Found", Snackbar.LENGTH_LONG);
                    }

                    @Override
                    public void onUnknownError(Exception e) {
                        dialog.dismiss();
                        Log.e(TAG, "onUnknownError: exception " + e.getMessage());
                        Utility.showErrorMessage(activity, e.getMessage(), Snackbar.LENGTH_LONG);
                    }
                });
            } else {
                Utility.showErrorMessage(activity, "Could not connect to the internet", Snackbar.LENGTH_LONG);
            }
        } catch (Exception ex) {
            Utility.showErrorMessage(activity, ex.getMessage(), Snackbar.LENGTH_LONG);
        }
    }

    private void sendNotification(final ChatModel chatModel, MediaFile media) {
        int mediatype = 3;
        int isDownloaded = 0;

        String SenderId = firebaseUser.getUid();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        String msgTime=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30:20 PM
        String msgDate=date.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30:20 PM

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",SenderId);
        hashMap.put("message",chatModel.getMessagess());
        hashMap.put("msgTime",msgTime);// message time
        hashMap.put("msgDate",msgDate);
        //to generate unique random message uid
        // Get the size n
        int n = 10;
        String msgUid=SenderId+getAlphaNumericString(n);
        //String msgUid=firebaseAuth.getUid()+getAlphaNumericString(n);
        //String msgUid=String.valueOf(currentUser.getUserid())+getAlphaNumericString(n);
        Log.e("msgUid",""+msgUid);
        hashMap.put("msgUid",msgUid);
        hashMap.put("msgType","text");
        hashMap.put("isread",0);
        hashMap.put("isdownloaded",1);
        hashMap.put("mediatype",mediatype);
        hashMap.put("type",1);
        hashMap.put("replyToMsgId","-1");
        hashMap.put("replyToMsgSenderId","");
        hashMap.put("replyToMsgText","");
        hashMap.put("replyToMsgTextMediaType",-1);
        hashMap.put("isForward", 0);
        hashMap.put("msgStatus", "sent");
        hashMap.put("senderName", currentUser.getFullname());
        //hashMap.put("senderName", firebaseUser.getDisplayName());
        hashMap.put("isDeleted", 0);

        //hashMap.put("type",0);
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(myGroupId).child("Messages").child(msgUid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //message sent
                        Toast.makeText(activity, "Message Sent Successfully..", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //message sending failed
                        Toast.makeText(activity, "message sending failed", Toast.LENGTH_SHORT).show();
                    }
                });

        if (participantsList != null) {
            for (ParticipantsModel participant : participantsList) {
                String to = participant.getToken();
                Log.e(TAG, "onResponse: " + to);
                JSONObject notification = new JSONObject();
                JSONObject notifcationBody = new JSONObject();

                try {
                    notifcationBody.put("isFromGroup", 1);
                    notifcationBody.put("title", currentUser.getFullname());
                    notifcationBody.put("message", chatModel.getMessagess());
                    notifcationBody.put("senderid", currentUser.getUserid());
                    notifcationBody.put("mediatype", mediatype);
                    notifcationBody.put("msgUid", msgUid);
                    notifcationBody.put("created", chatModel.getCreated());
                    notifcationBody.put("replyToMsgId", -1);
                    notifcationBody.put("replyToMsgSenderId", -1);
                    notifcationBody.put("replyToMsgText", " ");
                    notifcationBody.put("replyToMsgTextMediaType", -1);

                    notification.put("to", to);
                    notification.put("data", notifcationBody);

                } catch (JSONException e) {
                    Log.e(TAG, "onCreate: " + e.getMessage());
                }

                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", Constants.GET_SERVER_KEY);
                    params.put("Content-Type", "application/json");

                    final int finalMediatype = mediatype;
                    Log.e(TAG, "sendNotification: mediatype" + mediatype);
                    final int finalIsDownloaded = isDownloaded;
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
                                    Log.e("notification parti", "" + participant.toString());
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.e("notification:anError", "" + anError);
                                    Log.e("notification:anError", "" + anError.getErrorBody());
                                }

                            });
                } catch (Exception ex) {
                    Log.e("onUnknownError", "" + ex);
                }

            }
        }

        count = count + 1;
        saveNotificationToSqlite(mediaType, chatModel.getMessagess(), media);

    }

    private void saveNotificationToSqlite(int mediaType, String message, MediaFile media) {
        //move file into sent folder
        String path = Constants.OFFLINE_AUDIO_PATH + "Sent/"+ message ;
        try {
            File source = new File(RealPathUtil.getRealPath(activity, media.getUri()));
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

        /*//notification save
        notification.setIsdownloaded(1);

        NotificationDatabaseHelper notificationSqliteOperations = new NotificationDatabaseHelper(getApplicationContext());
        long result = notificationSqliteOperations.saveNotification(notification);
        Log.e("insertResult", "" + result);
*/
        if (count >= files.size() - 1) {
            SystemClock.sleep(3000);
            Log.e(TAG, "run: caalled ");
            Intent intent1 = new Intent();
            setResult(2, intent1);
            finish();
        }
    }

    static String getAlphaNumericString(int n)
    {
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

    private void loadParticipants() {

        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Groups");
        dref.child(myGroupId).child("Participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //get uid from Group > Participants
                    ParticipantsModel participantsModel = snapshot.getValue(ParticipantsModel.class);
                    if (!participantsModel.getUid().equals(firebaseUser.getUid())) {
                        participantsList.add(participantsModel);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
