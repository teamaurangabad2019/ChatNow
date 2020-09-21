package com.teammandroid.chatnow.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.teammandroid.chatnow.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.ponnamkarthik.richlinkpreview.RichLinkView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_nameLeft)
    TextView tv_nameLeft;
    @BindView(R.id.txtQuotedMsgLeft)
    TextView txtQuotedMsgLeft;
    @BindView(R.id.txtQuotedMsgTypeLeft)
    TextView txtQuotedMsgTypeLeft;
    @BindView(R.id.iv_previewLeft)
    ImageView iv_previewLeft;
    @BindView(R.id.rl_reply_layoutLeft)
    RelativeLayout rl_reply_layoutLeft;
    @BindView(R.id.tv_msgLeft)
    TextView tv_msgLeft;
    @BindView(R.id.ll_textLeft)
    LinearLayout ll_textLeft;
    @BindView(R.id.richLinkView_left)
    RichLinkView richLinkView_left;
    @BindView(R.id.ll_linkLeft)
    LinearLayout ll_linkLeft;
    @BindView(R.id.iv_imageLeft)
    ImageView iv_imageLeft;
    @BindView(R.id.pb_imgLeft)
    ProgressBar pb_imgLeft;
    @BindView(R.id.tv_sizeImgLeft)
    TextView tv_sizeImgLeft;
    @BindView(R.id.ll_img_sizeLeft)
    LinearLayout ll_img_sizeLeft;
    @BindView(R.id.rl_imageLeft)
    RelativeLayout rl_imageLeft;
    @BindView(R.id.iv_videoLeft)
    ImageView iv_videoLeft;
    @BindView(R.id.pb_videoLeft)
    ProgressBar pb_videoLeft;
    @BindView(R.id.tv_sizeLeft)
    TextView tv_sizeLeft;
    @BindView(R.id.ll_file_sizeLeft)
    LinearLayout ll_file_sizeLeft;
    @BindView(R.id.ll_videoLeft)
    RelativeLayout ll_videoLeft;
    @BindView(R.id.iv_playLeft)
    ImageView iv_playLeft;
    @BindView(R.id.pb_audioLeft)
    ProgressBar pb_audioLeft;
    @BindView(R.id.iv_pauseLeft)
    ImageView iv_pauseLeft;
    @BindView(R.id.rl_playPauseLeft)
    RelativeLayout rl_playPauseLeft;
    @BindView(R.id.seekbarLeft)
    SeekBar seekbarLeft;
    @BindView(R.id.iv_gifLeft)
    ImageView iv_gifLeft;
    @BindView(R.id.tv_hintLeft)
    TextView tv_hintLeft;
    @BindView(R.id.rl_gifLayoutLeft)
    RelativeLayout rl_gifLayoutLeft;
    @BindView(R.id.ll_audioLeft)
    RelativeLayout ll_audioLeft;
    @BindView(R.id.iv_iconLeft)
    ImageView iv_iconLeft;
    @BindView(R.id.tv_tv_docNameLeft)
    TextView tv_tv_docNameLeft;
    @BindView(R.id.iv_sizeDocLeft)
    ImageView iv_sizeDocLeft;
    @BindView(R.id.pb_docLeft)
    ProgressBar pb_docLeft;
    @BindView(R.id.tv_docExtentionLeft)
    TextView tv_docExtentionLeft;
    @BindView(R.id.rl_pdfLeft)
    RelativeLayout rl_pdfLeft;
    @BindView(R.id.tv_contactMsgLeft)
    TextView tv_contactMsgLeft;
    @BindView(R.id.tv_viewContactLeft)
    TextView tv_viewContactLeft;
    @BindView(R.id.ll_contactLeft)
    LinearLayout ll_contactLeft;
    @BindView(R.id.tv_timeLeft)
    TextView tv_timeLeft;
    @BindView(R.id.ll_left)
    LinearLayout ll_left;
    @BindView(R.id.tv_nameRight)
    TextView tv_nameRight;
    @BindView(R.id.txtQuotedMsgRight)
    TextView txtQuotedMsgRight;
    @BindView(R.id.txtQuotedMsgTypeRight)
    TextView txtQuotedMsgTypeRight;
    @BindView(R.id.iv_previewRight)
    ImageView iv_previewRight;
    @BindView(R.id.rl_reply_layoutRight)
    RelativeLayout rl_reply_layoutRight;
    @BindView(R.id.tv_msgRight)
    TextView tv_msgRight;
    @BindView(R.id.ll_msgRight)
    LinearLayout ll_msgRight;
    @BindView(R.id.richLinkView_right)
    RichLinkView richLinkView_right;
    @BindView(R.id.ll_linkRight)
    LinearLayout ll_linkRight;
    @BindView(R.id.iv_imageRight)
    ImageView iv_imageRight;
    @BindView(R.id.pb_imgRight)
    ProgressBar pb_imgRight;
    @BindView(R.id.tv_sizeImgRight)
    TextView tv_sizeImgRight;
    @BindView(R.id.ll_img_sizeRight)
    LinearLayout ll_img_sizeRight;
    @BindView(R.id.rl_imageRight)
    RelativeLayout rl_imageRight;
    @BindView(R.id.iv_videoRight)
    ImageView iv_videoRight;
    @BindView(R.id.pb_videoRight)
    ProgressBar pb_videoRight;
    @BindView(R.id.tv_sizeRight)
    TextView tv_sizeRight;
    @BindView(R.id.ll_file_sizeRight)
    LinearLayout ll_file_sizeRight;
    @BindView(R.id.ll_videoRight)
    RelativeLayout ll_videoRight;
    @BindView(R.id.iv_playRight)
    ImageView iv_playRight;
    @BindView(R.id.pb_audioRight)
    ProgressBar pb_audioRight;
    @BindView(R.id.iv_pauseRight)
    ImageView iv_pauseRight;
    @BindView(R.id.rl_playPauseRight)
    RelativeLayout rl_playPauseRight;
    @BindView(R.id.seekbarRight)
    SeekBar seekbarRight;
    @BindView(R.id.iv_gifRight)
    ImageView iv_gifRight;
    @BindView(R.id.tv_hintRight)
    TextView tv_hintRight;
    @BindView(R.id.rl_gifLayoutRight)
    RelativeLayout rl_gifLayoutRight;
    @BindView(R.id.ll_audioRight)
    RelativeLayout ll_audioRight;
    @BindView(R.id.iv_iconRight)
    ImageView iv_iconRight;
    @BindView(R.id.tv_tv_docNameRight)
    TextView tv_tv_docNameRight;
    @BindView(R.id.iv_sizeDocRight)
    ImageView iv_sizeDocRight;
    @BindView(R.id.pb_docRight)
    ProgressBar pb_docRight;
    @BindView(R.id.tv_docExtentionRight)
    TextView tv_docExtentionRight;
    @BindView(R.id.rl_pdfRight)
    RelativeLayout rl_pdfRight;
    @BindView(R.id.tv_contactMsgRight)
    TextView tv_contactMsgRight;
    @BindView(R.id.tv_viewContactRight)
    TextView tv_viewContactRight;
    @BindView(R.id.ll_contactRight)
    LinearLayout ll_contactRight;
    @BindView(R.id.rl_msgRight)
    RelativeLayout rl_msgRight;
    @BindView(R.id.tv_timeRight)
    TextView tv_timeRight;
    @BindView(R.id.ll_right)
    LinearLayout ll_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_chat_time_line);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_nameLeft, R.id.txtQuotedMsgLeft, R.id.txtQuotedMsgTypeLeft, R.id.iv_previewLeft, R.id.rl_reply_layoutLeft, R.id.tv_msgLeft, R.id.ll_textLeft, R.id.richLinkView_left, R.id.ll_linkLeft, R.id.iv_imageLeft, R.id.pb_imgLeft, R.id.tv_sizeImgLeft, R.id.ll_img_sizeLeft, R.id.rl_imageLeft, R.id.iv_videoLeft, R.id.pb_videoLeft, R.id.tv_sizeLeft, R.id.ll_file_sizeLeft, R.id.ll_videoLeft, R.id.iv_playLeft, R.id.pb_audioLeft, R.id.iv_pauseLeft, R.id.rl_playPauseLeft, R.id.seekbarLeft, R.id.iv_gifLeft, R.id.tv_hintLeft, R.id.rl_gifLayoutLeft, R.id.ll_audioLeft, R.id.iv_iconLeft, R.id.tv_tv_docNameLeft, R.id.iv_sizeDocLeft, R.id.pb_docLeft, R.id.tv_docExtentionLeft, R.id.rl_pdfLeft, R.id.tv_contactMsgLeft, R.id.tv_viewContactLeft, R.id.ll_contactLeft, R.id.tv_timeLeft, R.id.ll_left, R.id.tv_nameRight, R.id.txtQuotedMsgRight, R.id.txtQuotedMsgTypeRight, R.id.iv_previewRight, R.id.rl_reply_layoutRight, R.id.tv_msgRight, R.id.ll_msgRight, R.id.richLinkView_right, R.id.ll_linkRight, R.id.iv_imageRight, R.id.pb_imgRight, R.id.tv_sizeImgRight, R.id.ll_img_sizeRight, R.id.rl_imageRight, R.id.iv_videoRight, R.id.pb_videoRight, R.id.tv_sizeRight, R.id.ll_file_sizeRight, R.id.ll_videoRight, R.id.iv_playRight, R.id.pb_audioRight, R.id.iv_pauseRight, R.id.rl_playPauseRight, R.id.seekbarRight, R.id.iv_gifRight, R.id.tv_hintRight, R.id.rl_gifLayoutRight, R.id.ll_audioRight, R.id.iv_iconRight, R.id.tv_tv_docNameRight, R.id.iv_sizeDocRight, R.id.pb_docRight, R.id.tv_docExtentionRight, R.id.rl_pdfRight, R.id.tv_contactMsgRight, R.id.tv_viewContactRight, R.id.ll_contactRight, R.id.rl_msgRight, R.id.tv_timeRight, R.id.ll_right})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_nameLeft:
                break;
            case R.id.txtQuotedMsgLeft:
                break;
            case R.id.txtQuotedMsgTypeLeft:
                break;
            case R.id.iv_previewLeft:
                break;
            case R.id.rl_reply_layoutLeft:
                break;
            case R.id.tv_msgLeft:
                break;
            case R.id.ll_textLeft:
                break;
            case R.id.richLinkView_left:
                break;
            case R.id.ll_linkLeft:
                break;
            case R.id.iv_imageLeft:
                break;
            case R.id.pb_imgLeft:
                break;
            case R.id.tv_sizeImgLeft:
                break;
            case R.id.ll_img_sizeLeft:
                break;
            case R.id.rl_imageLeft:
                break;
            case R.id.iv_videoLeft:
                break;
            case R.id.pb_videoLeft:
                break;
            case R.id.tv_sizeLeft:
                break;
            case R.id.ll_file_sizeLeft:
                break;
            case R.id.ll_videoLeft:
                break;
            case R.id.iv_playLeft:
                break;
            case R.id.pb_audioLeft:
                break;
            case R.id.iv_pauseLeft:
                break;
            case R.id.rl_playPauseLeft:
                break;
            case R.id.seekbarLeft:
                break;
            case R.id.iv_gifLeft:
                break;
            case R.id.tv_hintLeft:
                break;
            case R.id.rl_gifLayoutLeft:
                break;
            case R.id.ll_audioLeft:
                break;
            case R.id.iv_iconLeft:
                break;
            case R.id.tv_tv_docNameLeft:
                break;
            case R.id.iv_sizeDocLeft:
                break;
            case R.id.pb_docLeft:
                break;
            case R.id.tv_docExtentionLeft:
                break;
            case R.id.rl_pdfLeft:
                break;
            case R.id.tv_contactMsgLeft:
                break;
            case R.id.tv_viewContactLeft:
                break;
            case R.id.ll_contactLeft:
                break;
            case R.id.tv_timeLeft:
                break;
            case R.id.ll_left:
                break;
            case R.id.tv_nameRight:
                break;
            case R.id.txtQuotedMsgRight:
                break;
            case R.id.txtQuotedMsgTypeRight:
                break;
            case R.id.iv_previewRight:
                break;
            case R.id.rl_reply_layoutRight:
                break;
            case R.id.tv_msgRight:
                break;
            case R.id.ll_msgRight:
                break;
            case R.id.richLinkView_right:
                break;
            case R.id.ll_linkRight:
                break;
            case R.id.iv_imageRight:
                break;
            case R.id.pb_imgRight:
                break;
            case R.id.tv_sizeImgRight:
                break;
            case R.id.ll_img_sizeRight:
                break;
            case R.id.rl_imageRight:
                break;
            case R.id.iv_videoRight:
                break;
            case R.id.pb_videoRight:
                break;
            case R.id.tv_sizeRight:
                break;
            case R.id.ll_file_sizeRight:
                break;
            case R.id.ll_videoRight:
                break;
            case R.id.iv_playRight:
                break;
            case R.id.pb_audioRight:
                break;
            case R.id.iv_pauseRight:
                break;
            case R.id.rl_playPauseRight:
                break;
            case R.id.seekbarRight:
                break;
            case R.id.iv_gifRight:
                break;
            case R.id.tv_hintRight:
                break;
            case R.id.rl_gifLayoutRight:
                break;
            case R.id.ll_audioRight:
                break;
            case R.id.iv_iconRight:
                break;
            case R.id.tv_tv_docNameRight:
                break;
            case R.id.iv_sizeDocRight:
                break;
            case R.id.pb_docRight:
                break;
            case R.id.tv_docExtentionRight:
                break;
            case R.id.rl_pdfRight:
                break;
            case R.id.tv_contactMsgRight:
                break;
            case R.id.tv_viewContactRight:
                break;
            case R.id.ll_contactRight:
                break;
            case R.id.rl_msgRight:
                break;
            case R.id.tv_timeRight:
                break;
            case R.id.ll_right:
                break;
        }
    }
}
