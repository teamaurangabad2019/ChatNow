package com.teammandroid.chatnow.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaiselrahman.filepicker.model.MediaFile;
import com.teammandroid.chatnow.R;

import java.util.ArrayList;

public class CustomMusicAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<MediaFile> arrayList;
    private MediaPlayer mediaPlayer;
    private Boolean flag = true;

    public CustomMusicAdapter(Context context, int layout, ArrayList<MediaFile> arrayList) {
        this.context = context;
        this.layout = layout;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView txtName, txtSinger;
        ImageView ivPlay, ivStop;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(layout, null);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            viewHolder.txtSinger = (TextView) convertView.findViewById(R.id.txtSinger);
            viewHolder.ivPlay = (ImageView) convertView.findViewById(R.id.ivPlay);
            viewHolder.ivStop = (ImageView) convertView.findViewById(R.id.ivStop);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final MediaFile music = arrayList.get(position);

        viewHolder.txtName.setText(music.getName());
        viewHolder.txtSinger.setText(String.valueOf(music.getDuration()));

        View[] mLastView = new View[1];
        // play music
        viewHolder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag){
                    mediaPlayer = MediaPlayer.create(context, music.getUri());
                    flag = false;
                }


                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playView(v);
                    // viewHolder.ivPlay.setImageResource(R.drawable.ic_play);
                    mLastView[0] = v;

                } else {
                    mediaPlayer.start();
                    viewHolder.ivPlay.setImageResource(R.drawable.ic_pause);
                }

             /*   else {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    flag = true;
                    if (mLastView[0] != null){
                        playView(mLastView[0]);
                    }
                   viewHolder.ivPlay.setImageResource(R.drawable.ic_play);
                }*/

            }
        });

        // stop
        viewHolder.ivStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flag) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    flag = true;
                }
                playView(v);
                viewHolder.ivPlay.setImageResource(R.drawable.ic_play);
            }
        });

        return convertView;
    }

    private void playView(View v) {
        RelativeLayout parent = (RelativeLayout) (v.getParent());
        ImageView imgView = parent.findViewById(R.id.ivPlay);
        imgView.setImageResource(R.drawable.ic_play);
    }
}
