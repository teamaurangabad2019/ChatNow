package com.teammandroid.chatnow.adapters;


import android.app.Activity;
import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.teammandroid.chatnow.R;

import com.teammandroid.chatnow.models.Contacts;

import java.util.ArrayList;
import java.util.List;

public class InviteContactsRecyclerAdapter extends RecyclerView.Adapter<InviteContactsRecyclerAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    public List<Contacts> cont;
    Contacts list;
    private ArrayList<Contacts> arraylist;
    boolean checked = false;
    View vv;

    //Context context;

    Activity context;

    private ItemClickListener itemClickListener;


    public InviteContactsRecyclerAdapter(LayoutInflater inflater, List<Contacts> items,Activity context) {
        this.layoutInflater = inflater;
        this.cont = items;
        this.arraylist = new ArrayList<Contacts>();
        this.arraylist.addAll(cont);
        this.context=context;
    }


    public InviteContactsRecyclerAdapter(Activity context, ArrayList<Contacts> list, ItemClickListener itemClickListener){
        this.context = context;
        this.arraylist = list;
        this.itemClickListener=itemClickListener;
    }

    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.item_contactlist, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        list = cont.get(position);
        String name = (list.getName());

        holder.title.setText(name);
        holder.phone.setText(list.getPhone());


        holder.btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mobileNo=holder.phone.getText().toString();

                Log.e("model", "hello.."+mobileNo);

                sendSMS(mobileNo);

            }
        });
    }



    @Override
    public int getItemCount() {
        return cont.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView title;
        public TextView phone;
        public LinearLayout contact_select_layout;

        public Button btn_invite;

        public ViewHolder(View itemView) {
            super(itemView);
            this.setIsRecyclable(false);
            title = (TextView) itemView.findViewById(R.id.tv_contactName);
            phone = (TextView) itemView.findViewById(R.id.tv_contactNumber);
            contact_select_layout = (LinearLayout) itemView.findViewById(R.id.contact_select_layout);
            btn_invite=(Button)itemView.findViewById(R.id.btn_invite);

            itemView.setOnClickListener(this); // bind the listener

        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) itemClickListener.onClick(v, getAdapterPosition());
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    private void sendSMS(String mobileNo) {

        try
        {

            //Get the SmsManager instance and call the sendTextMessage method to send message

            SmsManager sms = SmsManager.getDefault();

            String message="Download the app "+"\t"+"https://www.google.com";

            sms.sendTextMessage(mobileNo, null, message, null, null);

            Log.e("success", "sent..");

            Toast.makeText(context, "Invitation Sent successfully!",
                    Toast.LENGTH_LONG).show();


        }
        catch (Exception e){

            Log.e("failed", "sending failed..");

            Toast.makeText(context,
                    "SMS faild, please try again later!",
                    Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }

    }

}

