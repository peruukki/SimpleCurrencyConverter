package com.simplecurrencyconverter.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.simplecurrencyconverter.R;
import com.simplecurrencyconverter.adapters.ConversionRateListAdapter;
import com.simplecurrencyconverter.db.ConverterContract;
import com.simplecurrencyconverter.db.ConverterDbHelper;
import com.simplecurrencyconverter.models.ConversionRate;
import com.simplecurrencyconverter.network.FetchConversionRatesTask;
import com.simplecurrencyconverter.utils.Settings;

/**
 * A {@link Fragment} that contains widgets to select currently used currencies.
 */
public class CurrenciesFragment extends ListFragment
    implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CONVERSION_RATES_LOADER_ID = 0;

    private static final String LOG_TAG = CurrenciesFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    private ConversionRateListAdapter mListAdapter;

    /**
     * Creates an instance of the fragment.
     */
    public CurrenciesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mListAdapter = new ConversionRateListAdapter(getActivity(), null, 0);
        setListAdapter(mListAdapter);

        View rootView = inflater.inflate(R.layout.fragment_currencies, container, false);
        Button updateButton = (Button) rootView.findViewById(R.id.button_update_conversion_rates);
        updateButton.setOnClickListener(this);
        return rootView;
    }

    private void setCurrentItemSelected(ListView listView, ConversionRate selectedConversionRate) {
        int itemCount = listView.getCount();
        for (int i = 0; i < itemCount; i++) {
            Cursor cursor = (Cursor) listView.getItemAtPosition(i);
            ConversionRate rate = ConverterDbHelper.conversionRateFromCursor(cursor);
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
            Cursor cursor = (Cursor) l.getItemAtPosition(position);
            Settings.writeConversionRate(getActivity(), ConverterDbHelper.conversionRateFromCursor(cursor));
            mListener.onCurrenciesUpdated();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_update_conversion_rates:
                updateConversionRates(v);
                break;
            default:
                Log.e(LOG_TAG, "Unknown view in onClick: " + v);
        }
    }

    private void updateConversionRates(View updateButton) {
        Log.i(LOG_TAG, "Updating conversion rates");
        updateButton.setEnabled(false);
        new FetchConversionRatesTask(getActivity(), updateButton).execute();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CONVERSION_RATES_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), ConverterContract.BASE_CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mListAdapter.swapCursor(data);
        setCurrentItemSelected(getListView(), Settings.readConversionRate(getActivity()));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mListAdapter.swapCursor(null);
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
