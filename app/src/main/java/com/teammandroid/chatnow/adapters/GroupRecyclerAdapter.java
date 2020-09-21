package com.teammandroid.chatnow.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.activities.firebase.GroupChattingActivity;
import com.teammandroid.chatnow.models.TrainerModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class GroupRecyclerAdapter extends RecyclerView.Adapter<GroupRecyclerAdapter.MyViewHolder> {

    //private Context context;

    //
    //private Activity context;

    Context mContext;

    ArrayList<TrainerModel> list;

    //PrefManager prefManager;

    String role_id = "";

    private GroupRecyclerAdapter.ItemClickListener itemClickListener;

    /*
    public GroupRecyclerAdapter(Context context, ArrayList<TrainerModel> list) {
        this.mContext = context;
        this.list = list;
    }*/


    public GroupRecyclerAdapter(Context context,ArrayList<TrainerModel> list){
        this.mContext=context;
        this.list=list;
    }

    public GroupRecyclerAdapter(Context context, ArrayList<TrainerModel> list, GroupRecyclerAdapter.ItemClickListener itemClickListener) {
        this.mContext = context;
        this.list = list;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_peoples, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        GroupRecyclerAdapter.MyViewHolder viewHolder = new GroupRecyclerAdapter.MyViewHolder(view);
        return viewHolder;
    }

    public interface ItemClickListener {
        void onGroupItemClick(View view, int position);
    }

    public void filterList(ArrayList<TrainerModel> filterdNames) {
        this.list = filterdNames;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final TrainerModel item = list.get(position);

        holder.civ_trainer.setImageResource(R.drawable.male_avatar);

        holder.tv_groupName.setText(item.getTrainername());
        holder.tv_groupTagline.setText(item.getTraineraddress());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Utility.launchActivity(context, AddNewGroupActivity.class,false)
                String name=holder.tv_groupName.getText().toString();

                //Toast.makeText(context,name , Toast.LENGTH_SHORT).show();

                Log.e("groupclick","groupclick"+holder.tv_groupName.getText().toString());

                //Utility.launchActivity(context, AddNewGroupActivity.class,false);
                Intent intent=new Intent(mContext, GroupChattingActivity.class);
                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CircleImageView civ_trainer;

        TextView tv_groupName;
        TextView tv_groupTagline;

        TextView tv_trainingCirtificate;
        TextView tv_ratings;

        LinearLayout ll_trainer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            civ_trainer = itemView.findViewById(R.id.civ_trainer);
          //  tv_groupName = itemView.findViewById(R.id.tv_groupName);
           // tv_groupTagline = itemView.findViewById(R.id.tv_groupTagline);


            ll_trainer = itemView.findViewById(R.id.ll_trainer);

            /*itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) itemClickListener.onGroupItemClick(v, getAdapterPosition());
                }
            });*/

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) itemClickListener.onGroupItemClick(v, getAdapterPosition());
        }

    }
}
