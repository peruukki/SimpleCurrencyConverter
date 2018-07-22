package com.peruukki.simplecurrencyconverter.utils

import android.support.v4.app.Fragment

/**
 * A container class for a tab's title and content Fragment.
 */
class Tab
/**
 * Creates a container for a tab's title and content Fragment.
 *
 * @param title  the tab title
 * @param content  the tab content Fragment
 */
(
        /**
         * The tab title.
         */
        val title: String,
        /**
         * The tab content Fragment.
         */
        val content: Fragment)
