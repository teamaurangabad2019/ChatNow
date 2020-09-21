package com.teammandroid.chatnow.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.interfaces.ApiStatusCallBack;
import com.teammandroid.chatnow.models.Response;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.network.UserServices;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEditActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ProfileEditActivity.class.getSimpleName();
    private Uri filePath;
    String path;
    private int PICK_IMAGE_REQUEST = 1;

    private CircleImageView iv_pic;
    private ImageView iv_pedit;

    private EditText et_name;

    private EditText et_mobile;

    private EditText et_email;

    private EditText et_address;

    FirebaseUser firebaseUser;

    private TextView tv_grade;

    private Button btn_submit;

   // LinearLayout lyt_progress_reg;

    ImageView iv_backprofile;

    private static final int REQUEST_CHOOSER = 1234;
    Activity activity;
    private UserModel user;
    File fileToUpdate = null;
    private Bitmap bitmap;
    private String token;
    private Bitmap currentImage;

    private ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        activity = ProfileEditActivity.this;
        bindView();
        listener();
        getTokan();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading page, please wait.");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        SessionManager sessionManager = new SessionManager(activity);
        user = sessionManager.getUserDetails();
        bindValues();
    }

    private void listener() {
        iv_pedit.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        iv_backprofile.setOnClickListener(this);
    }

    private void bindView() {
        iv_pic = (CircleImageView) findViewById(R.id.iv_pic);
        iv_pedit = (ImageView) findViewById(R.id.iv_pedit);
        et_name = (EditText) findViewById(R.id.et_name);
        et_mobile = (EditText) findViewById(R.id.et_mobile);
        et_email = (EditText) findViewById(R.id.et_email);
        et_address = (EditText) findViewById(R.id.et_address);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        iv_backprofile = findViewById(R.id.iv_backprofile);
       // lyt_progress_reg = findViewById(R.id.lyt_progress_reg);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();


    }

    private void bindValues() {

        String profile = Constants.URL_USER_PROFILE_PIC+user.getProfilepic();
        Log.e(TAG, "bindValues: profile "+profile );
        Picasso.get().load(profile).placeholder(R.drawable.male_avatar).into(iv_pic );

        et_name.setText(user.getFullname());
        et_mobile.setText(user.getMobile());
        et_email.setText(user.getEmail());
        et_address.setText(user.getAddress());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;

            case R.id.iv_backprofile:
                onBackPressed();
                break;

            case R.id.iv_pedit:
                //showFileChooser();
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);

                // start cropping activity for pre-acquired image saved on the device
                CropImage.activity(filePath)
                        .start(this);

                // for fragment (DO NOT use `getActivity()`)
                CropImage.activity()
                        .start(activity);
               // showFileChooser();
                /*   Intent getContentIntent = FileUtils.createGetContentIntent();
        Intent intent = Intent.createChooser(getContentIntent, "Select a file");
        startActivityForResult(intent, REQUEST_CHOOSER);*/
                break;

            case R.id.btn_submit:
                if (!et_name.getText().toString().equals("") && !et_mobile.getText().toString().equals("") &&
                        !et_email.getText().toString().equals("") && !et_address.getText().toString().equals("")
                ) {
                    user.setFullname(et_name.getText().toString());
                    user.setMobile(et_mobile.getText().toString());
                    user.setEmail(et_email.getText().toString());
                    user.setAddress(et_address.getText().toString());
                    user.setToken(token);
                    Log.e(TAG, "onClick: Token"+user.getToken() );
                    user.setDevice(Utility.getDeviceName());

                    if (fileToUpdate == null) {
                        UpdateUserWithoutPic();
                    } else {
                        UpdateUserWithPic();
                    }

                    //Utility.showErrorMessage(activity, "Profile Updated");
                } else {
                    Utility.showErrorMessage(activity, "Please Fill All  Details");
                }
                break;

        }

    }

    private void UpdateUserWithoutPic() {
        try {
            if (Utility.isNetworkAvailable(getApplicationContext())) {

                //Creating a multi part request
                AndroidNetworking.upload(Constants.URL_LOGIN)
                        .addMultipartParameter("type", "1")
                        .addMultipartParameter("Action", "1")
                        .addMultipartParameter("Userid", String.valueOf(this.user.getUserid()))
                        .addMultipartParameter("Fullname", this.user.getFullname())
                        .addMultipartParameter("Roleid", String.valueOf(this.user.getRoleid()))
                        .addMultipartParameter("Address", this.user.getAddress())
                        .addMultipartParameter("Mobile", this.user.getMobile())
                        .addMultipartParameter("Token", this.user.getToken())
                        .addMultipartParameter("Email", this.user.getEmail())
                        .addMultipartParameter("Profilepic", this.user.getProfilepic())
                        .addMultipartParameter("Latitute", String.valueOf(this.user.getLatitute()))
                        .addMultipartParameter("Longitude", String.valueOf(this.user.getLongitude()))
                        .addMultipartParameter("Device", Utility.getDeviceName())
                        .addMultipartParameter("LogedinUserId", String.valueOf(this.user.getUserid()))
                        .addMultipartParameter("Firebaseuserid", firebaseUser.getUid())
                        //.setNotificationConfig(new UploadNotificationConfig())
                        .setTag("uploadTest")
                        .setPriority(Priority.HIGH)
                        .build()
                        .setUploadProgressListener(new UploadProgressListener() {
                            @Override
                            public void onProgress(long bytesUploaded, long totalBytes) {
                                dialog.show();

                                Log.e(TAG, "uploadImage: totalBytes: " + totalBytes);
                                Log.e(TAG, "uploadImage: bytesUploaded: " + bytesUploaded);
                            }
                        })
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                TypeToken<Response> token = new TypeToken<Response>() {
                                };
                                Response response1 = new Gson().fromJson(response.toString(), token.getType());
                                dialog.dismiss();
                                UpdateFirebaseUser(user, response1);
                            }

                            @Override
                            public void onError(ANError error) {
                                dialog.dismiss();
                                Utility.showErrorMessage(activity, "Network error" + error.getMessage());
                            }
                        });
            } else {
                Utility.showErrorMessage(activity, "Could not connect to the internet");
            }

        } catch (Exception ex) {
            dialog.dismiss();
            Utility.showErrorMessage(activity, ex.getMessage());
        }
    }

    private void UpdateFirebaseUser(UserModel user1, Response response1) {
        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("username",user1.getFullname());
        hashMap.put("email",user1.getEmail());
        hashMap.put("password","***");
        hashMap.put("status","offline");
        hashMap.put("onlineUserId",String.valueOf(user1.getUserid()));
        //hashMap.put("status","Hi there,i am using Chatnow app");

        hashMap.put("typingTo","noOne");//for typing status

        hashMap.put("imageUrl",user1.getProfilepic());
        hashMap.put("search",user1.getFullname().toLowerCase());

        hashMap.put("token",token);
        hashMap.put("about","Hey there! I am using Chatnow");
        hashMap.put("createdOn",user1.getCreated());
        hashMap.put("mobile",user1.getMobile());
        hashMap.put("address",user1.getAddress());

        //update role in db
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                        //lyt_progress_reg.setVisibility(View.GONE);
                        SessionManager sessionManager = new SessionManager(activity);
                        sessionManager.createLoginSession(ProfileEditActivity.this.user);
                        //   Log.e(TAG, "onSuccess: " + ProfileEditActivity.this.user.getUserid());
                        Log.e(TAG, "onResponse: withoutPic "+response1.getMessage() );

                        Intent intent = new Intent();
                        intent.putExtra("response", response1);
                        setResult(1, intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(activity, "something went wrong ", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void UpdateUserWithPic() {
        try {
            if (Utility.isNetworkAvailable(getApplicationContext())) {

                    //Creating a multi part request
                    AndroidNetworking.upload(Constants.URL_LOGIN)
                            .addMultipartParameter("type", "1")
                            .addMultipartParameter("Action", "1")
                            .addMultipartParameter("Userid", String.valueOf(this.user.getUserid()))
                            .addMultipartParameter("Fullname", this.user.getFullname())
                            .addMultipartParameter("Roleid", String.valueOf(this.user.getRoleid()))
                            .addMultipartParameter("Address", this.user.getAddress())
                            .addMultipartParameter("Mobile", this.user.getMobile())
                            .addMultipartParameter("Token", this.user.getToken())
                            .addMultipartParameter("Email", this.user.getEmail())
                            .addMultipartFile("Profilepic", fileToUpdate)
                            .addMultipartParameter("Latitute", String.valueOf(this.user.getLatitute()))
                            .addMultipartParameter("Longitude", String.valueOf(this.user.getLongitude()))
                            .addMultipartParameter("Device", Utility.getDeviceName())
                            .addMultipartParameter("LogedinUserId", String.valueOf(this.user.getUserid()))
                            .addMultipartParameter("Firebaseuserid", firebaseUser.getUid())
                            //.setNotificationConfig(new UploadNotificationConfig())
                            .setTag("uploadTest")
                            .setPriority(Priority.HIGH)
                            .build()
                            .setUploadProgressListener(new UploadProgressListener() {
                                @Override
                                public void onProgress(long bytesUploaded, long totalBytes) {
                                    dialog.show();
                                    Log.e(TAG, "uploadImage: totalBytes: " + totalBytes);
                                    Log.e(TAG, "uploadImage: bytesUploaded: " + bytesUploaded);
                                }
                            })
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                 public void onResponse(JSONObject response) {
                                    TypeToken<Response> token = new TypeToken<Response>() {
                                    };
                                    Response response1 = new Gson().fromJson(response.toString(), token.getType());

                                    GetUser(user.getUserid(), response1);
                                    dialog.dismiss();
                                    SessionManager sessionManager = new SessionManager(activity);
                                    sessionManager.createLoginSession(ProfileEditActivity.this.user);
                                  //  Log.e(TAG, "onSuccess: " + ProfileEditActivity.this.user.getUserid());
                                    Log.e(TAG, "onResponse: withImage"+response1.getMessage() );

                                    Intent intent = new Intent();
                                    intent.putExtra("response", response1);
                                    setResult(1, intent);
                                    finish();
                                }

                                @Override
                                public void onError(ANError error) {
                                    dialog.dismiss();
                                    Utility.showErrorMessage(activity, "Network error" + error.getMessage());
                                }
                            });
            } else {
                Utility.showErrorMessage(activity, "Could not connect to the internet");
            }

        } catch (Exception ex) {
            dialog.dismiss();
            Utility.showErrorMessage(activity, ex.getMessage());
        }
    }


    private void GetUser(int UserId, Response response1) {
        try {
            if (Utility.isNetworkAvailable(getApplicationContext())) {
               /* lyt_progress_reg.setVisibility(View.VISIBLE);
                lyt_progress_reg.setAlpha(1.0f);
*/
                dialog.show();
                Log.e(TAG, "GetUser: " + UserId);
                UserServices.getInstance(getApplicationContext()).GetUserDetails(UserId, new ApiStatusCallBack<ArrayList<UserModel>>() {
                    @Override
                    public void onSuccess(ArrayList<UserModel> response) {
                        //   lyt_progress_reg.setVisibility(View.GONE);
                        dialog.dismiss();
                        user = response.get(0);
                        UpdateFirebaseUser(user, response1);

                    }

                    @Override
                    public void onError(ANError anError) {
                        //lyt_progress_reg.setVisibility(View.GONE);
                        dialog.dismiss();
                        Utility.showErrorMessage(activity, "Invalid Credentials");
                    }

                    @Override
                    public void onUnknownError(Exception e) {
                        //lyt_progress_reg.setVisibility(View.GONE);
                        dialog.dismiss();
                        Utility.showErrorMessage(activity, e.getMessage());
                    }
                });
            } else {
                Utility.showErrorMessage(activity, "Could not connect to the internet");
            }
        } catch (Exception ex) {
            //lyt_progress_reg.setVisibility(View.GONE);
            dialog.dismiss();
            Utility.showErrorMessage(activity, ex.getMessage());
        }
    }

    private void getTokan() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        token = task.getResult().getToken();
                        Log.e("token", token);

                    }
                });
    }


    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       /* if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                fileToUpdate = new File(filePath.getPath());
                String filepath = fileToUpdate.getAbsolutePath();
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                iv_pic.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.e("path", "" + filePath);*/

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Log.e(TAG, "onActivityResult: "+result );

            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    currentImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    Log.e("ResultUri",""+resultUri);
                    // File file=new File(resultUri.getPath());
                    fileToUpdate = new File(resultUri.getPath());
                    String filepath=fileToUpdate.getAbsolutePath();
                    iv_pic.setImageURI(Uri.fromFile(fileToUpdate));

                    //uploadImage(fileToUpdate);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Response response = null;
        Intent intent = new Intent();
        intent.putExtra("response", response);
        setResult(1, intent);
        finish();
    }

    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
}
