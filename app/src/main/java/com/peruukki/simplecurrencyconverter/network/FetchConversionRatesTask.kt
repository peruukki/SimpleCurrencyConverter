package com.peruukki.simplecurrencyconverter.network

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.util.Log

import com.peruukki.simplecurrencyconverter.R
import com.peruukki.simplecurrencyconverter.models.ConversionRate
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request

import org.json.JSONException
import org.json.JSONObject

import java.io.IOException
import java.net.URL
import java.util.*

/**
 * The background task to use for fetching updated conversion rates. Updates the rates in the
 * database after fetching.
 */
class FetchConversionRatesTask
/**
 * Creates a new background task for fetching updated conversion rates.
 *
 * @param context  application context
 * @param updateListener  listener to notify after successful fetching
 */
(private val mContext: Context, private val mUpdateListener: OnConversionRatesFetchedListener) : AsyncTask<Void, Void, String>() {
    private var mConversionRates: List<ConversionRate> = ArrayList()

    private val currencyPairsForApiQuery: String
        get() {
            val currencyPairs = StringBuilder()
            for (conversionRate in ConversionRate.allRates) {
                if (conversionRate != ConversionRate.allRates[0]) {
                    currencyPairs.append(",")
                }
                currencyPairs.append(conversionRateToApiKeyName(conversionRate))
            }
            return currencyPairs.toString()
        }

    override fun doInBackground(vararg params: Void): String? {
        val client = OkHttpClient()
        val uri = Uri.parse("https://free.currencyconverterapi.com/api/v5/convert").buildUpon()
                .appendQueryParameter("q", currencyPairsForApiQuery)
                .build()

        try {
            val url = URL(uri.toString())

            Log.i(LOG_TAG, "Fetching conversion rates from $url")
            val request = Request.Builder()
                    .url(url)
                    .build()
            val responseBody = client.newCall(request)
                    .execute()
                    .body()
            val response = responseBody.string()
            Log.i(LOG_TAG, "Conversion rates response: '$response'")

            val conversionRates = parseConversionRates(response)
            for (conversionRate in conversionRates) {
                Log.d(LOG_TAG, "Parsed conversion rate: " + conversionRate.fixedCurrencyRateString +
                        " <-> " + conversionRate.variableCurrencyRateString)
            }
            storeConversionRates(conversionRates)
            mConversionRates = conversionRates

            responseBody.close()
            return null
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Error fetching conversion rates", e)
            return mContext.getString(R.string.failed_to_fetch_conversion_rates)
        } catch (e: JSONException) {
            Log.e(LOG_TAG, "Error parsing conversion rates to JSON", e)
            return mContext.getString(R.string.unexpected_conversion_rate_data)
        } catch (e: NumberFormatException) {
            Log.e(LOG_TAG, "Error converting JSON string to number", e)
            return mContext.getString(R.string.invalid_conversion_rates)
        }

    }

    private fun conversionRateToApiKeyName(conversionRate: ConversionRate): String {
        return conversionRate.fixedCurrency + "_" + conversionRate.variableCurrency
    }

    @Throws(JSONException::class)
    private fun parseConversionRates(responseBody: String): List<ConversionRate> {
        val responseJson = JSONObject(responseBody)
        val results = responseJson.getJSONObject("results")

        val conversionRates = ArrayList<ConversionRate>()
        for (conversionRate in ConversionRate.allRates) {
            val rate = results.getJSONObject(conversionRateToApiKeyName(conversionRate))
            val rateValue = java.lang.Float.valueOf(rate.getString("val"))
            conversionRates.add(ConversionRate(
                    conversionRate.fixedCurrency,
                    conversionRate.variableCurrency,
                    rateValue
            ))
        }
        return conversionRates
    }

    private fun storeConversionRates(conversionRates: List<ConversionRate>) {
        for (conversionRate in conversionRates) {
            conversionRate.writeToDb(mContext.contentResolver)
        }
    }

    override fun onPostExecute(errorMessage: String?) {
        super.onPostExecute(errorMessage)

        if (errorMessage == null) {
            mUpdateListener.onConversionRatesUpdated(mConversionRates)
        } else {
            mUpdateListener.onUpdateFailed(errorMessage)
        }
    }

    interface OnConversionRatesFetchedListener {
        fun onConversionRatesUpdated(conversionRates: List<ConversionRate>)
        fun onUpdateFailed(errorMessage: String)
    }

    companion object {

        private val LOG_TAG = FetchConversionRatesTask::class.java.simpleName
    }
}
