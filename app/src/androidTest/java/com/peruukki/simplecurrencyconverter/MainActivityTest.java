package com.peruukki.simplecurrencyconverter;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.test.ActivityInstrumentationTestCase2;

import com.peruukki.simplecurrencyconverter.adapters.TabAdapter;
import com.peruukki.simplecurrencyconverter.fragments.ConvertFragment;
import com.peruukki.simplecurrencyconverter.fragments.CurrenciesFragment;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mMainActivity;
    private TabAdapter mTabAdapter;
    private ActionBar mActionBar;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mMainActivity = getActivity();

        ViewPager tabPager = (ViewPager) mMainActivity.findViewById(R.id.pager);
        mTabAdapter = (TabAdapter) tabPager.getAdapter();

        mActionBar = mMainActivity.getSupportActionBar();
    }

    public void testPreconditions() {
        assertNotNull("TabAdapter is null", mTabAdapter);
        assertNotNull("ActionBar is null", mActionBar);
    }

    public void testMainActivity_tabsCreated() {
        assertEquals("TabAdapter doesn't have 2 tabs", 2, mTabAdapter.getCount());
        assertEquals("ActionBar doesn't have 2 tabs", 2, mActionBar.getTabCount());

        assertEquals("1st tab is not instance of ConvertFragment",
            ConvertFragment.class, mTabAdapter.getItem(0).getClass());
        assertEquals("2nd tab is not instance of CurrenciesFragment",
            CurrenciesFragment.class, mTabAdapter.getItem(1).getClass());

        assertEquals("1st tab has unexpected label",
            mMainActivity.getString(R.string.tab_convert), mActionBar.getTabAt(0).getText());
        assertEquals("2nd tab has unexpected label",
            mMainActivity.getString(R.string.tab_currencies), mActionBar.getTabAt(1).getText());
    }
}
