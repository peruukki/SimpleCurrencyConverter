package com.peruukki.simplecurrencyconverter

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarActivity
import android.view.inputmethod.InputMethodManager

import com.peruukki.simplecurrencyconverter.adapters.TabAdapter
import com.peruukki.simplecurrencyconverter.fragments.ConvertFragment
import com.peruukki.simplecurrencyconverter.fragments.CurrenciesFragment
import com.peruukki.simplecurrencyconverter.utils.Tab

import java.util.*

class MainActivity : ActionBarActivity(), CurrenciesFragment.OnFragmentInteractionListener {

    private var mConvertTab: ConvertFragment? = null

    private var mViewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hideActionBar()

        val tabs = ArrayList<Tab>()
        val currenciesTab: Fragment
        if (savedInstanceState == null) {
            mConvertTab = ConvertFragment()
            currenciesTab = CurrenciesFragment()
        } else {
            val fm = supportFragmentManager
            val existingTabs = fm.fragments
            mConvertTab = existingTabs[0] as ConvertFragment
            currenciesTab = existingTabs[1]
        }
        tabs.add(Tab(getString(R.string.tab_convert), mConvertTab!!))
        tabs.add(Tab(getString(R.string.tab_currencies), currenciesTab))
        addTabs(tabs)
    }

    private fun hideActionBar() {
        supportActionBar!!.title = ""
    }

    private fun addTabs(tabs: List<Tab>) {
        val tabPagerAdapter = TabAdapter(supportFragmentManager, tabs)
        val actionBar = supportActionBar
        mViewPager = findViewById(R.id.pager) as ViewPager?

        // Add swipe views
        mViewPager!!.adapter = tabPagerAdapter
        mViewPager!!.setOnPageChangeListener(
                object : ViewPager.SimpleOnPageChangeListener() {
                    override fun onPageSelected(position: Int) {
                        actionBar!!.setSelectedNavigationItem(position)
                    }
                }
        )

        // Add tabs to action bar
        actionBar!!.navigationMode = ActionBar.NAVIGATION_MODE_TABS
        val tabListener = object : ActionBar.TabListener {
            override fun onTabSelected(tab: ActionBar.Tab, fragmentTransaction: FragmentTransaction) {
                hideKeyboard()
                mViewPager!!.currentItem = tab.position
            }

            override fun onTabUnselected(tab: ActionBar.Tab, fragmentTransaction: FragmentTransaction) {}
            override fun onTabReselected(tab: ActionBar.Tab, fragmentTransaction: FragmentTransaction) {}
        }

        for (tab in tabs) {
            actionBar.addTab(actionBar.newTab()
                    .setText(tab.title)
                    .setTabListener(tabListener))
        }
    }

    private fun hideKeyboard() {
        // From http://stackoverflow.com/a/7696791
        val view = this.currentFocus
        if (view != null) {
            val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    override fun onCurrenciesSelected() {
        onConversionRatesUpdated()
        mViewPager!!.currentItem = 0
    }

    override fun onConversionRatesUpdated() {
        mConvertTab!!.updateConversionRate()
    }
}
