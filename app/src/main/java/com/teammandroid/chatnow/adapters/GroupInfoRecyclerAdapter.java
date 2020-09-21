package com.teammandroid.chatnow.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.activities.PrivacySettingsActivity;
import com.teammandroid.chatnow.models.SettingsModel;
import com.teammandroid.chatnow.models.TrainerModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/*
public class GroupInfoRecyclerAdapter {
}
*/


public class GroupInfoRecyclerAdapter extends RecyclerView.Adapter<GroupInfoRecyclerAdapter.MyViewHolder> {

    //private Context context;

    Activity context;

    ArrayList<TrainerModel> list;

    //PrefManager prefManager;

    String role_id = "";


    private ItemClickListener itemClickListener;

    public GroupInfoRecyclerAdapter(Activity context, ArrayList<TrainerModel> list) {
        this.context = context;
        this.list = list;

    }

    public GroupInfoRecyclerAdapter(Activity context, ArrayList<TrainerModel> list, ItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.itemClickListener=itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_peoples, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;

    }

    public void filterList(ArrayList<TrainerModel> filterdNames) {
        this.list = filterdNames;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final TrainerModel item = list.get(position);

        holder.tv_groupName.setText(item.getTrainername());

        holder.tv_groupTagline.setText(item.getTraineraddress());

    }




    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ItemClickListener {
        void onGroupItemClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView civ_trainer;

        TextView tv_groupName;
        TextView tv_groupTagline;

        TextView tv_trainingCirtificate;
        TextView tv_ratings;

        LinearLayout ll_trainer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            civ_trainer = itemView.findViewById(R.id.civ_trainer);
            tv_groupName = itemView.findViewById(R.id.tv_groupName);
            //   tv_groupTagline = itemView.findViewById(R.id.tv_groupTagline);


            ll_trainer = itemView.findViewById(R.id.ll_trainer);

            itemView.setClickable(true);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) itemClickListener.onGroupItemClick(v, getAdapterPosition());
                }
            });

        }

    }

}

