package com.simplecurrencyconverter;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.simplecurrencyconverter.adapters.TabAdapter;
import com.simplecurrencyconverter.fragments.ConvertFragment;
import com.simplecurrencyconverter.fragments.CurrenciesFragment;
import com.simplecurrencyconverter.utils.ConversionRate;
import com.simplecurrencyconverter.utils.Tab;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity
    implements CurrenciesFragment.OnFragmentInteractionListener {

    private ConvertFragment mConvertTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mConvertTab = new ConvertFragment();

        List<Tab> tabs = new ArrayList<>();
        tabs.add(new Tab(getString(R.string.tab_convert), mConvertTab));
        tabs.add(new Tab(getString(R.string.tab_currencies), new CurrenciesFragment()));
        addTabs(tabs);
    }

    private void addTabs(List<Tab> tabs) {
        final TabAdapter tabPagerAdapter = new TabAdapter(getSupportFragmentManager(), tabs);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final ActionBar actionBar = getSupportActionBar();

        // Add swipe views
        viewPager.setAdapter(tabPagerAdapter);
        viewPager.setOnPageChangeListener(
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
                viewPager.setCurrentItem(tab.getPosition());
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

    @Override
    public void onCurrenciesFragmentInteraction(ConversionRate selectedConversionRate) {
        mConvertTab.setConversionRate(selectedConversionRate);
    }
}
