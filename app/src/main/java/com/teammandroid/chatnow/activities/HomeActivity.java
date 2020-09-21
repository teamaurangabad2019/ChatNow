package com.teammandroid.chatnow.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teammandroid.chatnow.adapters.HomeActivityTabsAdapter;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.services.MyForgroundLocationService;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    DrawerLayout Drawer_Layout;

    private ImageView img_openDrawer, img_map;
    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;
    RelativeLayout rl_profile;
    RelativeLayout logout;
    RelativeLayout rl_contactUs,rl_logout;

    ImageView iv_add_new;
    TextView tv_shareApp,tv_settings,tv_invite;

    HomeActivityTabsAdapter adapter;
    private String phoneNo;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private String message;

    private UserModel userModel;

    TextView tv_mobileNo, tv_Name, tv_email;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    FloatingActionButton fab_add_new;

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        activity = HomeActivity.this;

        SessionManager sessionManager = new SessionManager(this);
        userModel = sessionManager.getUserDetails();

        startService(); // starts forground service to fetch location updates
        createDir();
        bindView();
        listener();

        try {
            firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
            reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        }catch (Exception e){

        }

        tabLayout.addTab(tabLayout.newTab().setText("Find"));
        tabLayout.addTab(tabLayout.newTab().setText("Chats"));
        //tabLayout.addTab(tabLayout.newTab().setText("Explore"));
        tabLayout.addTab(tabLayout.newTab().setText("Groups"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        fab_add_new.hide();
                        break;
                    case 1:
                        fab_add_new.hide();
                        break;
                    case 2:
                        fab_add_new.show();
                        break;
                    default:
                        fab_add_new.hide();
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        adapter=new HomeActivityTabsAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

    }

    public void startService() {
        Intent serviceIntent = new Intent(this, MyForgroundLocationService.class);
        //Intent serviceIntent = new Intent(this, ForegroundService.class);
        //serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");

        serviceIntent.putExtra("inputExtra", "Foreground Location Service");

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void bindView() {
        Drawer_Layout = findViewById(R.id.drl_Opener);
        img_openDrawer = (ImageView) findViewById(R.id.img_openDrawer);
        tabLayout =  findViewById(R.id.tabLayout);
        viewPager =  findViewById(R.id.pager);
        img_map =  findViewById(R.id.img_map);
        rl_profile =  findViewById(R.id.rl_profile);
        logout =  findViewById(R.id.rl_profile);
        rl_contactUs =  findViewById(R.id.rl_contactUs);

        tv_shareApp=findViewById(R.id.tv_share_app);
        tv_settings=findViewById(R.id.tv_settings);



        tv_invite=findViewById(R.id.tv_invite);
        //iv_add_new=findViewById(R.id.iv_add_new);

        rl_logout=findViewById(R.id.rl_logout);

        fab_add_new = (FloatingActionButton)findViewById(R.id.fab_add_new);


        //iv_add_new.setVisibility(View.GONE);

        tv_mobileNo=findViewById(R.id.tv_mobileNo);
        tv_Name=findViewById(R.id.tv_Name);
        tv_email=findViewById(R.id.tv_email);

        try{
            tv_mobileNo.setText(userModel.getMobile());
            tv_Name.setText(userModel.getFullname());
            tv_email.setText(userModel.getEmail());
        }catch (Exception e){

        }

    }

    private void listener() {
        img_openDrawer.setOnClickListener(this);
        img_map.setOnClickListener(this);
        rl_profile.setOnClickListener(this);
        logout.setOnClickListener(this);
        rl_contactUs.setOnClickListener(this);

        tv_shareApp.setOnClickListener(this);
        tv_settings.setOnClickListener(this);
        tv_invite.setOnClickListener(this);
        //iv_add_new.setOnClickListener(this);

        fab_add_new.setOnClickListener(this);
        rl_logout.setOnClickListener(this);

    }


    private void stopService() {
        Intent serviceIntent = new Intent(this, MyForgroundLocationService.class);
        //Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            default:
                break;

            case R.id.rl_logout:

                SessionManager sessionManager=new SessionManager(activity);
                sessionManager.logoutUser();
                activity.finish();

                break;

            case R.id.img_openDrawer:
                Drawer_Layout.openDrawer(Gravity.LEFT);
                break;

            case R.id.img_map:
                Utility.launchActivity(HomeActivity.this,SearchNearMeActivity.class, false);
                break;

            case R.id.fab_add_new:
                Utility.launchActivity(HomeActivity.this, SelectUsersActivity.class,false);
                //Utility.launchActivity(HomeActivity.this, CreateNewGroupActivity.class,false);
                break;

            case R.id.rl_profile:
                Utility.launchActivity(HomeActivity.this,ProfileActivity.class,false);
                break;

            case R.id.rl_contactUs:
                stopService();
                Log.e(TAG, "onClick: clicked" );
                break;

            case R.id.tv_share_app:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, (getResources().getString(R.string.share_app_link)));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;

            case R.id.tv_settings:
                startActivity(new Intent(HomeActivity.this,SettingsActivity.class));
                break;

            case R.id.tv_invite:
                //startActivity(new Intent(HomeActivity.this, DemoActivity.class));
                startActivity(new Intent(HomeActivity.this,InviteContactsActivity.class));
                //startActivity(new Intent(HomeActivity.this,InviteContactsActivity.class));
                //startActivity(new Intent(HomeActivity.this,AllContactsActivity.class));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }

    // to make folders and subfolders to store audio,videos and pics
    public void createDir() {

        // media files
        File SDCardRoot1 = new File(Environment.getExternalStorageDirectory()
                .toString() + "/Chatnow/Media");

        Toast.makeText(getApplicationContext(), SDCardRoot1.toString(),
                Toast.LENGTH_LONG).show();

        if (!SDCardRoot1.exists()) {
            Log.d("DIRECTORY CHECK",
                    "Directory doesnt exist creating directory "
                            + Environment.getExternalStorageDirectory()
                            .toString());
            boolean outcome = SDCardRoot1.mkdirs();
        }

        // backup files
        File SDCardRoot2 = new File(Environment.getExternalStorageDirectory()
                .toString() + "/Chatnow/Backups");

        Toast.makeText(getApplicationContext(), SDCardRoot2.toString(),
                Toast.LENGTH_LONG).show();

        if (!SDCardRoot2.exists()) {
            Log.d("DIRECTORY CHECK",
                    "Directory doesnt exist creating directory "
                            + Environment.getExternalStorageDirectory()
                            .toString());
            boolean outcome = SDCardRoot2.mkdirs();
        }

        // database files
        File SDCardRoot3 = new File(Environment.getExternalStorageDirectory()
                .toString() + "/Chatnow/Databases");

        Toast.makeText(getApplicationContext(), SDCardRoot3.toString(),
                Toast.LENGTH_LONG).show();

        if (!SDCardRoot3.exists()) {
            Log.d("DIRECTORY CHECK",
                    "Directory doesnt exist creating directory "
                            + Environment.getExternalStorageDirectory()
                            .toString());
            boolean outcome = SDCardRoot3.mkdirs();
        }


        //chatnow images
        File subDir1 = new File(Environment.getExternalStorageDirectory()
                .toString() + "/Chatnow/Media/Chatnow Images/Sent");

        Toast.makeText(getApplicationContext(), subDir1.toString(),
                Toast.LENGTH_LONG).show();

        if (!subDir1.exists()) {
            Log.d("DIRECTORY CHECK",
                    "Directory doesnt exist creating directory "
                            + Environment.getExternalStorageDirectory()
                            .toString());
            boolean outcome = subDir1.mkdirs();
        }


        //audio
        File subDir2 = new File(Environment.getExternalStorageDirectory()
                .toString() + "/Chatnow/Media/Chatnow Audio/Sent");

        Toast.makeText(getApplicationContext(), subDir2.toString(),
                Toast.LENGTH_LONG).show();

        if (!subDir2.exists()) {
            Log.d("DIRECTORY CHECK",
                    "Directory doesnt exist creating directory "
                            + Environment.getExternalStorageDirectory()
                            .toString());
            boolean outcome = subDir2.mkdirs();
        }


        //videos
        File subDir3 = new File(Environment.getExternalStorageDirectory()
                .toString() + "/Chatnow/Media/Chatnow Videos/Sent");

        Toast.makeText(getApplicationContext(), subDir3.toString(),
                Toast.LENGTH_LONG).show();

        if (!subDir3.exists()) {
            Log.d("DIRECTORY CHECK",
                    "Directory doesnt exist creating directory "
                            + Environment.getExternalStorageDirectory()
                            .toString());
            boolean outcome = subDir3.mkdirs();
        }

        //profile photos
        File subDir4 = new File(Environment.getExternalStorageDirectory()
                .toString() + "/Chatnow/Media/Chatnow Profile Photos");

        Toast.makeText(getApplicationContext(), subDir4.toString(),
                Toast.LENGTH_LONG).show();

        if (!subDir4.exists()) {
            Log.d("DIRECTORY CHECK",
                    "Directory doesnt exist creating directory "
                            + Environment.getExternalStorageDirectory()
                            .toString());
            boolean outcome = subDir4.mkdirs();
        }


        //gif files
        File subDir5 = new File(Environment.getExternalStorageDirectory()
                .toString() + "/Chatnow/Media/Chatnow Gif/Sent");

        Toast.makeText(getApplicationContext(), subDir5.toString(),
                Toast.LENGTH_LONG).show();

        if (!subDir5.exists()) {
            Log.d("DIRECTORY CHECK",
                    "Directory doesnt exist creating directory "
                            + Environment.getExternalStorageDirectory()
                            .toString());
            boolean outcome = subDir5.mkdirs();
        }


        //document files
        File subDir6 = new File(Environment.getExternalStorageDirectory()
                .toString() + "/Chatnow/Media/Chatnow Documents/Sent");

        Toast.makeText(getApplicationContext(), subDir6.toString(),
                Toast.LENGTH_LONG).show();

        if (!subDir6.exists()) {
            Log.d("DIRECTORY CHECK",
                    "Directory doesnt exist creating directory "
                            + Environment.getExternalStorageDirectory()
                            .toString());
            boolean outcome = subDir6.mkdirs();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseUser = null;
        reference = null;
    }

    public void status(String status){

        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("status",status);
        reference.updateChildren(hashMap);

    }

    @Override
    protected void onStart() {

        status("online");

        super.onStart();
    }


    @Override
    protected void onPause() {

        super.onPause();

        //status("offline");

        SimpleDateFormat dform = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date obj = new Date();
        //System.out.println(dform.format(obj));

        status(dform.format(obj));
    }


    @Override
    protected void onResume() {

        status("online");

        super.onResume();


    }

}
