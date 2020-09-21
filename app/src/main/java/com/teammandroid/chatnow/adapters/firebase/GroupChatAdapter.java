package com.teammandroid.chatnow.adapters.firebase;

import android.content.Context;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseGroupChatModel;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.ChatViewHolder>{

    private static final String TAG = GroupChatAdapter.class.getSimpleName();

    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;

    private Context context;
    private ArrayList<FirebaseGroupChatModel> groupChatModel;

    private FirebaseAuth firebaseAuth;

    String groupId;

    FirebaseUser firebaseUser;

    UserModel currentUser;

    private  GroupChatAdapter.ItemClickListener itemClickListener;

    public GroupChatAdapter(Context context, ArrayList<FirebaseGroupChatModel> groupChatModel, String groupId, GroupChatAdapter.ItemClickListener itemClickListener){

        this.context=context;
        this.groupChatModel=groupChatModel;
        this.groupId=groupId;

        this.itemClickListener=itemClickListener;

        SessionManager sessionManager = new SessionManager(context);
        currentUser = sessionManager.getUserDetails();

        Log.e("currentuser","userid"+currentUser.getUserid());

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MSG_TYPE_RIGHT){
            View view= LayoutInflater.from(context).inflate(R.layout.item_group_chat_right,parent,false);
            return new ChatViewHolder(view);
        }
        else {
            View view= LayoutInflater.from(context).inflate(R.layout.item_group_chat_left,parent,false);
            return new ChatViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {

        //get data
        FirebaseGroupChatModel item=groupChatModel.get(position);

        if (item.getSender().equals(firebaseUser.getUid())){
          /*  holder.ll_left.setVisibility(View.GONE);
            holder.ll_right.setVisibility(View.VISIBLE);*/
            holder.tv_time.setText(String.valueOf(item.getMsgTime()));
            holder.tv_name.setText("You");
            setMsgStatus(item,holder.tv_time);

            if (item.getIsForward()==1){
                holder.tv_name_forward.setVisibility(View.VISIBLE);
                holder.tv_name_forward.setText("Forwarded");  //ic_arrow
                holder.tv_name_forward.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow, 0, 0, 0);

            }
            else {
                holder.tv_name_forward.setVisibility(View.GONE);
            }
            //check if msg is reply to existing msg
            //video
            if (item.getMediatype() == 4) {

                if (item.getIsDeleted() == 1){

                    holder.tv_msg.setText(String.valueOf(item.getMessage()));
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_image.setVisibility(View.GONE);
                    holder.ll_msg.setVisibility(View.VISIBLE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                    holder.ll_contact.setVisibility(View.GONE);
                }
                else {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_VIDEO_PATH + "Sent/" + item.getMessage());
                        if (file.exists()) {
                            holder.ll_file_size.setVisibility(View.GONE);
                            // Log.e("fileexists", "file exists " + item.getMessage());
                            Glide.with(context).load(file).placeholder(R.drawable.ic_audio).into(holder.iv_video);
                        } else {
                            // Log.e("fileexists", "file not exists " + item.getMessage());
                            String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                            holder.ll_file_size.setVisibility(View.VISIBLE);
                            holder.tv_size.setText(Formatter.formatShortFileSize(context, new File(media).length()));
                            Glide.with(context)
                                    .load(media)
                                    .thumbnail(0.01f)
                                    .override(50, 50)
                                    .placeholder(R.drawable.ic_video)
                                    .into(holder.iv_video);

                            holder.ll_file_size.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile(holder.pb_video, holder.tv_size, file, media);
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "onBindViewHolder: video " + e.getMessage());
                    }

                    //   Glide.with(context).load(media).into( holder.iv_videoRight);
                    holder.ll_video.setVisibility(View.VISIBLE);
                    holder.rl_image.setVisibility(View.GONE);
                    holder.ll_msg.setVisibility(View.GONE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                    holder.ll_contact.setVisibility(View.GONE);
                }

            }
            //image
            else if (item.getMediatype() == 1) {
                //Log.e(TAG, "onBindViewHolder: image "+Constants.URL_CHATTING_MEDIA + item.getMessage());

                if (item.getIsDeleted() == 1){

                    holder.tv_msg.setText(String.valueOf(item.getMessage()));
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_image.setVisibility(View.GONE);
                    holder.ll_msg.setVisibility(View.VISIBLE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                    holder.ll_contact.setVisibility(View.GONE);

                }else {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_IMAGE_PATH + "Sent/" + item.getMessage());
                        if (file.exists()) {
                            //      Log.e("fileexists", "file exists " + item.getMessage());
                            holder.ll_img_size.setVisibility(View.GONE);
                            Glide.with(context).load(file).placeholder(R.drawable.ic_gallery).into(holder.iv_image);
                        } else {
                            String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                            holder.ll_img_size.setVisibility(View.VISIBLE);
                            holder.tv_sizeImg.setText(Formatter.formatShortFileSize(context, new File(media).length()));
                            Glide.with(context)
                                    .load(media)
                                    .thumbnail(0.01f)
                                    .override(50, 50)
                                    .placeholder(R.drawable.ic_gallery)
                                    .into(holder.iv_image);

                            holder.ll_img_size.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile(holder.pb_img, holder.tv_sizeImg, file, media);
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "onBindViewHolder: image " + e.getMessage());
                    }
                    //Picasso.get().load(media).placeholder(R.drawable.male_avatar).into(holder.iv_imageRight);
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_image.setVisibility(View.VISIBLE);
                    holder.ll_msg.setVisibility(View.GONE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                    holder.ll_contact.setVisibility(View.GONE);
                }
            }
            //gif
            else if (item.getMediatype() == 2) {
                if (item.getIsDeleted() == 1){
                    holder.tv_msg.setText(String.valueOf(item.getMessage()));
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_image.setVisibility(View.GONE);
                    holder.ll_msg.setVisibility(View.VISIBLE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                    holder.ll_contact.setVisibility(View.GONE);
                }
                else {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_GIF_PATH + "Sent/" + item.getMessage());
                        if (file.exists()) {
                            holder.ll_img_size.setVisibility(View.GONE);
                            // Glide.with(context).load(file).placeholder(R.drawable.ic_gallery).into(holder.iv_imageRight);
                            Glide.with(context).asGif().load(file).placeholder(R.drawable.ic_gallery).into(holder.iv_image);
                        } else {
                            String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                            holder.ll_img_size.setVisibility(View.VISIBLE);
                            holder.tv_sizeImg.setText(Formatter.formatShortFileSize(context, new File(media).length()));
                            Glide.with(context)
                                    .load(media)
                                    .thumbnail(0.01f)
                                    .override(50, 50)
                                    .placeholder(R.drawable.ic_gallery)
                                    .into(holder.iv_image);

                            holder.ll_img_size.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile(holder.pb_img, holder.tv_sizeImg, file, media);
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "onBindViewHolder: image " + e.getMessage());
                    }
                /*String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                Glide.with(context).asGif().load(media).into(holder.iv_imageRight);*/
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_image.setVisibility(View.VISIBLE);
                    holder.ll_msg.setVisibility(View.GONE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                    holder.ll_contact.setVisibility(View.GONE);
                }
            }
            // audio
            else if (item.getMediatype() == 3) {
                if (item.getIsDeleted() == 1){
                    holder.tv_msg.setText(String.valueOf(item.getMessage()));
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_image.setVisibility(View.GONE);
                    holder.ll_msg.setVisibility(View.VISIBLE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                    holder.ll_contact.setVisibility(View.GONE);
                }
                else {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_AUDIO_PATH + "Sent/" + item.getMessage());
                        if (file.exists()) {
                            holder.iv_play.setImageResource(R.drawable.ic_play_arrow);
                            //Glide.with(context).load(file).placeholder(R.drawable.ic_gallery).into(holder.iv_imageRight);
                        } else {
                            holder.iv_play.setImageResource(R.drawable.ic_vertical_align_bottom);
                            String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                            holder.iv_play.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile(holder.pb_audio, holder.tv_sizeImg, file, media);
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "onBindViewHolder: image " + e.getMessage());
                    }
                    holder.ll_audio.setVisibility(View.VISIBLE);
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_image.setVisibility(View.GONE);
                    holder.ll_msg.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                    holder.ll_contact.setVisibility(View.GONE);
                }

            }

            // doc
            else if (item.getMediatype() == 5) {

                if (item.getIsDeleted() == 1){
                    holder.tv_msg.setText(String.valueOf(item.getMessage()));
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_image.setVisibility(View.GONE);
                    holder.ll_msg.setVisibility(View.VISIBLE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                    holder.ll_contact.setVisibility(View.GONE);
                }
                else {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_DOCUMENT_PATH + "Sent/" + item.getMessage());
                        if (file.exists()) {
                            holder.iv_sizeDoc.setVisibility(View.GONE);
                            holder.tv_tv_docName.setText(item.getMessage());
                            holder.tv_docExtention.setText(item.getMessage().substring(item.getMessage().lastIndexOf(".")));
                            //Glide.with(context).asBitmap().load(file).placeholder(R.drawable.ic_gallery).into(holder.iv_imageRight);
                        } else {
                            String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                            holder.iv_sizeDoc.setVisibility(View.VISIBLE);
                            holder.tv_tv_docName.setText(item.getMessage());
                            holder.tv_docExtention.setText(item.getMessage().substring(item.getMessage().lastIndexOf(".")));

                            holder.iv_sizeDoc.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile(holder.pb_doc, holder.tv_sizeImg, file, media);
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "onBindViewHolder: image " + e.getMessage());
                    }
                    // String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                    // Glide.with(context).asGif().load(media).into(holder.iv_imageRight);
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.VISIBLE);
                    holder.rl_image.setVisibility(View.GONE);
                    holder.ll_msg.setVisibility(View.GONE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.ll_contact.setVisibility(View.GONE);
                }
            }
            else if (item.getMediatype() == 7) {
                holder.ll_video.setVisibility(View.GONE);
                holder.rl_image.setVisibility(View.GONE);
                holder.ll_msg.setVisibility(View.VISIBLE);
                holder.ll_audio.setVisibility(View.GONE);
                holder.rl_pdf.setVisibility(View.GONE);
                holder.ll_contact.setVisibility(View.GONE);

                holder.tv_msg.setText(String.valueOf(item.getMessage()));

                if (!item.getReplyToMsgId().equals("-1") ){
                    showQuotedMessage(item,holder.txtQuotedMsg, holder.txtQuotedMsgType,holder.iv_preview, holder.rl_reply_layout);
                }else {
                    holder.rl_reply_layout.setVisibility(View.GONE);
                }
            }
            else if (item.getMediatype() == 8) {

                Log.e("groupcontact","groupcontact 0 : " +item.getMessage());

                if (item.getIsDeleted() == 1){

                    holder.tv_msg.setText(String.valueOf(item.getMessage()));
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_image.setVisibility(View.GONE);
                    holder.ll_msg.setVisibility(View.VISIBLE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                    holder.ll_contact.setVisibility(View.GONE);

                    Log.e("groupcontact","groupcontact 1 : " +item.getMessage());


                }
                else {
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_image.setVisibility(View.GONE);
                    holder.ll_msg.setVisibility(View.GONE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                    holder.ll_contact.setVisibility(View.VISIBLE);
                    holder.tv_contactMsg.setText(item.getMessage());

                    Log.e("groupcontact","groupcontact 2 : " +item.getMessage());

                }
            }
            else {
                try {
                    String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
                    Pattern p = Pattern.compile(URL_REGEX);
                    Matcher m = p.matcher(item.getMessage());//replace with string to compare
                    //Matcher m = p.matcher("example.com");//replace with string to compare
                    // if (m.find()) {
                    if (Utility.containsURL(item.getMessage())) {
                        //System.out.println("String contains URL");
                        holder.ll_video.setVisibility(View.GONE);
                        holder.rl_image.setVisibility(View.GONE);
                        holder.ll_link.setVisibility(View.VISIBLE);
                        holder.ll_audio.setVisibility(View.GONE);
                        holder.rl_pdf.setVisibility(View.GONE);
                        holder.ll_contact.setVisibility(View.GONE);

                        holder.richLinkView.setLink(item.getMessage(), new ViewListener() {
                            @Override
                            public void onSuccess(boolean status) {
                                Log.e("link :", "" + item.getMessage());
                            }

                            @Override
                            public void onError(Exception e) {
                                // Log.e(TAG, "onError: e " + e.getMessage());
                                holder.tv_msg.setText(String.valueOf(item.getMessage()));
                                holder.ll_video.setVisibility(View.GONE);
                                holder.rl_image.setVisibility(View.GONE);
                                holder.ll_msg.setVisibility(View.VISIBLE);
                                holder.ll_audio.setVisibility(View.GONE);
                                holder.rl_pdf.setVisibility(View.GONE);
                                holder.ll_contact.setVisibility(View.GONE);

                            }
                        });
                    } else {
                        holder.tv_msg.setText(String.valueOf(item.getMessage()));
                        holder.ll_video.setVisibility(View.GONE);
                        holder.rl_image.setVisibility(View.GONE);
                        holder.ll_msg.setVisibility(View.VISIBLE);
                        holder.ll_audio.setVisibility(View.GONE);
                        holder.rl_pdf.setVisibility(View.GONE);
                        holder.ll_contact.setVisibility(View.GONE);

                    }

                } catch (Exception e) {
                    Log.e(TAG, "onBindViewHolder: link " + e.getMessage());
                }
            }

        }
        else {
          //  holder.ll_left.setVisibility(View.VISIBLE);
          //  holder.ll_right.setVisibility(View.GONE);
            holder.tv_time.setText(String.valueOf(item.getMsgTime()));
            holder.tv_name.setText(item.getSenderName());

            setMsgStatus(item,holder.tv_time);

            if (item.getIsForward()==1){
                holder.tv_name_forward.setVisibility(View.VISIBLE);
                holder.tv_name_forward.setText("Forwarded");  //ic_arrow
                holder.tv_name_forward.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow, 0, 0, 0);

            }else {
                holder.tv_name_forward.setVisibility(View.GONE);
            }

            //video
            if (item.getMediatype() == 4){
                if (item.getIsDeleted() == 1){

                    holder.tv_msg.setText(String.valueOf(item.getMessage()) );
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_image.setVisibility(View.GONE);
                    holder.ll_text.setVisibility(View.VISIBLE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                }else {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory()+"/"+Constants.OFFLINE_VIDEO_PATH  + item.getMessage());
                        if (file.exists()) {
                            Glide.with(context).load(file).placeholder(R.drawable.ic_audio).into(holder.iv_video);
                            holder.ll_file_size.setVisibility(View.GONE);
                        }
                        else {
                            String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                            holder.ll_file_size.setVisibility(View.VISIBLE);
                            File  file1 = new File(media);
                            holder.tv_size.setText(Formatter.formatShortFileSize(context,file1.length()));
                            Glide.with(context)
                                    .load(media)
                                    .thumbnail(0.01f)
                                    .override(50,50)
                                    .placeholder(R.drawable.ic_video)
                                    .into(holder.iv_video);

                            holder.ll_file_size.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile( holder.pb_video,holder.tv_size,file,media);
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.e(TAG, "onBindViewHolder: video "+e.getMessage()  );
                    }

                    //String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                    //  Glide.with(context).load(media).into( holder.iv_videoLeft);
                    holder.ll_video.setVisibility(View.VISIBLE);
                    holder.rl_image.setVisibility(View.GONE);
                    holder.ll_text.setVisibility(View.GONE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                    holder.ll_contact.setVisibility(View.GONE);
                }
            }
            //image
            else if (item.getMediatype() == 1){

                //  Log.e(TAG, "onBindViewHolder: image "+Constants.URL_CHATTING_MEDIA + item.getMessage());

                if (item.getIsDeleted() == 1){
                    holder.tv_msg.setText(String.valueOf(item.getMessage()) );
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_image.setVisibility(View.GONE);
                    holder.ll_text.setVisibility(View.VISIBLE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                }
                else {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory()+"/"+Constants.OFFLINE_IMAGE_PATH  + item.getMessage());
                        if (file.exists()) {
                            holder.ll_img_size.setVisibility(View.GONE);
                            Glide.with(context).load(file).placeholder(R.drawable.ic_gallery).into(holder.iv_image);
                        }
                        else {
                            String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                            holder.ll_img_size.setVisibility(View.VISIBLE);
                            holder.tv_sizeImg.setText(Formatter.formatShortFileSize(context,new File(media).length()));
                            Glide.with(context)
                                    .load(media)
                                    .thumbnail(0.01f)
                                    .override(50,50)
                                    .placeholder(R.drawable.ic_gallery)
                                    .into(holder.iv_image);

                            holder.ll_img_size.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile( holder.pb_img,holder.tv_sizeImg,file,media);
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.e(TAG, "onBindViewHolder: image "+e.getMessage()  );
                    }
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_image.setVisibility(View.VISIBLE);
                    holder.ll_text.setVisibility(View.GONE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                    holder.ll_contact.setVisibility(View.GONE);
                }
            }
            // gif
            else if(item.getMediatype() == 2){
                if (item.getIsDeleted() == 1){
                    holder.tv_msg.setText(String.valueOf(item.getMessage()) );
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_image.setVisibility(View.GONE);
                    holder.ll_text.setVisibility(View.VISIBLE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                }
                else {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory()+"/"+Constants.OFFLINE_GIF_PATH  + item.getMessage());
                        if (file.exists()) {
                            holder.ll_img_size.setVisibility(View.GONE);
                            Glide.with(context).asGif().load(file).placeholder(R.drawable.ic_gallery).into(holder.iv_image);
                        }
                        else {
                            String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                            holder.ll_img_size.setVisibility(View.VISIBLE);
                            holder.tv_sizeImg.setText(Formatter.formatShortFileSize(context,new File(media).length()));
                            Glide.with(context)
                                    .load(media)
                                    .thumbnail(0.01f)
                                    .override(50,50)
                                    .placeholder(R.drawable.ic_gallery)
                                    .into(holder.iv_image);

                            holder.ll_img_size.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile( holder.pb_img,holder.tv_sizeImg,file,media);
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.e(TAG, "onBindViewHolder: image "+e.getMessage()  );
                    }
                    // String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                    // Glide.with(context).asGif().load(media).into(holder.iv_imageRight);
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_image.setVisibility(View.VISIBLE);
                    holder.ll_text.setVisibility(View.GONE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                    holder.ll_contact.setVisibility(View.GONE);
                }
            }
            //audio
            else if (item.getMediatype() == 3){
                if (item.getIsDeleted() == 1){
                    holder.tv_msg.setText(String.valueOf(item.getMessage()) );
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_image.setVisibility(View.GONE);
                    holder.ll_text.setVisibility(View.VISIBLE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                }
                else {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory()+"/"+Constants.OFFLINE_AUDIO_PATH  + item.getMessage());
                        if (file.exists()) {
                            holder.iv_play.setImageResource(R.drawable.ic_play_arrow);
                            //Glide.with(context).load(file).placeholder(R.drawable.ic_gallery).into(holder.iv_imageRight);
                        }
                        else {
                            holder.iv_play.setImageResource(R.drawable.ic_vertical_align_bottom);
                            String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                            holder.iv_play.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile( holder.pb_audio,holder.tv_sizeImg,file,media);
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.e(TAG, "onBindViewHolder: image "+e.getMessage()  );
                    }
                    holder.ll_audio.setVisibility(View.VISIBLE);
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_image.setVisibility(View.GONE);
                    holder.ll_text.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                    holder.ll_contact.setVisibility(View.GONE);
                }

            }
            else if (item.getMediatype() == 7){
                holder.ll_video.setVisibility(View.GONE);
                holder.rl_image.setVisibility(View.GONE);
                holder.ll_text.setVisibility(View.VISIBLE);
                holder.ll_audio.setVisibility(View.GONE);
                holder.rl_pdf.setVisibility(View.GONE);
                holder.ll_contact.setVisibility(View.GONE);

                holder.tv_msg.setText(String.valueOf(item.getMessage()));

                if (!item.getReplyToMsgId().equals("-1")) {
                    showQuotedMessage(item,holder.txtQuotedMsg, holder.txtQuotedMsgType,holder.iv_preview, holder.rl_reply_layout);
                }else {
                    holder.rl_reply_layout.setVisibility(View.GONE);
                }
            }
            //document
            else if(item.getMediatype() == 5){
                if (item.getIsDeleted() == 1){
                    holder.tv_msg.setText(String.valueOf(item.getMessage()) );
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_image.setVisibility(View.GONE);
                    holder.ll_text.setVisibility(View.VISIBLE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                }
                else {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory()+"/"+Constants.OFFLINE_DOCUMENT_PATH  + item.getMessage());
                        if (file.exists()) {
                            holder.iv_sizeDoc.setVisibility(View.GONE);
                            holder.tv_tv_docName.setText(item.getMessage());
                            holder.tv_docExtention.setText(item.getMessage().substring(item.getMessage().lastIndexOf(".")));
                            //Glide.with(context).asBitmap().load(file).placeholder(R.drawable.ic_gallery).into(holder.iv_imageRight);
                        }
                        else {
                            String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                            holder.iv_sizeDoc.setVisibility(View.VISIBLE);
                            holder.tv_tv_docName.setText(item.getMessage());
                            holder.tv_docExtention.setText(item.getMessage().substring(item.getMessage().lastIndexOf(".")));

                            holder.iv_sizeDoc.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile( holder.pb_doc,holder.tv_sizeImg,file,media);
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.e(TAG, "onBindViewHolder: image "+e.getMessage()  );
                    }
                    // String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                    // Glide.with(context).asGif().load(media).into(holder.iv_imageRight);
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.VISIBLE);
                    holder.rl_image.setVisibility(View.GONE);
                    holder.ll_text.setVisibility(View.GONE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.ll_contact.setVisibility(View.GONE);
                }

            }
            // contact
            else if (item.getMediatype() == 8) {
                if (item.getIsDeleted() == 1){
                    holder.tv_msg.setText(String.valueOf(item.getMessage()) );
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_image.setVisibility(View.GONE);
                    holder.ll_text.setVisibility(View.VISIBLE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                }
                else {
                    holder.ll_video.setVisibility(View.GONE);
                    holder.rl_image.setVisibility(View.GONE);
                    holder.ll_text.setVisibility(View.GONE);
                    holder.ll_audio.setVisibility(View.GONE);
                    holder.rl_pdf.setVisibility(View.GONE);
                    holder.ll_contact.setVisibility(View.VISIBLE);
                    holder.tv_contactMsg.setText(item.getMessage());
                }
            }
            else {
                try {
                    String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
                    Pattern p = Pattern.compile(URL_REGEX);
                    Matcher m = p.matcher(item.getMessage());//replace with string to compare
                    //if (m.find()){
                    if (Utility.containsURL(item.getMessage())){
                        holder.ll_video.setVisibility(View.GONE);
                        holder.rl_image.setVisibility(View.GONE);
                        holder.ll_link.setVisibility(View.VISIBLE);
                        holder.ll_audio.setVisibility(View.GONE);
                        holder.rl_pdf.setVisibility(View.GONE);
                        holder.ll_contact.setVisibility(View.GONE);


                        holder.richLinkView.setLink(item.getMessage(), new ViewListener() {
                            @Override
                            public void onSuccess(boolean status) {
                                Log.e("link :", "" + item.getMessage());
                            }

                            @Override
                            public void onError(Exception e) {
                                holder.tv_msg.setText(String.valueOf(item.getMessage()));
                                holder.ll_video.setVisibility(View.GONE);
                                holder.rl_image.setVisibility(View.GONE);
                                holder.ll_text.setVisibility(View.VISIBLE);
                                holder.ll_audio.setVisibility(View.GONE);
                                holder.rl_pdf.setVisibility(View.GONE);
                                Log.e(TAG, "link onError: e " + e.getMessage());
                            }
                        });
                    }else {
                        holder.tv_msg.setText(String.valueOf(item.getMessage()) );
                        holder.ll_video.setVisibility(View.GONE);
                        holder.rl_image.setVisibility(View.GONE);
                        holder.ll_text.setVisibility(View.VISIBLE);
                        holder.ll_audio.setVisibility(View.GONE);
                        holder.rl_pdf.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    Log.e(TAG, "onBindViewHolder: link "+e.getMessage() );
                }
            }
        }
        }

    private void setMsgStatus(FirebaseGroupChatModel item, TextView tv_time){
        switch (item.getMsgStatus()){
            case "sent":
                tv_time.setCompoundDrawablesWithIntrinsicBounds( 0, 0,R.drawable.ic_tick, 0);
                break;

            case "delivered":
                tv_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_indicator, 0);
                break;

            case "seen":
                tv_time.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_double_tick_blue,  0);
                break;
        }
    }

    private void showQuotedMessage(FirebaseGroupChatModel replyModel, TextView txtQuotedMsg, TextView txtQuotedMsgType, ImageView iv_preview, RelativeLayout rl_reply_layout) {
        try{
            if (replyModel.getReplyToMsgSenderId()==firebaseUser.getUid()){
                txtQuotedMsg.setText("You");
            }else {
              //  txtQuotedMsg.setText(replyModel.getSenderName());
                DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Users");
                dref.keepSynced(true);
                dref.orderByChild("id").equalTo(replyModel.getReplyToMsgSenderId())
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
        }catch (Exception e){
            Log.e(TAG, "showQuotedMessage: "+e.getMessage() );
        }
        rl_reply_layout.setVisibility(View.VISIBLE);

        //1 img //2 gif //3 audio //4 video //5 document //6 url // 7 text //8 contacts
        String media = Constants.URL_CHATTING_MEDIA + replyModel.getReplyToMsgText();
        // Log.e(TAG, "showQuotedMessage: media "+media );
        switch (replyModel.getReplyToMsgTextMediaType()){
            case 1:
                iv_preview.setVisibility(View.VISIBLE);
                txtQuotedMsgType.setText(" Image");
                txtQuotedMsgType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_image_small, 0, 0, 0);
                Glide.with(context).load(media).centerCrop().placeholder(R.drawable.ic_gallery).error(R.drawable.ic_gallery).dontAnimate().into(iv_preview);
                break;

            case 2:
                iv_preview.setVisibility(View.VISIBLE);
                txtQuotedMsgType.setText(" GIF");
                txtQuotedMsgType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_image_small, 0, 0, 0);
                Glide.with(context).load(media).centerCrop().placeholder(R.drawable.ic_video).error(R.drawable.ic_video).dontAnimate().into(iv_preview);
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
                Glide.with(context).load(media).centerCrop().placeholder(R.drawable.ic_video).error(R.drawable.ic_video).dontAnimate().into(iv_preview);
                break;

            case 5:
                txtQuotedMsgType.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                iv_preview.setVisibility(View.GONE);
                txtQuotedMsgType.setText(replyModel.getReplyToMsgText());
                break;

            case 6:
                iv_preview.setVisibility(View.GONE);
                txtQuotedMsgType.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                txtQuotedMsgType.setText(replyModel.getReplyToMsgText());
                break;

            case 7:
                iv_preview.setVisibility(View.GONE);
                txtQuotedMsgType.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                txtQuotedMsgType.setText(replyModel.getReplyToMsgText());
                break;

            case 8:
                iv_preview.setVisibility(View.GONE);
                txtQuotedMsgType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_contact_small, 0, 0, 0);
                txtQuotedMsgType.setText("Contact : "+replyModel.getReplyToMsgText());
                break;
        }

    }

    private void downloadFile(ProgressBar progressBar, TextView tv_size, File file, String media) {

        //   Log.e(TAG, "downloadFile:url "+media );
        progressBar.setVisibility(View.VISIBLE);
        Ion.with(context)
                .load(media)
                .progressBar(progressBar)
                .progressHandler(new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {
                        // tv_size.setText(Formatter.formatShortFileSize(context,downloaded) );
                        tv_size.setText(Formatter.formatShortFileSize(context,downloaded) );
                    }
                })
                .write(file)
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File result) {
                        notifyDataSetChanged();
                        if (e != null) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(context, "Error downloading file", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void deleteMessage(int position,String groupId) {

        //get uid of clicked message
        // compare that message uid wih all messages in chats
        // where both values matches delete that message

        String msgUid=groupChatModel.get(position).getMsgUid();
        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference("Groups");

        Query query=dbRef.child(groupId).child("Messages").orderByChild("msgUid")
                .equalTo(msgUid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){


                    /*
                    we can do one of two things
                    1)remove message from chats
                    2)set the value of message " This message was deleted.."
                    */

                    // 1) delete message from chats
                    //snapshot.getRef().removeValue();

                    //2) set the value of message to "this message was deleted"

                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("message","This message was deleted...");
                    snapshot.getRef().updateChildren(hashMap);

                    Toast.makeText(context, "message deleted", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return groupChatModel.size();
    }

    @Override
    public int getItemViewType(int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        //if (groupChatModel.get(position).getSender().equals(firebaseUser.getUid())){

        //if (groupChatModel.get(position).getSender().equals(currentUser.getUserid())){
        if (groupChatModel.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }

    }


    public interface ItemClickListener {
        void onItemClick(View v, int adapterPosition);
        void onMessageClick(View v, int adapterPosition);
        void onMessageLongClick(View v, int adapterPosition);
    }

    class ChatViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout rl_image;
        LinearLayout ll_img_size;
        ProgressBar pb_img;
        ProgressBar pb_audio;
        TextView tv_sizeImg;
        LinearLayout ll_left, ll_right;
        TextView tv_msg;
        TextView tv_time;
        TextView tv_name;
        LinearLayout ll_text;
        RelativeLayout ll_video;
        ImageView iv_image;
        ImageView iv_video;
        RelativeLayout ll_audio;
        ImageView iv_play;
        ImageView iv_pause;
        SeekBar seekbar;
        ImageView iv_gif;
        TextView tv_hint;
        LinearLayout ll_link;
        LinearLayout ll_file_size;
        TextView tv_size;
        RichLinkView richLinkView;
        ProgressBar pb_video;
        TextView tv_tv_docName;
        ImageView iv_sizeDoc;
        ProgressBar pb_doc;
        TextView tv_docExtention;
        RelativeLayout rl_pdf;
        LinearLayout ll_msg;

        RelativeLayout rl_reply_layout;
        TextView txtQuotedMsg;
        TextView txtQuotedMsgType;
        ImageView iv_preview;

        LinearLayout ll_contact;
        TextView tv_contactMsg;
        TextView tv_viewContact;
        TextView tv_name_forward;



        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name_forward = itemView.findViewById(R.id.tv_name_forward);
            tv_viewContact = itemView.findViewById(R.id.tv_viewContact);
            tv_contactMsg = itemView.findViewById(R.id.tv_contactMsg);
            ll_contact = itemView.findViewById(R.id.ll_contact);
            iv_preview = itemView.findViewById(R.id.iv_preview);
            txtQuotedMsgType = itemView.findViewById(R.id.txtQuotedMsgType);
            txtQuotedMsg = itemView.findViewById(R.id.txtQuotedMsg);
            rl_reply_layout = itemView.findViewById(R.id.rl_reply_layout);
            rl_pdf = itemView.findViewById(R.id.rl_pdf);
            tv_docExtention = itemView.findViewById(R.id.tv_docExtention);
            pb_doc = itemView.findViewById(R.id.pb_doc);
            iv_sizeDoc = itemView.findViewById(R.id.iv_sizeDoc);
            tv_tv_docName = itemView.findViewById(R.id.tv_tv_docName);
            pb_audio = itemView.findViewById(R.id.pb_audio);
            tv_sizeImg = itemView.findViewById(R.id.tv_sizeImg);
            pb_img = itemView.findViewById(R.id.pb_img);
            ll_img_size = itemView.findViewById(R.id.ll_img_size);
            rl_image = itemView.findViewById(R.id.rl_image);
            pb_video = itemView.findViewById(R.id.pb_video);
            ll_file_size = itemView.findViewById(R.id.ll_file_size);
            tv_size = itemView.findViewById(R.id.tv_size);
            ll_left = itemView.findViewById(R.id.ll_left);
            ll_right = itemView.findViewById(R.id.ll_right);
            tv_msg = itemView.findViewById(R.id.tv_msg);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_name = itemView.findViewById(R.id.tv_name);
            ll_text = itemView.findViewById(R.id.ll_text);
            iv_image = itemView.findViewById(R.id.iv_image);
            ll_video = itemView.findViewById(R.id.ll_video);
            ll_msg = itemView.findViewById(R.id.ll_msg);
            iv_image = itemView.findViewById(R.id.iv_image);
            ll_video = itemView.findViewById(R.id.ll_video);
            iv_video = itemView.findViewById(R.id.iv_video);
            iv_play = itemView.findViewById(R.id.iv_play);
            iv_pause = itemView.findViewById(R.id.iv_pause);
            seekbar = itemView.findViewById(R.id.seekbar);
            iv_gif = itemView.findViewById(R.id.iv_gif);
            tv_hint = itemView.findViewById(R.id.tv_hint);
            ll_audio = itemView.findViewById(R.id.ll_audio);
            ll_link = itemView.findViewById(R.id.ll_link);
            richLinkView = itemView.findViewById(R.id.richLinkView);

            itemView.setClickable(true);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null)
                        itemClickListener.onItemClick(v, getAdapterPosition());
                }
            });

            tv_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null)
                        itemClickListener.onMessageClick(v, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener != null)
                        itemClickListener.onMessageLongClick(v, getAdapterPosition());
                    return true;
                }
            });

        }

    }

}
