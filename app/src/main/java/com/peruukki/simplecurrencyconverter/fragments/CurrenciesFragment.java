package com.peruukki.simplecurrencyconverter.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.peruukki.simplecurrencyconverter.R;
import com.peruukki.simplecurrencyconverter.adapters.ConversionRateListAdapter;
import com.peruukki.simplecurrencyconverter.db.ConverterContract;
import com.peruukki.simplecurrencyconverter.db.ConverterDbHelper;
import com.peruukki.simplecurrencyconverter.models.ConversionRate;
import com.peruukki.simplecurrencyconverter.network.FetchConversionRatesTask;
import com.peruukki.simplecurrencyconverter.utils.Settings;

import java.util.List;

/**
 * A {@link Fragment} that contains widgets to select currently used currencies.
 */
public class CurrenciesFragment extends ListFragment
    implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>,
        FetchConversionRatesTask.OnConversionRatesFetchedListener {

    private static final String LOG_TAG = CurrenciesFragment.class.getSimpleName();

    private static final int CONVERSION_RATES_LOADER_ID = 0;

    private static final long STATUS_RESET_DELAY_MS = 1000;

    private Handler mTimerHandler = new Handler();

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

        final Context context = getActivity();
        TextSwitcher updateText = (TextSwitcher) rootView.findViewById(R.id.textswitcher_update_conversion_rates);
        Animation in = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        updateText.setInAnimation(in);
        updateText.setOutAnimation(out);
        updateText.setFactory(() -> {
            TextView textView = new TextView(context);
            textView.setGravity(Gravity.CENTER);
            return textView;
        });

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
            mListener.onCurrenciesSelected();
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

        TextSwitcher updateText = (TextSwitcher) getActivity().findViewById(R.id.textswitcher_update_conversion_rates);
        updateText.setText(getString(R.string.fetching_conversion_rates));
        updateButton.setEnabled(false);

        new FetchConversionRatesTask(getActivity(), this).execute();
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

    @Override
    public void onConversionRatesUpdated(List<ConversionRate> conversionRates) {
        View rootView = getView();
        if (rootView == null) {
            Log.e(LOG_TAG, "rootView was null in onConversionRatesUpdated");
            return;
        }

        // Find updated rate of currently selected conversion rate
        ListView listView = getListView();
        Cursor cursor = (Cursor) listView.getItemAtPosition(listView.getCheckedItemPosition());
        ConversionRate selectedConversionRate = ConverterDbHelper.conversionRateFromCursor(cursor);
        ConversionRate updatedConversionRate = conversionRates.get(conversionRates.indexOf(selectedConversionRate));

        // Store updated rate and notify the Convert tab about it
        Settings.writeConversionRate(getActivity(), updatedConversionRate);
        mListener.onConversionRatesUpdated();

        // Enable update button and set status text
        Button updateButton = (Button) rootView.findViewById(R.id.button_update_conversion_rates);
        updateButton.setEnabled(true);
        TextSwitcher statusText = (TextSwitcher) rootView.findViewById(R.id.textswitcher_update_conversion_rates);
        statusText.setText(getString(R.string.updated));
        clearUpdateStatusAfterDelay(statusText);
    }

    @Override
    public void onUpdateFailed(String errorMessage) {
        View rootView = getView();
        if (rootView == null) {
            Log.e(LOG_TAG, "rootView was null in onUpdateFailed");
            return;
        }

        // Enable update button and set status text
        Button updateButton = (Button) rootView.findViewById(R.id.button_update_conversion_rates);
        updateButton.setEnabled(true);
        TextSwitcher statusText = (TextSwitcher) rootView.findViewById(R.id.textswitcher_update_conversion_rates);
        statusText.setText(errorMessage);
    }

    private void clearUpdateStatusAfterDelay(final TextSwitcher statusText) {
        Runnable timerRunnable = () -> statusText.setText("");
        mTimerHandler.postDelayed(timerRunnable, STATUS_RESET_DELAY_MS);
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
        public void onCurrenciesSelected();
        public void onConversionRatesUpdated();
    }
}
