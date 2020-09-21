package com.teammandroid.chatnow.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.adapters.HomeActivityTabsAdapter;
import com.teammandroid.chatnow.adapters.TabbedViewPagerAdapter;
import com.teammandroid.chatnow.fragments.DocumentsListFrgment;
import com.teammandroid.chatnow.fragments.LinksListFrgment;
import com.teammandroid.chatnow.fragments.MediaListFrgment;
import com.teammandroid.chatnow.models.OfflineUserModel;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;

public class MediaListActivity extends AppCompatActivity {

    private static final String TAG = MediaListActivity.class.getSimpleName() ;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private HomeActivityTabsAdapter adapter;
    private Bundle bundle;
    ImageView img_back;
    TextView txtTitleBar;
    FirebaseUserModel chattingPartner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        bindView();

        bundle = getIntent().getExtras();
        if (bundle != null) {
            chattingPartner = bundle.getParcelable("chattingPartner");
            Log.e(TAG, "onCreate:chattingPartner "+chattingPartner.toString());
            txtTitleBar.setText(chattingPartner.getUsername());
        }

        createViewPager( viewPager);
        tabLayout.setupWithViewPager(viewPager);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void bindView() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        img_back = findViewById(R.id.img_back);
        txtTitleBar = findViewById(R.id.txtTitleBar);
    }


    private void createViewPager(ViewPager viewPager) {
        TabbedViewPagerAdapter adapter = new TabbedViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putParcelable("chattingPartner",chattingPartner);

        MediaListFrgment mediaListFrgment = new MediaListFrgment();
        mediaListFrgment.setArguments(bundle);
        adapter.addFrag(mediaListFrgment, "MEDIA");

        DocumentsListFrgment documentsListFrgment = new DocumentsListFrgment();
        documentsListFrgment.setArguments(bundle);
        adapter.addFrag(documentsListFrgment, "DOCS");

        LinksListFrgment linksListFrgment = new LinksListFrgment();
        linksListFrgment.setArguments(bundle);
        adapter.addFrag(linksListFrgment, "LINKS");

        viewPager.setAdapter(adapter);
    }

}
