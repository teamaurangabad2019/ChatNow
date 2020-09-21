package com.teammandroid.chatnow.pagination;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.teammandroid.chatnow.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.ponnamkarthik.richlinkpreview.RichLinkView;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    RelativeLayout rl_imageRight, rl_imageLeft;
    LinearLayout ll_img_sizeRight, ll_img_sizeLeft;
    ProgressBar pb_imgRight, pb_imgLeft;
    ProgressBar pb_audioRight, pb_audioLeft;
    TextView tv_sizeImgRight, tv_sizeImgLeft;
    LinearLayout ll_left, ll_right;
    TextView tv_msgRight, tv_msgLeft;
    TextView tv_timeRight, tv_timeLeft;
    TextView tv_nameLeft, tv_nameRight;
    LinearLayout ll_textLeft, ll_msgRight;
    RelativeLayout ll_videoLeft, ll_videoRight;
    ImageView iv_imageRight, iv_imageLeft;
    ImageView iv_videoRight, iv_videoLeft;
    RelativeLayout ll_audioLeft, ll_audioRight;
    ImageView iv_playLeft, iv_playRight;
    ImageView iv_pauseLeft, iv_pauseRight;
    SeekBar seekbarLeft, seekbarRight;
    ImageView iv_gifLeft, iv_gifRight;
    TextView tv_hintRight, tv_hintLeft;
    LinearLayout ll_linkLeft, ll_linkRight;
    LinearLayout ll_file_sizeLeft, ll_file_sizeRight;
    TextView tv_sizeLeft, tv_sizeRight;
    RichLinkView richLinkView_left, richLinkView_right;
    ProgressBar pb_videoRight, pb_videoLeft;
    TextView tv_tv_docNameRight, tv_tv_docNameLeft;
    ImageView iv_sizeDocRight, iv_sizeDocLeft;
    ProgressBar pb_docRight, pb_docLeft;
    TextView tv_docExtentionRight, tv_docExtentionLeft;
    RelativeLayout rl_pdfRight, rl_pdfLeft;

    RelativeLayout rl_reply_layoutRight, rl_reply_layoutLeft;
    TextView txtQuotedMsgRight, txtQuotedMsgLeft;
    TextView txtQuotedMsgTypeRight, txtQuotedMsgTypeLeft;
    ImageView iv_previewRight, iv_previewLeft;

    LinearLayout ll_contactLeft, ll_contactRight;
    TextView tv_contactMsgLeft, tv_contactMsgRight;
    TextView tv_viewContactLeft, tv_viewContactRight;

    private int mCurrentPosition;

    public BaseViewHolder(View itemView) {
        super(itemView);
        //ButterKnife.bind(this, itemView);
        tv_viewContactRight = itemView.findViewById(R.id.tv_viewContactRight);
        tv_viewContactLeft = itemView.findViewById(R.id.tv_viewContactLeft);
        tv_contactMsgRight = itemView.findViewById(R.id.tv_contactMsgRight);
        tv_contactMsgLeft = itemView.findViewById(R.id.tv_contactMsgLeft);
        ll_contactRight = itemView.findViewById(R.id.ll_contactRight);
        ll_contactLeft = itemView.findViewById(R.id.ll_contactLeft);
        iv_previewLeft = itemView.findViewById(R.id.iv_previewLeft);
        iv_previewRight = itemView.findViewById(R.id.iv_previewRight);
        txtQuotedMsgTypeLeft = itemView.findViewById(R.id.txtQuotedMsgTypeLeft);
        txtQuotedMsgTypeRight = itemView.findViewById(R.id.txtQuotedMsgTypeRight);
        txtQuotedMsgLeft = itemView.findViewById(R.id.txtQuotedMsgLeft);
        txtQuotedMsgRight = itemView.findViewById(R.id.txtQuotedMsgRight);
        rl_reply_layoutLeft = itemView.findViewById(R.id.rl_reply_layoutLeft);
        rl_reply_layoutRight = itemView.findViewById(R.id.rl_reply_layoutRight);
        rl_pdfLeft = itemView.findViewById(R.id.rl_pdfLeft);
        rl_pdfRight = itemView.findViewById(R.id.rl_pdfRight);
        tv_docExtentionLeft = itemView.findViewById(R.id.tv_docExtentionLeft);
        tv_docExtentionRight = itemView.findViewById(R.id.tv_docExtentionRight);
        pb_docLeft = itemView.findViewById(R.id.pb_docLeft);
        pb_docRight = itemView.findViewById(R.id.pb_docRight);
        iv_sizeDocLeft = itemView.findViewById(R.id.iv_sizeDocLeft);
        iv_sizeDocRight = itemView.findViewById(R.id.iv_sizeDocRight);
        tv_tv_docNameLeft = itemView.findViewById(R.id.tv_tv_docNameLeft);
        tv_tv_docNameRight = itemView.findViewById(R.id.tv_tv_docNameRight);
        pb_audioRight = itemView.findViewById(R.id.pb_audioRight);
        pb_audioLeft = itemView.findViewById(R.id.pb_audioLeft);
        tv_sizeImgLeft = itemView.findViewById(R.id.tv_sizeImgLeft);
        tv_sizeImgRight = itemView.findViewById(R.id.tv_sizeImgRight);
        pb_imgRight = itemView.findViewById(R.id.pb_imgRight);
        pb_imgLeft = itemView.findViewById(R.id.pb_imgLeft);
        ll_img_sizeRight = itemView.findViewById(R.id.ll_img_sizeRight);
        ll_img_sizeLeft = itemView.findViewById(R.id.ll_img_sizeLeft);
        rl_imageRight = itemView.findViewById(R.id.rl_imageRight);
        rl_imageLeft = itemView.findViewById(R.id.rl_imageLeft);
        pb_videoRight = itemView.findViewById(R.id.pb_videoRight);
        pb_videoLeft = itemView.findViewById(R.id.pb_videoLeft);
        ll_file_sizeLeft = itemView.findViewById(R.id.ll_file_sizeLeft);
        ll_file_sizeRight = itemView.findViewById(R.id.ll_file_sizeRight);
        tv_sizeLeft = itemView.findViewById(R.id.tv_sizeLeft);
        tv_sizeRight = itemView.findViewById(R.id.tv_sizeRight);
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
        ll_linkLeft = itemView.findViewById(R.id.ll_linkLeft);
        ll_linkRight = itemView.findViewById(R.id.ll_linkRight);
        richLinkView_left = itemView.findViewById(R.id.richLinkView_left);
        richLinkView_right = itemView.findViewById(R.id.richLinkView_right);


    }

    protected abstract void clear();

    public void onBind(int position) {
        mCurrentPosition = position;
        clear();
    }
    public int getCurrentPosition() {
        return mCurrentPosition;
    }
}
