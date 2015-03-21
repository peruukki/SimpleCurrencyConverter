package com.simplecurrencyconverter.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.simplecurrencyconverter.R;
import com.simplecurrencyconverter.adapters.ConversionRateListAdapter;
import com.simplecurrencyconverter.utils.ConversionRate;
import com.simplecurrencyconverter.utils.Settings;

/**
 * A {@link Fragment} that contains widgets to select currently used currencies.
 */
public class CurrenciesFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;

    /**
     * Creates an instance of the fragment.
     */
    public CurrenciesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new ConversionRateListAdapter(getActivity(),
            R.layout.list_item_currencies, R.id.list_item_currency_view,
            ConversionRate.ALL));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_currencies, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCurrentItemSelected(getListView(), Settings.readConversionRate(this));
    }

    private void setCurrentItemSelected(ListView listView, ConversionRate selectedConversionRate) {
        int itemCount = listView.getCount();
        for (int i = 0; i < itemCount; i++) {
            ConversionRate rate = (ConversionRate) listView.getItemAtPosition(i);
            if (rate.equals(selectedConversionRate)) {
                listView.setItemChecked(i, true);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        l.setItemChecked(position, true);

        if (mListener != null) {
            ConversionRate conversionRate = (ConversionRate) l.getItemAtPosition(position);
            Settings.writeConversionRate(this, conversionRate);
            mListener.onCurrenciesUpdated();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onCurrenciesUpdated();
    }
}
