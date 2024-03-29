/*
 * Project: Waves
 *
 * Purpose: Updates data set of fragments for gesture-based navigation
 *
 * Reference(s): Briana Berger, Angela Liu
 */

package com.example.waves_app.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class VPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragmentList;

    public VPagerAdapter(FragmentManager manager, List<Fragment> fragmentList) {
        super(manager);
        this.fragmentList = fragmentList;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        // Ensures that each time data set is notified of change that it resets each fragment
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}