package com.teammandroid.chatnow.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.models.NotificationModel;
import com.teammandroid.chatnow.models.firebase.FirebaseMsgModel;
import com.teammandroid.chatnow.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;


public class LinkListRecyclerAdapter extends RecyclerView.Adapter<LinkListRecyclerAdapter.MyViewHolder> {

    final String TAG = LinkListRecyclerAdapter.class.getSimpleName();

    private Activity context;

    Context mContext;

    ArrayList<FirebaseMsgModel> list;

    //PrefManager prefManager;

    String role_id = "";

    private ItemClickListener itemClickListener;

    /*public LinkListRecyclerAdapter(Activity context, ArrayList<NotificationModel> list) {
        this.context = context;
        this.list = list;
    }

    public LinkListRecyclerAdapter(Activity context, ArrayList<NotificationModel> list, LinkListRecyclerAdapter.ItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.itemClickListener = itemClickListener;
    }*/

    public LinkListRecyclerAdapter(Context context, ArrayList<FirebaseMsgModel> list) {
        this.mContext = context;
        this.list = list;
    }

    public LinkListRecyclerAdapter(Context context, ArrayList<FirebaseMsgModel> list, ItemClickListener itemClickListener) {
        this.mContext = context;
        this.list = list;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_peoples, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_link_list, parent, false);
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


        switch (item.getMediatype()) {

            case 6:
                try {
                    holder.richLinkView.setLink(item.getMessage(), new ViewListener() {

                        @Override
                        public void onSuccess(boolean status) {
                            Log.e("lll :", "hi");
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("lll :", "error");
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, "bindValues: glide e " + e.getMessage());
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

        //ImageView iv_media_image;

        RichLinkView richLinkView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //iv_media_image = itemView.findViewById(R.id.iv_media_image);

            richLinkView=itemView.findViewById(R.id.richLinkView);

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

