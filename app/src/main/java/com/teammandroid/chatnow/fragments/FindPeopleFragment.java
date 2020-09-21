
package com.teammandroid.chatnow.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teammandroid.chatnow.activities.ChattingActivity;
import com.teammandroid.chatnow.activities.PeopleProfileActivity;
import com.teammandroid.chatnow.activities.SearchNearMeActivity;
import com.teammandroid.chatnow.adapters.PeopleRecyclerAdapter;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;
import com.teammandroid.chatnow.models.PageViewModel;
import com.teammandroid.chatnow.models.TrainerModel;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.network.UserServices;
import com.teammandroid.chatnow.utils.MyDividerItemDecoration;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import java.util.ArrayList;

public class FindPeopleFragment extends Fragment {

    private static final String TAG = FindPeopleFragment.class.getSimpleName();
    RecyclerView rv_peoples;

   // Context mContext;

    PeopleRecyclerAdapter peopleRecyclerAdapter;

    //private ArrayList<NewRequestDetailsModel> mModel;
    private ArrayList<TrainerModel> mModel;
    private Activity activity;

    private ProgressDialog dialog;
    private UserModel currentUser;
    private PageViewModel pageViewModel;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    public FindPeopleFragment() {
        // Required empty public constructor
    }

    @Override public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        // init ViewModel
        pageViewModel = ViewModelProviders.of(requireActivity()).get(PageViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_find_people, container, false);
        activity = getActivity();

        dialog = new ProgressDialog(activity);

        dialog.setMessage("Loading page, please wait.");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        SessionManager sessionManager = new SessionManager(getContext());
        currentUser = sessionManager.getUserDetails();

        bindView(view);
        GetUserNearMe();
        return view;
    }

    private void bindView(View view) {
        rv_peoples=view.findViewById(R.id.recyclerView);
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

        rv_peoples.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        rv_peoples.setLayoutManager(mLayoutManager);
        rv_peoples.setItemAnimator(new DefaultItemAnimator());

        /*rv_peoples.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));*/

        rv_peoples.addItemDecoration(new MyDividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL, 80));


        peopleRecyclerAdapter=new PeopleRecyclerAdapter(activity, response,currentUser,firebaseUser, reference, new PeopleRecyclerAdapter.ItemClickListener() {
            @Override
            public void onTrainerItemClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("chattingPartner", response.get(position));
                Utility.launchActivity(getActivity(), PeopleProfileActivity.class, false, bundle);
                pageViewModel.setIsUpdate("1");
            }
        });
        rv_peoples.setAdapter(peopleRecyclerAdapter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        reference = null;
        firebaseUser = null;
    }
}






