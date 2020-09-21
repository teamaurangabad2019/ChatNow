package com.teammandroid.chatnow.adapters;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.models.TrainerModel;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.SessionManager;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

/*
public class PeopleRecyclerAdapter {
}
*/


public class PeopleRecyclerAdapter extends RecyclerView.Adapter<PeopleRecyclerAdapter.MyViewHolder> {
    final static  String TAG = ChatsRecyclerAdapter.class.getSimpleName();
    private Context context;

    ArrayList<UserModel> list;
    //PrefManager prefManager;

    String role_id = "";
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private FirebaseUserModel firebaseUserModel;

    private PeopleRecyclerAdapter.ItemClickListener itemClickListener;
    private UserModel currentUser;

    String partnerFirebaseUserId;
    private double lat;
    private double log;

    public PeopleRecyclerAdapter(Context context, ArrayList<UserModel> list, UserModel currentUser, FirebaseUser firebaseUser, DatabaseReference reference, ItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.itemClickListener = itemClickListener;
        this.firebaseUser = firebaseUser;
        this.reference = reference;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        PeopleRecyclerAdapter.MyViewHolder viewHolder = new PeopleRecyclerAdapter.MyViewHolder(view);

        SessionManager sessionManager  = new SessionManager(context);
        currentUser = sessionManager.getUserDetails();
        //lat = sessionManager.getLat();
        //log = sessionManager.getLog();
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
        Log.e(TAG, "distance: lat1 "+startLatitude+" lon1 "+startLongitude+" lat2 "+endLatitude+" lon2 "+endLongitude );

        float[] result = new float[1];
        Location.distanceBetween(startLatitude, startLongitude,
                endLatitude, endLongitude, result);
        return (double) result[0];
    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        Log.e(TAG, "distance: lat1 "+lat1+" lon1 "+lon1+" lat2 "+lat2+" lon2 "+lon2 );
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final UserModel item = list.get(position);
        GetFirebaseData(item, holder.tv_online);

        //String profile = Constants.URL_USER_PROFILE_PIC + item.getProfilepic();
        // Log.e(TAG, "bindValues: profile "+profile );
        //Picasso.get().load(profile).placeholder(R.drawable.male_avatar).into(holder.civ_user);

        isBlockedOrNot(item,firebaseUser.getUid(),holder.civ_user);


        holder.tv_name.setText(item.getFullname());
        holder.tv_trainingFrom.setText(item.getAddress());
        Double distance = getDistanceBetween(currentUser.getLatitute(), currentUser.getLongitude(), item.getLatitute(), item.getLongitude());
        //Double distance = distance(currentUser.getLatitute(), currentUser.getLongitude(), item.getLatitute(), item.getLongitude());
        //holder.tv_distance.setText(String.valueOf(distance * 0.001));
        holder.tv_distance.setText(new DecimalFormat("##.##").format(distance * 0.001) + " KM");
        // holder.tv_distance.setText(new DecimalFormat("##.##").format(distance )+ " KM");
    }



    public void isBlockedOrNot(UserModel model,String currentUserId,CircleImageView imageView){

        //first check if sender(current user) is blocked by receiver or not
        //logic: if uid of the sender(current user) exists in 'BlockedUsers' of receiver then sender(current user) is blocked,otherwise not
        //if blocked then just display a message e.g. You're blocked by that user,can't send message
        //if not blocked then simply start the chat activity

        partnerFirebaseUserId=model.getFirebaseuserid();

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");

        ref.child(partnerFirebaseUserId).child("BlockedUsers").orderByChild("uid").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if (snapshot.exists()){

                                //block,don't show profile pic

                                //Toast.makeText(activity, "You're blocked by this user,you can't send message", Toast.LENGTH_SHORT).show();

                                //civ_profile.setImageResource(R.drawable.ic_male);

                                //imageView.setImageResource(R.mipmap.ic_launcher);

                                //Picasso.get().load(R.drawable.ic_male).into(imageView);

                                Glide.with(context).load(R.drawable.ic_male).into(imageView);

                                Log.e("mylog", "log 1");

                                return;
                            }
                            /*
                            else {

                                String media = Constants.URL_USER_PROFILE_PIC +model.getProfilepic();

                                //Picasso.get().load(media).placeholder(R.drawable.ic_male).into(imageView);

                                Log.e("mylog", "log 2");

                            }*/

                        }

                        if (model.getProfilepic().equals(" ")){
                            //imageView.setImageResource(R.mipmap.ic_launcher);
                            //imageView.setImageResource(R.drawable.ic_male);
                            Log.e("mylog", "log 3");

                            Glide.with(context).load(R.drawable.ic_male).into(imageView);
                        }
                        else {
                            String media = Constants.URL_USER_PROFILE_PIC +model.getProfilepic();

                            //Picasso.get().load(media).placeholder(R.drawable.ic_male).into(imageView);
                            Picasso.get().load(media).into(imageView);

                            Log.e("mylog", "log 2");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void GetFirebaseData(UserModel item, TextView tv_online) {
        /*region firebase work*/
        Query query = reference.orderByChild("onlineUserId").equalTo(String.valueOf(item.getUserid()));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        firebaseUserModel = childSnapshot.getValue(FirebaseUserModel.class);
                    }
                    if (firebaseUserModel.getStatus().equals("online")) {
                        tv_online.setText("online");
                    } else {
                        tv_online.setText("offline");
                    }
                }catch (Exception e){
                    Log.e(TAG, "onDataChange: exception "+e.getMessage() );
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: "+databaseError.getMessage() );
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ItemClickListener {
        void onTrainerItemClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_online;
        CircleImageView civ_user;
        TextView tv_name;
        TextView tv_trainingFrom;
        TextView tv_distance;
        TextView tv_ratings;
        LinearLayout ll_trainer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            civ_user = itemView.findViewById(R.id.civ_user);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_trainingFrom = itemView.findViewById(R.id.tv_trainingFrom);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            ll_trainer = itemView.findViewById(R.id.ll_trainer);
            tv_online = itemView.findViewById(R.id.tv_online);

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

