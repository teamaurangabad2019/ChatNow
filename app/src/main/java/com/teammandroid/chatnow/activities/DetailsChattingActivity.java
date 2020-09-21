package com.teammandroid.chatnow.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.models.ChatModel;
import com.teammandroid.chatnow.models.NotificationModel;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseMsgModel;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DetailsChattingActivity extends AppCompatActivity implements View.OnClickListener, Runnable {

    private static final String TAG = DetailsChattingActivity.class.getSimpleName();
    ImageView viewMenuIconBack;
    TextView txtTitleName, txtDate;
    ImageView iv_star, iv_forward, iv_more;
    ZoomageView  iv_image;
    TextView tv_hint;
    VideoView vv_video;
    private FirebaseMsgModel chatModel;
    LinearLayout ll_audio;
    Activity activity;

    private ProgressDialog dialog;
    private String sender_name;

    MediaPlayer mediaPlayer = new MediaPlayer();
    SeekBar seekBar;
    boolean wasPlaying = false;
    FloatingActionButton fab;
    TextView seekBarHint;
    ArrayList<FirebaseMsgModel> selectedList = new ArrayList<>();

    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;

    File directory1;
    // private String media;

    UserModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_chatting);
        bindView();
        btnListener();
        activity = DetailsChattingActivity.this;
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        SessionManager sessionManager = new SessionManager(activity);
        currentUser = sessionManager.getUserDetails();

        Bundle bundle = getIntent().getExtras();
        chatModel = bundle.getParcelable("chatModel");
        sender_name =  bundle.getString("sender_name");
        if (chatModel != null) {
            bindValues(chatModel);
            selectedList.add(chatModel);
        } else {
            Toast.makeText(this, "Something went wrong ! ", Toast.LENGTH_SHORT).show();
        }
    }

    private void btnListener() {
        viewMenuIconBack.setOnClickListener(this);
        fab.setOnClickListener(this);
        iv_forward.setOnClickListener(this);
    }

    private void bindView() {
        viewMenuIconBack = findViewById(R.id.viewMenuIconBack);
        txtTitleName = findViewById(R.id.txtTitleName);
        txtDate = findViewById(R.id.txtDate);
        iv_star = findViewById(R.id.iv_star);
        iv_forward = findViewById(R.id.iv_forward);
        iv_more = findViewById(R.id.iv_more);
        iv_image = findViewById(R.id.iv_image);
        tv_hint = findViewById(R.id.tv_hint);
        vv_video = findViewById(R.id.vv_video);
        ll_audio = findViewById(R.id.ll_audio);
        fab = findViewById(R.id.button);
        seekBarHint = findViewById(R.id.textView);
        seekBar = findViewById(R.id.seekbar);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading page, please wait.");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void bindValues(FirebaseMsgModel item) {
        txtTitleName.setText(sender_name);
        txtDate.setText(chatModel.getMsgtime());
        tv_hint.setText("");

        //if (item.getType() == 2) {
        if (item.getSenderid() == currentUser.getUserid()){
            directory1 = new File(Environment.getExternalStorageDirectory() + "/" +Constants.OFFLINE_AUDIO_PATH + "Sent/" + item.getMessage());
        } else {
            directory1 = new File(Environment.getExternalStorageDirectory() + "/" +Constants.OFFLINE_AUDIO_PATH + item.getMessage());
        }

       // media = Constants.URL_CHATTING_MEDIA + item.getMessage();
        //1 img //2 gif //3 audio //4 video //5 document //6 url // 7 text

        if (item.getMediatype() == 1){
           iv_image.setVisibility(View.VISIBLE);
           vv_video.setVisibility(View.GONE);
           ll_audio.setVisibility(View.GONE);

           try {
               File directory;
               if (item.getSenderid() == currentUser.getUserid()) {
                   directory = new File(Environment.getExternalStorageDirectory() + "/" +Constants.OFFLINE_IMAGE_PATH + "Sent/" + item.getMessage());
               } else {
                   directory = new File(Environment.getExternalStorageDirectory() + "/" +Constants.OFFLINE_IMAGE_PATH + item.getMessage());
               }

               if (directory.exists()) {
                   Bitmap myBitmap = BitmapFactory.decodeFile(directory.getAbsolutePath());
                   iv_image.setImageBitmap(myBitmap);

               }else {
                   Log.e(TAG, "bindValues: directory "+directory );
               }

           }catch (Exception e){
               Log.e(TAG, "bindValues: image except "+e.getMessage() );
           }

         /*   String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
            Picasso.with(activity).load(media).placeholder(R.drawable.male_avatar).into(iv_image);*/

        } else if (item.getMediatype() == 2) {
            iv_image.setVisibility(View.VISIBLE);
            vv_video.setVisibility(View.GONE);
            ll_audio.setVisibility(View.GONE);
            File directory;
            if (item.getSenderid() == currentUser.getUserid()) {
                directory = new File(Environment.getExternalStorageDirectory() + "/" +Constants.OFFLINE_GIF_PATH + "Sent/" + item.getMessage());
            } else {
                directory = new File(Environment.getExternalStorageDirectory() + "/" +Constants.OFFLINE_GIF_PATH + item.getMessage());
            }

            String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
            try {
                Glide.with(activity).asGif().load(directory).into(iv_image);
            }catch (Exception e){
                Log.e(TAG, "bindValues: glide e "+e.getMessage() );
            }

        } else if (item.getMediatype() == 4) {

            iv_image.setVisibility(View.GONE);
            vv_video.setVisibility(View.VISIBLE);
            ll_audio.setVisibility(View.GONE);

            dialog.show();

            File directory;
            //if (item.getType() == 2) {
            if (item.getSenderid() == currentUser.getUserid()) {

                //directory = new File(Environment.getExternalStorageDirectory() + "/" +Constants.OFFLINE_VIDEO_PATH + item.getMessage());
                directory = new File(Environment.getExternalStorageDirectory() + "/" +Constants.OFFLINE_VIDEO_PATH + "Sent/" + item.getMessage());

            } else {

                //directory = new File(Environment.getExternalStorageDirectory() + "/" +Constants.OFFLINE_VIDEO_PATH + "Sent/" + item.getMessage());
                directory = new File(Environment.getExternalStorageDirectory() + "/" +Constants.OFFLINE_VIDEO_PATH + item.getMessage());

            }

            vv_video.setVideoPath(directory.getPath());

            MediaController mediaController = new
                    MediaController(this);
            mediaController.setAnchorView(vv_video);
            vv_video.setMediaController(mediaController);

            vv_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    dialog.dismiss();
                    Log.i(TAG, "Duration = " +
                            vv_video.getDuration());
                }
            });

            vv_video.start();

        }else if (item.getMediatype() == 3){
            iv_image.setVisibility(View.GONE);
            vv_video.setVisibility(View.GONE);
            ll_audio.setVisibility(View.VISIBLE);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                    seekBarHint.setVisibility(View.VISIBLE);
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

                    seekBarHint.setVisibility(View.VISIBLE);
                    int x = (int) Math.ceil(progress / 1000f);

                    if (x>0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
                        clearMediaPlayer();
                        fab.setImageDrawable(ContextCompat.getDrawable(activity, android.R.drawable.ic_media_play));
                        seekBar.setProgress(0);
                    }


                   /* seekBarHint.setVisibility(View.VISIBLE);
                    int x = (int) Math.ceil(progress / 1000f);

                    if (x < 10)
                        seekBarHint.setText("0:0" + x);
                    else
                        seekBarHint.setText("0:" + x);

                    double percent = progress / (double) seekBar.getMax();
                    int offset = seekBar.getThumbOffset();
                    int seekWidth = seekBar.getWidth();
                    int val = (int) Math.round(percent * (seekWidth - 2 * offset));
                    int labelWidth = seekBarHint.getWidth();
                    seekBarHint.setX(offset + seekBar.getX() + val
                            - Math.round(percent * offset)
                            - Math.round(percent * labelWidth / 2));

                    if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
                        clearMediaPlayer();
                        fab.setImageDrawable(ContextCompat.getDrawable(activity, android.R.drawable.ic_media_play));
                        DetailsChattingActivity.this.seekBar.setProgress(0);
                    }
*/
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.seekTo(seekBar.getProgress());
                    }
                }
            });
            dialog.show();
            playSong();

        }else if (item.getMediatype() == 5){

            File directory;
            if (item.getSenderid() == currentUser.getUserid()) {
                directory = new File(Environment.getExternalStorageDirectory() + "/" +Constants.OFFLINE_DOCUMENT_PATH + "Sent/" + item.getMessage());
            } else {
                directory = new File(Environment.getExternalStorageDirectory() + "/" +Constants.OFFLINE_DOCUMENT_PATH + item.getMessage());
            }
          /*  try {
                FileOpen.openFile(this, directory);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "bindValues: openFile "+ e.getMessage() );
            }*/

           // File file = new File(model.getUri().getPath());
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            // Old Approach
            // install.setDataAndType(Uri.fromFile(file), "application/pdf");
            // End Old approach
            // New Approach

         //   Uri apkURI = model.getUri();
            install.setDataAndType(Uri.fromFile(directory), "application/pdf");
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // End New Approach
            startActivity(install);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        scaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            iv_image.setScaleX(mScaleFactor);
            iv_image.setScaleY(mScaleFactor);
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.viewMenuIconBack:
                onBackPressed();
                break;

            case R.id.button:
                playSong();
                break;

            case R.id.iv_forward:
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("multiSelectedList", selectedList);
                Utility.launchActivity(activity, MessageForwardActivity.class, true, bundle);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
    public void playSong() {

        try {

            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                clearMediaPlayer();
                seekBar.setProgress(0);
                wasPlaying = true;
                fab.setImageDrawable(ContextCompat.getDrawable(activity, android.R.drawable.ic_media_play));
            }

            if (!wasPlaying) {

                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }

                fab.setImageDrawable(ContextCompat.getDrawable(activity, android.R.drawable.ic_media_pause));

                mediaPlayer.setDataSource(directory1.getPath());
                mediaPlayer.prepare();

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        dialog.dismiss();
                    }
                });
                mediaPlayer.setVolume(0.5f, 0.5f);
                mediaPlayer.setLooping(true);
                seekBar.setMax(mediaPlayer.getDuration());

                mediaPlayer.start();
                new Thread(this).start();
            }
            wasPlaying = false;
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void run() {

        int currentPosition = mediaPlayer.getCurrentPosition();
        int total = mediaPlayer.getDuration();


        while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
            try {
                Thread.sleep(1000);
                currentPosition = mediaPlayer.getCurrentPosition();
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }
            seekBar.setProgress(currentPosition);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearMediaPlayer();
    }

    private void clearMediaPlayer() {
        try {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }catch (Exception e){

        }

    }

    public static class FileOpen {

        public static void openFile(Context context, File url) throws IOException {
            // Create URI
            File file = url;
            Uri uri = Uri.fromFile(file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            // Check what kind of file you are trying to open, by comparing the url with extensions.
            // When the if condition is matched, plugin sets the correct intent (mime) type,
            // so Android knew what application to use to open the file
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav");
            } else if (url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if (url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                //if you want you can also define the intent type for any other file

                //additionally use else clause below, to manage other unknown extensions
                //in this case, Android will show all applications installed on the device
                //so you can choose which application to use
                intent.setDataAndType(uri, "*/*");
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
