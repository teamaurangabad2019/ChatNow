package com.teammandroid.chatnow.activities.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.models.firebase.GroupChatList;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class GroupEditActivity extends AppCompatActivity implements View.OnClickListener {

    String groupId;

    Intent intent;

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

    private FirebaseAuth firebaseAuth;

    CircleImageView iv_groupPic;
    EditText et_groupDesc,et_groupTitle;
    FloatingActionButton btn_updateGroupInfo;

    ProgressDialog progressDialog;

    GroupChatList groupChatModel;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);

        intent=getIntent();
        groupId=intent.getStringExtra("groupId");
        Log.e("mygroupId",groupId);


        bindView();
        btnListener();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //init permission arrays
        cameraPermissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        firebaseAuth=FirebaseAuth.getInstance();

        checkUser();

        loadGroupInfo();


    }

    private void loadGroupInfo() {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    //get group info

                    groupChatModel=dataSnapshot.getValue(GroupChatList.class);

                    Log.e("mygroupId",groupChatModel.getGroupTitle());

                    //tv_group_name.setText(groupChatModel.getGroupTitle());
                    //tv_groupDescription.setText(groupChatModel.getGroupDescription());

                    //tv_createdBy.setText("Created by : "+groupChatModel.getCreatedBy());

                    //String groupCreator=groupChatModel.getCreatedBy();
                    //String timeStamp=groupChatModel.getTimeStamp();

                    et_groupTitle.setText(groupChatModel.getGroupTitle());
                    et_groupDesc.setText(groupChatModel.getGroupDescription());

                    String groupIcon=groupChatModel.getGroupIcon();

                    if (groupIcon.equals("default")){
                        iv_groupPic.setImageResource(R.drawable.ic_male);
                    }else {
                        Picasso.get().load(groupIcon).placeholder(R.drawable.ic_male).into(iv_groupPic);
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void btnListener() {
        btn_updateGroupInfo.setOnClickListener(this);
        iv_groupPic.setOnClickListener(this);
    }

    private void bindView() {
        btn_updateGroupInfo=findViewById(R.id.btn_updateGroupInfo);
        et_groupDesc=findViewById(R.id.et_groupDesc);
        et_groupTitle=findViewById(R.id.et_groupTitle);

        iv_groupPic=findViewById(R.id.iv_groupPic);
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
            case R.id.btn_updateGroupInfo:

                startUpdatingGroup();

                break;

        }
    }

    private void startUpdatingGroup() {

        //input data
        String groupTitle=et_groupTitle.getText().toString().trim();
        String groupDescription=et_groupDesc.getText().toString().trim();

        //validate data
        if (TextUtils.isEmpty(groupTitle)){
            Toast.makeText(this, "Group Title is required", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Updating Group Info");
        progressDialog.show();

        if (image_uri==null){

            //update group without icon
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("groupTitle",groupTitle);
            hashMap.put("groupDescription",groupDescription);

            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
            ref.child(groupId).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //updated
                            progressDialog.dismiss();
                            Toast.makeText(GroupEditActivity.this, "Group info updated", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failure to update group info
                            progressDialog.dismiss();
                            Toast.makeText(GroupEditActivity.this, "Failed in updating group info", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {

            //update group with icon

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

            String timeStamp=dateTime.format(cal.getTime());//prints current date with time ex: 03/05/2020 11:30 PM

            String filePathAndName="Group_Images/"+firebaseUser.getUid();

            //String filePathAndName="Group_Images/"+"image"+"_"+timeStamp;

            //upload image to firebase storage
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //image uploaded

                            //get uri
                            Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadUri=uriTask.getResult();
                            if (uriTask.isSuccessful()){

                                HashMap<String,Object> hashMap=new HashMap<>();
                                hashMap.put("groupTitle",groupTitle);
                                hashMap.put("groupDescription",groupDescription);
                                hashMap.put("groupIcon",""+downloadUri);

                                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
                                ref.child(groupId).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //updated
                                                progressDialog.dismiss();
                                                Toast.makeText(GroupEditActivity.this, "Group info updated", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failure to update group info
                                                progressDialog.dismiss();
                                                Toast.makeText(GroupEditActivity.this, "Failed in updating group info", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //image upload failed
                            progressDialog.dismiss();
                            Toast.makeText(GroupEditActivity.this, "Image uploading failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void showImagePickerDialog() {
        //options to pick from
        String[] options={"Camera","Gallery"};

        //dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(GroupEditActivity.this);

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
        boolean result=ContextCompat.checkSelfPermission(this,
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
