package com.teammandroid.chatnow.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.steelkiwi.library.view.BadgeHolderLayout;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.models.MyChatModel;
import com.teammandroid.chatnow.models.NotificationModel;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseMsgModel;
import com.teammandroid.chatnow.offline.NotificationDatabaseHelper;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.SessionManager;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class MediaListRecyclerAdapter extends RecyclerView.Adapter<MediaListRecyclerAdapter.MyViewHolder> {
    final String TAG = MediaListRecyclerAdapter.class.getSimpleName();

    private Activity context;

    ArrayList<FirebaseMsgModel> list;

    //PrefManager prefManager;

    String role_id = "";

    private MediaListRecyclerAdapter.ItemClickListener itemClickListener;

    UserModel currentUser;
    public MediaListRecyclerAdapter(Activity context, ArrayList<FirebaseMsgModel> list, MediaListRecyclerAdapter.ItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.itemClickListener = itemClickListener;

        SessionManager sessionManager = new SessionManager(context);
        currentUser = sessionManager.getUserDetails();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_peoples, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media_list, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;

    }

    public void filterList(ArrayList<FirebaseMsgModel> filterdNames) {
        this.list = filterdNames;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final FirebaseMsgModel item = list.get(position);

        if (item.getIsdownloaded() == 1){

            //1 img //2 gif //3 audio //4 video //5 document //6 url // 7 text
            switch (item.getMediatype()){
                case 1:

                    try {
                        File directory;
                        if (item.getSenderid() == currentUser.getUserid()) {
                            directory = new File(Environment.getExternalStorageDirectory() + "/" +Constants.OFFLINE_IMAGE_PATH + "Sent/" + item.getMessage());

                        } else {
                            directory = new File(Environment.getExternalStorageDirectory() + "/" +Constants.OFFLINE_IMAGE_PATH + item.getMessage());
                        }

                        if (directory.exists()) {
                            Bitmap myBitmap = BitmapFactory.decodeFile(directory.getAbsolutePath());
                            holder.iv_media_image.setImageBitmap(myBitmap);
                        }else {
                            Log.e(TAG, "bindValues: directory "+directory );
                        }

                    }catch (Exception e){
                        Log.e(TAG, "bindValues: image except "+e.getMessage() );
                    }

                    break;

                case 2:
                    try {
                    File directory;
                    if (item.getSenderid() == currentUser.getUserid()) {
                        directory = new File(Environment.getExternalStorageDirectory() + "/" +Constants.OFFLINE_GIF_PATH + "Sent/" + item.getMessage());
                    } else {
                        directory = new File(Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_GIF_PATH + item.getMessage());
                    }
                        Glide.with(context).asGif().load(directory).into(holder.iv_media_image);
                    }catch (Exception e){
                        Log.e(TAG, "bindValues: glide e "+e.getMessage() );
                    }
                    break;

                case 4:
                    try {
                        File directory2 =  null;
                    if (item.getSenderid() == currentUser.getUserid()) {
                        directory2 = new File(Environment.getExternalStorageDirectory() + "/" +Constants.OFFLINE_VIDEO_PATH + "Sent/" + item.getMessage());
                    } else {
                        directory2 = new File(Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_VIDEO_PATH + item.getMessage());
                    }
                        Glide.with(context).load(directory2.getPath()).into(holder.iv_media_image);
                    }catch (Exception e){
                        Log.e(TAG, "bindValues: glide e "+e.getMessage() );
                    }
                    break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_media_image;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_media_image = itemView.findViewById(R.id.iv_media_image);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null)
                        itemClickListener.onClick(v, getAdapterPosition());
                }
            });

        }
    }
}

