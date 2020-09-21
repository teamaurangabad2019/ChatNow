package com.teammandroid.chatnow.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.models.NotificationModel;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseMsgModel;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import java.io.File;
import java.util.ArrayList;


public class DocumentListRecyclerAdapter extends RecyclerView.Adapter<DocumentListRecyclerAdapter.MyViewHolder> {

    final String TAG = DocumentListRecyclerAdapter.class.getSimpleName();

    private Activity context;

    ArrayList<FirebaseMsgModel> list;

    //PrefManager prefManager;

    String role_id = "";

    UserModel currentUser;

    private DocumentListRecyclerAdapter.ItemClickListener itemClickListener;

    public DocumentListRecyclerAdapter(Activity context, ArrayList<FirebaseMsgModel> list, DocumentListRecyclerAdapter.ItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.itemClickListener = itemClickListener;

        SessionManager sessionManager = new SessionManager(context);
        currentUser = sessionManager.getUserDetails();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_peoples, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_documents_list, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;

    }

    public void filterList(ArrayList<FirebaseMsgModel> filterdNames) {
        this.list = filterdNames;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final FirebaseMsgModel item = list.get(position);

            //1 img //2 gif //3 audio //4 video //5 document //6 url // 7 text

        //holder.tv_Date.setText(item.getMsgtime());
        String msgDate=item.getMsgtime();

        msgDate=msgDate.substring(0,10);

        holder.tv_Date.setText(msgDate);

        holder.tv_fileName.setText(item.getMessage());
        String filename=item.getMessage();

        String extension= Utility.getFileExtension(filename);

        holder.tv_fileExtension.setText(extension.toUpperCase());

            switch (item.getMediatype()){
                case 5:
                    try {
                        File directory;
                        if (item.getSenderid() == currentUser.getUserid()) {
                            directory = new File(Environment.getExternalStorageDirectory() + "/" +Constants.OFFLINE_DOCUMENT_PATH + "Sent/" + item.getMessage());

                            Log.e("groupDoc","groupDoc 1");

                        } else {
                            directory = new File(Environment.getExternalStorageDirectory() + "/" +Constants.OFFLINE_DOCUMENT_PATH + item.getMessage());

                            Log.e("groupDoc","groupDoc 2");

                        }
                        int file_size = Integer.parseInt(String.valueOf(directory.length()/1024));
                        holder.tv_size.setText(String.valueOf(file_size)+" kb");

                        Log.e("groupDoc","groupDoc"+String.valueOf(file_size));


                    }catch (Exception e){
                        Log.e(TAG, "bindValues: image except "+e.getMessage() );
                    }

                    break;

        }

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

