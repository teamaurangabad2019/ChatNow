package com.teammandroid.chatnow.activities.preview_activity;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.adapters.ContactRecyclerAdapter;
import com.teammandroid.chatnow.adapters.SentContactRecyclerAdapter;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseContactResult;
import com.teammandroid.chatnow.models.firebase.FirebasePhoneNumber;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.SessionManager;
import com.wafflecopter.multicontactpicker.ContactResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SentContactPreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = SentContactPreviewActivity.class.getSimpleName();
    Activity activity;
    private UserModel currentUser;
    Bundle bundle;
    private FirebaseUserModel offchattingPartner;
    ArrayList<FirebaseContactResult> contacts = new ArrayList<>();
    private int mediaType;

    ImageView viewMenuIconBack;
    TextView tv_toolbar;
    RecyclerView rv_contacts;
    FloatingActionButton fab_send;
    private SentContactRecyclerAdapter contactRecyclerAdapter;
    private int isSent;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_preview);

        activity = SentContactPreviewActivity.this;
        bindView();
        btnListener();

        SessionManager sessionManager = new SessionManager(activity);
        currentUser = sessionManager.getUserDetails();

        bundle = getIntent().getExtras();
        if (bundle != null) {
            //  filePathString  = bundle.getString("filePath");
            offchattingPartner = bundle.getParcelable("offchattingPartner");
            contacts = bundle.getParcelableArrayList("contacts");
            mediaType = bundle.getInt("mediaType");
            Log.e(TAG, "onCreate: contact "+contacts.toString() );
            isSent = bundle.getInt("isSent");
        } else {
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

        BindContactList();

    }

    private void BindContactList() {
        rv_contacts.setHasFixedSize(true);
        Log.e(TAG, "BindContactList: contact "+contacts.toString() );
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        rv_contacts.setLayoutManager(mLayoutManager);
        rv_contacts.setItemAnimator(new DefaultItemAnimator());
        rv_contacts.addItemDecoration(new DividerItemDecoration(activity,
                DividerItemDecoration.VERTICAL));
        contactRecyclerAdapter=new SentContactRecyclerAdapter(activity, contacts,isSent, new SentContactRecyclerAdapter.ItemClickListener() {
            @Override
            public void onAddItemClick(View view, int position) {
               // addContactToPhoneBook(contacts.get(position));
                insertContact(contacts.get(position));
            }
        });
        rv_contacts.setAdapter(contactRecyclerAdapter);
    }

    private void insertContact(FirebaseContactResult firebaseContactResult) {
        String email1 = "";
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        ArrayList<ContentValues> data = new ArrayList<ContentValues>();

        try {
            for (String email : firebaseContactResult.getEmails()) {
                intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email1);
            }
        }catch (Exception e){

        }

//Filling data with phone numbers
       // for (int i = 0; i < numberOfPhones; i++) {
            for (FirebasePhoneNumber number : firebaseContactResult.getPhoneNumbers()) {

            ContentValues row = new ContentValues();
            row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            row.put(ContactsContract.CommonDataKinds.Phone.NUMBER, number.getNumber());
            row.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            data.add(row);
        }

        intent.putExtra(ContactsContract.Intents.Insert.NAME, firebaseContactResult.getDisplayName());
        intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);
        startActivity(intent);
    }


    private void addContactToPhoneBook(FirebaseContactResult firebaseContactResult) {

        String DisplayName = firebaseContactResult.getDisplayName();
        String MobileNumber = "";
        String HomeNumber = "";
        String WorkNumber = "";
        String emailID = "";
        String company = "";
        String jobTitle = "";

        for (FirebasePhoneNumber number :firebaseContactResult.getPhoneNumbers() ) {
            if (number.getTypeLabel().equals("Mobile")){
                MobileNumber = number.getNumber();
            }else if(number.getTypeLabel().equals("Custom")){
                MobileNumber = number.getNumber();
            }else if(number.getTypeLabel().equals("Work")){
                WorkNumber = number.getNumber();
            }else {
                HomeNumber = number.getNumber();
            }
        }

        if (firebaseContactResult.getEmails()!=null){
           // emailID = firebaseContactResult.getEmails().get(0);
        }

        ArrayList <ContentProviderOperation> ops = new ArrayList < ContentProviderOperation > ();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Names
        if (DisplayName != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            DisplayName).build());
        }

        //------------------------------------------------------ Mobile Number
        if (MobileNumber != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        //------------------------------------------------------ Home Numbers
        if (HomeNumber != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, HomeNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                    .build());
        }

        //------------------------------------------------------ Work Numbers
        if (WorkNumber != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, WorkNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                    .build());
        }

        //------------------------------------------------------ Email
        if (emailID != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }

        //------------------------------------------------------ Organization
        if (!company.equals("") && !jobTitle.equals("")) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .build());
        }

        // Asking the Contact provider to create a new contact
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void btnListener() {
        viewMenuIconBack.setOnClickListener(this);
        fab_send.setOnClickListener(this);
    }

    private void bindView() {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        viewMenuIconBack = findViewById(R.id.viewMenuIconBack);
        tv_toolbar = findViewById(R.id.tv_toolbar);
        rv_contacts = findViewById(R.id.rv_contacts);
        fab_send = findViewById(R.id.fab_send);
        fab_send.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_send:
               // sendNotification(contacts);
                break;

            case R.id.viewMenuIconBack:
                onBackPressed();
                break;
        }
    }

    private void sendNotification(ArrayList<FirebaseContactResult> contacts) {

        int mediatype = 8;
        int isDownloaded = 0;
        String msgString = "";
        if (contacts.size()>1){
            msgString = contacts.get(0).getDisplayName();
        }else {
            msgString =  contacts.get(0).getDisplayName()+" "+(contacts.size()-1)+"  More";
        }

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
        //System.out.println( dateTime1.format(cal.getTime()) );//prints current date with time ex: 03/05/2020 11:30 PM
        String msgTime=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM
        HashMap<String,Object> hashMap=new HashMap<>();
        //to generate unique random message uid
        // Get the size n
        int n = 7;
        String msgUid=firebaseUser.getUid()+getAlphaNumericString(n);
        Log.e("msgUid",""+msgUid);
        hashMap.put("messageid",msgUid);
        hashMap.put("senderid",currentUser.getUserid());
        hashMap.put("receiverid",Integer.parseInt(offchattingPartner.getOnlineUserId()));
        hashMap.put("message",msgString);
        hashMap.put("msgtime",msgTime);// message time
        hashMap.put("isread",0);
        hashMap.put("isdownloaded",1);
        hashMap.put("mediatype",mediatype);
        hashMap.put("isdeleted",0);
        hashMap.put("type",0);
        hashMap.put("replyToMsgId","-1");
        hashMap.put("replyToMsgSenderId",-1);
        hashMap.put("replyToMsgText","");
        hashMap.put("replyToMsgTextMediaType",-1);
        hashMap.put("contacts",contacts);

        databaseReference.child("Chats").child(msgUid).setValue(hashMap);

        String to = offchattingPartner.getToken();
        //  String to = "ftU0PTXttbk:APA91bFHUGKhBdDABO-cLoeI-2bNcqRg58yO79po2132Rkx0UpSAQiZMI5_zYBPDkllNe4skr0YxjdJp8ayD5haZen1-YAASnbJM8nyTbhWyAXhkXbjK7DKV9Twpl_XmDuknV2Bkdnc6";
        Log.e(TAG, "onResponse: " + to);
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();

        try {
            notifcationBody.put("title", currentUser.getFullname());
            notifcationBody.put("message",msgString );
            notifcationBody.put("senderid", currentUser.getUserid());
            notifcationBody.put("mediatype", mediatype);
            notifcationBody.put("serverid", 0);
            notifcationBody.put("created", dateTime);
            notifcationBody.put("replyToMsgId", -1);
            notifcationBody.put("replyToMsgSenderId", -1);
            notifcationBody.put("replyToMsgText", " ");
            notifcationBody.put("replyToMsgTextMediaType", -1);
            notification.put("to", to);
            notification.put("data", notifcationBody);

        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
        }

        try {
            Map<String, String> params = new HashMap<>();
            params.put("Authorization", Constants.GET_SERVER_KEY);
            params.put("Content-Type", "application/json");

            final int finalMediatype = mediatype;
            Log.e(TAG, "sendNotification: mediatype" + mediatype);
            final int finalIsDownloaded = isDownloaded;
            AndroidNetworking.post(Constants.FCM_API)
                    .addJSONObjectBody(notification)
                    .addHeaders(params)
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                        @Override
                        public void onError(ANError anError) {
                            Log.e("notification:anError", "" + anError);
                            Log.e("notification:anError", "" + anError.getErrorBody());
                        }

                    });
        } catch (Exception ex) {
            Log.e("onUnknownError", "" + ex);
        }
    }

    static String getAlphaNumericString(int n)
    {
        // chose a Character random from this String    "baQfVCfUSzS0XHNUPtGtqWmlPFt1jmyhkh3"
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
}
