package com.teammandroid.chatnow.activities.preview_activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.jsibbold.zoomage.ZoomageView;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.adapters.PreviewListRecyclerAdapter;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;
import com.teammandroid.chatnow.models.ChatModel;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;
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
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ImageEditorIntentBuilder;
import id.zelory.compressor.Compressor;

public class CameraPreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CameraPreviewActivity.class.getSimpleName();
    ImageView iv_undo, iv_crop, iv_emoji, iv_text, iv_pen;
    ImageView viewMenuIconBack;
    ImageView iv_selectedImage;
    CircleImageView civ_profile;
    VideoView vv_selectedVideo;
    TextView tv_name;
    TextView tv_completeTime;
    FloatingActionButton fab_send;
    private UserModel currentUser;
    String filePathString;
    Integer partnerId;
    Bundle bundle;
    Activity activity;
    private ProgressDialog dialog;
    private FirebaseUserModel offchattingPartner;

    FirebaseUser firebaseUser;

    LinearLayout ll_audio;
    MediaPlayer mediaPlayer = new MediaPlayer();
    SeekBar seekBar;
    boolean wasPlaying = false;
    FloatingActionButton fab;
    TextView seekBarHint;
    int mediaType;
    int count = 0;
    ViewPager viewPager;
    RecyclerView rv_media;
    Toolbar toolbar, toolbar1;

    int selectedPage = 0 ;
    Uri uriData;
    private final int PHOTO_EDITOR_REQUEST_CODE = 231;// Any integer value as a request code.
    // String files;
    ZoomageView iv_image;

    private PreviewListRecyclerAdapter adapter;
    //  private MediaFile currentimagefile;

    TextView text;
    RelativeLayout rl_progress;
    private ProgressBar progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);
        activity = CameraPreviewActivity.this;
        bindView();
        btnListener();

        SessionManager sessionManager = new SessionManager(activity);
        currentUser = sessionManager.getUserDetails();

        bundle = getIntent().getExtras();
        if (bundle != null){
            filePathString  = bundle.getString("files");
            offchattingPartner = bundle.getParcelable("offchattingPartner");
            Bitmap bitmap = null;
            try {
                uriData = Uri.fromFile(new File(filePathString));
                Glide.with(this).load(Uri.fromFile(new File(filePathString))).placeholder(R.drawable.ic_camera).into(iv_image);
                // iv_selectedImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onCreate: camera "+e.getMessage() );
            }

        }else {
            Toast.makeText(activity,"Something went wrong",Toast.LENGTH_SHORT).show();
        }
    }

    private void bindView() {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        iv_image = findViewById(R.id.iv_image);
        iv_undo = findViewById(R.id.iv_undo);
        iv_crop = findViewById(R.id.iv_crop);
        iv_emoji = findViewById(R.id.iv_emoji);
        iv_text = findViewById(R.id.iv_text);
        iv_pen = findViewById(R.id.iv_pen);
        iv_selectedImage = findViewById(R.id.iv_selectedImage);
        vv_selectedVideo = findViewById(R.id.vv_selectedVideo);
        tv_name = findViewById(R.id.tv_name);
        fab_send = findViewById(R.id.fab_send);
        viewMenuIconBack = findViewById(R.id.viewMenuIconBack);
        civ_profile = findViewById(R.id.civ_profile);
        fab = findViewById(R.id.button);
        seekBarHint = findViewById(R.id.textView);
        seekBar = findViewById(R.id.seekbar);
        ll_audio = findViewById(R.id.ll_audio);
        tv_completeTime = findViewById(R.id.tv_completeTime);
        // viewPager = findViewById(R.id.viewPager);
        toolbar = findViewById(R.id.toolbar);
        toolbar1 = findViewById(R.id.toolbar1);
        rv_media = findViewById(R.id.rv_media);

        text = findViewById(R.id.textView1);
        rl_progress = findViewById(R.id.rl_progress);
        progress = (ProgressBar) findViewById(R.id.progressBar1);

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void btnListener() {
        viewMenuIconBack.setOnClickListener(this);
        fab_send.setOnClickListener(this);
        iv_pen.setOnClickListener(this);
    }

    public void startProgress() {
        // do something long
        rl_progress.setVisibility(View.VISIBLE);
        fab_send.setVisibility(View.GONE);
        progress.setMax(1);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                progress.post(new Runnable() {
                    @Override
                    public void run() {
                        text.setText("Sending File ... ");
                        progress.setProgress(1);
                        InsertMessageWithFile();
                    }
                });
            }
        };
        new Thread(runnable).start();
        // this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.viewMenuIconBack:
                onBackPressed();
                break;

            case R.id.fab_send:
                startProgress();
                //InsertMessageWithFile();
                /*for (MediaFile media: files) {
                    InsertMessageWithFile(media);
                }*/
                /*  Intent intent1 = new Intent();
                setResult(2, intent1);
                finish();*/
                break;

            case R.id.iv_pen:
                //currentimagefile  =  files.get(selectedPage);
                Log.e(TAG, "onClick: imagefile"+uriData.getPath()+"    "+selectedPage );
                try {
                    Intent intent = new ImageEditorIntentBuilder(this, uriData.getPath(), uriData.getPath()+"CH")
                            .withAddText() // Add the features you need
                            .withPaintFeature()
                            .withFilterFeature()
                            .withRotateFeature()
                            .withCropFeature()
                            .withBrightnessFeature()
                            .withSaturationFeature()
                            .withBeautyFeature()
                            .withStickerFeature()
                            .forcePortrait(true)  // Add this to force portrait mode (It's set to false by default)
                            .build();

                    EditImageActivity.start(activity, intent, PHOTO_EDITOR_REQUEST_CODE);
                } catch (Exception e) {
                    Log.e(TAG, "onClick: e "+e.getMessage() );
                    Log.e("Demo App", e.getMessage()); // This could throw if either `sourcePath` or `outputPath` is blank or Null
                }
                break;
        }
    }

    private void InsertMessageWithFile() {
        try {
            File path = new File(filePathString);
            long length = path.length();
            Log.e(TAG, "InsertMessageWithFile: original siz "+(length/1024));
            if (mediaType == 1){
                path = new Compressor(activity).compressToFile(path);
                long length1 = path.length();
                Log.e(TAG, "InsertMessageWithFile: compress siz "+(length1/1024));
            }

            Log.e(TAG, "uploadWithFilePath: " + path.toString());
            int UserId = currentUser.getUserid();
            AndroidNetworking.upload(Constants.CHATTING)
                    // .addFileToUpload("", "certificate") //Adding file
                    .addMultipartParameter("type", "1")
                    .addMultipartParameter("Action", "1")
                    .addMultipartParameter("ChattingId", "0")
                    .addMultipartParameter("SenderId", String.valueOf(UserId))
                    .addMultipartParameter("ReceiverId", String.valueOf(offchattingPartner.getOnlineUserId()))
                    .addMultipartFile("Messages", path)
                    .addMultipartParameter("LogedinUserId", "1")
                    .setTag("uploadTest")
                    .setPriority(Priority.HIGH)
                    .build()
                    .setUploadProgressListener(new UploadProgressListener() {
                        @Override
                        public void onProgress(long bytesUploaded, long totalBytes) {
                            Log.e(TAG, "uploadImage: totalBytes: " + totalBytes);
                            Log.e(TAG, "uploadImage: bytesUploaded: " + bytesUploaded);
                            dialog.setMessage("Sending File, please wait.");
                            dialog.show();
                        }
                    })

                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            dialog.hide();
                            try {
                                GetMsgDetails(Integer.parseInt(response.getString("Result")));
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
            //  Toast.makeText(CameraPreviewActivity.this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void GetMsgDetails(final int chattingId) {  //(sender , receiver)
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
                        sendNotification(arrayList.get(0));
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


    private void sendNotification(final ChatModel chatModel) {

        int mediatype = 1;
        int isDownloaded = 1;


        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
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
        hashMap.put("senderid",currentUser.getUserid());
        hashMap.put("receiverid",Integer.parseInt(offchattingPartner.getOnlineUserId()));
        hashMap.put("message",chatModel.getMessagess());
        hashMap.put("msgtime",msgTime);// message time
        hashMap.put("isread",0);
        hashMap.put("isdownloaded",1);
        hashMap.put("mediatype",mediatype);
        hashMap.put("type",0);
        hashMap.put("replyToMsgId","-1");
        hashMap.put("replyToMsgSenderId",-1);
        hashMap.put("replyToMsgText","");
        hashMap.put("replyToMsgTextMediaType",-1);
        hashMap.put("isForward",0);
        hashMap.put("msgStatus","sent");

        databaseReference.child("Chats").push().setValue(hashMap);
        //  databaseReference.child("Chats").child(msgUid).setValue(hashMap);

        String to = offchattingPartner.getToken();
        //  String to = "ftU0PTXttbk:APA91bFHUGKhBdDABO-cLoeI-2bNcqRg58yO79po2132Rkx0UpSAQiZMI5_zYBPDkllNe4skr0YxjdJp8ayD5haZen1-YAASnbJM8nyTbhWyAXhkXbjK7DKV9Twpl_XmDuknV2Bkdnc6";
        Log.e(TAG, "onResponse: " + to);
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();

        try {
            notifcationBody.put("isFromGroup", 0);
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
                            count = count+1;
                            saveFilesToSd(finalMediatype, chatModel.getMessagess(), CameraPreviewActivity.this);
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

    private void saveFilesToSd(int mediatype, String message, CameraPreviewActivity trainerChatActivity) {
        String path = Constants.OFFLINE_IMAGE_PATH + "Sent/" + message;
        try {
            File source = new File(RealPathUtil.getRealPath(activity, uriData));
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
        SystemClock.sleep(3000);
        Log.e(TAG, "run: called ");
        Intent intent1 = new Intent();
        setResult(2, intent1);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PHOTO_EDITOR_REQUEST_CODE) { // same code you used while starting
            boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IS_IMAGE_EDITED, false);
            if(isImageEdit){

                adapter.notifyDataSetChanged();
                Log.e(TAG, "onActivityResult: ");
            }
        }
    }


}
