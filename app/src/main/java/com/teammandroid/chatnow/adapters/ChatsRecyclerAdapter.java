package com.teammandroid.chatnow.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.steelkiwi.library.view.BadgeHolderLayout;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.models.firebase.FirebaseMsgModel;
import com.teammandroid.chatnow.offline.NotificationDatabaseHelper;
import com.teammandroid.chatnow.models.MyChatModel;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatsRecyclerAdapter extends RecyclerView.Adapter<ChatsRecyclerAdapter.MyViewHolder> {

    final String TAG = ChatsRecyclerAdapter.class.getSimpleName();

    private Context context;

    ArrayList<MyChatModel> list;

    //PrefManager prefManager;

    String role_id = "";

    private ChatsRecyclerAdapter.ItemClickListener itemClickListener;

    private UserModel currentUser;

    FirebaseUser firebaseUser;

    String partnerFirebaseUserId;
    private DatabaseReference reference;
    private ArrayList<FirebaseMsgModel> firebaseChatModel;

    public ChatsRecyclerAdapter(Context context, ArrayList<MyChatModel> list, UserModel currentUser,  ChatsRecyclerAdapter.ItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.currentUser=currentUser;
        this.itemClickListener = itemClickListener;

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_peoples, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;

    }


    public void filterList(ArrayList<MyChatModel> filterdNames) {
        this.list = filterdNames;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final MyChatModel item = list.get(position);

        //String media = Constants.URL_USER_PROFILE_PIC + item.getProfilepic();

        // Picasso.get().load(media).placeholder(R.drawable.male_avatar).into(civ_profile);

        //isBlockedOrNot(item,firebaseUser.getUid(),holder.civ_senderImg);

        isBlockedOrNot(item,firebaseUser.getUid(),holder.civ_senderImg);

        Log.e("MYCHATMODEL","MYCHATMODEL"+ item.getFirebasePartnerId()+","+item.getPartnerid()+","+item.getPartnerName());

        /*
        Picasso.get()
                .load(media)
                .into( holder.civ_senderImg, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        // Try again online if cache failed
                        Picasso.get()
                                .load(media)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.male_avatar)
                                .error(R.drawable.male_avatar)
                                .into( holder.civ_senderImg);
                    }
                });
                */

        // holder.civ_senderImg.setImageResource(R.drawable.male_avatar);
        holder.tv_senderName.setText(item.getPartnerName());

        //holder.tv_lastMessage.setText(item.getMessage());

        int mediaType=item.getMediaType();

        //image
        if (mediaType == 1){

            if (item.getIsdeleted() == 1){
                holder.ll_text.setVisibility(View.VISIBLE);
                holder.rl_audio.setVisibility(View.GONE);
                holder.rl_video.setVisibility(View.GONE);
                holder.rl_file.setVisibility(View.GONE);
                holder.rl_contact.setVisibility(View.GONE);
                holder.rl_photo.setVisibility(View.GONE);
                holder.rl_gif.setVisibility(View.GONE);

                holder.tv_lastMessage.setText(item.getMessage());
            }
            else {
                holder.ll_text.setVisibility(View.GONE);
                holder.rl_audio.setVisibility(View.GONE);
                holder.rl_video.setVisibility(View.GONE);
                holder.rl_file.setVisibility(View.GONE);
                holder.rl_contact.setVisibility(View.GONE);
                holder.rl_photo.setVisibility(View.VISIBLE);
                holder.rl_gif.setVisibility(View.GONE);
            }
        }

        //audio
        else if (mediaType == 3){

            if (item.getIsdeleted() == 1){
                holder.ll_text.setVisibility(View.VISIBLE);
                holder.rl_audio.setVisibility(View.GONE);
                holder.rl_video.setVisibility(View.GONE);
                holder.rl_file.setVisibility(View.GONE);
                holder.rl_contact.setVisibility(View.GONE);
                holder.rl_photo.setVisibility(View.GONE);
                holder.rl_gif.setVisibility(View.GONE);

                holder.tv_lastMessage.setText(item.getMessage());
            }
            else {
                holder.ll_text.setVisibility(View.GONE);
                holder.rl_audio.setVisibility(View.VISIBLE);
                holder.rl_video.setVisibility(View.GONE);
                holder.rl_file.setVisibility(View.GONE);
                holder.rl_contact.setVisibility(View.GONE);
                holder.rl_photo.setVisibility(View.GONE);
                holder.rl_gif.setVisibility(View.GONE);
            }


        }
        //video
        else if (mediaType == 4){

            if (item.getIsdeleted() == 1){
                holder.ll_text.setVisibility(View.VISIBLE);
                holder.rl_audio.setVisibility(View.GONE);
                holder.rl_video.setVisibility(View.GONE);
                holder.rl_file.setVisibility(View.GONE);
                holder.rl_contact.setVisibility(View.GONE);
                holder.rl_photo.setVisibility(View.GONE);
                holder.rl_gif.setVisibility(View.GONE);

                holder.tv_lastMessage.setText(item.getMessage());
            }
            else {
                holder.ll_text.setVisibility(View.GONE);
                holder.rl_audio.setVisibility(View.GONE);
                holder.rl_video.setVisibility(View.VISIBLE);
                holder.rl_file.setVisibility(View.GONE);
                holder.rl_contact.setVisibility(View.GONE);
                holder.rl_photo.setVisibility(View.GONE);
                holder.rl_gif.setVisibility(View.GONE);
            }
        }
        //file
        else if (mediaType == 5){

            if (item.getIsdeleted() == 1){
                holder.ll_text.setVisibility(View.VISIBLE);
                holder.rl_audio.setVisibility(View.GONE);
                holder.rl_video.setVisibility(View.GONE);
                holder.rl_file.setVisibility(View.GONE);
                holder.rl_contact.setVisibility(View.GONE);
                holder.rl_photo.setVisibility(View.GONE);
                holder.rl_gif.setVisibility(View.GONE);

                holder.tv_lastMessage.setText(item.getMessage());
            }
            else {
                holder.ll_text.setVisibility(View.GONE);
                holder.rl_audio.setVisibility(View.GONE);
                holder.rl_video.setVisibility(View.GONE);
                holder.rl_file.setVisibility(View.VISIBLE);
                holder.rl_contact.setVisibility(View.GONE);
                holder.rl_photo.setVisibility(View.GONE);
                holder.rl_gif.setVisibility(View.GONE);

                //holder.tv_file.setText(item.getMessage());
            }


        }

        //contact
        else if (mediaType == 8){

            if (item.getIsdeleted() == 1){
                holder.ll_text.setVisibility(View.VISIBLE);
                holder.rl_audio.setVisibility(View.GONE);
                holder.rl_video.setVisibility(View.GONE);
                holder.rl_file.setVisibility(View.GONE);
                holder.rl_contact.setVisibility(View.GONE);
                holder.rl_photo.setVisibility(View.GONE);
                holder.rl_gif.setVisibility(View.GONE);

                holder.tv_lastMessage.setText(item.getMessage());
            }
            else {
                holder.ll_text.setVisibility(View.GONE);
                holder.rl_audio.setVisibility(View.GONE);
                holder.rl_video.setVisibility(View.GONE);
                holder.rl_file.setVisibility(View.GONE);
                holder.rl_contact.setVisibility(View.VISIBLE);
                holder.rl_photo.setVisibility(View.GONE);
                holder.rl_gif.setVisibility(View.GONE);

                holder.tv_contact.setText(item.getMessage());
            }

        }

        //gif
        else if (mediaType == 2){

            if (item.getIsdeleted() == 1){
                holder.ll_text.setVisibility(View.VISIBLE);
                holder.rl_audio.setVisibility(View.GONE);
                holder.rl_video.setVisibility(View.GONE);
                holder.rl_file.setVisibility(View.GONE);
                holder.rl_contact.setVisibility(View.GONE);
                holder.rl_photo.setVisibility(View.GONE);
                holder.rl_gif.setVisibility(View.GONE);

                holder.tv_lastMessage.setText(item.getMessage());
            }
            else {

                holder.ll_text.setVisibility(View.GONE);
                holder.rl_audio.setVisibility(View.GONE);
                holder.rl_video.setVisibility(View.GONE);
                holder.rl_file.setVisibility(View.GONE);
                holder.rl_contact.setVisibility(View.GONE);
                holder.rl_photo.setVisibility(View.GONE);
                holder.rl_gif.setVisibility(View.VISIBLE);
            }

        }
        else {

            holder.ll_text.setVisibility(View.VISIBLE);
            holder.rl_audio.setVisibility(View.GONE);
            holder.rl_video.setVisibility(View.GONE);
            holder.rl_file.setVisibility(View.GONE);
            holder.rl_contact.setVisibility(View.GONE);
            holder.rl_photo.setVisibility(View.GONE);
            holder.rl_gif.setVisibility(View.GONE);

            holder.tv_lastMessage.setText(item.getMessage());

        }


        //holder.tv_lastMessageDate.setText(item.getMsgTime());

        Log.e("lastmsgdate","lastmsgdate"+item.getMsgTime());

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");

        String time=item.getMsgTime();

        try {

            Date date1 = formatter.parse(time);

            Date exitdate =date1;
            Date currdate = new Date();

            long diff = currdate.getTime() - exitdate.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

            Log.e("currentdate","diff"+TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

            if(days==0)
            {

                time=time.substring(11);

                //holder.tv_last_group_message_time.setText(time);

                try {

                    //code to convert 24hr time format 12 hours time format
                    final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");

                    final Date dateObj = sdf.parse(time);

                    Log.e("currentdate","dateObj : "+new SimpleDateFormat("K:mm a").format(dateObj));

                    holder.tv_lastMessageDate.setText((new SimpleDateFormat("K:mm a").format(dateObj)));


                } catch (final ParseException e) {
                    e.printStackTrace();
                }

            }
            else if (days==1){

                //dateViewHolder.txtTitle.setText("YESTERDAY");

                Log.e("currentdate",""+"yesterday");

                holder.tv_lastMessageDate.setText("YESTERDAY");

            }

            else {

                Date date2 = formatter.parse(time);

                Log.e("currentdate","2");

                Log.e("currentdate","dd MMMM yyyy : "+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a").format(date2));


                holder.tv_lastMessageDate.setText((new SimpleDateFormat("dd/MM/yyyy").format(date2)));

            }


        }
        catch (Exception e){
            e.printStackTrace();
        }

        try {
            NotificationDatabaseHelper notificationSqliteOperations = new NotificationDatabaseHelper(context);
            int notification_count = notificationSqliteOperations.GetUnReadNotificationsfromUserId(Integer.valueOf(item.getPartnerid())).size();
            Log.e(TAG, "onBindViewHolder: count "+notification_count );
            if (notification_count!=0){
                holder.badgeChat.setCountWithAnimation(notification_count);
            }else {
                holder.badgeChat.setVisibility(View.GONE);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    public void isBlockedOrNot(MyChatModel model, String currentUserId, CircleImageView imageView){

        //first check if sender(current user) is blocked by receiver or not
        //logic: if uid of the sender(current user) exists in 'BlockedUsers' of receiver then sender(current user) is blocked,otherwise not
        //if blocked then just display a message e.g. You're blocked by that user,can't send message
        //if not blocked then simply start the chat activity

        try {
            partnerFirebaseUserId=model.getFirebasePartnerId();

            DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");

            ref.child(partnerFirebaseUserId).child("BlockedUsers").orderByChild("uid").equalTo(firebaseUser.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                if (snapshot.exists()){

                                    //block,don't show profile pic

                                    //Toast.makeText(activity, "You're blocked by this user,you can't send message", Toast.LENGTH_SHORT).show();

                                    //imageView.setImageResource(R.drawable.ic_male);

                                    //Glide.with(context).load(R.drawable.ic_male).into(imageView);

                                    //Glide.with(context).load(R.drawable.ic_account_circle).into(imageView);
                                    Glide.with(context).load(R.drawable.ic_male).into(imageView);

                                    Log.e("chatlog", "chatlog 1");

                                    return;

                                }
                            }


                            if (model.getProfilepic().equals(" ")){

                                Log.e("chatlog", "chatlog 3");

                                Glide.with(context).load(R.drawable.ic_male).into(imageView);

                            }
                            else {
                                String media = Constants.URL_USER_PROFILE_PIC +model.getProfilepic();

                                //Picasso.get().load(media).placeholder(R.drawable.ic_male).into(imageView);
                                Picasso.get().load(media).placeholder(R.drawable.ic_male).into(imageView);

                                Log.e("chatlog", "chatlog 2");
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ItemClickListener {
        void onTrainerItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView civ_senderImg;
        TextView tv_senderName;
        TextView tv_lastMessage;
        TextView tv_lastMessageDate;
        BadgeHolderLayout badgeChat;
        LinearLayout ll_chat;


        LinearLayout ll_text;

        TextView tv_photo,tv_audio,tv_video,tv_file,tv_contact,tv_gif;
        RelativeLayout rl_text,rl_photo,rl_audio,rl_video,rl_file,rl_contact,rl_gif;
        ImageView iv_photo,iv_audio,iv_video,iv_file,iv_contact,iv_gif;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            civ_senderImg = itemView.findViewById(R.id.civ_senderImg);
            tv_senderName = itemView.findViewById(R.id.tv_chatUserName);
            tv_lastMessage = itemView.findViewById(R.id.tv_message);
            tv_lastMessageDate = itemView.findViewById(R.id.tv_lastMessageDate);
            badgeChat = itemView.findViewById(R.id.badgeChat);
            ll_chat = itemView.findViewById(R.id.ll_chat);

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

            ll_text=itemView.findViewById(R.id.ll_text);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null)
                        itemClickListener.onTrainerItemClick(v, getAdapterPosition());
                }
            });

            ll_chat.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener != null)
                        itemClickListener.onItemLongClick(v, getAdapterPosition());
                    return true;
                }
            });

        }
    }
}

