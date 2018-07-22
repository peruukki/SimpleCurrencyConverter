package com.peruukki.simplecurrencyconverter.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import com.peruukki.simplecurrencyconverter.utils.Tab

class TabAdapter(fm: FragmentManager, private val tabs: List<Tab>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return tabs[position].content
    }

    override fun getCount(): Int {
        return tabs.size
    }
}
