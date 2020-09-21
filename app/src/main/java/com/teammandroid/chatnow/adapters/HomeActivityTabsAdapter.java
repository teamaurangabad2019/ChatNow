package com.teammandroid.chatnow.adapters;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.teammandroid.chatnow.fragments.ChatsFragment;
import com.teammandroid.chatnow.fragments.ExploreFragment;
import com.teammandroid.chatnow.fragments.FindPeopleFragment;
import com.teammandroid.chatnow.fragments.GroupsFragment;
import com.teammandroid.chatnow.fragments.firebase.GroupChatsFragment;

public class HomeActivityTabsAdapter extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount, Subcategoryid;
    //CustomerDetailsModel model;
    //Constructor to the class

    public HomeActivityTabsAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
        this.Subcategoryid=Subcategoryid;

    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                FindPeopleFragment tab1=new FindPeopleFragment();
                return tab1;

            case 1:
                ChatsFragment tab2=new ChatsFragment();
                return tab2;
            /*
            case 2:
                ExploreFragment tab3=new ExploreFragment();
                return tab3;*/

            case 2:
                GroupChatsFragment tab3=new GroupChatsFragment();
                return tab3;

            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}