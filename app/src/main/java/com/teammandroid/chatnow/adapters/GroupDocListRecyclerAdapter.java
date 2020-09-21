package com.teammandroid.chatnow.adapters;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;


import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.models.firebase.FirebaseGroupChatModel;
import com.teammandroid.chatnow.models.firebase.FirebaseMsgModel;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.Utility;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GroupDocListRecyclerAdapter extends RecyclerView.Adapter<GroupDocListRecyclerAdapter.MyViewHolder> {
    final String TAG = GroupDocListRecyclerAdapter.class.getSimpleName();

    private Activity context;

    ArrayList<FirebaseGroupChatModel> list;

    //PrefManager prefManager;

    String role_id = "";

    FirebaseUser firebaseUser;

    private GroupDocListRecyclerAdapter.ItemClickListener itemClickListener;

    public GroupDocListRecyclerAdapter(Activity context, ArrayList<FirebaseGroupChatModel> list, GroupDocListRecyclerAdapter.ItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.itemClickListener = itemClickListener;

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_peoples, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_documents_list, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;

    }

    public void filterList(ArrayList<FirebaseGroupChatModel> filterdNames) {
        this.list = filterdNames;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final FirebaseGroupChatModel item = list.get(position);

        //1 img //2 gif //3 audio //4 video //5 document //6 url // 7 text

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        //holder.tv_Date.setText(item.getMsgTime());
        //holder.tv_Date.setText(new SimpleDateFormat("dd/MM/yyyy").format(item.getMsgTime()));

        String msgDate=item.getMsgTime();
        //holder.tv_Date.setText(new SimpleDateFormat("dd/MM/yyyy").format(msgDate));
        holder.tv_Date.setText(item.getMsgDate());

        holder.tv_fileName.setText(item.getMessage());

        String filename=item.getMessage();

        String extension= Utility.getFileExtension(filename);

        holder.tv_fileExtension.setText(extension.toUpperCase());




        switch (item.getMediatype()){
            case 5:
                try {
                    File directory;
                    if (item.getSender().equals(firebaseUser.getUid())) {
                        directory = new File(Environment.getExternalStorageDirectory() + "/" +Constants.OFFLINE_DOCUMENT_PATH + "Sent/" + item.getMessage());
                    } else {
                        directory = new File(Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_DOCUMENT_PATH + item.getMessage());
                    }
                    int file_size = Integer.parseInt(String.valueOf(directory.length()/1024));
                    holder.tv_size.setText(String.valueOf(file_size)+" kb");

                    Log.e("groupDoc","groupDoc"+item.getMessage()+" "+String.valueOf(file_size)+" "+item.getMsgTime());

                }catch (Exception e){
                    Log.e(TAG, "bindValues: image except "+e.getMessage() );
                }

                break;

        }

    }

    public String getFileExtension(String filename){
        String extension = "";

        int i = filename.lastIndexOf('.');
        int p = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));

        if (i > p) {
            extension = filename.substring(i+1);
        }
        return extension;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_fileName;
        TextView tv_size;
        TextView tv_Date;
        TextView tv_fileExtension;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            tv_fileName = itemView.findViewById(R.id.tv_fileName);
            tv_size = itemView.findViewById(R.id.tv_size);
            tv_Date = itemView.findViewById(R.id.tv_Date);
            tv_fileExtension = itemView.findViewById(R.id.tv_fileExtension);


            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null)
                        itemClickListener.onClick(v, getAdapterPosition());
                }
            });

        }
    }
}


