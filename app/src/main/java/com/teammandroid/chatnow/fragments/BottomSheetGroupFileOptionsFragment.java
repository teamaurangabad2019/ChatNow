package com.teammandroid.chatnow.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.activities.group_preview.GrpAudioPreviewListActivity;
import com.teammandroid.chatnow.activities.group_preview.GrpCameraPreviewActivity;
import com.teammandroid.chatnow.activities.group_preview.GrpContactPreviewActivity;
import com.teammandroid.chatnow.activities.group_preview.GrpMediaPreviewActivity;
import com.teammandroid.chatnow.activities.preview_activity.AudioPreviewListActivity;
import com.teammandroid.chatnow.activities.preview_activity.CameraPreviewActivity;
import com.teammandroid.chatnow.activities.preview_activity.ContactPreviewActivity;
import com.teammandroid.chatnow.activities.group_preview.GrpVideoPreviewActivity;
import com.teammandroid.chatnow.activities.preview_activity.MediaPreviewActivity;
import com.teammandroid.chatnow.models.firebase.GroupChatList;
import com.teammandroid.chatnow.utils.Constants;
import com.teammandroid.chatnow.utils.Utility;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class BottomSheetGroupFileOptionsFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    private static final int CONTACT_PICKER_REQUEST = 9 ;
    Activity activity;
    private String[] cameraPermissions;
    private static final int CAMERA_REQUEST_CODE = 100;
    TextView tv_document, tv_camera, tv_gallary, tv_audio, tv_video, tv_contact;
    private int PICK_IMAGE_REQUEST = 1;
    static final int OPEN_IMAGE_PICKER = 5;  // Request code
    static final int OPEN_VIDEO_PICKER = 6;  // Request code
    static final int FILE_REQUEST_CODE = 7;  // Request code
    static final int AUDIO_REQUEST_CODE = 8;  // Request code
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    public static final int REQUEST_CODE_PICK_CONTACT = 1;
    public static final int MAX_PICK_CONTACT = 10;

    public static final String TAG = BottomSheetGroupFileOptionsFragment.class.getSimpleName();
    private ItemClickListener mListener;
    String myGroupId;
   // FirebaseUserModel offchattingPartner;
    private Uri image_uri;
    private String cameraUrl;
    GroupChatList groupChatModel;

    public BottomSheetGroupFileOptionsFragment() {
        // return;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_option, container, false);
        activity = getActivity();
        myGroupId = getArguments().getString("myGroupId");
        groupChatModel = getArguments().getParcelable("groupChatModel");
        bindView(view);
        listener();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ItemClickListener) {
            mListener = (ItemClickListener) context;
        } else {
            //throw new RuntimeException();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void listener() {
        tv_document.setOnClickListener(this);
        tv_camera.setOnClickListener(this);
        tv_gallary.setOnClickListener(this);
        tv_audio.setOnClickListener(this);
        tv_video.setOnClickListener(this);
        tv_contact.setOnClickListener(this);
    }

    private void bindView(View view) {
        tv_document = view.findViewById(R.id.tv_document);
        tv_camera = view.findViewById(R.id.tv_camera);
        tv_gallary = view.findViewById(R.id.tv_gallary);
        tv_audio = view.findViewById(R.id.tv_audio);
        tv_video = view.findViewById(R.id.tv_video);
        tv_contact = view.findViewById(R.id.tv_contact);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;

            case R.id.tv_document:
                Intent intent = new Intent(activity, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setMaxSelection(-1)
                        .setSkipZeroSizeFiles(true)
                        .setShowFiles(true)
                        .setShowImages(false)
                        .setShowVideos(false)
                        .setSuffixes("txt", "pdf", "html", "rtf", "csv", "xml",
                                "zip", "tar", "gz", "rar", "7z", "torrent",
                                "doc", "docx", "odt", "ott",
                                "ppt", "pptx", "pps",
                                "xls", "xlsx", "ods", "ots")
                        .build());
                startActivityForResult(intent, FILE_REQUEST_CODE);

                break;

            case R.id.tv_camera:
                if (!checkCameraPermissions()) {
                    requestCameraPermissions();
                } else {
                    pickFromCamera();
                }
                break;

            case R.id.tv_gallary:
                Intent intent2 = new Intent(activity, FilePickerActivity.class);
                intent2.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setMaxSelection(-1)
                        .setSkipZeroSizeFiles(true)
                        .setShowFiles(false)
                        .setShowImages(true)
                        .setShowVideos(false)
                        .build());

                startActivityForResult(intent2, OPEN_IMAGE_PICKER);

                break;

            case R.id.tv_audio:
                Intent intent3 = new Intent(activity, FilePickerActivity.class);
                intent3.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setMaxSelection(-1)
                        .setSkipZeroSizeFiles(true)
                        .setShowFiles(false)
                        .setShowImages(false)
                        .setShowVideos(false)
                        .setShowAudios(true)
                        .build());
                startActivityForResult(intent3, AUDIO_REQUEST_CODE);
                break;

            case R.id.tv_video:
                Intent intent4 = new Intent(activity, FilePickerActivity.class);
                intent4.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setMaxSelection(-1)
                        .setSkipZeroSizeFiles(true)
                        .setShowFiles(false)
                        .setShowImages(false)
                        .setShowVideos(true)
                        .build());
                startActivityForResult(intent4, OPEN_VIDEO_PICKER);

                break;

            case R.id.tv_contact:

                new MultiContactPicker.Builder(getActivity()) //Activity/fragment context
                        .theme(R.style.MyCustomPickerTheme) //Optional - default: MultiContactPicker.Azure
                        .hideScrollbar(false) //Optional - default: false
                        .showTrack(true) //Optional - default: true
                        .searchIconColor(Color.WHITE) //Option - default: White
                        .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
                        .handleColor(ContextCompat.getColor(activity, R.color.colorPrimary)) //Optional - default: Azure Blue
                        .bubbleColor(ContextCompat.getColor(activity, R.color.colorPrimary)) //Optional - default: Azure Blue
                        .bubbleTextColor(Color.WHITE) //Optional - default: White
                        .setTitleText("Select Contacts") //Optional - default: Select Contacts
                      //  .setSelectedContacts("10", "5" / myList) //Optional - will pre-select contacts of your choice. String... or List<ContactResult>
                        .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                        .limitToColumn(LimitColumn.NONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)
                        .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                                android.R.anim.fade_in,
                                android.R.anim.fade_out) //Optional - default: No animation overrides
                        .showPickerForResult(CONTACT_PICKER_REQUEST);
                break;
        }
    }

    public interface ItemClickListener {
        void onItemClick();
    }

    private void pickFromCamera() {
       /* ContentValues cv=new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Group Image Icon Title");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Group Image Icon Description");

        image_uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);

        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
*/


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, IMAGE_PICK_CAMERA_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //1 img //3 audio //4 video //5 document
        try {
            if (requestCode == OPEN_IMAGE_PICKER) {
                // Make sure the request was successful
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                Bundle bundle = new Bundle();
                bundle.putString("myGroupId", myGroupId);
                bundle.putParcelableArrayList("files", files);
                bundle.putParcelable("groupChatModel",groupChatModel);
                bundle.putInt("mediaType", 1);
                Utility.launchActivityForResult(activity, GrpMediaPreviewActivity.class, bundle, 2);

            } else if (requestCode == OPEN_VIDEO_PICKER) {
                // Make sure the request was successful
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                Bundle bundle = new Bundle();
                bundle.putString("myGroupId", myGroupId);
                bundle.putParcelableArrayList("files", files);
                bundle.putInt("mediaType", 4);
                bundle.putParcelable("groupChatModel",groupChatModel);
                Utility.launchActivityForResult(activity, GrpVideoPreviewActivity.class, bundle, 2);

            } else if (requestCode == FILE_REQUEST_CODE) {
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                Bundle bundle = new Bundle();
                bundle.putString("myGroupId", myGroupId);
                bundle.putParcelableArrayList("files", files);
                bundle.putInt("mediaType", 5);
                bundle.putParcelable("groupChatModel",groupChatModel);
                Utility.launchActivityForResult(activity, GrpMediaPreviewActivity.class, bundle, 2);
                // Log.e(TAG, "onActivityResult: files "+ files.toString() );
            } else if (requestCode == AUDIO_REQUEST_CODE) {
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                Bundle bundle = new Bundle();
                bundle.putString("myGroupId", myGroupId);
                bundle.putParcelableArrayList("files", files);
                bundle.putInt("mediaType", 3);
                bundle.putParcelable("groupChatModel",groupChatModel);
                Utility.launchActivityForResult(activity, GrpAudioPreviewListActivity.class, bundle, 2);
            }/*else if (resultCode == 2){
            Log.e(TAG, "onActivityResult: called" );

        }*/ else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //was picked from camera

                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                // circleImageView.setImageBitmap(thumbnail);
                String file = saveImage(thumbnail);
                Toast.makeText(activity, "Image Saved in fragment !", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onActivityResult: file "+file );
                Bundle bundle = new Bundle();
                bundle.putString("myGroupId", myGroupId);
                bundle.putString("files",file);
                bundle.putInt("mediaType",5);
                bundle.putParcelable("groupChatModel",groupChatModel);
                Utility.launchActivityForResult(activity, GrpCameraPreviewActivity.class,bundle,2);

            } else if (requestCode == CONTACT_PICKER_REQUEST) {
                if (resultCode == RESULT_OK) {
                    ArrayList<ContactResult> results = MultiContactPicker.obtainResult(data);
                    Log.d("MyTag", results.get(0).getDisplayName());
                    Bundle bundle = new Bundle();
                    bundle.putString("myGroupId", myGroupId);
                    bundle.putParcelableArrayList("contacts", results);
                    bundle.putInt("mediaType", 8);
                    bundle.putInt("isSent", 0);
                    bundle.putParcelable("groupChatModel",groupChatModel);
                    Utility.launchActivityForResult(activity, GrpContactPreviewActivity.class, bundle, 2);
                } else if (resultCode == RESULT_CANCELED) {
                    System.out.println("User closed the picker without selecting items.");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "onActivityResult: e " + e.getMessage());
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    public void saveBitmap(Bitmap bmp) {
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM";
        try {
            File dir = new File(file_path);
            if (!dir.exists())
                dir.mkdirs();
            File file = new File(dir, "time");
            cameraUrl = file.getAbsolutePath();
            FileOutputStream fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            Log.e("errr in ", "inserting the image at parictular location");
        }
    }

    private boolean checkCameraPermissions() {
        boolean result = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermissions() {
        ActivityCompat.requestPermissions(activity, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //handle permission result
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:

                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        //permission allowed
                        pickFromCamera();
                    } else {
                        //both or one is denied
                        Toast.makeText(activity, "Camera and storage permission are required", Toast.LENGTH_SHORT).show();
                    }
                }

                break;

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File( Environment.getExternalStorageDirectory() + "/" + Constants.OFFLINE_IMAGE_PATH );
        if (!wallpaperDirectory.exists()) {  // have the object build the directory structure, if needed.
            wallpaperDirectory.mkdirs();
        }
        File  f = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(activity,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::---&gt;" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return f.getAbsolutePath();
    }
}
