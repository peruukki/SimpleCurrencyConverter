package com.peruukki.simplecurrencyconverter

import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.test.ActivityInstrumentationTestCase2

import com.peruukki.simplecurrencyconverter.adapters.TabAdapter
import com.peruukki.simplecurrencyconverter.fragments.ConvertFragment
import com.peruukki.simplecurrencyconverter.fragments.CurrenciesFragment

import junit.framework.Assert

class MainActivityTest : ActivityInstrumentationTestCase2<MainActivity>(MainActivity::class.java) {

    private var mMainActivity: MainActivity? = null
    private var mTabAdapter: TabAdapter? = null
    private var mActionBar: ActionBar? = null

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()

        mMainActivity = activity

        val tabPager = mMainActivity!!.findViewById(R.id.pager) as ViewPager?
        mTabAdapter = tabPager!!.adapter as TabAdapter

        mActionBar = mMainActivity!!.supportActionBar
    }

    fun testPreconditions() {
        Assert.assertNotNull("TabAdapter is null", mTabAdapter)
        Assert.assertNotNull("ActionBar is null", mActionBar)
    }

    fun testMainActivity_tabsCreated() {
        Assert.assertEquals("TabAdapter doesn't have 2 tabs", 2, mTabAdapter!!.count)
        Assert.assertEquals("ActionBar doesn't have 2 tabs", 2, mActionBar!!.tabCount)

        Assert.assertEquals("1st tab is not instance of ConvertFragment",
                ConvertFragment::class.java, mTabAdapter!!.getItem(0).javaClass)
        Assert.assertEquals("2nd tab is not instance of CurrenciesFragment",
                CurrenciesFragment::class.java, mTabAdapter!!.getItem(1).javaClass)

        Assert.assertEquals("1st tab has unexpected label",
                mMainActivity!!.getString(R.string.tab_convert), mActionBar!!.getTabAt(0).text)
        Assert.assertEquals("2nd tab has unexpected label",
                mMainActivity!!.getString(R.string.tab_currencies), mActionBar!!.getTabAt(1).text)
    }
}
