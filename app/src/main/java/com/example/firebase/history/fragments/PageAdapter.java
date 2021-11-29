package com.example.firebase.history.fragments;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.firebase.history.Constants;

public class PageAdapter extends FragmentPagerAdapter {

    int tabCount;
    private Context context;

    public PageAdapter(@NonNull FragmentManager fm, int behavior, Context context) {
        super(fm, behavior);
        this.tabCount = behavior;
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case Constants.GAS_TAB : return new GasFragment(context);
            case Constants.OIL_TAB : return new OilFragment(context);
            case Constants.REPAIR_TAB : return new RepairFragment(context);
            default: return new GasFragment(context);
        }

    }

    @Override
    public int getCount() {
        return tabCount;
    }
}

