package com.teammandroid.chatnow.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.teammandroid.chatnow.R;

public class PrivacySettingsActivity extends AppCompatActivity implements View.OnClickListener {


    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_settings);

        bindView();
        btnListener();
    }

    private void btnListener() {
        iv_back.setOnClickListener(this);
    }

    private void bindView() {
        iv_back=findViewById(R.id.viewMenuIconBack);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.viewMenuIconBack:
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
