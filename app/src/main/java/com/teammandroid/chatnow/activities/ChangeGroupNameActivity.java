package com.teammandroid.chatnow.activities;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.utils.Utility;

public class ChangeGroupNameActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_changeGroupName;
    ImageView viewMenuIconBack;

    Button btn_changeGroupNameCancel,btn_changeGroupNameOk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_group_name);

        bindViews();
        btnListeners();

        et_changeGroupName.requestFocus();
    }

    private void bindViews() {
        et_changeGroupName=findViewById(R.id.et_changeGroupName);
        viewMenuIconBack=findViewById(R.id.viewMenuIconBack);

        btn_changeGroupNameCancel=findViewById(R.id.btn_changeGroupNameCancel);
        btn_changeGroupNameOk=findViewById(R.id.btn_changeGroupNameOk);
    }

    private void btnListeners() {
        viewMenuIconBack.setOnClickListener(this);
        btn_changeGroupNameCancel.setOnClickListener(this);
        btn_changeGroupNameOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                break;
            case R.id.viewMenuIconBack:

                onBackPressed();

                break;
            case R.id.btn_changeGroupNameCancel:
                Utility.launchActivity(this,ChangeGroupNameActivity.class,false);

                break;
            case R.id.btn_changeGroupNameOk:
                Utility.launchActivity(this, GroupInfoActivity.class,false);

                break;
        }
    }
}
