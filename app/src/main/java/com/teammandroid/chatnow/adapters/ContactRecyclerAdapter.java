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

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;
import com.teammandroid.chatnow.utils.Constants;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.RxContacts.Contact;
import com.wafflecopter.multicontactpicker.RxContacts.PhoneNumber;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/*
public class PeopleRecyclerAdapter {
}
*/


public class ContactRecyclerAdapter extends RecyclerView.Adapter<ContactRecyclerAdapter.MyViewHolder> {
    final String TAG = ChatsRecyclerAdapter.class.getSimpleName();
    private Context context;
    int isSent;

    ArrayList<ContactResult> list;
    //PrefManager prefManager;


    private ContactRecyclerAdapter.ItemClickListener itemClickListener;

    public ContactRecyclerAdapter(Context context, ArrayList<ContactResult> list, int isSent, ItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.isSent = isSent;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_preview, parent, false);
        ContactRecyclerAdapter.MyViewHolder viewHolder = new ContactRecyclerAdapter.MyViewHolder(view);

        return viewHolder;
    }

    public void filterList(ArrayList<ContactResult> filterdNames) {
        this.list = filterdNames;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ContactResult item = list.get(position);
        holder.civ_contact.setImageURI(item.getPhoto());
        String mobile = "";

        for (PhoneNumber phoneNumber :item.getPhoneNumbers()) {
            mobile = mobile+phoneNumber.getNumber()+"\n";
        }
        holder.tv_mobile.setText(String.valueOf(mobile));
        holder.tv_name.setText(String.valueOf(item.getDisplayName()));

        if (isSent==1){
            holder.tv_add.setVisibility(View.VISIBLE);
        }else {
            holder.tv_add.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ItemClickListener {
        void onAddItemClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civ_contact;
        TextView tv_name;
        TextView tv_add;
        TextView tv_mobile;
        TextView tv_mobileTxt;
        LinearLayout ll_root;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            civ_contact = itemView.findViewById(R.id.civ_contact);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_add = itemView.findViewById(R.id.tv_add);
            tv_mobile = itemView.findViewById(R.id.tv_mobile);
            tv_mobileTxt = itemView.findViewById(R.id.tv_mobileTxt);
            ll_root = itemView.findViewById(R.id.ll_root);

            tv_add.setClickable(true);
            tv_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null)
                        itemClickListener.onAddItemClick(v, getAdapterPosition());
                }
            });
        }
    }
}

