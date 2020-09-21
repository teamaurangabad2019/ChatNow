package com.teammandroid.chatnow.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.activities.PrivacySettingsActivity;
import com.teammandroid.chatnow.activities.SettingsActivity;

import com.teammandroid.chatnow.models.SettingsModel;

import java.util.ArrayList;

public class SettingsRecyclerAdapter extends RecyclerView.Adapter<SettingsRecyclerAdapter.MyViewHolder> {

    //private Context context;

    Activity context;

    ArrayList<SettingsModel> list;

    //PrefManager prefManager;

    String role_id = "";


    private ItemClickListener itemClickListener;

    public SettingsRecyclerAdapter(Activity context, ArrayList<SettingsModel> list) {
        this.context = context;
        this.list = list;

    }

    public SettingsRecyclerAdapter(Activity context, ArrayList<SettingsModel> list, ItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.itemClickListener=itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_peoples, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_settings, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;


    }

    public void filterList(ArrayList<SettingsModel> filterdNames) {
        this.list = filterdNames;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final SettingsModel item = list.get(position);

        holder.tv_settingsName.setText(item.getSettingsName());

        //holder.tv_settingsDescription.setText(item.getSettingsDescription());

        String desc=item.getSettingsDescription();

        if (desc.isEmpty()){
            holder.tv_settingsDescription.setVisibility(View.GONE);
        }
        else{
            holder.tv_settingsDescription.setText(item.getSettingsDescription());
        }

        String title=holder.tv_settingsName.getText().toString();

        if (title.equals("Show typing")){
            holder.ll_checkbox.setVisibility(View.VISIBLE);
        }

        else if (title.equals("Hide keyboard on Sent")){
            holder.ll_checkbox.setVisibility(View.VISIBLE);
        }

        else if (title.equals("Show icon in status bar")){
            holder.ll_checkbox.setVisibility(View.VISIBLE);
        }

        else if (title.equals("Notify me when new message arrive")){
            holder.ll_checkbox.setVisibility(View.VISIBLE);
        }

        else if (title.equals("Enable Vibration")){
            holder.ll_checkbox.setVisibility(View.VISIBLE);
        }

        else if (title.equals("Daily bonus notification")){
            holder.ll_checkbox.setVisibility(View.VISIBLE);
        }

        else if (title.equals("Disable Broadcast")){
            holder.ll_checkbox.setVisibility(View.VISIBLE);
        }

        else if (title.equals("Use points automatically")){
            holder.ll_checkbox.setVisibility(View.VISIBLE);
        }
        else {
            holder.ll_checkbox.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String settingName=holder.tv_settingsName.getText().toString();

                Toast.makeText(context,settingName , Toast.LENGTH_SHORT).show();

                if (settingName.equals("Unit of length")) {

                    openDialog1(holder.tv_settingsDescription);
                }

                if (settingName.equals("Clear Chat History")){
                    showCustomDialogForClearChatHistory();
                }

                if (settingName.equals("Privacy settings")){
                    context.startActivity(new Intent(context, PrivacySettingsActivity.class));
                }


                if (settingName.equals("Suggestions & Feedback")){

                    sendEmail();
                }

            }
        });

    }

    public void sendEmail(){

        //Toast.makeText(context, "Send EMail", Toast.LENGTH_SHORT).show();
        String subject="Hi";
        String message="how are you?";

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","email@email.com", null));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        context.startActivity(Intent.createChooser(intent, "Choose an Email client :"));


    }

    public void openDialog1(final TextView txt_desc) {

        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom);
        TextView tv_kilometers = (TextView) dialog.findViewById(R.id.tv_kilometers);

        // set the custom dialog components - text, image and button
        TextView tv_miles = (TextView) dialog.findViewById(R.id.tv_miles);

        tv_miles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(context, "miles..", Toast.LENGTH_SHORT).show();

                txt_desc.setText("Miles");

                dialog.dismiss();
            }
        });

        tv_kilometers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(context, "kilometers", Toast.LENGTH_SHORT).show();

                txt_desc.setText("Kilometers");

                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void showCustomDialogForClearChatHistory() {

        // this.correct = correct;
        final Dialog resultbox = new Dialog(context);
        resultbox.setContentView(R.layout.clear_chat_message_dialog);
        // resultbox.setCanceledOnTouchOutside(false);
        Button btn_finish = (Button) resultbox.findViewById(R.id.btn_finish);
        Button btn_resume = (Button) resultbox.findViewById(R.id.btn_resume);
        btn_finish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                resultbox.cancel();
                Toast.makeText(context, "chat history is deleted", Toast.LENGTH_SHORT).show();
                //addVacation();
                //Utility.launchActivity(AddVacationActivity.this, HomepageActivity.class,true);

            }
        });

        btn_resume.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                resultbox.cancel();
            }
        });

        resultbox.show();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView tv_settingsName;
        public TextView tv_settingsDescription;

        CheckBox checkBox;
        LinearLayout ll_checkbox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_settingsName = itemView.findViewById(R.id.tv_settingsName);
            tv_settingsDescription = itemView.findViewById(R.id.tv_settingsDescription);

            checkBox=itemView.findViewById(R.id.checkBox);

            ll_checkbox=itemView.findViewById(R.id.ll_checkbox);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (itemClickListener != null) itemClickListener.onClick(v, getAdapterPosition());
        }
    }

}

