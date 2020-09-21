package com.teammandroid.chatnow.adapters;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.models.ChatModel;
import com.teammandroid.chatnow.utils.Constants;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatTimeLineAdapter extends RecyclerView.Adapter<ChatTimeLineAdapter.MemberListViewHolder> {

    private static final String TAG = ChatTimeLineAdapter.class.getSimpleName();

    private Context context;
    List<ChatModel> mPayList;
    int senderId;
    int receiverId;
    MediaPlayer mediaPlayer = new MediaPlayer();
    boolean wasPlaying = false;
    String downloadUrl="";

    String imgFileFlag="";
    String audioFileFlag="";
    String videoFileFlag="";


    private  ChatTimeLineAdapter.ItemClickListener itemClickListener;

    public ChatTimeLineAdapter(Context context, ArrayList<ChatModel> mPayList, int senderId, int receiverId, ChatTimeLineAdapter.ItemClickListener itemClickListener) {
        this.context = context;
        this.mPayList = mPayList;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.itemClickListener = itemClickListener;
    }

    public void filterList(ArrayList<ChatModel> filterdNames) {
        this.mPayList = filterdNames;
        notifyDataSetChanged();
    }

    @Override
    public ChatTimeLineAdapter.MemberListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_chat_time_line, parent, false);
        return new ChatTimeLineAdapter.MemberListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ChatTimeLineAdapter.MemberListViewHolder holder, final int position) {

        final ChatModel item = mPayList.get(position);

     //   Log.e(TAG, "onBindViewHolder: chat"+item.toString() );
        if (item.getSenderid() == senderId){
            holder.ll_left.setVisibility(View.GONE);
            holder.ll_right.setVisibility(View.VISIBLE);
            holder.tv_timeRight.setText(String.valueOf(item.getCreated()));
            holder.tv_nameRight.setText(String.valueOf(item.getSenderid()));

            if (item.getMessagess().endsWith(".mp4")){

                String media = Constants.URL_CHATTING_MEDIA + item.getMessagess();

                Glide.with(context).load(media).into( holder.iv_videoRight);
                //holder.ll_videoRight.setBackgroundResource(R.drawable.ic_launcher_background);
                holder.ll_videoRight.setVisibility(View.VISIBLE);

                holder.iv_videoRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //downloadImgFile(downloadUrl);
                        new Thread(new Runnable() {
                            public void run() {
                                //Log.e("imgmsg","send msg"+Constants.URL_CHATTING_MEDIA+item.getMessage());
                                downloadUrl=Constants.URL_CHATTING_MEDIA+item.getMessagess();

                                videoFileFlag="sent";

                                DownloadvideoFile(downloadUrl,videoFileFlag);

                            }
                        }).start();

                    }
                });


                holder.iv_imageLeft.setVisibility(View.GONE);
                holder.ll_msgRight.setVisibility(View.GONE);
                holder.ll_audioRight.setVisibility(View.GONE);


            }
            else if (item.getMessagess().endsWith(".jpg") || item.getMessagess().endsWith(".png") || item.getMessagess().endsWith(".jpeg")){
                final String media = Constants.URL_CHATTING_MEDIA + item.getMessagess();


                Picasso.get().load(media).placeholder(R.drawable.male_avatar).into(holder.iv_imageRight);

                //Picasso.with(context).load(R.drawable.male_avatar).into(holder.iv_imageRight);

                holder.ll_videoRight.setVisibility(View.GONE);
                holder.iv_imageRight.setVisibility(View.VISIBLE);

                holder.iv_imageRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //downloadImgFile(downloadUrl);

                        new Thread(new Runnable() {
                            public void run() {

                                Log.e("imgmsg","send msg"+Constants.URL_CHATTING_MEDIA+item.getMessagess());

                                downloadUrl=Constants.URL_CHATTING_MEDIA+item.getMessagess();

                                imgFileFlag="sent";

                                DownloadImageFile(downloadUrl,imgFileFlag);

                                //DownloadImageFile(downloadUrl);

                            }
                        }).start();

                    }
                });


                holder.ll_msgRight.setVisibility(View.GONE);
                holder.ll_audioRight.setVisibility(View.GONE);

            }
            else if(item.getMessagess().endsWith(".gif")){
                String media = Constants.URL_CHATTING_MEDIA + item.getMessagess();
                Glide.with(context).asGif().load(media).into(holder.iv_imageRight);
                holder.ll_videoRight.setVisibility(View.GONE);
                holder.iv_imageRight.setVisibility(View.VISIBLE);
                holder.ll_msgRight.setVisibility(View.GONE);
                holder.ll_audioRight.setVisibility(View.GONE);

            }
            else if (item.getMessagess().endsWith(".mp3")){
                final String media = Constants.URL_CHATTING_MEDIA + item.getMessagess();

                holder.ll_audioRight.setVisibility(View.VISIBLE);

                holder.ll_audioRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //downloadUrl=Constants.URL_CHATTING_MEDIA+item.getMessage();

                        //new DownloadFileAsync().execute(downloadUrl);

                        //downloadImgFile(downloadUrl);

                        new Thread(new Runnable() {
                            public void run() {


                                //Log.e("imgmsg","send msg"+Constants.URL_CHATTING_MEDIA+item.getMessage());

                                //downloadUrl=media;

                                //downloadUrl=Constants.URL_CHATTING_MEDIA+item.getMessage();

                                audioFileFlag="sent";

                                DownloadAudioFile(downloadUrl,media);
                                //DownloadAudioFile(downloadUrl,audioFileFlag);

                                //DownloadImageFile(downloadUrl);

                            }
                        }).start();



                    }
                });


                holder.ll_videoRight.setVisibility(View.GONE);
                holder.iv_imageRight.setVisibility(View.GONE);
                holder.ll_msgRight.setVisibility(View.GONE);

                holder.iv_playRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //playSong(holder.seekbarRight, holder.iv_playRight, media);
                    }
                });

                holder.seekbarLeft.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                        holder.tv_hintRight.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.seekTo(seekBar.getProgress());
                        }
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                        //seekBarHint.setVisibility(View.VISIBLE);
                        int x = (int) Math.ceil(progress / 1000f);

                        if (x < 10)
                            holder.tv_hintRight.setText("0:0" + x);
                        else
                            holder.tv_hintRight.setText("0:" + x);

                        double percent = progress / (double) seekBar.getMax();
                        int offset = seekBar.getThumbOffset();
                        int seekWidth = seekBar.getWidth();
                        int val = (int) Math.round(percent * (seekWidth - 2 * offset));
                        int labelWidth = holder.tv_hintRight.getWidth();
                        holder.tv_hintRight.setX(offset + seekBar.getX() + val
                                - Math.round(percent * offset)
                                - Math.round(percent * labelWidth / 2));

                        if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
                            clearMediaPlayer();
                            holder.iv_playRight.setImageDrawable(ContextCompat.getDrawable(context, android.R.drawable.ic_media_play));
                            holder.seekbarRight.setProgress(0);
                        }

                    }

                });


            }
            else {
                holder.tv_msgRight.setText(String.valueOf(item.getMessagess()) );
                holder.ll_videoRight.setVisibility(View.GONE);
                holder.iv_imageRight.setVisibility(View.GONE);
                holder.ll_msgRight.setVisibility(View.VISIBLE);
                holder.ll_audioRight.setVisibility(View.GONE);
            }


        }else if(item.getSenderid() == receiverId){
            holder.ll_left.setVisibility(View.VISIBLE);
            holder.ll_right.setVisibility(View.GONE);
            holder.tv_timeLeft.setText(String.valueOf(item.getCreated()));
            holder.tv_nameLeft.setText(String.valueOf(item.getSenderid()));


            if (item.getMessagess().endsWith(".mp4")){

                String media = Constants.URL_CHATTING_MEDIA + item.getMessagess();
                Glide.with(context).load(media).into( holder.iv_videoLeft);
                holder.ll_videoLeft.setVisibility(View.VISIBLE);


                holder.iv_videoLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //downloadImgFile(downloadUrl);

                        new Thread(new Runnable() {
                            public void run() {


                                //Log.e("imgmsg","send msg"+Constants.URL_CHATTING_MEDIA+item.getMessage());

                                downloadUrl=Constants.URL_CHATTING_MEDIA+item.getMessagess();

                                videoFileFlag="received";

                                DownloadvideoFile(downloadUrl,videoFileFlag);

                                //DownloadImageFile(downloadUrl);

                            }
                        }).start();

                    }
                });


                holder.iv_imageLeft.setVisibility(View.GONE);
                holder.ll_textLeft.setVisibility(View.GONE);
                holder.ll_audioLeft.setVisibility(View.GONE);

            }
            else if (item.getMessagess().endsWith(".jpg") || item.getMessagess().endsWith(".png") || item.getMessagess().endsWith(".jpeg")){

                String media = Constants.URL_CHATTING_MEDIA + item.getMessagess();
                Picasso.get().load(media).placeholder(R.drawable.male_avatar).into(holder.iv_imageLeft);


                holder.ll_videoLeft.setVisibility(View.GONE);
                holder.iv_imageLeft.setVisibility(View.VISIBLE);

                holder.iv_imageLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //downloadImgFile(downloadUrl);

                        new Thread(new Runnable() {
                            public void run() {


                                Log.e("imgmsg","send msg"+Constants.URL_CHATTING_MEDIA+item.getMessagess());

                                downloadUrl=Constants.URL_CHATTING_MEDIA+item.getMessagess();

                                imgFileFlag="received";

                                DownloadImageFile(downloadUrl,imgFileFlag);
                                //DownloadImageFile(downloadUrl);

                            }
                        }).start();

                    }
                });




                holder.ll_textLeft.setVisibility(View.GONE);
                holder.ll_audioLeft.setVisibility(View.GONE);


            }
            else if(item.getMessagess().endsWith(".gif")){
                String media = Constants.URL_CHATTING_MEDIA + item.getMessagess();
                Glide.with(context).asGif().load(media).into(holder.iv_imageRight);
                holder.ll_videoLeft.setVisibility(View.GONE);
                holder.iv_imageLeft.setVisibility(View.VISIBLE);
                holder.ll_textLeft.setVisibility(View.GONE);
                holder.ll_audioLeft.setVisibility(View.GONE);


            }
            else if (item.getMessagess().endsWith(".mp3")){


                holder.ll_audioLeft.setVisibility(View.VISIBLE);

                holder.ll_audioLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //downloadImgFile(downloadUrl);

                        new Thread(new Runnable() {
                            public void run() {

                                //Log.e("imgmsg","send msg"+Constants.URL_CHATTING_MEDIA+item.getMessage());

                                downloadUrl=Constants.URL_CHATTING_MEDIA+item.getMessagess();

                                audioFileFlag="received";

                                DownloadAudioFile(downloadUrl,audioFileFlag);

                                //DownloadImageFile(downloadUrl);

                            }
                        }).start();

                    }
                });


                holder.ll_videoLeft.setVisibility(View.GONE);
                holder.iv_imageLeft.setVisibility(View.GONE);
                holder.ll_textLeft.setVisibility(View.GONE);



                final String media = Constants.URL_CHATTING_MEDIA + item.getMessagess();

                holder.iv_playLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // playSong(holder.seekbarLeft, holder.iv_playLeft, media);
                    }
                });

                holder.seekbarLeft.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        holder.tv_hintLeft.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.seekTo(seekBar.getProgress());
                        }
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                        //seekBarHint.setVisibility(View.VISIBLE);
                        int x = (int) Math.ceil(progress / 1000f);

                        if (x < 10)
                            holder.tv_hintLeft.setText("0:0" + x);
                        else
                            holder.tv_hintLeft.setText("0:" + x);

                        double percent = progress / (double) seekBar.getMax();
                        int offset = seekBar.getThumbOffset();
                        int seekWidth = seekBar.getWidth();
                        int val = (int) Math.round(percent * (seekWidth - 2 * offset));
                        int labelWidth = holder.tv_hintLeft.getWidth();
                        holder.tv_hintLeft.setX(offset + seekBar.getX() + val
                                - Math.round(percent * offset)
                                - Math.round(percent * labelWidth / 2));

                        if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
                            clearMediaPlayer();
                            holder.iv_playLeft.setImageDrawable(ContextCompat.getDrawable(context, android.R.drawable.ic_media_play));
                            holder.seekbarLeft.setProgress(0);
                        }

                    }

                });

            }
            else {
                holder.tv_msgLeft.setText(String.valueOf(item.getMessagess()) );

                holder.ll_videoLeft.setVisibility(View.GONE);
                holder.iv_imageLeft.setVisibility(View.GONE);
                holder.ll_textLeft.setVisibility(View.VISIBLE);
                holder.ll_audioLeft.setVisibility(View.GONE);

            }
        }

    }

    private void playSong(final SeekBar seekBar, ImageView iv_play, String media) {

        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                clearMediaPlayer();
                seekBar.setProgress(0);
                wasPlaying = true;
                iv_play.setImageDrawable(ContextCompat.getDrawable(context, android.R.drawable.ic_media_play));
            }


            if (!wasPlaying) {

                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }

                iv_play.setImageDrawable(ContextCompat.getDrawable(context, android.R.drawable.ic_media_pause));

                //AssetFileDescriptor descriptor = getAssets().openFd("suits.mp3");
                mediaPlayer.setDataSource(media);
               // descriptor.close();

                mediaPlayer.prepare();
                mediaPlayer.setVolume(0.5f, 0.5f);
                mediaPlayer.setLooping(false);
                seekBar.setMax(mediaPlayer.getDuration());

                mediaPlayer.start();
               // new Thread(context.).start();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopAudio(seekBar);
                    }
                }, mediaPlayer.getDuration());


            }

            wasPlaying = false;
        } catch (Exception e) {
            e.printStackTrace();

        }

}
    private void stopAudio(SeekBar seekBar) {

        try {

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

        }catch (Exception e){
            Log.e(TAG, "stopAudio: "+e.getMessage() );
        }
    }

    private void clearMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }


    @Override
    public int getItemCount() {
        return mPayList.size();
    }

    public interface ItemClickListener {
        void onItemClick(View v, int adapterPosition);
        void onMessageClick(View v, int adapterPosition);
        void onMessageLongClick(View v, int adapterPosition);
    }

    public class MemberListViewHolder extends RecyclerView.ViewHolder  {

      LinearLayout ll_left, ll_right;
      TextView tv_msgRight;
      TextView tv_msgLeft;
      TextView tv_timeRight;
      TextView tv_timeLeft;
      TextView tv_nameLeft;
      TextView tv_nameRight;
      LinearLayout ll_textLeft;
        RelativeLayout ll_videoLeft;
      LinearLayout ll_msgRight;
      RelativeLayout ll_videoRight;
      ImageView iv_imageRight, iv_imageLeft;
      ImageView iv_videoRight, iv_videoLeft;

      RelativeLayout ll_audioLeft, ll_audioRight;
      ImageView iv_playLeft, iv_playRight;
      ImageView iv_pauseLeft, iv_pauseRight;
      SeekBar seekbarLeft, seekbarRight;
      ImageView iv_gifLeft, iv_gifRight;
      TextView tv_hintRight, tv_hintLeft;

        public MemberListViewHolder(View itemView) {
            super(itemView);
            ll_left = itemView.findViewById(R.id.ll_left);
            ll_right = itemView.findViewById(R.id.ll_right);
            tv_msgRight = itemView.findViewById(R.id.tv_msgRight);
            tv_msgLeft = itemView.findViewById(R.id.tv_msgLeft);
            tv_timeRight = itemView.findViewById(R.id.tv_timeRight);
            tv_timeLeft = itemView.findViewById(R.id.tv_timeLeft);
            tv_nameLeft = itemView.findViewById(R.id.tv_nameLeft);
            tv_nameRight = itemView.findViewById(R.id.tv_nameRight);
            ll_textLeft = itemView.findViewById(R.id.ll_textLeft);
            iv_imageLeft = itemView.findViewById(R.id.iv_imageLeft);
            ll_videoLeft = itemView.findViewById(R.id.ll_videoLeft);
            ll_msgRight = itemView.findViewById(R.id.ll_msgRight);
            iv_imageRight = itemView.findViewById(R.id.iv_imageRight);
            ll_videoRight = itemView.findViewById(R.id.ll_videoRight);
            iv_videoRight = itemView.findViewById(R.id.iv_videoRight);
            iv_videoLeft = itemView.findViewById(R.id.iv_videoLeft);
            iv_playLeft = itemView.findViewById(R.id.iv_playLeft);
            iv_playRight = itemView.findViewById(R.id.iv_playRight);
            iv_pauseLeft = itemView.findViewById(R.id.iv_pauseLeft);
            iv_pauseRight = itemView.findViewById(R.id.iv_pauseRight);
            seekbarLeft = itemView.findViewById(R.id.seekbarLeft);
            seekbarRight = itemView.findViewById(R.id.seekbarRight);
            iv_gifLeft = itemView.findViewById(R.id.iv_gifLeft);
            iv_gifRight = itemView.findViewById(R.id.iv_gifRight);
            tv_hintRight = itemView.findViewById(R.id.tv_hintRight);
            tv_hintLeft = itemView.findViewById(R.id.tv_hintLeft);
            ll_audioRight = itemView.findViewById(R.id.ll_audioRight);
            ll_audioLeft = itemView.findViewById(R.id.ll_audioLeft);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) itemClickListener.onItemClick(v, getAdapterPosition());
                }
            });

            tv_msgRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) itemClickListener.onMessageClick(v, getAdapterPosition());
                }
            });

            tv_msgLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) itemClickListener.onMessageClick(v, getAdapterPosition());
                }
            });


            ll_right.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener!=null)itemClickListener.onMessageLongClick(v,getAdapterPosition());
                    return true;
                }
            });

            ll_left.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener!=null)itemClickListener.onMessageLongClick(v,getAdapterPosition());
                    return true;
                }
            });

            tv_msgRight.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener!=null)itemClickListener.onMessageLongClick(v,getAdapterPosition());
                    return true;
                }
            });

            tv_msgLeft.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener!=null)itemClickListener.onMessageLongClick(v,getAdapterPosition());
                    return true;
                }
            });
            iv_imageLeft.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener!=null)itemClickListener.onMessageLongClick(v,getAdapterPosition());
                    return true;
                }
            });
            iv_imageRight.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener!=null)itemClickListener.onMessageLongClick(v,getAdapterPosition());
                    return true;
                }
            });

            iv_videoRight.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener!=null)itemClickListener.onMessageLongClick(v,getAdapterPosition());
                    return true;
                }
            });

            iv_videoLeft.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener!=null)itemClickListener.onMessageLongClick(v,getAdapterPosition());
                    return true;
                }
            });
            ll_audioRight.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener!=null)itemClickListener.onMessageLongClick(v,getAdapterPosition());
                    return true;
                }
            });
            ll_audioLeft.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener!=null)itemClickListener.onMessageLongClick(v,getAdapterPosition());
                    return true;
                }
            });

        }

    }



    private void DownloadvideoFile(String downloadUrl, String videoFileFlag) {

        try {

            URL u = new URL(downloadUrl);

            InputStream is = u.openStream();

            DataInputStream dis = new DataInputStream(is);

            byte[] buffer = new byte[1024];

            int length;

            Date currentTime = Calendar.getInstance().getTime();


            String filename="";

            if (videoFileFlag.equals("received")){
                filename="Chatnow/Media/Chatnow Videos/"+currentTime.toString()+".mp4";
            }else
            {
                filename="Chatnow/Media/Chatnow Videos/Sent/"+currentTime.toString()+".mp4";
            }

            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/" +filename));


            while ((length = dis.read(buffer))>0) {

                fos.write(buffer, 0, length);

            }

            //Picasso.with(context).load(downloadUrl).into(imageView);


        } catch (MalformedURLException mue) {
            Log.e("SYNC getUpdate", "malformed url error", mue);
        } catch (IOException ioe) {
            Log.e("SYNC getUpdate", "io error", ioe);
        } catch (SecurityException se) {
            Log.e("SYNC getUpdate", "security error", se);
        }
    }


    private void DownloadAudioFile(String downloadUrl, String audioFileFlag) {

        try {

            URL u = new URL(downloadUrl);

            InputStream is = u.openStream();

            DataInputStream dis = new DataInputStream(is);

            byte[] buffer = new byte[1024];

            int length;

            Date currentTime = Calendar.getInstance().getTime();


            String filename="";

            if (audioFileFlag.equals("received")){
                filename="Chatnow/Media/Chatnow Audio/"+currentTime.toString()+".mp3";
            }else
            {
                filename="Chatnow/Media/Chatnow Audio/Sent/"+currentTime.toString()+".mp3";
            }

            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/" +filename));


            while ((length = dis.read(buffer))>0) {

                fos.write(buffer, 0, length);

            }


        } catch (MalformedURLException mue) {
            Log.e("SYNC getUpdate", "malformed url error", mue);
        } catch (IOException ioe) {
            Log.e("SYNC getUpdate", "io error", ioe);
        } catch (SecurityException se) {
            Log.e("SYNC getUpdate", "security error", se);
        }
    }


    private void DownloadImageFile(String downloadUrl, String fileFlag) {

        try {

            URL u = new URL(downloadUrl);

            InputStream is = u.openStream();

            DataInputStream dis = new DataInputStream(is);

            byte[] buffer = new byte[1024];

            int length;

            Date currentTime = Calendar.getInstance().getTime();


            String filename="";

            if (fileFlag.equals("received")){
                filename="Chatnow/Media/Chatnow Images/"+currentTime.toString()+".png";
            }else
            {
                filename="Chatnow/Media/Chatnow Images/Sent/"+currentTime.toString()+".png";
            }

            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/" +filename));


            while ((length = dis.read(buffer))>0) {

                fos.write(buffer, 0, length);

            }

            //Picasso.with(context).load(downloadUrl).into(imageView);


        } catch (MalformedURLException mue) {
            Log.e("SYNC getUpdate", "malformed url error", mue);
        } catch (IOException ioe) {
            Log.e("SYNC getUpdate", "io error", ioe);
        } catch (SecurityException se) {
            Log.e("SYNC getUpdate", "security error", se);
        }
    }


//endregion


}
