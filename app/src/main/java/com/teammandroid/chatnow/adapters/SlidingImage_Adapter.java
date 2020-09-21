package com.teammandroid.chatnow.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcelable;
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

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.teammandroid.chatnow.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class SlidingImage_Adapter extends PagerAdapter implements Runnable {

    private static final String TAG = SlidingImage_Adapter.class.getSimpleName();

    private ArrayList<MediaFile> IMAGES;
    private LayoutInflater inflater;
    private Context context;
    private MediaController ctlr;
    MediaPlayer mediaPlayer = new MediaPlayer();
  //  private ProgressDialog dialog;
    boolean wasPlaying = false;

    int mediatype;


    public SlidingImage_Adapter(Context context, ArrayList<MediaFile> IMAGES, int mediaType) {
        this.context = context;
        this.IMAGES = IMAGES;
        this.mediatype = mediaType;
        inflater = LayoutInflater.from(context);

       /* dialog.setMessage("Loading Media, please wait.");
        dialog.setCanceledOnTouchOutside(false);*/
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.item_preview_list_layout, view, false);
        assert imageLayout != null;

        final ImageView iv_selectedImage = imageLayout.findViewById(R.id.iv_selectedImage);
        final VideoView vv_selectedVideo = imageLayout.findViewById(R.id.vv_selectedVideo);
        final LinearLayout ll_audio = imageLayout.findViewById(R.id.ll_audio);
        final SeekBar seekBar = imageLayout.findViewById(R.id.seekbar);
        final TextView seekBarHint = imageLayout.findViewById(R.id.textView);
        final FloatingActionButton fab = imageLayout.findViewById(R.id.button);

        MediaFile model = IMAGES.get(position);
        //1 img //3 audio //4 video //5 document

        try {
            if (mediatype == 1) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), model.getUri());
                iv_selectedImage.setImageBitmap(bitmap);
                vv_selectedVideo.setVisibility(View.GONE);
                ll_audio.setVisibility(View.GONE);
                iv_selectedImage.setVisibility(View.VISIBLE);
            } else if (model.getMimeType() == ".gif") {
                Glide.with(context).asGif().load(model.getUri()).into(iv_selectedImage);
                vv_selectedVideo.setVisibility(View.GONE);
                ll_audio.setVisibility(View.GONE);
                iv_selectedImage.setVisibility(View.VISIBLE);
            } else if (mediatype == 4) {
                vv_selectedVideo.setVisibility(View.VISIBLE);
                ll_audio.setVisibility(View.GONE);
                iv_selectedImage.setVisibility(View.GONE);

                try {
                    vv_selectedVideo.setVideoPath(model.getPath());
                    MediaController mediaController = new
                            MediaController(context);
                    mediaController.setAnchorView(vv_selectedVideo);
                    vv_selectedVideo.setMediaController(mediaController);
                    vv_selectedVideo.setBackgroundColor(Color.TRANSPARENT);

                    vv_selectedVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            //dialog.dismiss();
                        }
                    });
                    vv_selectedVideo.requestFocus();
                    vv_selectedVideo.start();
                } catch (Exception e) {
                    Log.e(TAG, "bindValues: e " + e.getMessage());
                }

            } else if (mediatype == 3) {
                iv_selectedImage.setVisibility(View.GONE);
                vv_selectedVideo.setVisibility(View.GONE);
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
                            fab.setImageDrawable(ContextCompat.getDrawable(context, android.R.drawable.ic_media_play));
                            seekBar.setProgress(0);
                        }

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.seekTo(seekBar.getProgress());
                        }
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
                context.startActivity(install);

                view.addView(imageLayout, 0);

                return imageLayout;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        view.addView(imageLayout, 0);

        return imageLayout;

    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


    private void clearMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void playSong(MediaFile model, SeekBar seekBar, FloatingActionButton fab) {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                clearMediaPlayer();
                seekBar.setProgress(0);
                wasPlaying = true;
                fab.setImageDrawable(ContextCompat.getDrawable(context, android.R.drawable.ic_media_play));
            }

            if (!wasPlaying) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }

                fab.setImageDrawable(ContextCompat.getDrawable(context, android.R.drawable.ic_media_pause));

                File file = new File(model.getUri().getPath());
                FileInputStream inputStream = new FileInputStream(file);
                mediaPlayer.reset();
                mediaPlayer.setDataSource(inputStream.getFD());
                inputStream.close();
                //  mediaPlayer.setDataSource(filePathString);
                mediaPlayer.prepare();

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                       // dialog.dismiss();
                    }
                });
                mediaPlayer.setVolume(0.5f, 0.5f);
                mediaPlayer.setLooping(false);
                seekBar.setMax(mediaPlayer.getDuration());

                mediaPlayer.start();
                new Thread(this).start();
            }
            wasPlaying = false;
        } catch (Exception e) {
            Log.e(TAG, "playSong: e "+e.getMessage() );
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
                Log.e(TAG, "run: e "+e.getMessage() );
                return;
            } catch (Exception e) {
                Log.e(TAG, "run: e "+e.getMessage() );
                return;
            }
            //seekBar.setProgress(currentPosition);
        }
    }

}


