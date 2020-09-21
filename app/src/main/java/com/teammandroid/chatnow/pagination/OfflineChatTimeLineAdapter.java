package com.teammandroid.chatnow.pagination;

import android.content.Context;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.adapters.ChatsRecyclerAdapter;
import com.teammandroid.chatnow.adapters.PeopleRecyclerAdapter;

import com.teammandroid.chatnow.models.firebase.FirebaseMsgModel;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;

//OfflineChatTimeLineAdapter
public class OfflineChatTimeLineAdapter extends RecyclerView.Adapter<BaseViewHolder>  {

    final static  String TAG = ChatsRecyclerAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    //private List<FirebaseMsgModel> mPayList;


    private Context context;
    List<FirebaseMsgModel> mPayList;
    int senderId;
    int receiverId;
    FirebaseUserModel offchattingPartner;

   /* public OfflineChatTimeLineAdapter(List<FirebaseMsgModel> postItems) {
        this.mPayList = postItems;
    }*/

    public OfflineChatTimeLineAdapter(Context context, ArrayList<FirebaseMsgModel> mPayList, int senderId, int receiverId, FirebaseUserModel offchattingPartner, ItemClickListener itemClickListener) {
        this.context = context;
        this.mPayList = mPayList;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.itemClickListener = itemClickListener;
        this.offchattingPartner = offchattingPartner;
    }



    ArrayList<FirebaseMsgModel> list;
    //PrefManager prefManager;

    String role_id = "";
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private FirebaseUserModel firebaseUserModel;

    private ItemClickListener itemClickListener;
    private FirebaseMsgModel currentUser;

    String partnerFirebaseUserId;
    private double lat;
    private double log;

    public OfflineChatTimeLineAdapter(Context context, ArrayList<FirebaseMsgModel> list, FirebaseMsgModel currentUser, FirebaseUser firebaseUser, DatabaseReference reference, ItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.itemClickListener = itemClickListener;
        this.firebaseUser = firebaseUser;
        this.reference = reference;
    }

    public void setFilter(ArrayList<FirebaseMsgModel> newList) {
        mPayList = new ArrayList<>();
        mPayList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_time_line, parent, false));
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);

        FirebaseMsgModel item = mPayList.get(position);
        //1 img //2 gif //3 audio //4 video //5 document //6 url // 7 text
        //sent msg

        if (item.getSenderid() == senderId){

            Log.e("contact","toString : " +item.toString());

            holder.ll_left.setVisibility(View.GONE);
            holder.ll_right.setVisibility(View.VISIBLE);
            holder.tv_timeRight.setText(String.valueOf(item.getMsgtime()));
            holder.tv_nameRight.setText(String.valueOf(item.getSenderid()));

            setMsgStatus(item,holder.tv_timeRight);

            if (item.getIsForward()==1){
                holder.tv_nameRight.setVisibility(View.VISIBLE);
                holder.tv_nameRight.setText("Forwarded");  //ic_arrow
                holder.tv_nameRight.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow, 0, 0, 0);

            }else {
                holder.tv_nameRight.setVisibility(View.GONE);
            }
            //check if msg is reply to existing msg
            //video
            if (item.getMediatype() == 4) {
                if (item.getIsdeleted() == 1){

                    holder.tv_msgRight.setText(String.valueOf(item.getMessage()));
                    holder.ll_videoRight.setVisibility(View.GONE);
                    holder.rl_imageRight.setVisibility(View.GONE);
                    holder.ll_msgRight.setVisibility(View.VISIBLE);
                    holder.ll_audioRight.setVisibility(View.GONE);
                    holder.rl_pdfRight.setVisibility(View.GONE);
                    holder.ll_contactRight.setVisibility(View.GONE);
                }
                else {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_VIDEO_PATH + "Sent/" + item.getMessage());
                        if (file.exists()) {
                            holder.ll_file_sizeRight.setVisibility(View.GONE);
                            // Log.e("fileexists", "file exists " + item.getMessage());
                            Glide.with(context).load(file).placeholder(R.drawable.ic_audio).into(holder.iv_videoRight);
                        } else {
                            // Log.e("fileexists", "file not exists " + item.getMessage());
                            String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                            holder.ll_file_sizeRight.setVisibility(View.VISIBLE);
                            holder.tv_sizeRight.setText(Formatter.formatShortFileSize(context, new File(media).length()));
                            Glide.with(context)
                                    .load(media)
                                    .thumbnail(0.01f)
                                    .override(50, 50)
                                    .placeholder(R.drawable.ic_video)
                                    .into(holder.iv_videoRight);

                            holder.ll_file_sizeRight.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile(holder.pb_videoRight, holder.tv_sizeRight, file, media);
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "onBindViewHolder: video " + e.getMessage());
                    }

                    //   Glide.with(context).load(media).into( holder.iv_videoRight);
                    holder.ll_videoRight.setVisibility(View.VISIBLE);
                    holder.rl_imageRight.setVisibility(View.GONE);
                    holder.ll_msgRight.setVisibility(View.GONE);
                    holder.ll_audioRight.setVisibility(View.GONE);
                    holder.rl_pdfRight.setVisibility(View.GONE);
                    holder.ll_contactRight.setVisibility(View.GONE);
                }
            }
            //image
            else if (item.getMediatype() == 1) {
                //Log.e(TAG, "onBindViewHolder: image "+Constants.URL_CHATTING_MEDIA + item.getMessage());

                if (item.getIsdeleted() == 1){

                    holder.tv_msgRight.setText(String.valueOf(item.getMessage()));
                    holder.ll_videoRight.setVisibility(View.GONE);
                    holder.rl_imageRight.setVisibility(View.GONE);
                    holder.ll_msgRight.setVisibility(View.VISIBLE);
                    holder.ll_audioRight.setVisibility(View.GONE);
                    holder.rl_pdfRight.setVisibility(View.GONE);
                    holder.ll_contactRight.setVisibility(View.GONE);
                }
                else {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_IMAGE_PATH + "Sent/" + item.getMessage());
                        if (file.exists()) {
                            //      Log.e("fileexists", "file exists " + item.getMessage());
                            holder.ll_img_sizeRight.setVisibility(View.GONE);
                            Glide.with(context).load(file).placeholder(R.drawable.ic_gallery).into(holder.iv_imageRight);
                        } else {
                            String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                            holder.ll_img_sizeRight.setVisibility(View.VISIBLE);
                            holder.tv_sizeImgRight.setText(Formatter.formatShortFileSize(context, new File(media).length()));
                            Glide.with(context)
                                    .load(media)
                                    .thumbnail(0.01f)
                                    .override(50, 50)
                                    .placeholder(R.drawable.ic_gallery)
                                    .into(holder.iv_imageRight);

                            holder.ll_img_sizeRight.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile(holder.pb_imgRight, holder.tv_sizeImgRight, file, media);
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "onBindViewHolder: image " + e.getMessage());
                    }
                    //Picasso.get().load(media).placeholder(R.drawable.male_avatar).into(holder.iv_imageRight);
                    holder.ll_videoRight.setVisibility(View.GONE);
                    holder.rl_imageRight.setVisibility(View.VISIBLE);
                    holder.ll_msgRight.setVisibility(View.GONE);
                    holder.ll_audioRight.setVisibility(View.GONE);
                    holder.rl_pdfRight.setVisibility(View.GONE);
                    holder.ll_contactRight.setVisibility(View.GONE);
                }

            }
            //gif
            else if (item.getMediatype() == 2) {

                if (item.getIsdeleted() == 1){

                    holder.tv_msgRight.setText(String.valueOf(item.getMessage()));
                    holder.ll_videoRight.setVisibility(View.GONE);
                    holder.rl_imageRight.setVisibility(View.GONE);
                    holder.ll_msgRight.setVisibility(View.VISIBLE);
                    holder.ll_audioRight.setVisibility(View.GONE);
                    holder.rl_pdfRight.setVisibility(View.GONE);
                    holder.ll_contactRight.setVisibility(View.GONE);
                }
                else {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_GIF_PATH + "Sent/" + item.getMessage());
                        if (file.exists()) {
                            holder.ll_img_sizeRight.setVisibility(View.GONE);
                            // Glide.with(context).load(file).placeholder(R.drawable.ic_gallery).into(holder.iv_imageRight);
                            Glide.with(context).asGif().load(file).placeholder(R.drawable.ic_gallery).into(holder.iv_imageRight);
                        } else {
                            String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                            holder.ll_img_sizeRight.setVisibility(View.VISIBLE);
                            holder.tv_sizeImgRight.setText(Formatter.formatShortFileSize(context, new File(media).length()));
                            Glide.with(context)
                                    .load(media)
                                    .thumbnail(0.01f)
                                    .override(50, 50)
                                    .placeholder(R.drawable.ic_gallery)
                                    .into(holder.iv_imageRight);

                            holder.ll_img_sizeRight.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile(holder.pb_imgRight, holder.tv_sizeImgRight, file, media);
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "onBindViewHolder: image " + e.getMessage());
                    }
                        /*String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                        Glide.with(context).asGif().load(media).into(holder.iv_imageRight);*/
                    holder.ll_videoRight.setVisibility(View.GONE);
                    holder.rl_imageRight.setVisibility(View.VISIBLE);
                    holder.ll_msgRight.setVisibility(View.GONE);
                    holder.ll_audioRight.setVisibility(View.GONE);
                    holder.rl_pdfRight.setVisibility(View.GONE);
                    holder.ll_contactRight.setVisibility(View.GONE);
                }
            }
            //audio
            else if (item.getMediatype() == 3) {

                if (item.getIsdeleted() == 1){

                    holder.tv_msgRight.setText(String.valueOf(item.getMessage()));
                    holder.ll_videoRight.setVisibility(View.GONE);
                    holder.rl_imageRight.setVisibility(View.GONE);
                    holder.ll_msgRight.setVisibility(View.VISIBLE);
                    holder.ll_audioRight.setVisibility(View.GONE);
                    holder.rl_pdfRight.setVisibility(View.GONE);
                    holder.ll_contactRight.setVisibility(View.GONE);
                }
                else {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_AUDIO_PATH + "Sent/" + item.getMessage());
                        if (file.exists()) {
                            holder.iv_playRight.setImageResource(R.drawable.ic_play_arrow);
                            //Glide.with(context).load(file).placeholder(R.drawable.ic_gallery).into(holder.iv_imageRight);
                        } else {
                            holder.iv_playRight.setImageResource(R.drawable.ic_vertical_align_bottom);
                            String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                            holder.iv_playRight.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile(holder.pb_audioRight, holder.tv_sizeImgRight, file, media);
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "onBindViewHolder: image " + e.getMessage());
                    }
                    holder.ll_audioRight.setVisibility(View.VISIBLE);
                    holder.ll_videoRight.setVisibility(View.GONE);
                    holder.rl_imageRight.setVisibility(View.GONE);
                    holder.ll_msgRight.setVisibility(View.GONE);
                    holder.rl_pdfRight.setVisibility(View.GONE);
                    holder.ll_contactRight.setVisibility(View.GONE);

                }

            }
            //doc file
            else if (item.getMediatype() == 5) {
                if (item.getIsdeleted() == 1){

                    holder.tv_msgRight.setText(String.valueOf(item.getMessage()));
                    holder.ll_videoRight.setVisibility(View.GONE);
                    holder.rl_imageRight.setVisibility(View.GONE);
                    holder.ll_msgRight.setVisibility(View.VISIBLE);
                    holder.ll_audioRight.setVisibility(View.GONE);
                    holder.rl_pdfRight.setVisibility(View.GONE);
                    holder.ll_contactRight.setVisibility(View.GONE);
                }
                else {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_DOCUMENT_PATH + "Sent/" + item.getMessage());
                        if (file.exists()) {
                            holder.iv_sizeDocRight.setVisibility(View.GONE);
                            holder.tv_tv_docNameRight.setText(item.getMessage());
                            holder.tv_docExtentionRight.setText(item.getMessage().substring(item.getMessage().lastIndexOf(".")));
                            //Glide.with(context).asBitmap().load(file).placeholder(R.drawable.ic_gallery).into(holder.iv_imageRight);
                        } else {
                            String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                            holder.iv_sizeDocRight.setVisibility(View.VISIBLE);
                            holder.tv_tv_docNameRight.setText(item.getMessage());
                            holder.tv_docExtentionRight.setText(item.getMessage().substring(item.getMessage().lastIndexOf(".")));

                            holder.iv_sizeDocRight.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile(holder.pb_docRight, holder.tv_sizeImgRight, file, media);
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "onBindViewHolder: image " + e.getMessage());
                    }
                    // String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                    // Glide.with(context).asGif().load(media).into(holder.iv_imageRight);
                    holder.ll_videoRight.setVisibility(View.GONE);
                    holder.rl_pdfRight.setVisibility(View.VISIBLE);
                    holder.rl_imageRight.setVisibility(View.GONE);
                    holder.ll_msgRight.setVisibility(View.GONE);
                    holder.ll_audioRight.setVisibility(View.GONE);
                    holder.ll_contactRight.setVisibility(View.GONE);
                }

            }
            else if (item.getMediatype() == 7) {
                holder.ll_videoRight.setVisibility(View.GONE);
                holder.rl_imageRight.setVisibility(View.GONE);
                holder.ll_msgRight.setVisibility(View.VISIBLE);
                holder.ll_audioRight.setVisibility(View.GONE);
                holder.rl_pdfRight.setVisibility(View.GONE);
                holder.ll_contactRight.setVisibility(View.GONE);

                holder.tv_msgRight.setText(String.valueOf(item.getMessage()));


                if (!item.getReplyToMsgId().equals("-1") ){
                    showQuotedMessage(item,holder.txtQuotedMsgRight, holder.txtQuotedMsgTypeRight,holder.iv_previewRight, holder.rl_reply_layoutRight);
                }
            }

            //contact
            else if (item.getMediatype() == 8) {

                Log.e("contact","contact 0 : " +item.getMessage());

                if (item.getIsdeleted() == 1){

                    holder.tv_msgRight.setText(String.valueOf(item.getMessage()));
                    holder.ll_videoRight.setVisibility(View.GONE);
                    holder.rl_imageRight.setVisibility(View.GONE);
                    holder.ll_msgRight.setVisibility(View.VISIBLE);
                    holder.ll_audioRight.setVisibility(View.GONE);
                    holder.rl_pdfRight.setVisibility(View.GONE);
                    holder.ll_contactRight.setVisibility(View.GONE);

                    Log.e("contact","contact 1 : " +item.getMessage());

                }
                else {

                    holder.ll_videoRight.setVisibility(View.GONE);
                    holder.rl_imageRight.setVisibility(View.GONE);
                    holder.ll_msgRight.setVisibility(View.GONE);
                    holder.ll_audioRight.setVisibility(View.GONE);
                    holder.rl_pdfRight.setVisibility(View.GONE);
                    holder.ll_contactRight.setVisibility(View.VISIBLE);

                    holder.tv_contactMsgRight.setText(item.getMessage());

                    Log.e("contact","contact 2 :" +item.getMessage());

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
                        holder.ll_videoRight.setVisibility(View.GONE);
                        holder.rl_imageRight.setVisibility(View.GONE);
                        holder.ll_linkRight.setVisibility(View.VISIBLE);
                        holder.ll_audioRight.setVisibility(View.GONE);
                        holder.rl_pdfRight.setVisibility(View.GONE);
                        holder.ll_contactRight.setVisibility(View.GONE);

                        holder.richLinkView_right.setLink(item.getMessage(), new ViewListener() {
                            @Override
                            public void onSuccess(boolean status) {
                                Log.e("link :", "" + item.getMessage());
                            }

                            @Override
                            public void onError(Exception e) {
                                // Log.e(TAG, "onError: e " + e.getMessage());
                                holder.tv_msgRight.setText(String.valueOf(item.getMessage()));
                                holder.ll_videoRight.setVisibility(View.GONE);
                                holder.rl_imageRight.setVisibility(View.GONE);
                                holder.ll_msgRight.setVisibility(View.VISIBLE);
                                holder.ll_audioRight.setVisibility(View.GONE);
                                holder.rl_pdfRight.setVisibility(View.GONE);
                                holder.ll_contactRight.setVisibility(View.GONE);

                            }
                        });
                    } else {
                        if (item.getIsdeleted() == 1){
                            holder.tv_msgRight.setText(String.valueOf(item.getMessage()));
                            holder.ll_videoRight.setVisibility(View.GONE);
                            holder.rl_imageRight.setVisibility(View.GONE);
                            holder.ll_msgRight.setVisibility(View.VISIBLE);
                            holder.ll_audioRight.setVisibility(View.GONE);
                            holder.rl_pdfRight.setVisibility(View.GONE);
                            holder.ll_contactRight.setVisibility(View.GONE);
                        }
                        else {
                            holder.tv_msgRight.setText(String.valueOf(item.getMessage()));
                            holder.ll_videoRight.setVisibility(View.GONE);
                            holder.rl_imageRight.setVisibility(View.GONE);
                            holder.ll_msgRight.setVisibility(View.VISIBLE);
                            holder.ll_audioRight.setVisibility(View.GONE);
                            holder.rl_pdfRight.setVisibility(View.GONE);
                            holder.ll_contactRight.setVisibility(View.GONE);
                        }

                    }

                } catch (Exception e) {
                    Log.e(TAG, "onBindViewHolder: link " + e.getMessage());
                }
            }

        }

        //received msg
        else if(item.getSenderid() == receiverId){
            holder.ll_left.setVisibility(View.VISIBLE);
            holder.ll_right.setVisibility(View.GONE);
            holder.tv_timeLeft.setText(String.valueOf(item.getMsgtime()));
            holder.tv_nameLeft.setText(String.valueOf(item.getSenderid()));

            setMsgStatus(item,holder.tv_timeLeft);

            if (item.getIsForward()==1){
                holder.tv_nameLeft.setVisibility(View.VISIBLE);
                holder.tv_nameLeft.setText("Forwarded");  //ic_arrow
                holder.tv_nameLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow, 0, 0, 0);

            }else {
                holder.tv_nameRight.setVisibility(View.GONE);
            }


            //video
            if (item.getMediatype() == 4){
                if (item.getIsdeleted() == 1){
                    holder.tv_msgLeft.setText(String.valueOf(item.getMessage()) );
                    holder.ll_videoLeft.setVisibility(View.GONE);
                    holder.rl_imageLeft.setVisibility(View.GONE);
                    holder.ll_textLeft.setVisibility(View.VISIBLE);
                    holder.ll_audioLeft.setVisibility(View.GONE);
                    holder.rl_pdfLeft.setVisibility(View.GONE);

                }else {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory()+"/"+Constants.OFFLINE_VIDEO_PATH  + item.getMessage());
                        if (file.exists()) {
                            Glide.with(context).load(file).placeholder(R.drawable.ic_audio).into(holder.iv_videoLeft);
                            holder.ll_file_sizeLeft.setVisibility(View.GONE);
                        }
                        else {
                            String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                            holder.ll_file_sizeLeft.setVisibility(View.VISIBLE);
                            File  file1 = new File(media);
                            holder.tv_sizeLeft.setText(Formatter.formatShortFileSize(context,file1.length()));
                            Glide.with(context)
                                    .load(media)
                                    .thumbnail(0.01f)
                                    .override(50,50)
                                    .placeholder(R.drawable.ic_video)
                                    .into(holder.iv_videoLeft);

                            holder.ll_file_sizeLeft.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile( holder.pb_videoLeft,holder.tv_sizeLeft,file,media);
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.e(TAG, "onBindViewHolder: video "+e.getMessage()  );
                    }

                    //String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                    //  Glide.with(context).load(media).into( holder.iv_videoLeft);
                    holder.ll_videoLeft.setVisibility(View.VISIBLE);
                    holder.rl_imageLeft.setVisibility(View.GONE);
                    holder.ll_textLeft.setVisibility(View.GONE);
                    holder.ll_audioLeft.setVisibility(View.GONE);
                    holder.rl_pdfLeft.setVisibility(View.GONE);
                    holder.ll_contactLeft.setVisibility(View.GONE);
                }

            }

            //image
            else if (item.getMediatype() == 1){

                //  Log.e(TAG, "onBindViewHolder: image "+Constants.URL_CHATTING_MEDIA + item.getMessage());

                if (item.getIsdeleted() == 1){
                    holder.tv_msgLeft.setText(String.valueOf(item.getMessage()) );
                    holder.ll_videoLeft.setVisibility(View.GONE);
                    holder.rl_imageLeft.setVisibility(View.GONE);
                    holder.ll_textLeft.setVisibility(View.VISIBLE);
                    holder.ll_audioLeft.setVisibility(View.GONE);
                    holder.rl_pdfLeft.setVisibility(View.GONE);

                }
                else {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory()+"/"+Constants.OFFLINE_IMAGE_PATH  + item.getMessage());
                        if (file.exists()) {
                            holder.ll_img_sizeLeft.setVisibility(View.GONE);
                            Glide.with(context).load(file).placeholder(R.drawable.ic_gallery).into(holder.iv_imageLeft);
                        }
                        else {
                            String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                            holder.ll_img_sizeLeft.setVisibility(View.VISIBLE);
                            holder.tv_sizeImgLeft.setText(Formatter.formatShortFileSize(context,new File(media).length()));
                            Glide.with(context)
                                    .load(media)
                                    .thumbnail(0.01f)
                                    .override(50,50)
                                    .placeholder(R.drawable.ic_gallery)
                                    .into(holder.iv_imageLeft);

                            holder.ll_img_sizeLeft.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile( holder.pb_imgLeft,holder.tv_sizeImgLeft,file,media);
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.e(TAG, "onBindViewHolder: image "+e.getMessage()  );
                    }
                    holder.ll_videoLeft.setVisibility(View.GONE);
                    holder.rl_imageLeft.setVisibility(View.VISIBLE);
                    holder.ll_textLeft.setVisibility(View.GONE);
                    holder.ll_audioLeft.setVisibility(View.GONE);
                    holder.rl_pdfLeft.setVisibility(View.GONE);
                    holder.ll_contactLeft.setVisibility(View.GONE);
                }
            }

            //gif
            else if(item.getMediatype() == 2){
                if (item.getIsdeleted() == 1){
                    holder.tv_msgLeft.setText(String.valueOf(item.getMessage()) );
                    holder.ll_videoLeft.setVisibility(View.GONE);
                    holder.rl_imageLeft.setVisibility(View.GONE);
                    holder.ll_textLeft.setVisibility(View.VISIBLE);
                    holder.ll_audioLeft.setVisibility(View.GONE);
                    holder.rl_pdfLeft.setVisibility(View.GONE);

                }
                else {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory()+"/"+Constants.OFFLINE_GIF_PATH  + item.getMessage());
                        if (file.exists()) {
                            holder.ll_img_sizeLeft.setVisibility(View.GONE);
                            Glide.with(context).asGif().load(file).placeholder(R.drawable.ic_gallery).into(holder.iv_imageRight);
                        }
                        else {
                            String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                            holder.ll_img_sizeLeft.setVisibility(View.VISIBLE);
                            holder.tv_sizeImgLeft.setText(Formatter.formatShortFileSize(context,new File(media).length()));
                            Glide.with(context)
                                    .load(media)
                                    .thumbnail(0.01f)
                                    .override(50,50)
                                    .placeholder(R.drawable.ic_gallery)
                                    .into(holder.iv_imageLeft);

                            holder.ll_img_sizeLeft.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile( holder.pb_imgLeft,holder.tv_sizeImgLeft,file,media);
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.e(TAG, "onBindViewHolder: image "+e.getMessage()  );
                    }
                    // String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                    // Glide.with(context).asGif().load(media).into(holder.iv_imageRight);
                    holder.ll_videoLeft.setVisibility(View.GONE);
                    holder.rl_imageLeft.setVisibility(View.VISIBLE);
                    holder.ll_textLeft.setVisibility(View.GONE);
                    holder.ll_audioLeft.setVisibility(View.GONE);
                    holder.rl_pdfLeft.setVisibility(View.GONE);
                    holder.ll_contactLeft.setVisibility(View.GONE);
                }

            }

            //audio
            else if (item.getMediatype() == 3){
                if (item.getIsdeleted() == 1){
                    holder.tv_msgLeft.setText(String.valueOf(item.getMessage()) );
                    holder.ll_videoLeft.setVisibility(View.GONE);
                    holder.rl_imageLeft.setVisibility(View.GONE);
                    holder.ll_textLeft.setVisibility(View.VISIBLE);
                    holder.ll_audioLeft.setVisibility(View.GONE);
                    holder.rl_pdfLeft.setVisibility(View.GONE);

                }else {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory()+"/"+Constants.OFFLINE_AUDIO_PATH  + item.getMessage());
                        if (file.exists()) {
                            holder.iv_playLeft.setImageResource(R.drawable.ic_play_arrow);
                            //Glide.with(context).load(file).placeholder(R.drawable.ic_gallery).into(holder.iv_imageRight);
                        }
                        else {
                            holder.iv_playLeft.setImageResource(R.drawable.ic_vertical_align_bottom);
                            String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                            holder.iv_playLeft.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile( holder.pb_audioLeft,holder.tv_sizeImgRight,file,media);
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.e(TAG, "onBindViewHolder: image "+e.getMessage()  );
                    }
                    holder.ll_audioLeft.setVisibility(View.VISIBLE);
                    holder.ll_videoLeft.setVisibility(View.GONE);
                    holder.rl_imageLeft.setVisibility(View.GONE);
                    holder.ll_textLeft.setVisibility(View.GONE);
                    holder.rl_pdfLeft.setVisibility(View.GONE);
                    holder.ll_contactLeft.setVisibility(View.GONE);
                }


            }
            else if (item.getMediatype() == 7){
                holder.ll_videoLeft.setVisibility(View.GONE);
                holder.rl_imageLeft.setVisibility(View.GONE);
                holder.ll_textLeft.setVisibility(View.VISIBLE);
                holder.ll_audioLeft.setVisibility(View.GONE);
                holder.rl_pdfLeft.setVisibility(View.GONE);
                holder.ll_contactLeft.setVisibility(View.GONE);

                holder.tv_msgLeft.setText(String.valueOf(item.getMessage()));

                if (!item.getReplyToMsgId().equals("-1")) {
                    showQuotedMessage(item,holder.txtQuotedMsgLeft, holder.txtQuotedMsgTypeLeft,holder.iv_previewLeft, holder.rl_reply_layoutLeft);
                }
            }
            //document
            else if(item.getMediatype() == 5){
                if (item.getIsdeleted() == 1){
                    holder.tv_msgLeft.setText(String.valueOf(item.getMessage()) );
                    holder.ll_videoLeft.setVisibility(View.GONE);
                    holder.rl_imageLeft.setVisibility(View.GONE);
                    holder.ll_textLeft.setVisibility(View.VISIBLE);
                    holder.ll_audioLeft.setVisibility(View.GONE);
                    holder.rl_pdfLeft.setVisibility(View.GONE);

                }else {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory()+"/"+Constants.OFFLINE_DOCUMENT_PATH  + item.getMessage());
                        if (file.exists()) {
                            holder.iv_sizeDocLeft.setVisibility(View.GONE);
                            holder.tv_tv_docNameLeft.setText(item.getMessage());
                            holder.tv_docExtentionLeft.setText(item.getMessage().substring(item.getMessage().lastIndexOf(".")));
                            //Glide.with(context).asBitmap().load(file).placeholder(R.drawable.ic_gallery).into(holder.iv_imageRight);
                        }
                        else {
                            String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                            holder.iv_sizeDocLeft.setVisibility(View.VISIBLE);
                            holder.tv_tv_docNameLeft.setText(item.getMessage());
                            holder.tv_docExtentionLeft.setText(item.getMessage().substring(item.getMessage().lastIndexOf(".")));

                            holder.iv_sizeDocLeft.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile( holder.pb_docLeft,holder.tv_sizeImgLeft,file,media);
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.e(TAG, "onBindViewHolder: image "+e.getMessage()  );
                    }
                    // String media = Constants.URL_CHATTING_MEDIA + item.getMessage();
                    // Glide.with(context).asGif().load(media).into(holder.iv_imageRight);
                    holder.ll_videoLeft.setVisibility(View.GONE);
                    holder.rl_pdfLeft.setVisibility(View.VISIBLE);
                    holder.rl_imageLeft.setVisibility(View.GONE);
                    holder.ll_textLeft.setVisibility(View.GONE);
                    holder.ll_audioLeft.setVisibility(View.GONE);
                    holder.ll_contactLeft.setVisibility(View.GONE);
                }
            }
            //contact
            else if (item.getMediatype() == 8) {
                if (item.getIsdeleted() == 1){
                    holder.tv_msgLeft.setText(String.valueOf(item.getMessage()) );
                    holder.ll_videoLeft.setVisibility(View.GONE);
                    holder.rl_imageLeft.setVisibility(View.GONE);
                    holder.ll_textLeft.setVisibility(View.VISIBLE);
                    holder.ll_audioLeft.setVisibility(View.GONE);
                    holder.rl_pdfLeft.setVisibility(View.GONE);

                }else {
                    holder.ll_videoLeft.setVisibility(View.GONE);
                    holder.rl_imageLeft.setVisibility(View.GONE);
                    holder.ll_textLeft.setVisibility(View.GONE);
                    holder.ll_audioLeft.setVisibility(View.GONE);
                    holder.rl_pdfLeft.setVisibility(View.GONE);
                    holder.ll_contactLeft.setVisibility(View.VISIBLE);
                    holder.tv_contactMsgLeft.setText(item.getMessage());
                }

            }
            else {
                try {
                    String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
                    Pattern p = Pattern.compile(URL_REGEX);
                    Matcher m = p.matcher(item.getMessage());//replace with string to compare
                    //if (m.find()){
                    if (Utility.containsURL(item.getMessage())){
                        holder.ll_videoLeft.setVisibility(View.GONE);
                        holder.rl_imageLeft.setVisibility(View.GONE);
                        holder.ll_linkLeft.setVisibility(View.VISIBLE);
                        holder.ll_audioLeft.setVisibility(View.GONE);
                        holder.rl_pdfLeft.setVisibility(View.GONE);
                        holder.ll_contactLeft.setVisibility(View.GONE);


                        holder.richLinkView_left.setLink(item.getMessage(), new ViewListener() {
                            @Override
                            public void onSuccess(boolean status) {
                                Log.e("link :", "" + item.getMessage());
                            }

                            @Override
                            public void onError(Exception e) {
                                holder.tv_msgLeft.setText(String.valueOf(item.getMessage()));
                                holder.ll_videoLeft.setVisibility(View.GONE);
                                holder.rl_imageLeft.setVisibility(View.GONE);
                                holder.ll_textLeft.setVisibility(View.VISIBLE);
                                holder.ll_audioLeft.setVisibility(View.GONE);
                                holder.rl_pdfLeft.setVisibility(View.GONE);
                                Log.e(TAG, "link onError: e " + e.getMessage());
                            }
                        });
                    }else {
                        if (item.getIsdeleted() == 1){
                            holder.tv_msgLeft.setText(String.valueOf(item.getMessage()) );
                            holder.ll_videoLeft.setVisibility(View.GONE);
                            holder.rl_imageLeft.setVisibility(View.GONE);
                            holder.ll_textLeft.setVisibility(View.VISIBLE);
                            holder.ll_audioLeft.setVisibility(View.GONE);
                            holder.rl_pdfLeft.setVisibility(View.GONE);
                        }
                        else {
                            holder.tv_msgLeft.setText(String.valueOf(item.getMessage()) );
                            holder.ll_videoLeft.setVisibility(View.GONE);
                            holder.rl_imageLeft.setVisibility(View.GONE);
                            holder.ll_textLeft.setVisibility(View.VISIBLE);
                            holder.ll_audioLeft.setVisibility(View.GONE);
                            holder.rl_pdfLeft.setVisibility(View.GONE);
                        }
                    }
                }catch (Exception e){
                    Log.e(TAG, "onBindViewHolder: link "+e.getMessage() );
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == mPayList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return mPayList == null ? 0 : mPayList.size();
    }


    public void addItems(List<FirebaseMsgModel> postItems) {
        mPayList.addAll(postItems);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        mPayList.add(new FirebaseMsgModel());
        notifyItemInserted(mPayList.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = mPayList.size() - 1;
        FirebaseMsgModel item = getItem(position);
        if (item != null) {
            mPayList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        mPayList.clear();
        notifyDataSetChanged();
    }

    FirebaseMsgModel getItem(int position) {
        return mPayList.get(position);
    }

    public interface ItemClickListener {
        void onItemClick(View v, int adapterPosition);
        void onMessageClick(View v, int adapterPosition);
        void onMessageLongClick(View v, int adapterPosition);
        void onTrainerItemClick(View view, int position);
    }

    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.tv_timeRight)
        TextView tv_timeRight;
        //@BindView(R.id.tv_)
        //TextView tv_trainingFrom;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null)
                        itemClickListener.onTrainerItemClick(v, getAdapterPosition());
                }
            });

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null)
                        itemClickListener.onItemClick(v, getAdapterPosition());
                }
            });

            tv_msgRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null)
                        itemClickListener.onMessageClick(v, getAdapterPosition());
                }
            });

            tv_msgLeft.setOnClickListener(new View.OnClickListener() {
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

        protected void clear() {
        }

        public void onBind(int position) {
            super.onBind(position);
            FirebaseMsgModel item = mPayList.get(position);
            tv_timeRight.setText(item.getMessage());
            //tv_trainingFrom.setText(item.getMsgtime());
        }


    }

    public class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
        }
    }

    private void setMsgStatus(FirebaseMsgModel item, TextView tv_time){
        switch (item.getMsgStatus()){
            case "sent":

                //tv_time.setCompoundDrawablesWithIntrinsicBounds( 0, 0,R.drawable.ic_tick, 0);

                if (item.getSenderid() == senderId){
                    tv_time.setCompoundDrawablesWithIntrinsicBounds( 0, 0,R.drawable.ic_tick, 0);
                }
                else {
                    tv_time.setCompoundDrawablesWithIntrinsicBounds( 0, 0,0, 0);
                }

                break;

            case "delivered":
                //tv_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_indicator, 0);
                if (item.getSenderid() == senderId){
                    tv_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_indicator, 0);
                }
                else {
                    tv_time.setCompoundDrawablesWithIntrinsicBounds( 0, 0,0, 0);
                }
                break;

            case "seen":
                //tv_time.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_double_tick_blue,  0);
                if (item.getSenderid() == senderId){
                    tv_time.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_double_tick_blue,  0);
                }
                else {
                    tv_time.setCompoundDrawablesWithIntrinsicBounds( 0, 0,0, 0);
                }
                break;
        }
    }

    private void showQuotedMessage(FirebaseMsgModel replyModel, TextView txtQuotedMsg, TextView txtQuotedMsgType, ImageView iv_preview, RelativeLayout rl_reply_layout) {

        try{
            if (replyModel.getReplyToMsgSenderId()==senderId){
                txtQuotedMsg.setText("You");
            }else {
                txtQuotedMsg.setText(offchattingPartner.getUsername());
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


}
