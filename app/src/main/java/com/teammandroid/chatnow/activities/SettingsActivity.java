package com.teammandroid.chatnow.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.adapters.SettingsRecyclerAdapter;
import com.teammandroid.chatnow.models.SettingsModel;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView rv_settings;

    SettingsRecyclerAdapter settingsRecyclerAdapter;

    //private ArrayList<NewRequestDetailsModel> mModel;
    private ArrayList<SettingsModel> mModel;

    ImageView iv_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bindView();

        btnListener();

        mModel= GetSettingsList();;

        rv_settings.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv_settings.setLayoutManager(mLayoutManager);
        rv_settings.setItemAnimator(new DefaultItemAnimator());

        rv_settings.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        settingsRecyclerAdapter=new SettingsRecyclerAdapter(this,mModel);

        rv_settings.setAdapter(settingsRecyclerAdapter);

    }

    private void btnListener() {
        iv_back.setOnClickListener(this);
    }

    private void bindView() {
        rv_settings=findViewById(R.id.rv_settings);
        iv_back=findViewById(R.id.viewMenuIconBack);
    }


    private ArrayList<SettingsModel> GetSettingsList() {
        ArrayList<SettingsModel> list=new ArrayList<>();


        list.add(new SettingsModel("Show typing","Allow others to see when I'm typing a message "));
        list.add(new SettingsModel("Hide keyboard on Sent","Hide/Show keyboard after sending a message"));
        list.add(new SettingsModel("Privacy settings",""));
        list.add(new SettingsModel("Show icon in status bar","Show/Hide application status icon in status bar"));
        list.add(new SettingsModel("Suggestions & Feedback",""));
        list.add(new SettingsModel("Help",""));
        list.add(new SettingsModel("Notify me when new message arrive",""));
        list.add(new SettingsModel("Silent period",""));
        list.add(new SettingsModel("Notification Sound","Default(notification_008)"));
        list.add(new SettingsModel("Enable Vibration","Enable/Disable Vibtration"));
        list.add(new SettingsModel("Daily bonus notification","Enable/Disable daily bonus notification"));
        list.add(new SettingsModel("LED Color","Color name"));
        list.add(new SettingsModel("Disable Broadcast","Receive broadcast message"));
        list.add(new SettingsModel("Use points automatically",""));
        list.add(new SettingsModel("Block List","Block List"));
        list.add(new SettingsModel("Unit of length","Kilometers"));
        list.add(new SettingsModel("Clear Chat History","Click to clear chat history"));
        list.add(new SettingsModel("Logout",""));
        list.add(new SettingsModel("About ChatNow..!",""));

        return list;
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
