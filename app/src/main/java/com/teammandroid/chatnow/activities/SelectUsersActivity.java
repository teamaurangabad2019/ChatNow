package com.teammandroid.chatnow.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.activities.firebase.CreateNewGroupActivity;
import com.teammandroid.chatnow.adapters.firebase.SelectUsersAdapter;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.network.UserServices;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import java.util.ArrayList;


public class SelectUsersActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = SelectUsersActivity.class.getSimpleName();

    RecyclerView rv_selectUsers;

    SelectUsersAdapter selectUsersAdapter;

    private Activity activity;

    ImageView iv_back;
    private ProgressDialog dialog;
    private UserModel currentUser;

    String receivedMsg="";

    int senderId=0;
    int receiverId=0;
    Bundle bundle;

    FloatingActionButton fab_select_users;

    private ArrayList<UserModel> selectedUsersList = new ArrayList<>();

    FirebaseUser firebaseUser;

    Toolbar toolbar,toolbar_search;
    EditText et_search_view;
    ImageView search_clear, viewMenuIconBack2,img_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_users);


        activity = SelectUsersActivity.this;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        bindView();

        btnListener();

        dialog = new ProgressDialog(activity);
        dialog.setMessage("Loading page, please wait.");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        SessionManager sessionManager = new SessionManager(activity);
        currentUser = sessionManager.getUserDetails();

        GetUserNearMe();

        et_search_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //searchUser(s.toString().toLowerCase());

            }

            @Override
            public void afterTextChanged(Editable s) {
                //filter(s.toString());
                //filter(s.toString());


                /*
                InputMethodManager imm = (InputMethodManager) SelectUsersActivity.this.getSystemService(Service.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_search_view, 0);
                //imm.showSoftInputFromWindow(mainActivity.toolbarEditText.getWindowToken(), 0);
                filter(s.toString());*/

            }
        });

    }

    private void filter(String text) {

        ArrayList<UserModel> filteredNames=new ArrayList<>();

        for (UserModel model : selectedUsersList){
            String name=model.getFullname();

            if (name.contains(text.toLowerCase())){
                filteredNames.add(model);
            }
            selectUsersAdapter.setFilter(filteredNames);
        }


        /*
        try {
            //new array list that will hold the filtered data
            ArrayList<UserModel> filterdNames = new ArrayList<>();
            //looping through existing elements
            for (UserModel s : selectedUsersList) {
                //if the existing elements contains the search input
                if (s.getFullname() != "") {
                    if (s.getFullname().toLowerCase().contains(text.toLowerCase())) {
                        //adding the element to filtered list
                        filterdNames.add(s);
                    }
                }
            }
            //calling a method of the adapter class and passing the filtered list
            selectUsersAdapter.filterList(filterdNames);
        } catch (Exception e) {
            Log.e(TAG, "filter: e " + e.getMessage());
        }
        */

    }

    private void bindView() {
        rv_selectUsers=findViewById(R.id.rv_selectUsers);
        iv_back=findViewById(R.id.viewMenuIconBack);
        fab_select_users=findViewById(R.id.fab_select_users);

        img_search=findViewById(R.id.img_search);

        img_search.setVisibility(View.GONE);

        viewMenuIconBack2 = findViewById(R.id.viewMenuIconBack2);

        et_search_view = findViewById(R.id.et_search_view);
        search_clear = findViewById(R.id.search_clear);


        toolbar_search = findViewById(R.id.toolbar_search);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    private void btnListener() {
        iv_back.setOnClickListener(this);
        fab_select_users.setOnClickListener(this);

        img_search.setOnClickListener(this);
        search_clear.setOnClickListener(this);
        viewMenuIconBack2.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                break;

            case R.id.viewMenuIconBack2:
                toolbar_search.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                setSupportActionBar(toolbar);
                break;

            case R.id.search_clear:
                et_search_view.setText("");
                et_search_view.requestFocus();
                break;

            case R.id.img_search:

                toolbar_search.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.GONE);
                setSupportActionBar(toolbar_search);

                //selectUsersAdapter.filterList(mModel);

                break;

            case R.id.fab_select_users:

                //Utility.launchActivity(SelectUsersActivity.this, CreateNewGroupActivity.class,false);

                String data = "";
                ArrayList<UserModel> stList = ((SelectUsersAdapter) selectUsersAdapter)
                        .getStudentist();

                for (UserModel model : stList){
                    if (model.isSelected() == true){

                        data = data + "\n" + model.getFullname().toString();

                        selectedUsersList.add(model);
                    }
                }
                /*
                Toast.makeText(SelectUsersActivity.this,
                        "Selected Students: \n" + data, Toast.LENGTH_LONG)
                        .show();*/


                Bundle bundle1 = new Bundle();
                if (selectedUsersList.size() > 0) {
                    bundle1.putParcelableArrayList("SelectedUsersList", selectedUsersList);
                    Utility.launchActivity(activity, CreateNewGroupActivity.class, true, bundle1);
                } else {
                    //Toast.makeText(activity, "No Message Selected", Toast.LENGTH_SHORT).show();
                    Utility.launchActivity(activity, CreateNewGroupActivity.class, true);

                }

                break;

        }

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

        rv_selectUsers.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        rv_selectUsers.setLayoutManager(mLayoutManager);
        rv_selectUsers.setItemAnimator(new DefaultItemAnimator());
        rv_selectUsers.addItemDecoration(new DividerItemDecoration(activity,
                DividerItemDecoration.VERTICAL));

        selectUsersAdapter =new SelectUsersAdapter(activity, response, currentUser, new SelectUsersAdapter.ItemClickListener() {
            @Override
            public void onTrainerItemClick(View view, int position) {

                /*
                UserModel selectedUser = response.get(position);

                Toast.makeText(activity, ""+selectedUser.getFullname(), Toast.LENGTH_SHORT).show();*/

                /*UserModel selectedUser = response.get(position);
                Toast.makeText(activity, ""+selectedUser.getFullname(), Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("multiSelectedList",multiSelectedList);
                bundle.putInt("partnerId",selectedUser.getUserid());
                Utility.launchActivity(activity, ChattingActivity.class,true,bundle);*/

            }
        });


        /*messageForwardRecyclerAdapter =new MessageForwardRecyclerAdapter(activity, response, currentUser, new MessageForwardRecyclerAdapter.ItemClickListener() {
            @Override
            public void onTrainerItemClick(View view, int position) {
                UserModel selectedUser = response.get(position);
                Toast.makeText(activity, ""+selectedUser.getFullname(), Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("multiSelectedList",multiSelectedList);
                bundle.putInt("partnerId",selectedUser.getUserid());
                Utility.launchActivity(activity, ChattingActivity.class,true,bundle);

            }
        });
        */

        rv_selectUsers.setAdapter(selectUsersAdapter);

    }
}
