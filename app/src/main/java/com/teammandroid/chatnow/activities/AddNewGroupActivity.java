package com.teammandroid.chatnow.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.teammandroid.chatnow.R;

import java.io.File;
import java.io.IOException;

public class AddNewGroupActivity extends AppCompatActivity implements View.OnClickListener{

    RelativeLayout layoutGroupName;
    RelativeLayout layoutGroupDescription;
    RelativeLayout layoutGroupIcon;

    private Uri filePath;

    File fileToUpdate = null;
    private Bitmap bitmap;

    private int PICK_IMAGE_REQUEST = 1;

    EditText et_groupName,et_groupDescription;
    ImageButton imgBtn_GroupBack,imgBtn_GroupNext,imgBtn_groupDescBack,imgBtn_groupDescNext,imgBtn_groupIconBack;

    ImageButton imgb_backGroupName,imgb_backGroupDesc,imgb_backGroupIcon;

    ImageView img_groupIcon;
    Button btn_chooseGroupIcon,btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_group);

        bindViews();
        listeners();

        layoutGroupName.setVisibility(View.VISIBLE);
        layoutGroupDescription.setVisibility(View.GONE);
        layoutGroupIcon.setVisibility(View.GONE);

    }


    private void bindViews() {
        layoutGroupName = findViewById(R.id.layoutGroupName);
        layoutGroupDescription = findViewById(R.id.layoutGroupDescription);
        layoutGroupIcon = findViewById(R.id.layoutGroupIcon);

        et_groupName=findViewById(R.id.et_groupName);
        et_groupDescription=findViewById(R.id.et_groupDescription);

        btn_submit=findViewById(R.id.btn_submit);
        btn_chooseGroupIcon=findViewById(R.id.btn_chooseGroupIcon);

        imgBtn_GroupBack=findViewById(R.id.imgBtn_GroupBack);
        imgBtn_GroupNext=findViewById(R.id.imgBtn_GroupNext);

        imgBtn_groupDescBack=findViewById(R.id.imgBtn_groupDescBack);
        imgBtn_groupDescNext=findViewById(R.id.imgBtn_groupDescNext);

        imgBtn_groupIconBack=findViewById(R.id.imgBtn_groupIconBack);

        img_groupIcon=findViewById(R.id.img_groupIcon);

        imgb_backGroupName=findViewById(R.id.imgb_backGroupName);
        imgb_backGroupDesc=findViewById(R.id.imgb_backGroupDesc);
        imgb_backGroupIcon=findViewById(R.id.imgb_backGroupIcon);

    }


    private void listeners() {

        imgBtn_groupIconBack.setOnClickListener(this);

        imgBtn_GroupBack.setOnClickListener(this);
        imgBtn_GroupNext.setOnClickListener(this);

        imgBtn_groupDescBack.setOnClickListener(this);
        imgBtn_groupDescNext.setOnClickListener(this);

        btn_chooseGroupIcon.setOnClickListener(this);

        btn_submit.setOnClickListener(this);

        imgb_backGroupName.setOnClickListener(this);
        imgb_backGroupDesc.setOnClickListener(this);
        imgb_backGroupIcon.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            default:
                break;

            case R.id.imgBtn_GroupNext:

                layoutGroupName.setVisibility(View.GONE);
                layoutGroupIcon.setVisibility(View.GONE);
                layoutGroupDescription.setVisibility(View.VISIBLE);
                this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


                break;

            case R.id.imgBtn_groupDescNext:

                layoutGroupName.setVisibility(View.GONE);
                layoutGroupIcon.setVisibility(View.VISIBLE);
                layoutGroupDescription.setVisibility(View.GONE);

                this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                break;
            case R.id.imgBtn_groupDescBack:

                layoutGroupName.setVisibility(View.VISIBLE);
                layoutGroupIcon.setVisibility(View.GONE);
                layoutGroupDescription.setVisibility(View.GONE);

                this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                break;

            case R.id.imgBtn_groupIconBack:

                layoutGroupName.setVisibility(View.GONE);
                layoutGroupIcon.setVisibility(View.GONE);
                layoutGroupDescription.setVisibility(View.VISIBLE);

                this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                break;

            case R.id.btn_submit:

                /*layoutGroupName.setVisibility(View.VISIBLE);
                layoutGroupIcon.setVisibility(View.GONE);
                layoutGroupDescription.setVisibility(View.GONE);

                this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                */

                startActivity(new Intent(this,HomeActivity.class));

                //startActivity(new Intent(this,HomeActivity.class));
                break;

            case R.id.imgb_backGroupDesc:

                layoutGroupName.setVisibility(View.VISIBLE);
                layoutGroupIcon.setVisibility(View.GONE);
                layoutGroupDescription.setVisibility(View.GONE);

                this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                break;
            case R.id.imgb_backGroupIcon:

                layoutGroupName.setVisibility(View.GONE);
                layoutGroupIcon.setVisibility(View.GONE);
                layoutGroupDescription.setVisibility(View.VISIBLE);

                this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                break;

            case R.id.btn_chooseGroupIcon:
                showFileChooser();
                break;


        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                fileToUpdate = new File(filePath.getPath());
                String filepath = fileToUpdate.getAbsolutePath();
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                img_groupIcon.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.e("path", "" + filePath);
    }
}
