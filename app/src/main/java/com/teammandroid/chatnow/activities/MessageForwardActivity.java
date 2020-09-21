package com.teammandroid.chatnow.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.adapters.MessageForwardRecyclerAdapter;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;
import com.teammandroid.chatnow.models.NotificationModel;
import com.teammandroid.chatnow.models.TrainerModel;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseMsgModel;
import com.teammandroid.chatnow.network.UserServices;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import java.util.ArrayList;

public class MessageForwardActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = MessageForwardActivity.class.getSimpleName();

    RecyclerView rv_forwardMsg;
    MessageForwardRecyclerAdapter messageForwardRecyclerAdapter;
    private Activity activity;

    ImageView iv_back;
    private ProgressDialog dialog;
    private UserModel currentUser;

    String receivedMsg="";

    int senderId=0;
    int receiverId=0;
    Bundle bundle;

    ArrayList<FirebaseMsgModel> multiSelectedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_forward);

        activity = MessageForwardActivity.this;
        bindView();

        btnListener();

        dialog = new ProgressDialog(activity);
        dialog.setMessage("Loading page, please wait.");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        bundle=getIntent().getExtras();
        if (bundle != null) {
            multiSelectedList = bundle.getParcelableArrayList("multiSelectedList");
            Log.e(TAG, "multiSelectedList: "+multiSelectedList.toString() );
        }

        SessionManager sessionManager = new SessionManager(activity);
        currentUser = sessionManager.getUserDetails();

        GetUserNearMe();
    }

    private void bindView() {
        rv_forwardMsg=findViewById(R.id.rv_forwardMsg);
        iv_back=findViewById(R.id.viewMenuIconBack);
    }

    private void btnListener() {
        iv_back.setOnClickListener(this);
    }

    private void GetUserNearMe() {
        Log.e(TAG, "GetUserNearMe: called" );
        try {
            if (Utility.isNetworkAvailable(activity)) {
                dialog.show();
                //  Log.e(TAG, "GetUser: " + UserId);
                // UserServices.getInstance(getApplicationContext()).GetUserNearMe(user.getUserid(),latitude,longitude,distance_range, user.getRoleid(),new ApiStatusCallBack<ArrayList<UserModel>>() {
                UserServices.getInstance(activity).GetAllUser(new ApiStatusCallBack<ArrayList<UserModel>>() {
                    @Override
                    public void onSuccess(ArrayList<UserModel> response) {

                        dialog.dismiss();
                        ArrayList<UserModel> userNearMeList = response;
                        Log.e(TAG, "onSuccess: list "+userNearMeList.toString() );
                        bindList(response);
                        Toast.makeText(activity,userNearMeList.size()+" User Found",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        Utility.showErrorMessage(activity, "Network "+anError.getMessage());
                    }

                    @Override
                    public void onUnknownError(Exception e) {
                        dialog.dismiss();
                        Utility.showErrorMessage(activity, e.getMessage());
                    }
                });
            } else {
                Utility.showErrorMessage(activity, "Could not connect to the internet");
            }
        } catch (Exception ex) {
            dialog.dismiss();
            Utility.showErrorMessage(activity, ex.getMessage());
        }
    }

    private void bindList(final ArrayList<UserModel> response) {
        rv_forwardMsg.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        rv_forwardMsg.setLayoutManager(mLayoutManager);
        rv_forwardMsg.setItemAnimator(new DefaultItemAnimator());
        rv_forwardMsg.addItemDecoration(new DividerItemDecoration(activity,
                DividerItemDecoration.VERTICAL));


        messageForwardRecyclerAdapter =new MessageForwardRecyclerAdapter(activity, response, currentUser, new MessageForwardRecyclerAdapter.ItemClickListener() {
            @Override
            public void onTrainerItemClick(View view, int position) {
                UserModel selectedUser = response.get(position);
                Toast.makeText(activity, ""+selectedUser.getFullname(), Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("multiSelectedList",multiSelectedList);
                bundle.putInt("partnerId",selectedUser.getUserid());
                Utility.launchActivity(activity,ChattingActivity.class,true,bundle);

            }
        });

        rv_forwardMsg.setAdapter(messageForwardRecyclerAdapter);

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
