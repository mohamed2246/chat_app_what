package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class tabs_accessors_fragment extends FragmentPagerAdapter {
    public tabs_accessors_fragment(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){

            case 0 :
                chats chats = new chats();
                return chats;

            case 1 :
                group_fragment group_fragment = new group_fragment();
                return group_fragment;


            case 2 :
                Contact_fragment contact_fragment = new Contact_fragment();
                return contact_fragment;
            case 3 :
                requests_fragment requests_fragment = new requests_fragment();
                return requests_fragment;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){

            case 0 :
                return "Chats";

            case 1 :
                group_fragment group_fragment = new group_fragment();
                return "Groups";

            case 2 :
                Contact_fragment contact_fragment = new Contact_fragment();
                return "Contacts";

            case 3 :
                requests_fragment requests_fragment = new requests_fragment();
                return "Requests";

            default:
                return null;
        }



    }
}
