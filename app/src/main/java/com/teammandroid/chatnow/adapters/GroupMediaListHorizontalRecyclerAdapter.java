package com.teammandroid.chatnow.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.models.firebase.FirebaseGroupChatModel;
import com.teammandroid.chatnow.models.firebase.FirebaseMsgModel;
import com.teammandroid.chatnow.utils.Constants;

import java.io.File;
import java.util.ArrayList;

public class GroupMediaListHorizontalRecyclerAdapter extends RecyclerView.Adapter<GroupMediaListHorizontalRecyclerAdapter.MyViewHolder> {
    final String TAG = GroupMediaListHorizontalRecyclerAdapter.class.getSimpleName();

    private Activity context;

    ArrayList<FirebaseGroupChatModel> list;

    //PrefManager prefManager;

    String role_id = "";

    private GroupMediaListHorizontalRecyclerAdapter.ItemClickListener itemClickListener;

    FirebaseUser firebaseUser;

    public GroupMediaListHorizontalRecyclerAdapter(Activity context, ArrayList<FirebaseGroupChatModel> list, GroupMediaListHorizontalRecyclerAdapter.ItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.itemClickListener = itemClickListener;

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public GroupMediaListHorizontalRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_peoples, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_small_media_list, parent, false);
        GroupMediaListHorizontalRecyclerAdapter.MyViewHolder viewHolder = new GroupMediaListHorizontalRecyclerAdapter.MyViewHolder(view);
        return viewHolder;

    }

    public void filterList(ArrayList<FirebaseGroupChatModel> filterdNames) {
        this.list = filterdNames;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMediaListHorizontalRecyclerAdapter.MyViewHolder holder, int position) {

        final FirebaseGroupChatModel item = list.get(position);

        if (item.getIsdownloaded() == 1){

            //1 img //2 gif //3 audio //4 video //5 document //6 url // 7 text

            switch (item.getMediatype()){
                case 1:

                    try {
                        File directory;
                        if (item.getSender().equals(firebaseUser.getUid())) {
                            directory = new File(Environment.getExternalStorageDirectory() + "/" +Constants.OFFLINE_IMAGE_PATH + "Sent/" + item.getMessage());
                        } else {
                            directory = new File(Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_IMAGE_PATH + item.getMessage());
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
                        if (item.getSender().equals(firebaseUser.getUid())) {
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
                        if (item.getSender().equals(firebaseUser.getUid())) {
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


