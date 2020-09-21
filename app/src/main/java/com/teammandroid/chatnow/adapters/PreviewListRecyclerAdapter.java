package com.teammandroid.chatnow.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.models.NotificationModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;


public class PreviewListRecyclerAdapter extends RecyclerView.Adapter<PreviewListRecyclerAdapter.MyViewHolder> {
    final String TAG = PreviewListRecyclerAdapter.class.getSimpleName();
    int mediatype;

    Activity mContext;

    ArrayList<MediaFile> list;

    private ItemClickListener itemClickListener;

    public PreviewListRecyclerAdapter(Activity context, ArrayList<MediaFile> list, int mediaType) {
        this.mContext = context;
        this.list = list;
        this.itemClickListener = itemClickListener;
        this.mediatype = mediaType;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preview_list_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final MediaFile model = list.get(position);
        //1 img //2 gif //3 audio //4 video //5 document //6 url // 7 text
        try {
            if (mediatype == 1) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), model.getUri());
                holder.iv_selectedImage.setImageBitmap(bitmap);
                holder.ll_audio.setVisibility(View.GONE);
                holder.iv_selectedImage.setVisibility(View.VISIBLE);
            } else if (model.getMimeType() == ".gif") {
                Glide.with(mContext).asGif().load(model.getUri()).into(holder.iv_selectedImage);
                holder.ll_audio.setVisibility(View.GONE);
                holder.iv_selectedImage.setVisibility(View.VISIBLE);

            } else if (mediatype == 3) {
                holder.iv_selectedImage.setVisibility(View.GONE);
                holder.ll_audio.setVisibility(View.VISIBLE);

                holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                        holder.seekBarHint.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                        holder.seekBarHint.setVisibility(View.VISIBLE);
                        int x = (int) Math.ceil(progress / 1000f);

                        if (x < 10)
                            holder.seekBarHint.setText("0:0" + x);
                        else
                            holder.seekBarHint.setText("0:" + x);

                        double percent = progress / (double) seekBar.getMax();
                        int offset = seekBar.getThumbOffset();
                        int seekWidth = seekBar.getWidth();
                        int val = (int) Math.round(percent * (seekWidth - 2 * offset));
                        int labelWidth = holder.seekBarHint.getWidth();
                        holder.seekBarHint.setX(offset + seekBar.getX() + val
                                - Math.round(percent * offset)
                                - Math.round(percent * labelWidth / 2));

                     /*   if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
                            clearMediaPlayer();
                            holder.fab.setImageDrawable(ContextCompat.getDrawable(context, android.R.drawable.ic_media_play));
                            holder.seekBar.setProgress(0);
                        }*/

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                       /* if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.seekTo(seekBar.getProgress());
                        }*/
                    }
                });
               /* dialog.setMessage("Loading Audio, please wait.");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();*/
                //  playSong(model, seekBar,fab);

            }
            if (mediatype == 5) {

                File file = new File(model.getUri().getPath());
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                // Old Approach
                // install.setDataAndType(Uri.fromFile(file), "application/pdf");
                // End Old approach
                // New Approach

                Uri apkURI = model.getUri();
                install.setDataAndType(apkURI, "application/pdf");
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                // End New Approach
                mContext.startActivity(install);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
        ImageView iv_selectedImage;
        LinearLayout ll_audio;
        SeekBar seekBar;
        TextView seekBarHint;
        FloatingActionButton fab;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
              iv_selectedImage = itemView.findViewById(R.id.iv_selectedImage);
              ll_audio = itemView.findViewById(R.id.ll_audio);
              seekBar = itemView.findViewById(R.id.seekbar);
              seekBarHint = itemView.findViewById(R.id.textView);
              fab = itemView.findViewById(R.id.button);

            itemView.setClickable(true);
           /* itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null)
                        itemClickListener.onClick(v, getAdapterPosition());
                }
            });*/

        }
    }
}

