package com.simplecurrencyconverter.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.simplecurrencyconverter.R;
import com.simplecurrencyconverter.utils.ConversionRate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A {@link Fragment} that contains widgets to select currently used currencies.
 */
public class CurrenciesFragment extends ListFragment {

    private List<ConversionRate> mConversionRates;

    private OnFragmentInteractionListener mListener;

    /**
     * Creates an instance of the fragment.
     */
    public CurrenciesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConversionRate[] conversionRates = {
            new ConversionRate(getString(R.string.eur_label), getString(R.string.krw_label), new BigDecimal(1209.15)),
            new ConversionRate(getString(R.string.eur_label), getString(R.string.hkd_label), new BigDecimal(8.33)),
            new ConversionRate(getString(R.string.eur_label), getString(R.string.mop_label), new BigDecimal(8.58)),
            new ConversionRate(getString(R.string.eur_label), getString(R.string.cny_label), new BigDecimal(6.73))
        };
        mConversionRates = Arrays.asList(conversionRates);

        ArrayAdapter<ConversionRate> adapter = new ArrayAdapter<>(getActivity(),
            R.layout.list_item_currencies, R.id.list_item_currencies_textview, mConversionRates);
        setListAdapter(adapter);

        mListener.onCurrenciesFragmentInteraction(mConversionRates.get(0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_currencies, container, false);
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

        if (mListener != null) {
            ConversionRate conversionRate = (ConversionRate) l.getItemAtPosition(position);
            mListener.onCurrenciesFragmentInteraction(conversionRate);
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
        public void onCurrenciesFragmentInteraction(ConversionRate selectedConversionRate);
    }
}
