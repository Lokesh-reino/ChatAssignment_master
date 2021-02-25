package com.example.assignment.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.assignment.activities.ContactListFragment;
import com.example.assignment.activities.CreateEntryFragment;
import com.example.assignment.activities.ViewUsersInRecyclerFragment;

public class MyFragmentAdapter extends FragmentStatePagerAdapter {
    public MyFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 1: return new CreateEntryFragment();
            default: return new ViewUsersInRecyclerFragment();
            case 2: return new ContactListFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0: return "View";
            case 1: return "Create Entry";
            case 2: return "Contact List";
        }
        return null;

    }
}
