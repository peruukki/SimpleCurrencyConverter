package com.simplecurrencyconverter.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.simplecurrencyconverter.utils.Tab;

import java.util.List;

public class TabAdapter extends FragmentPagerAdapter {

    private List<Tab> tabs;

    public TabAdapter(FragmentManager fm, List<Tab> tabs) {
        super(fm);
        this.tabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position).getContent();
    }

    @Override
    public int getCount() {
        return tabs.size();
    }
}
