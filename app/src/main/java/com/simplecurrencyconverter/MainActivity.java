package com.simplecurrencyconverter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.simplecurrencyconverter.adapters.TabAdapter;
import com.simplecurrencyconverter.fragments.ConvertFragment;
import com.simplecurrencyconverter.fragments.CurrenciesFragment;
import com.simplecurrencyconverter.utils.Tab;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity
    implements CurrenciesFragment.OnFragmentInteractionListener {

    private ConvertFragment mConvertTab;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hideActionBar();

        List<Tab> tabs = new ArrayList<>();
        Fragment currenciesTab;
        if (savedInstanceState == null) {
            mConvertTab = new ConvertFragment();
            currenciesTab = new CurrenciesFragment();
        } else {
            FragmentManager fm = getSupportFragmentManager();
            List<Fragment> existingTabs = fm.getFragments();
            mConvertTab = (ConvertFragment) existingTabs.get(0);
            currenciesTab = existingTabs.get(1);
        }
        tabs.add(new Tab(getString(R.string.tab_convert), mConvertTab));
        tabs.add(new Tab(getString(R.string.tab_currencies), currenciesTab));
        addTabs(tabs);
    }

    private void hideActionBar() {
        getSupportActionBar().setTitle("");
    }

    private void addTabs(List<Tab> tabs) {
        final TabAdapter tabPagerAdapter = new TabAdapter(getSupportFragmentManager(), tabs);
        final ActionBar actionBar = getSupportActionBar();
        mViewPager = (ViewPager) findViewById(R.id.pager);

        // Add swipe views
        mViewPager.setAdapter(tabPagerAdapter);
        mViewPager.setOnPageChangeListener(
            new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    actionBar.setSelectedNavigationItem(position);
                }
            }
        );

        // Add tabs to action bar
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                hideKeyboard();
                mViewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            }
            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            }
        };

        for (Tab tab : tabs) {
            actionBar.addTab(actionBar.newTab()
                .setText(tab.getTitle())
                .setTabListener(tabListener));
        }
    }

    private void hideKeyboard() {
        // From http://stackoverflow.com/a/7696791
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onCurrenciesUpdated() {
        mConvertTab.updateConversionRate();
        mViewPager.setCurrentItem(0);
    }
}
