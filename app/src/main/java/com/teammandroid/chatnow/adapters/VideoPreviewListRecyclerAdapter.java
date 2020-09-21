package com.teammandroid.chatnow.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.teammandroid.chatnow.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class VideoPreviewListRecyclerAdapter extends RecyclerView.Adapter<VideoPreviewListRecyclerAdapter.MyViewHolder> {
    final String TAG = VideoPreviewListRecyclerAdapter.class.getSimpleName();
    int mediatype;

    Activity mContext;

    ArrayList<MediaFile> list;

    private ItemClickListener itemClickListener;

    public VideoPreviewListRecyclerAdapter(Activity context, ArrayList<MediaFile> list, int mediaType) {
        this.mContext = context;
        this.list = list;
        this.itemClickListener = itemClickListener;
        this.mediatype = mediaType;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_preview_list_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final MediaFile model = list.get(position);
        //1 img //2 gif //3 audio //4 video //5 document //6 url // 7 text
        try {

                holder.vv_selectedVideo.setVisibility(View.VISIBLE);
                    holder.vv_selectedVideo.setVideoPath(model.getPath());
                    MediaController mediaController = new
                            MediaController(mContext);
                    mediaController.setAnchorView(holder.vv_selectedVideo);
                    holder.vv_selectedVideo.setMediaController(mediaController);

                    holder.vv_selectedVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            //dialog.dismiss();
                        }
                    });
                    holder.vv_selectedVideo.requestFocus();
                    holder.vv_selectedVideo.start();
                } catch (Exception e) {
                    Log.e(TAG, "bindValues: e " + e.getMessage());
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
        VideoView vv_selectedVideo;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
              vv_selectedVideo = itemView.findViewById(R.id.vv_selectedVideo);

            itemView.setClickable(true);

        }
    }
}

