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
import com.teammandroid.chatnow.fragments.GroupDocListFragment;
import com.teammandroid.chatnow.fragments.GroupLinkListFragment;
import com.teammandroid.chatnow.fragments.GroupMediaListFragment;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;
import com.teammandroid.chatnow.models.firebase.GroupChatList;

public class GroupMediaListActivity extends AppCompatActivity {

    private static final String TAG = GroupMediaListActivity.class.getSimpleName() ;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private HomeActivityTabsAdapter adapter;
    private Bundle bundle;
    ImageView img_back;
    TextView txtTitleBar;
    FirebaseUserModel chattingPartner;

    GroupChatList groupChatList;

    String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_media_list);

        bindView();

        bundle = getIntent().getExtras();

        if (bundle != null) {

            //groupId=bundle.getString("groupId");

            groupChatList=bundle.getParcelable("groupChatModel");

            Log.e("groupMedia","groupMedia"+groupId+" "+groupChatList);

            groupId=groupChatList.getGroupId();

            txtTitleBar.setText(groupChatList.getGroupTitle());

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
        bundle.putParcelable("groupChatList",groupChatList);

        GroupMediaListFragment groupMediaListFragment=new GroupMediaListFragment();
        groupMediaListFragment.setArguments(bundle);
        adapter.addFrag(groupMediaListFragment, "MEDIA");

        GroupDocListFragment groupDocListFragment=new GroupDocListFragment();
        groupDocListFragment.setArguments(bundle);
        adapter.addFrag(groupDocListFragment, "DOCS");

        GroupLinkListFragment groupLinkListFragment=new GroupLinkListFragment();
        groupLinkListFragment.setArguments(bundle);
        adapter.addFrag(groupLinkListFragment, "LINKS");

        viewPager.setAdapter(adapter);
    }
}
