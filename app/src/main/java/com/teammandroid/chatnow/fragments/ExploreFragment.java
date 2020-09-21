package com.teammandroid.chatnow.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.adapters.CustomAdapter;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {

    Context context;

    // ArrayList for person names
    ArrayList<String> personNames = new ArrayList<>(Arrays.asList("Random Video Chat", "Random Voice Chat", "Who Checked Me Out", "Emoji", "Themes", "Help"));
    ArrayList<Integer> personImages = new ArrayList<>(Arrays.asList(R.drawable.logo_anim_bkg_3,R.drawable.logo_anim_bkg_4,R.drawable.explore_anim_wholook_look4,R.drawable.explore_anim_facial_facialbase,R.drawable.explore_anim_theme_themebase,R.drawable.explore_anim_help_help));
    RecyclerView rv_explore;

    public ExploreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_explore, container, false);

        bindView(view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,2);

        rv_explore.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        //  call the constructor of CustomAdapter to send the reference and data to Adapter

        CustomAdapter customAdapter = new CustomAdapter(context, personNames,personImages);

        //CustomAdapter customAdapter = new CustomAdapter(this, personNames,personImages,userId);

        rv_explore.setAdapter(customAdapter); // set the Adapter to RecyclerView

        return view;
    }

    private void bindView(View view) {

        rv_explore=view.findViewById(R.id.rv_explore);

    }

}
