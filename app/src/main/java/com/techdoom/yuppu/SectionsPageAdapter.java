package com.techdoom.yuppu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class SectionsPageAdapter extends FragmentPagerAdapter {
    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;


            case 1:

                NotificationFragment notificationFragment = new NotificationFragment();
                return notificationFragment;

            case 2:

                GroupFragment groupFragment = new GroupFragment();
                return groupFragment;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "CHATS";

            case 1:
                return "REQUEST";

            case 2:
                return "GROUPS";

            default:
                return null;
        }

    }

}
