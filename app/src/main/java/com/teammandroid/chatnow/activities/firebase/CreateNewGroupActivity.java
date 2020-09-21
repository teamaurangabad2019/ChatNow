package com.teammandroid.chatnow.activities.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.activities.HomeActivity;
import com.teammandroid.chatnow.adapters.firebase.AddParticipantAdapter;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseMsgModel;
import com.teammandroid.chatnow.network.UserServices;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class CreateNewGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG =AddGroupParticipantActivity.class.getSimpleName();

    private FirebaseAuth firebaseAuth;

    CircleImageView iv_groupPic;
    EditText et_groupDesc,et_groupTitle;
    FloatingActionButton btn_createGroup;

    ProgressDialog progressDialog;

    //permission constants
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;
    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE=300;
    private static final int IMAGE_PICK_GALLERY_CODE=400;

    //permission arrays
    private String[] cameraPermissions;
    private String[] storagePermissions;

    //picked image uri
    private Uri image_uri=null;
    private HashMap<String, Object> hashMap;

    RecyclerView rv_users;
    private AddParticipantAdapter addParticipantAdapter;

    Activity activity;
    private ProgressDialog dialog;

    private ArrayList<UserModel> selectedUsersList = new ArrayList<>();

    Bundle bundle;

    FirebaseUser firebaseUser;

    private UserModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);

        activity = CreateNewGroupActivity.this;

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        bundle=getIntent().getExtras();



        if (bundle != null) {
            selectedUsersList = bundle.getParcelableArrayList("SelectedUsersList");
            Log.e(TAG, "SelectedUsersList: "+selectedUsersList.toString());
        }


        bindView();
        btnListener();

        firebaseAuth=FirebaseAuth.getInstance();


        SessionManager sessionManager = new SessionManager(this);
        currentUser = sessionManager.getUserDetails();

        checkUser();


        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //init permission arrays
        cameraPermissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


    }

    private void btnListener() {
        btn_createGroup.setOnClickListener(this);
        iv_groupPic.setOnClickListener(this);
    }

    private void bindView() {
        btn_createGroup=findViewById(R.id.btn_createGroup);
        et_groupDesc=findViewById(R.id.et_groupDesc);
        et_groupTitle=findViewById(R.id.et_groupTitle);
        iv_groupPic=findViewById(R.id.iv_groupPic);

        rv_users=findViewById(R.id.rv_users);
    }

    public  void checkUser(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if (user!=null){

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                break;
            case R.id.iv_groupPic:

                showImagePickerDialog();

                break;

            case R.id.btn_createGroup:

                createGroup(selectedUsersList);

                break;
        }

    }

    //private void createGroup() {
    private void createGroup(ArrayList<UserModel> selectedUsersList) {

        String groupTitle=et_groupTitle.getText().toString().trim();
        String groupDescription=et_groupDesc.getText().toString().trim();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

        SimpleDateFormat clearTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
        String clearTimeStamp=clearTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM

        String groupTimeStamp=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM


        if (TextUtils.isEmpty(groupTitle)){
            Toast.makeText(this, "please enter group title", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Creating Group");
        progressDialog.show();

        //to generate unique random groupId
        // Get the size n
        int n = 15;
        String groupId=getAlphaNumericString(n);
        Log.e("groupId",""+groupId);


        if (image_uri==null){
            hashMap=new HashMap<>();
            hashMap.put("groupId",""+groupId);
            hashMap.put("groupTitle",""+groupTitle);
            hashMap.put("groupDescription",groupDescription);
            hashMap.put("groupIcon",""+"default");
            hashMap.put("timeStamp",""+groupTimeStamp);
            hashMap.put("createdBy",""+firebaseAuth.getUid());
        }
        else {
            hashMap=new HashMap<>();
            hashMap.put("groupId",""+groupId);
            hashMap.put("groupTitle",""+groupTitle);
            hashMap.put("groupDescription",groupDescription);
            hashMap.put("groupIcon",""+image_uri);
            hashMap.put("timeStamp",""+groupTimeStamp);
            hashMap.put("createdBy",""+firebaseAuth.getUid());
        }


        //create group
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Groups");

        //reference.child(groupTimeStamp).setValue(hashMap)
        reference.child(groupId).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //created Successfully
                        //progressDialog.dismiss();
                        //Toast.makeText(CreateNewGroupActivity.this, "Group Created Successfully..", Toast.LENGTH_SHORT).show();
                        //setup member info(add current user in group's partcipants list)



                        HashMap<String,Object> hashMap1=new HashMap<>();
                        hashMap1.put("uid",firebaseAuth.getUid());
                        hashMap1.put("role","creator");
                        hashMap1.put("timeStamp", groupTimeStamp);
                        hashMap1.put("clearDate", clearTimeStamp);

                        hashMap1.put("token",currentUser.getToken());

                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");

                        //ref.child(groupId).child("Participants").child(firebaseAuth.getUid())
                        ref.child(groupId).child("Participants").child(firebaseUser.getUid())
                                .setValue(hashMap1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {


                                        //addMultipleMembersInGroup(groupId,selectedUsersList);

                                        //if (selectedUsersList == null) {
                                        if (selectedUsersList.isEmpty()) {

                                            progressDialog.dismiss();
                                            Toast.makeText(CreateNewGroupActivity.this, "Group created Successfully..", Toast.LENGTH_SHORT).show();

                                            Utility.launchActivity(CreateNewGroupActivity.this, HomeActivity.class,true);
                                        }
                                        else {

                                            addMultipleMembersInGroup(groupId,selectedUsersList);


                                        }



                                        //participant added

                                        /*progressDialog.dismiss();
                                        Toast.makeText(CreateNewGroupActivity.this, "Group created Successfully..", Toast.LENGTH_SHORT).show();

                                        Utility.launchActivity(CreateNewGroupActivity.this, HomeActivity.class,true);*/

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        //failed adding participant
                                        progressDialog.dismiss();
                                        Toast.makeText(CreateNewGroupActivity.this, "failed adding participant", Toast.LENGTH_SHORT).show();
                                        //Toast.makeText(CreateNewGroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();


                                    }
                                });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed
                        progressDialog.dismiss();
                        Toast.makeText(CreateNewGroupActivity.this, "Failure while creating group", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(CreateNewGroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /*private void addMultipleMembersInGroup(String groupId, ArrayList<UserModel> selectedUsersList) {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

        SimpleDateFormat clearTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
        String clearTimeStamp=clearTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM

        String groupTimeStamp=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM


        if (selectedUsersList != null) {

            for (UserModel model : selectedUsersList) {

                //forwordMessage(UserId, partnerId, model);


*//*                DatabaseReference dref=FirebaseDatabase.getInstance().getReference("Groups");

                dref.child(groupId).child("Participants").orderByChild("uid").equalTo(model.getFirebaseuserid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    Log.e("groupLog","2");

                                }
                                else {

                                    Log.e("groupLog","3");

                                    HashMap<String,Object> hashMap1=new HashMap<>();
                                    hashMap1.put("uid",model.getFirebaseuserid());
                                    hashMap1.put("role","participant");
                                    hashMap1.put("timeStamp", groupTimeStamp);
                                    hashMap1.put("clearDate", clearTimeStamp);

                                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");

                                    ref.child(groupId).child("Participants").child(model.getFirebaseuserid())
                                            .setValue(hashMap1)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    //participant added

                                                    progressDialog.dismiss();
                                                    Toast.makeText(CreateNewGroupActivity.this, "Group created Successfully..", Toast.LENGTH_SHORT).show();

                                                    Utility.launchActivity(CreateNewGroupActivity.this, HomeActivity.class,true);

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    //failed adding participant
                                                    progressDialog.dismiss();

                                                    Toast.makeText(CreateNewGroupActivity.this, "failed adding participant", Toast.LENGTH_SHORT).show();
                                                    //Toast.makeText(CreateNewGroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();


                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });*//*


                if (model.getFirebaseuserid().equals(firebaseUser.getUid())){
                    Log.e("groupLog","1");

                }
                else {
                    DatabaseReference dref=FirebaseDatabase.getInstance().getReference("Groups");

                    dref.child(groupId).child("Participants").orderByChild("uid").equalTo(model.getFirebaseuserid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                       if (dataSnapshot.exists()){
                                           Log.e("groupLog","2");

                                       }
                                       else {

                                           Log.e("groupLog","3");

                                           HashMap<String,Object> hashMap1=new HashMap<>();
                                           hashMap1.put("uid",model.getFirebaseuserid());
                                           hashMap1.put("role","participant");
                                           hashMap1.put("timeStamp", groupTimeStamp);
                                           hashMap1.put("clearDate", clearTimeStamp);

                                           DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");

                                           ref.child(groupId).child("Participants").child(model.getFirebaseuserid())
                                                   .setValue(hashMap1)
                                                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                       @Override
                                                       public void onSuccess(Void aVoid) {

                                                           //participant added

                                                           progressDialog.dismiss();
                                                           Toast.makeText(CreateNewGroupActivity.this, "Group created Successfully..", Toast.LENGTH_SHORT).show();

                                                           Utility.launchActivity(CreateNewGroupActivity.this, HomeActivity.class,true);

                                                       }
                                                   })
                                                   .addOnFailureListener(new OnFailureListener() {
                                                       @Override
                                                       public void onFailure(@NonNull Exception e) {

                                                           //failed adding participant
                                                           progressDialog.dismiss();

                                                           Toast.makeText(CreateNewGroupActivity.this, "failed adding participant", Toast.LENGTH_SHORT).show();
                                                           //Toast.makeText(CreateNewGroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();


                                                       }
                                                   });
                                       }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                }

            }
        }

    }*/

    private void addMultipleMembersInGroup(String groupId, ArrayList<UserModel> selectedUsersList) {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

        SimpleDateFormat clearTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
        String clearTimeStamp=clearTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM

        String groupTimeStamp=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM


        if (selectedUsersList != null) {

            for (UserModel model : selectedUsersList) {
                //forwordMessage(UserId, partnerId, model);

                if (model.getFirebaseuserid().equals(firebaseUser.getUid())){

                    selectedUsersList.remove(model);

                    Log.e("groupLog","1");

                    //break;

                }
                else {

                    Log.e("groupLog","2");

                    HashMap<String,Object> hashMap1=new HashMap<>();
                    hashMap1.put("uid",model.getFirebaseuserid());
                    hashMap1.put("role","participant");
                    hashMap1.put("timeStamp", groupTimeStamp);
                    hashMap1.put("clearDate", clearTimeStamp);

                    hashMap1.put("token",model.getToken());


                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");

                    ref.child(groupId).child("Participants").child(model.getFirebaseuserid())
                            .setValue(hashMap1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    //participant added

                                    progressDialog.dismiss();
                                    Toast.makeText(CreateNewGroupActivity.this, "Group created Successfully..", Toast.LENGTH_SHORT).show();

                                    Utility.launchActivity(CreateNewGroupActivity.this, HomeActivity.class,true);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    //failed adding participant
                                    progressDialog.dismiss();

                                    Toast.makeText(CreateNewGroupActivity.this, "failed adding participant", Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(CreateNewGroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();


                                }
                            });
                }



            }
        }

    }



    /*private void createGroup() {

        String groupTitle=et_groupTitle.getText().toString().trim();
        String groupDescription=et_groupDesc.getText().toString().trim();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

        SimpleDateFormat clearTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
        String clearTimeStamp=clearTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM

        String groupTimeStamp=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM


        if (TextUtils.isEmpty(groupTitle)){
            Toast.makeText(this, "please enter group title", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Creating Group");
        progressDialog.show();

        //to generate unique random groupId
        // Get the size n
        int n = 15;
        String groupId=getAlphaNumericString(n);
        Log.e("groupId",""+groupId);


        if (image_uri==null){
            hashMap=new HashMap<>();
            hashMap.put("groupId",""+groupId);
            hashMap.put("groupTitle",""+groupTitle);
            hashMap.put("groupDescription",groupDescription);
            hashMap.put("groupIcon",""+"default");
            hashMap.put("timeStamp",""+groupTimeStamp);
            hashMap.put("createdBy",""+firebaseAuth.getUid());
        }
        else {
            hashMap=new HashMap<>();
            hashMap.put("groupId",""+groupId);
            hashMap.put("groupTitle",""+groupTitle);
            hashMap.put("groupDescription",groupDescription);
            hashMap.put("groupIcon",""+image_uri);
            hashMap.put("timeStamp",""+groupTimeStamp);
            hashMap.put("createdBy",""+firebaseAuth.getUid());
        }


        //create group
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Groups");

        //reference.child(groupTimeStamp).setValue(hashMap)
        reference.child(groupId).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //created Successfully
                        //progressDialog.dismiss();
                        //Toast.makeText(CreateNewGroupActivity.this, "Group Created Successfully..", Toast.LENGTH_SHORT).show();
                        //setup member info(add current user in group's partcipants list)
                        HashMap<String,Object> hashMap1=new HashMap<>();
                        hashMap1.put("uid",firebaseAuth.getUid());
                        hashMap1.put("role","creator");
                        hashMap1.put("timeStamp", groupTimeStamp);
                        hashMap1.put("clearDate", clearTimeStamp);

                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");

                        ref.child(groupId).child("Participants").child(firebaseAuth.getUid())
                                .setValue(hashMap1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        //participant added
                                        progressDialog.dismiss();
                                        Toast.makeText(CreateNewGroupActivity.this, "Group created Successfully..", Toast.LENGTH_SHORT).show();

                                        Utility.launchActivity(CreateNewGroupActivity.this, HomeActivity.class,true);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        //failed adding participant
                                        progressDialog.dismiss();
                                        Toast.makeText(CreateNewGroupActivity.this, "failed adding participant", Toast.LENGTH_SHORT).show();
                                        //Toast.makeText(CreateNewGroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();


                                    }
                                });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed
                        progressDialog.dismiss();
                        Toast.makeText(CreateNewGroupActivity.this, "Failure while creating group", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(CreateNewGroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }*/



    // to generate random alphanumeric string
    // function to generate a random string of length n
    static String getAlphaNumericString(int n)
    {
        // chose a Character random from this String
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

    private void showImagePickerDialog() {
        //options to pick from
        String[] options={"Camera","Gallery"};

        //dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(CreateNewGroupActivity.this);

        builder.setTitle("Pick image")
                .setItems(options,(dialog, which) -> {
                    //handle clicks
                    if (which==0){
                        //camera clicked
                        if (!checkCameraPermissions()){
                            requestCameraPermissions();
                        }
                        else {
                            pickFromCamera();
                        }
                    }else {
                        //gallery clicked
                        if (!checkStoragePermissions()){
                            requestStoragePermissions();
                        }
                        else{
                            pickFromGallery();
                        }
                    }

                }).show();

    }

    private void pickFromCamera() {
        ContentValues cv=new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Group Image Icon Title");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Group Image Icon Description");

        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);

        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);

    }


    private boolean checkCameraPermissions() {
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);

        boolean result1=ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private boolean checkStoragePermissions() {
        boolean result=ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermissions() {
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);
    }

    private void requestCameraPermissions() {
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);
    }

    private void pickFromGallery() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //handle permission result
        switch (requestCode){
            case CAMERA_REQUEST_CODE:

                if (grantResults.length > 0){
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        //permission allowed
                        pickFromCamera();
                    }
                    else {
                        //both or one is denied
                        Toast.makeText(this, "Camera and storage permission are required", Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0){
                    boolean storageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        //permission allowed
                        pickFromGallery();
                    }else {
                        //permission denied
                        Toast.makeText(this, "storage permission denied", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //handle image pick result

        if (resultCode==RESULT_OK){

            if (requestCode==IMAGE_PICK_GALLERY_CODE){
                //was picked from gallery
                image_uri=data.getData();
                //set to imageview
                iv_groupPic.setImageURI(image_uri);
            }
            else if (requestCode==IMAGE_PICK_CAMERA_CODE){
                //was picked from camera
                //set to imageview
                iv_groupPic.setImageURI(image_uri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }



}
