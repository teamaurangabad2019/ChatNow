package com.teammandroid.chatnow.adapters.firebase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.steelkiwi.library.view.BadgeHolderLayout;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.activities.firebase.GroupChattingActivity;
//import com.teammandroid.chatnow.grouping.DataGroupingActivity;
import com.teammandroid.chatnow.adapters.ChatsRecyclerAdapter;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseGroupChatModel;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;
import com.teammandroid.chatnow.models.firebase.GroupChatList;
import com.teammandroid.chatnow.models.firebase.ParticipantsModel;
import com.teammandroid.chatnow.offline.GrpNotificationDatabaseHelper;
import com.teammandroid.chatnow.offline.NotificationDatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatsFragmentAdapter extends RecyclerView.Adapter<GroupChatsFragmentAdapter.HolderGroupChats>{
    final String TAG = GroupChatsFragmentAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<GroupChatList> groupChatLists;
    DatabaseReference ref;
    FirebaseUser firebaseUser;
    private String chatClearDate;

    public GroupChatsFragmentAdapter(Context mContext, ArrayList<GroupChatList> groupChatLists,DatabaseReference ref) {
        this.mContext = mContext;
        this.groupChatLists = groupChatLists;
        this.ref=ref;

        //ref= FirebaseDatabase.getInstance().getReference("Groups");

        ref.keepSynced(true);
    }

    @NonNull
    @Override
    public HolderGroupChats onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.item_group,parent,false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        return new HolderGroupChats(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChats holder, int position) {


        //get data
        GroupChatList model=groupChatLists.get(position);
        String groupId=model.getGroupId();
        String groupIcon=model.getGroupIcon();
        String groupTitle=model.getGroupTitle();

        holder.tv_senderName.setText("");
        holder.tv_last_group_message.setText("");
        holder.tv_last_group_message_time.setText("");

        //load message and message time
        loadClearchatDate(model);
        loadLastMessage(model,holder);

        //set data
        holder.tv_groupTitle.setText(groupTitle);

        try {
            if (groupIcon.equals("default")){
                holder.iv_groupIcon.setImageResource(R.drawable.ic_male);
            }
            else {
                Picasso.get().load(groupIcon).placeholder(R.drawable.ic_male).into(holder.iv_groupIcon);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle=new Bundle();

                //bundle.putString("groupId",groupId);

                Intent intent=new Intent(mContext, GroupChattingActivity.class);
                //Intent intent=new Intent(mContext, DataGroupingActivity.class);
                intent.putExtra("groupId",groupId);
                mContext.startActivity(intent);

            }
        });

        GrpNotificationDatabaseHelper notificationSqliteOperations = new GrpNotificationDatabaseHelper(mContext);
        int notification_count = notificationSqliteOperations.GetUnReadNotificationsfromUserId(groupId).size();
        Log.e(TAG, "onBindViewHolder: count "+notification_count );
        if (notification_count!=0){
            holder.badgeChat.setCountWithAnimation(notification_count);
        }else {
            holder.badgeChat.setVisibility(View.GONE);
        }

    }

    private void loadClearchatDate(GroupChatList model) {

        ref.child(model.getGroupId()).child("Participants")
                .orderByChild("uid").equalTo(firebaseUser.getUid())
                //.orderByChild("uid").equalTo(String.valueOf(currentUser.getUserid()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            chatClearDate="" + snapshot.child("clearDate").getValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadLastMessage(GroupChatList model, HolderGroupChats holder) {

        try {
            ref.child(model.getGroupId()).child("Messages").orderByChild("msgTime").limitToLast(1)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                FirebaseGroupChatModel chatModel = snapshot.getValue(FirebaseGroupChatModel.class);
                                SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");

                                try {
                                    //Date date1 = dateTime.parse(participantsModel.getClearDate());
                                    Date date11 = dateTime.parse(chatClearDate);
                                    String msgTime1 = "" + snapshot.child("msgTime").getValue();
                                    Date date22 = dateTime.parse(msgTime1);

                                    if (date11.compareTo(date22) < 0) {
                                        // if (!dateTime.parse(model.getMsgTime()).before(dateTime.parse("26/05/2020 09:21:00 AM")))   {

                                        //myGroupRole=""+snapshot.child("role").getValue();
                                        //String message=chatModel.getMessage();
                                        String message = "" + snapshot.child("message").getValue();
                                        ;
                                        //String sender=chatModel.getSender();
                                        String sender = "" + snapshot.child("sender").getValue();
                                        //String msgTime=chatModel.getMsgTime();
                                        String msgTime = "" + snapshot.child("msgTime").getValue();
                                        //String mimeType=""+snapshot.child("msgTime").getValue();
                                        //int mediaType=Integer.parseInt(mimeType);
                                        int mediaType = chatModel.getMediatype();
                                        //holder.tv_last_group_message.setText(message);

                                        //image
                                        if (mediaType == 1) {

                                            if (chatModel.getIsDeleted() == 1) {

                                                holder.rl_text.setVisibility(View.VISIBLE);
                                                holder.rl_photo.setVisibility(View.GONE);

                                                holder.tv_last_group_message.setText(message);
                                            } else {

                                                holder.rl_text.setVisibility(View.GONE);
                                                holder.rl_audio.setVisibility(View.GONE);
                                                holder.rl_video.setVisibility(View.GONE);
                                                holder.rl_file.setVisibility(View.GONE);
                                                holder.rl_contact.setVisibility(View.GONE);
                                                holder.rl_photo.setVisibility(View.VISIBLE);
                                                holder.rl_gif.setVisibility(View.GONE);
                                            }

                                        }
                                        //audio
                                        else if (mediaType == 3) {
                                            if (chatModel.getIsDeleted() == 1) {

                                                holder.rl_text.setVisibility(View.VISIBLE);
                                                holder.rl_photo.setVisibility(View.GONE);

                                                holder.tv_last_group_message.setText(message);
                                            } else {
                                                holder.rl_text.setVisibility(View.GONE);
                                                holder.rl_audio.setVisibility(View.VISIBLE);
                                                holder.rl_video.setVisibility(View.GONE);
                                                holder.rl_file.setVisibility(View.GONE);
                                                holder.rl_photo.setVisibility(View.GONE);
                                                holder.rl_contact.setVisibility(View.GONE);
                                                holder.rl_gif.setVisibility(View.GONE);
                                            }
                                        }
                                        //video
                                        else if (mediaType == 4) {
                                            if (chatModel.getIsDeleted() == 1) {

                                                holder.rl_text.setVisibility(View.VISIBLE);
                                                holder.rl_photo.setVisibility(View.GONE);

                                                holder.tv_last_group_message.setText(message);
                                            } else {
                                                holder.rl_text.setVisibility(View.GONE);
                                                holder.rl_audio.setVisibility(View.GONE);
                                                holder.rl_video.setVisibility(View.VISIBLE);
                                                holder.rl_file.setVisibility(View.GONE);
                                                holder.rl_photo.setVisibility(View.GONE);
                                                holder.rl_contact.setVisibility(View.GONE);
                                                holder.rl_gif.setVisibility(View.GONE);
                                            }


                                        }
                                        //file
                                        else if (mediaType == 5) {
                                            if (chatModel.getIsDeleted() == 1) {

                                                holder.rl_text.setVisibility(View.VISIBLE);
                                                holder.rl_photo.setVisibility(View.GONE);

                                                holder.tv_last_group_message.setText(message);
                                            } else {
                                                holder.rl_text.setVisibility(View.GONE);
                                                holder.rl_audio.setVisibility(View.GONE);
                                                holder.rl_video.setVisibility(View.GONE);
                                                holder.rl_file.setVisibility(View.VISIBLE);
                                                holder.rl_photo.setVisibility(View.GONE);
                                                holder.rl_contact.setVisibility(View.GONE);
                                                holder.rl_gif.setVisibility(View.GONE);

                                                //holder.tv_file.setText(message);
                                            }

                                        }

                                        //contact
                                        else if (mediaType == 8) {

                                            if (chatModel.getIsDeleted() == 1) {

                                                holder.rl_text.setVisibility(View.VISIBLE);
                                                holder.rl_photo.setVisibility(View.GONE);

                                                holder.tv_last_group_message.setText(message);
                                            } else {
                                                holder.rl_text.setVisibility(View.GONE);
                                                holder.rl_audio.setVisibility(View.GONE);
                                                holder.rl_video.setVisibility(View.GONE);
                                                holder.rl_file.setVisibility(View.GONE);
                                                holder.rl_photo.setVisibility(View.GONE);
                                                holder.rl_contact.setVisibility(View.VISIBLE);
                                                holder.rl_gif.setVisibility(View.GONE);

                                                holder.tv_contact.setText(message);
                                            }

                                        }
                                        //gif
                                        else if (mediaType == 2) {
                                            if (chatModel.getIsDeleted() == 1) {

                                                holder.rl_text.setVisibility(View.VISIBLE);
                                                holder.rl_photo.setVisibility(View.GONE);

                                                holder.tv_last_group_message.setText(message);
                                            } else {
                                                holder.rl_text.setVisibility(View.GONE);
                                                holder.rl_audio.setVisibility(View.GONE);
                                                holder.rl_video.setVisibility(View.GONE);
                                                holder.rl_file.setVisibility(View.GONE);
                                                holder.rl_photo.setVisibility(View.GONE);
                                                holder.rl_contact.setVisibility(View.GONE);
                                                holder.rl_gif.setVisibility(View.VISIBLE);
                                            }

                                        } else {
                                            holder.rl_text.setVisibility(View.VISIBLE);
                                            holder.rl_photo.setVisibility(View.GONE);

                                            holder.tv_last_group_message.setText(message);

                                        }

                                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");

                                        String time = msgTime;

                                        try {
                                            Date date1 = formatter.parse(time);

                                            Date exitdate = date1;
                                            Date currdate = new Date();

                                            long diff = currdate.getTime() - exitdate.getTime();
                                            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                                            Log.e("currentdate", "diff" + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

                                            if (days == 0) {

                                                time = time.substring(11);

                                                //holder.tv_last_group_message_time.setText(time);

                                                try {

                                                    //code to convert 24hr time format 12 hours time format
                                                    final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");

                                                    final Date dateObj = sdf.parse(time);

                                                    Log.e("currentdate", "dateObj : " + new SimpleDateFormat("K:mm a").format(dateObj));

                                                    holder.tv_last_group_message_time.setText(new SimpleDateFormat("K:mm a").format(dateObj));


                                                } catch (final ParseException e) {
                                                    e.printStackTrace();
                                                }

                                            } else if (days == 1) {

                                                //dateViewHolder.txtTitle.setText("YESTERDAY");

                                                Log.e("currentdate", "" + "yesterday");

                                                holder.tv_last_group_message_time.setText("YESTERDAY");

                                            } else {

                                                Date date2 = formatter.parse(time);

                                                Log.e("currentdate", "2");

                                                Log.e("currentdate", "dd MMMM yyyy : " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a").format(date2));


                                                holder.tv_last_group_message_time.setText(new SimpleDateFormat("dd/MM/yyyy").format(date2));

                                            }


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                        //Log.e("lastmsg","lastmsg"+message);

                                        //holder.tv_last_group_message.setText(message);
                                        //holder.tv_last_group_message_time.setText(msgTime);

                                        //get info of sender of last message
                                        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Users");
                                        dref.keepSynced(true);

                                        dref.orderByChild("id").equalTo(sender)
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {

                                                            FirebaseUserModel userModel = snapshot1.getValue(FirebaseUserModel.class);
                                                            //UserModel userModel=snapshot1.getValue(UserModel.class);

                                                            //Log.e("sendername",""+userModel.getFullname());
                                                            Log.e("sendername", "" + userModel.getUsername());

                                                            //holder.tv_senderName.setText(userModel.getFullname());
                                                            holder.tv_senderName.setText(userModel.getUsername() + ":");
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    }
                                                });

                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return groupChatLists.size();
    }

    class HolderGroupChats extends RecyclerView.ViewHolder{

        private CircleImageView iv_groupIcon;
        private TextView tv_groupTitle,tv_senderName,tv_last_group_message_time,tv_last_group_message;
        BadgeHolderLayout badgeChat;

        TextView tv_photo,tv_audio,tv_video,tv_file,tv_contact,tv_gif;
        RelativeLayout rl_text,rl_photo,rl_audio,rl_video,rl_file,rl_contact,rl_gif;
        ImageView iv_photo,iv_audio,iv_video,iv_file,iv_contact,iv_gif;


        public HolderGroupChats(@NonNull View itemView) {
            super(itemView);
            iv_groupIcon=itemView.findViewById(R.id.iv_groupIcon);
            tv_groupTitle=itemView.findViewById(R.id.tv_groupTitle);
            tv_senderName=itemView.findViewById(R.id.tv_senderName);
            tv_last_group_message=itemView.findViewById(R.id.tv_last_group_message);
            tv_last_group_message_time=itemView.findViewById(R.id.tv_last_group_message_time);
            badgeChat=itemView.findViewById(R.id.badgeChat);

            rl_photo=itemView.findViewById(R.id.rl_photo);
            rl_text=itemView.findViewById(R.id.rl_text);
            rl_audio=itemView.findViewById(R.id.rl_audio);
            rl_video=itemView.findViewById(R.id.rl_video);
            rl_file=itemView.findViewById(R.id.rl_file);
            rl_contact=itemView.findViewById(R.id.rl_contact);
            rl_gif=itemView.findViewById(R.id.rl_gif);

            iv_photo=itemView.findViewById(R.id.iv_photo);
            iv_audio=itemView.findViewById(R.id.iv_audio);
            iv_video=itemView.findViewById(R.id.iv_video);
            iv_file=itemView.findViewById(R.id.iv_file);
            iv_contact=itemView.findViewById(R.id.iv_contact);
            iv_gif=itemView.findViewById(R.id.iv_gif);


            tv_photo=itemView.findViewById(R.id.tv_photo);
            tv_audio=itemView.findViewById(R.id.tv_video);
            tv_video=itemView.findViewById(R.id.tv_audio);
            tv_file=itemView.findViewById(R.id.tv_file);
            tv_contact=itemView.findViewById(R.id.tv_contact);
            tv_gif=itemView.findViewById(R.id.tv_gif);

        }
    }
}
