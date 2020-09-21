package com.teammandroid.chatnow.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.adapters.InviteContactsRecyclerAdapter;
import com.teammandroid.chatnow.models.Contacts;

import java.util.ArrayList;


public class InviteContactsActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<Contacts> selectUsers;
    private RecyclerView rv_contact_list;
    private RecyclerView.LayoutManager layoutManager;

    InviteContactsRecyclerAdapter inviteContactsRecyclerAdapter;
    Cursor phones;

    ImageView iv_back;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_contacts);

        bindView();
        btnListener();


        rv_contact_list.setHasFixedSize(true);

        rv_contact_list.setLayoutManager(layoutManager);

        rv_contact_list.setItemAnimator(new DefaultItemAnimator());

        rv_contact_list.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        selectUsers = new ArrayList<Contacts>();

        showContacts();

    }

    private void bindView() {
        iv_back=findViewById(R.id.viewMenuIconBack);

        rv_contact_list = (RecyclerView) findViewById(R.id.rv_contacts_list);
    }

    private void btnListener() {
        iv_back.setOnClickListener(this);

    }

    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            phones = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            LoadContact loadContact = new LoadContact();
            loadContact.execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.viewMenuIconBack:
               onBackPressed();
                break;
        }
    }

    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

            if (phones != null) {
                Log.e("count", "" + phones.getCount());
                if (phones.getCount() == 0) {

                }

                while (phones.moveToNext()) {
                    Bitmap bit_thumb = null;
                    String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                    Contacts selectUser = new Contacts();
                    selectUser.setName(name);
                    selectUser.setPhone(phoneNumber);
                    selectUsers.add(selectUser);


                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // sortContacts();
            int count=selectUsers.size();
            ArrayList<Contacts> removed=new ArrayList<>();
            ArrayList<Contacts> contacts=new ArrayList<>();
            for(int i=0;i<selectUsers.size();i++){
                Contacts inviteFriendsProjo = selectUsers.get(i);

                if(inviteFriendsProjo.getName().matches("\\d+(?:\\.\\d+)?")||inviteFriendsProjo.getName().trim().length()==0){
                    removed.add(inviteFriendsProjo);
                    Log.d("Removed Contact",new Gson().toJson(inviteFriendsProjo));
                }else{
                    contacts.add(inviteFriendsProjo);
                }
            }
            contacts.addAll(removed);
            selectUsers=contacts;
            //inviteContactsRecyclerAdapter = new InviteContactsRecyclerAdapter(inflater, selectUsers);
            inviteContactsRecyclerAdapter = new InviteContactsRecyclerAdapter(inflater, selectUsers,InviteContactsActivity.this);
            rv_contact_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            rv_contact_list.setAdapter(inviteContactsRecyclerAdapter);

        }
    }

}

