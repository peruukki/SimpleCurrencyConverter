package com.simplecurrencyconverter.utils;

import android.support.v4.app.Fragment;

public class Tab {

    private Fragment mContent;
    private String mTitle;

    public Tab(String title, Fragment content) {
        mTitle = title;
        mContent = content;
    }

    public Fragment getContent() {
        return mContent;
    }

    public String getTitle() {
        return mTitle;
    }
}
