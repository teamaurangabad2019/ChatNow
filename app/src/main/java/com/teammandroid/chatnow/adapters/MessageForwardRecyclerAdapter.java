package com.teammandroid.chatnow.adapters;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.utils.Constants;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageForwardRecyclerAdapter extends RecyclerView.Adapter<MessageForwardRecyclerAdapter.MyViewHolder> {

    private Context context;

    ArrayList<UserModel> list;
    //PrefManager prefManager;

    String role_id = "";


    private ItemClickListener itemClickListener;
    private UserModel currentUser;

    public MessageForwardRecyclerAdapter(Context context, ArrayList<UserModel> list, UserModel currentUser, ItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.currentUser = currentUser;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_peoples, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;


    }

    public void filterList(ArrayList<UserModel> filterdNames) {
        this.list = filterdNames;
        notifyDataSetChanged();
    }


    public static Double getDistanceBetween(double startLatitude,
                                            double startLongitude,
                                            double endLatitude,
                                            double endLongitude) {
        float[] result = new float[1];
        Location.distanceBetween(startLatitude, startLongitude,
                endLatitude, endLongitude, result);
        return (double) result[0];
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final UserModel item = list.get(position);

        String profile = Constants.URL_USER_PROFILE_PIC + item.getProfilepic();
        // Log.e(TAG, "bindValues: profile "+profile );
        Picasso.get().load(profile).placeholder(R.drawable.male_avatar).into(holder.civ_user);

        holder.tv_name.setText(item.getFullname());
        holder.tv_trainingFrom.setText(item.getAddress());
        Double distance = getDistanceBetween(currentUser.getLatitute(), currentUser.getLongitude(), item.getLatitute(), item.getLongitude());
        //holder.tv_distance.setText(String.valueOf(distance * 0.001));
        holder.tv_distance.setText(new DecimalFormat("##.##").format(distance * 0.001) + " KM");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ItemClickListener {
        void onTrainerItemClick(View view, int position);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView civ_user;

        TextView tv_name;
        TextView tv_trainingFrom;
        TextView tv_distance;
        TextView tv_ratings;
        //RelativeLayout ll_trainer;
        LinearLayout ll_trainer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            civ_user = itemView.findViewById(R.id.civ_user);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_trainingFrom = itemView.findViewById(R.id.tv_trainingFrom);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            ll_trainer = itemView.findViewById(R.id.ll_trainer);

            itemView.setClickable(true);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null)
                        itemClickListener.onTrainerItemClick(v, getAdapterPosition());
                }
            });
        }
    }
}

