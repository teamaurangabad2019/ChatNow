package com.teammandroid.chatnow.fragments;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.models.TrainerModel;
import com.teammandroid.chatnow.adapters.GroupRecyclerAdapter;
import com.teammandroid.chatnow.utils.MyDividerItemDecoration;

import java.util.ArrayList;

public class GroupsFragment extends Fragment {

    RecyclerView rv_groups;

    //Context mContext;

    Activity activity;

    //PeopleRecyclerAdapter peopleRecyclerAdapter;
    GroupRecyclerAdapter groupRecyclerAdapter;


    //private ArrayList<NewRequestDetailsModel> mModel;
    private ArrayList<TrainerModel> mModel;


    public GroupsFragment() {
        // Required empty public constructor
    }


/*
    public FindPeopleFragment(ArrayList<TrainerModel> mModel) {
        this.mModel=mModel;
    }
    */




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_groups, container, false);


        bindView(view);


        mModel= GetTrainerList();;

        rv_groups.setHasFixedSize(true);

        /*RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        rv_groups.setLayoutManager(mLayoutManager);
        rv_groups.setItemAnimator(new DefaultItemAnimator());

        *//*rv_groups.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));*//*

        rv_groups.addItemDecoration(new MyDividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL,36));
        //groupRecyclerAdapter=new GroupRecyclerAdapter(mContext,mModel);*/


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rv_groups.setLayoutManager(mLayoutManager);
        rv_groups.setItemAnimator(new DefaultItemAnimator());

        rv_groups.addItemDecoration(new MyDividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL, 36));


        groupRecyclerAdapter=new GroupRecyclerAdapter(getContext(),mModel);
        ///groupRecyclerAdapter=new GroupRecyclerAdapter(activity,mModel);

        /*groupRecyclerAdapter=new GroupRecyclerAdapter(activity, mModel, new GroupRecyclerAdapter.ItemClickListener() {
            @Override
            public void onTrainerItemClick(View view, int position) {

                Utility.launchActivity(activity, HomeActivity.class,false);

            }
        });
        */

        rv_groups.setAdapter(groupRecyclerAdapter);

        return view;
    }

    private void bindView(View view) {
        rv_groups=view.findViewById(R.id.rv_groups);
    }


    private ArrayList<TrainerModel> GetTrainerList() {
        ArrayList<TrainerModel> list=new ArrayList<>();

        /*
        list.add(new TrainerModel("Raju","Pune", 6,"4"));
        list.add(new TrainerModel("Raju","Pune", 6,"4"));
        list.add(new TrainerModel("Raju","Pune", 6,"4"));
        list.add(new TrainerModel("Raju","Pune", 6,"4"));
        */


        list.add(new TrainerModel("Education","50/100"));
        list.add(new TrainerModel("Fun and Excitement","80/100"));
        list.add(new TrainerModel("Friendship","100/100"));
        list.add(new TrainerModel("Shayari","95/100"));

        return list;
    }

}

