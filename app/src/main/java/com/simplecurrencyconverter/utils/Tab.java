package com.simplecurrencyconverter.utils;

import android.support.v4.app.Fragment;

public class Tab {

    private Fragment mContent;
    private String mTitle;

    /**
     * A container for tab title and its content Fragment.
     *
     * @param title  the tab title
     * @param content  the tab content Fragment
     */
    public Tab(String title, Fragment content) {
        mTitle = title;
        mContent = content;
    }

    /**
     * Returns the tab content Fragment.
     *
     * @return the tab content
     */
    public Fragment getContent() {
        return mContent;
    }

    /**
     * Returns the tab title.
     *
     * @return the tab title
     */
    public String getTitle() {
        return mTitle;
    }
}
