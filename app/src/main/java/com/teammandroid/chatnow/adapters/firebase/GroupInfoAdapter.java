package com.teammandroid.chatnow.adapters.firebase;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.activities.GroupInfoActivity;
import com.teammandroid.chatnow.adapters.PeopleRecyclerAdapter;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;
import com.teammandroid.chatnow.utils.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class GroupInfoAdapter extends RecyclerView.Adapter<GroupInfoAdapter.ParticipantHolder>{

    private Activity context;
    //private Context context;
    private ArrayList<FirebaseUserModel> userList;
    private ArrayList<UserModel> userModelList;

    private PeopleRecyclerAdapter.ItemClickListener itemClickListener;


    String groupId,myGroupRole; //creator/admin/partcipant

    public GroupInfoAdapter(Activity context, ArrayList<FirebaseUserModel> userList, String groupId, String myGroupRole) {
        this.context = context;
        this.userList = userList;
        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
    }

    /*public GroupInfoAdapter(Context context, ArrayList<UserModel> userModelList, String groupId, String myGroupRole) {

        this.context = context;
        //this.userList = userList;
        this.userModelList = userModelList;

        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
    }*/



    /*public AddParticipantAdapter(Context context, ArrayList<FirebaseUserModel> userList) {
        this.context = context;
        this.userList = userList;
    }
    */


    @NonNull
    @Override
    public ParticipantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view= LayoutInflater.from(context).inflate(R.layout.row_participant_add,parent,false);

        return new ParticipantHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantHolder holder, int position) {

        // get data

        FirebaseUserModel modelUser=userList.get(position);

        String name=modelUser.getUsername();
        String email=modelUser.getEmail();
        String image=modelUser.getImageUrl();
        String userId=modelUser.getId();
        String about=modelUser.getAbout();
        String onlineUserId=modelUser.getOnlineUserId();

        /*
        UserModel modelUser=userModelList.get(position);

        Log.e("usermodellist","usermodellist"+userModelList.toString());


        String name=modelUser.getFullname();
        String email=modelUser.getEmail();
        String image=modelUser.getProfilepic();
        String userId=String.valueOf(modelUser.getUserid());

        //String about=modelUser.getAbout();
        */

        Log.e("pARTNAME","pARTNAME"+name);

        //set data
        holder.tv_participant_name.setText(name);


        //holder.tv_personRole.setText(email);

        //holder.tv_personAbout.setText(about);


        try {
            Picasso.get().load(image).placeholder(R.drawable.male_avatar).into(holder.iv_participant);
        }
        catch (Exception e){
            holder.iv_participant.setImageResource(R.drawable.male_avatar);
        }

        checkIfAlreadyExists(modelUser,holder);


        /*
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//*
                check if user already added or not
                *if added : show remove-participant/make admin/remove admin option(admin will not be able to change role of creator)
                *if not added,show add participant option
                *//*


                DatabaseReference dref=FirebaseDatabase.getInstance().getReference("Groups");
                dref.child(groupId).child("Participants").child(userId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                if (dataSnapshot.exists()){

                                    try {
                                        // user exists/participant
                                        String hisPreviousRole=""+dataSnapshot.child("role").getValue();
                                        if (myGroupRole.equals("creator")){

                                            if (hisPreviousRole.equals("admin")){
                                                showCustomDialogForAdminUser(modelUser);

                                            }
                                            else if (hisPreviousRole.equals("participant")){
                                                showCustomDialogForParticipant(modelUser);

                                            }
                                        }
                                        else if (myGroupRole.equals("admin")){
                                            if (hisPreviousRole.equals("creator")){
                                                //in admin,he is creator
                                                Toast.makeText(context, "Creator of group", Toast.LENGTH_SHORT).show();
                                            }

                                            else if (hisPreviousRole.equals("admin")){
                                                showCustomDialogForAdminUser(modelUser);
                                            }
                                            else if (hisPreviousRole.equals("participant")){
                                                showCustomDialogForParticipant(modelUser);
                                            }
                                        }

                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                        Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    try {

                                        // user does not exists/not-participant : Add
                                        AlertDialog.Builder builder=new AlertDialog.Builder(context);
                                        builder.setTitle("Add participant")
                                                .setMessage("Add this user in this group")
                                                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //add user
                                                        addParticipant(modelUser);
                                                    }
                                                })
                                                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).show();

                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });*/


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /*     check if user already added or not
                 *if added : show remove-participant/make admin/remove admin option(admin will not be able to change role of creator)
                 *if not added,show add participant option
                 */

                DatabaseReference dref=FirebaseDatabase.getInstance().getReference("Groups");
                dref.child(groupId).child("Participants").child(userId)
                        //.addValueEventListener(new ValueEventListener() {
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()){
                                    // user exists/participant
                                    try {
                                        String hisPreviousRole=""+dataSnapshot.child("role").getValue();

                                        //options to display in dialog
                                        String[] options;

                                        AlertDialog.Builder builder=new AlertDialog.Builder(context);
                                        builder.setTitle("Choose option");
                                        if (myGroupRole.equals("creator")){

                                            if (hisPreviousRole.equals("admin")){
                                                //in creator,he is admin
                                                options=new String[]{"Dismiss as admin","Remove user"};
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //handle item clicks
                                                        if (which==0){
                                                            //remove admin clicked
                                                            removeAdmin(modelUser);
                                                        }
                                                        else {
                                                            //remove user clicked
                                                            removeParticipant(modelUser);
                                                        }
                                                    }
                                                }).show();
                                            }
                                            else if (hisPreviousRole.equals("participant")){
                                                // in creator he is participant
                                                options=new String[]{"Make group admin","Remove user"};

                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //handle item clicks
                                                        if (which==0){
                                                            //Make Admin clicked
                                                            makeAdmin(modelUser);
                                                        }
                                                        else {
                                                            //remove user clicked
                                                            removeParticipant(modelUser);
                                                        }
                                                    }
                                                }).show();
                                            }
                                        }
                                        else if (myGroupRole.equals("admin")){
                                            if (hisPreviousRole.equals("creator")){
                                                //in admin,he is creator
                                                Toast.makeText(context, "Creator of group", Toast.LENGTH_SHORT).show();
                                            }
                                            else if (hisPreviousRole.equals("admin")){
                                                //in admin,he is admin too
                                                options=new String[]{"Dismiss as admin","Remove user"};
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //handle item clicks
                                                        if (which==0){
                                                            //remove admin clicked
                                                            removeAdmin(modelUser);
                                                        }
                                                        else {
                                                            //remove user clicked
                                                            removeParticipant(modelUser);
                                                        }
                                                    }
                                                }).show();
                                            }
                                            else if (hisPreviousRole.equals("participant")){
                                                //in admin,he is participant
                                                options=new String[]{"Make group admin","Remove user"};

                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //handle item clicks
                                                        if (which==0){
                                                            //Make Admin clicked
                                                            makeAdmin(modelUser);
                                                        }
                                                        else {
                                                            //remove user clicked
                                                            removeParticipant(modelUser);
                                                        }
                                                    }
                                                }).show();
                                            }

                                        }
                                    }
                                    catch (Exception e){
                                        Toast.makeText(context, "Something went wrong..", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }
                                else {
                                    // user does not exists/not-participant : Add
                                    try {
                                        AlertDialog.Builder builder=new AlertDialog.Builder(context);
                                        builder.setTitle("Add participant")
                                                .setMessage("Add this user in this group")
                                                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //add user
                                                        addParticipant(modelUser);
                                                    }
                                                })
                                                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).show();

                                    }
                                    catch (Exception e){
                                        Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });


    }

    //private void removeAdmin(FirebaseUserModel modelUser) {
    //private void removeAdmin(UserModel modelUser) {

    private void removeAdmin(FirebaseUserModel modelUser) {

        //setup data-remove admin-just change role

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("role","participant"); //roles are: participant/admin/creator
        //update role in db
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        //ref.child(groupId).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
        ref.child(groupId).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //remove as admin make participant
                        Toast.makeText(context, "The user is no longer admin", Toast.LENGTH_SHORT).show();
                        return;
                        //context.startActivity(new Intent(context, GroupChattingActivity.class));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed in removing as a admin
                        Toast.makeText(context, "failed in removing as a admin", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //private void removeParticipant(FirebaseUserModel modelUser) {
    //private void removeParticipant(UserModel modelUser) {
    private void removeParticipant(FirebaseUserModel modelUser) {
        //remove participant from group

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        //ref.child(groupId).child("Participants").child(String.valueOf(modelUser.getUserid())).removeValue()
        ref.child(groupId).child("Participants").child(modelUser.getId()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //removed successfully
                        Toast.makeText(context, "Participant removed successfully", Toast.LENGTH_SHORT).show();
                        //context.startActivity(new Intent(context, GroupChattingActivity.class));

                        Bundle chatBundle = new Bundle();

                        chatBundle.putString("groupId",groupId);

                        Log.e("groupInfo","groupInfo chat"+groupId);


                        Utility.launchActivity(context, GroupInfoActivity.class, false, chatBundle);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed removing participant
                        Toast.makeText(context, "Failed in removing participant", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //private void makeAdmin(FirebaseUserModel modelUser) {
    //private void makeAdmin(UserModel modelUser) {
    private void makeAdmin(FirebaseUserModel modelUser) {

        //setup data - change role

        String timeStamp;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

        timeStamp=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("role","admin"); //roles are: participant/admin/creator
        //update role in db
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                //ref.child(groupId).child("Participants").child(String.valueOf(modelUser.getUserid())).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //make admin
                        Toast.makeText(context, "The user is now admin", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed making admin
                        Toast.makeText(context, "failed making admin", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //private void addParticipant(FirebaseUserModel modelUser) {
    //private void addParticipant(UserModel modelUser) {
    private void addParticipant(FirebaseUserModel modelUser) {
        //setup data - add user in group
        String timeStamp;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

        SimpleDateFormat clearTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
        String clearTimeStamp=clearTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM

        //System.out.println( dateTime1.format(cal.getTime()) );//prints current date with time ex: 03/05/2020 11:30 PM
        timeStamp=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("uid",modelUser.getId());
        hashMap.put("onlineUserId",String.valueOf(modelUser.getOnlineUserId()));
        hashMap.put("role","participant");
        hashMap.put("timeStamp",timeStamp);
        hashMap.put("clearDate",clearTimeStamp);

        //add that user in Group>groupId>Participants
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        //ref.child(groupId).child("Participants").child(modelUser.getId()).setValue(hashMap)
        ref.child(groupId).child("Participants").child(modelUser.getId()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // added successfully
                        Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed in adding participant", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void showCustomDialogForAdminUser(FirebaseUserModel modelUser){

        // this.correct = correct;

        final Dialog resultbox = new Dialog(context);
        resultbox.setContentView(R.layout.dialog_admin);

        // resultbox.setCanceledOnTouchOutside(false);

        TextView btn_removeUser = (TextView) resultbox.findViewById(R.id.btn_removeUser);
        //Button btn_finish = (Button) resultbox.findViewById(R.id.btn_finish);

        TextView btn_dismissAsAdmin = (TextView) resultbox.findViewById(R.id.btn_dismissAsAdmin);
        //Button btn_resume = (Button) resultbox.findViewById(R.id.btn_resume);

        btn_removeUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                resultbox.cancel();

                //removeParticipant(modelUser);
            }
        });

        btn_dismissAsAdmin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                resultbox.cancel();

                //removeAdmin(modelUser);

            }
        });

        resultbox.show();
    }


    private void showCustomDialogForParticipant(FirebaseUserModel modelUser){

        // this.correct = correct;

        final Dialog resultbox = new Dialog(context);
        resultbox.setContentView(R.layout.dialog_participant);

        // resultbox.setCanceledOnTouchOutside(false);

        TextView btn_removeParticipant = (TextView) resultbox.findViewById(R.id.btn_removeParticipant);
        //Button btn_finish = (Button) resultbox.findViewById(R.id.btn_finish);

        TextView btn_makeGroupAdmin = (TextView) resultbox.findViewById(R.id.btn_makeGroupAdmin);
        //Button btn_resume = (Button) resultbox.findViewById(R.id.btn_resume);

        btn_removeParticipant.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                resultbox.cancel();

                //removeParticipant(modelUser);
            }
        });

        btn_makeGroupAdmin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                resultbox.cancel();

                //makeAdmin(modelUser);

            }
        });

        resultbox.show();
    }


    //private void checkIfAlreadyExists(FirebaseUserModel modelUser, ParticipantHolder holder) {
    //private void checkIfAlreadyExists(UserModel modelUser, ParticipantHolder holder) {
    private void checkIfAlreadyExists(FirebaseUserModel modelUser, ParticipantHolder holder) {

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        //ref.child(groupId).child("Participants").child(String.valueOf(modelUser.getUserid()))
        ref.child(groupId).child("Participants").child(modelUser.getId())
                .addValueEventListener(new ValueEventListener() {
                //.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //already exists
                        String hisRole=""+dataSnapshot.child("role").getValue();

                        //holder.tv_personRole.setText(hisRole);

                        if (hisRole.equals("admin")){
                            holder.tv_personRole.setVisibility(View.VISIBLE);
                            holder.tv_personRole.setText(hisRole);
                        }
                        else if (hisRole.equals("creator")){
                            holder.tv_personRole.setVisibility(View.VISIBLE);
                            holder.tv_personRole.setText(hisRole);
                        }
                        else if (hisRole.equals("participant")){
                            holder.tv_personRole.setVisibility(View.VISIBLE);
                            holder.tv_personRole.setText(hisRole);
                        }
                        else {
                            holder.tv_personRole.setVisibility(View.INVISIBLE);
                            //holder.tv_personRole.setText(hisRole);
                        }


                        Log.e("role",hisRole);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //doesnot exists
                        holder.tv_personAbout.setText("");
                    }
                });
    }

    public interface ItemClickListener {
        void onTrainerItemClick(View view, int position);
    }

    @Override
    public int getItemCount() {
        return userList.size();
        //return userModelList.size();
    }

    class ParticipantHolder extends RecyclerView.ViewHolder{

        TextView tv_participant_name,tv_personRole,tv_personAbout;
        CircleImageView iv_participant;

        public ParticipantHolder(@NonNull View itemView) {
            super(itemView);

            iv_participant=itemView.findViewById(R.id.iv_participant);
            tv_participant_name=itemView.findViewById(R.id.tv_participant_name);
            tv_personRole =itemView.findViewById(R.id.tv_personRole);
            tv_personAbout =itemView.findViewById(R.id.tv_personAbout);

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