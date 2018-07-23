package com.peruukki.simplecurrencyconverter.fragments

import android.app.Activity
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ListFragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ListView
import android.widget.TextSwitcher
import android.widget.TextView

import com.peruukki.simplecurrencyconverter.R
import com.peruukki.simplecurrencyconverter.adapters.ConversionRateListAdapter
import com.peruukki.simplecurrencyconverter.db.ConverterContract
import com.peruukki.simplecurrencyconverter.db.ConverterDbHelper
import com.peruukki.simplecurrencyconverter.models.ConversionRate
import com.peruukki.simplecurrencyconverter.network.FetchConversionRatesTask
import com.peruukki.simplecurrencyconverter.utils.Settings

/**
 * A [Fragment] that contains widgets to select currently used currencies.
 */
/**
 * Creates an instance of the fragment.
 */
class CurrenciesFragment : ListFragment(), View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>, FetchConversionRatesTask.OnConversionRatesFetchedListener {

    private val mTimerHandler = Handler()

    private var mListener: OnFragmentInteractionListener? = null

    private var mListAdapter: ConversionRateListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mListAdapter = ConversionRateListAdapter(activity, null, 0)
        listAdapter = mListAdapter

        val rootView = inflater!!.inflate(R.layout.fragment_currencies, container, false)

        val updateButton = rootView.findViewById(R.id.button_update_conversion_rates) as Button
        updateButton.setOnClickListener(this)

        val context = activity
        val updateText = rootView.findViewById(R.id.textswitcher_update_conversion_rates) as TextSwitcher
        updateText.inAnimation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        updateText.outAnimation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
        updateText.setFactory {
            val textView = TextView(context)
            textView.gravity = Gravity.CENTER
            textView
        }

        return rootView
    }

    private fun setCurrentItemSelected(listView: ListView, selectedConversionRate: ConversionRate) {
        val itemCount = listView.count
        for (i in 0 until itemCount) {
            val cursor = listView.getItemAtPosition(i) as Cursor
            val rate = ConverterDbHelper.conversionRateFromCursor(cursor)
            if (rate == selectedConversionRate) {
                listView.setItemChecked(i, true)
            }
        }
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        try {
            mListener = activity as OnFragmentInteractionListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(activity!!.toString() + " must implement OnFragmentInteractionListener")
        }

    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        l!!.setItemChecked(position, true)

        if (mListener != null) {
            val cursor = l.getItemAtPosition(position) as Cursor
            Settings.writeConversionRate(activity, ConverterDbHelper.conversionRateFromCursor(cursor))
            mListener!!.onCurrenciesSelected()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_update_conversion_rates -> updateConversionRates(v)
            else -> Log.e(LOG_TAG, "Unknown view in onClick: $v")
        }
    }

    private fun updateConversionRates(updateButton: View) {
        Log.i(LOG_TAG, "Updating conversion rates")

        val updateText = activity.findViewById(R.id.textswitcher_update_conversion_rates) as TextSwitcher
        updateText.setText(getString(R.string.fetching_conversion_rates))
        updateButton.isEnabled = false

        FetchConversionRatesTask(activity, this).execute()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loaderManager.initLoader(CONVERSION_RATES_LOADER_ID, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(activity, ConverterContract.BASE_CONTENT_URI, null, null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        mListAdapter!!.swapCursor(data)
        setCurrentItemSelected(listView, Settings.readConversionRate(activity))
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mListAdapter!!.swapCursor(null)
    }

    override fun onConversionRatesUpdated(conversionRates: List<ConversionRate>) {
        val rootView = view
        if (rootView == null) {
            Log.e(LOG_TAG, "rootView was null in onConversionRatesUpdated")
            return
        }

        // Find updated rate of currently selected conversion rate
        val listView = listView
        val cursor = listView.getItemAtPosition(listView.checkedItemPosition) as Cursor
        val selectedConversionRate = ConverterDbHelper.conversionRateFromCursor(cursor)
        val updatedConversionRate = conversionRates[conversionRates.indexOf(selectedConversionRate)]

        // Store updated rate and notify the Convert tab about it
        Settings.writeConversionRate(activity, updatedConversionRate)
        mListener!!.onConversionRatesUpdated()

        // Enable update button and set status text
        val updateButton = rootView.findViewById(R.id.button_update_conversion_rates) as Button
        updateButton.isEnabled = true
        val statusText = rootView.findViewById(R.id.textswitcher_update_conversion_rates) as TextSwitcher
        statusText.setText(getString(R.string.updated))
        clearUpdateStatusAfterDelay(statusText)
    }

    override fun onUpdateFailed(errorMessage: String) {
        val rootView = view
        if (rootView == null) {
            Log.e(LOG_TAG, "rootView was null in onUpdateFailed")
            return
        }

        // Enable update button and set status text
        val updateButton = rootView.findViewById(R.id.button_update_conversion_rates) as Button
        updateButton.isEnabled = true
        val statusText = rootView.findViewById(R.id.textswitcher_update_conversion_rates) as TextSwitcher
        statusText.setText(errorMessage)
    }

    private fun clearUpdateStatusAfterDelay(statusText: TextSwitcher) {
        val timerRunnable = { statusText.setText("") }
        mTimerHandler.postDelayed(timerRunnable, STATUS_RESET_DELAY_MS)
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        fun onCurrenciesSelected()
        fun onConversionRatesUpdated()
    }

    companion object {

        private val LOG_TAG = CurrenciesFragment::class.java.simpleName

        private val CONVERSION_RATES_LOADER_ID = 0

        private val STATUS_RESET_DELAY_MS: Long = 1000
    }
}
