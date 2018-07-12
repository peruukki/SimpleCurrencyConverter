package com.peruukki.simplecurrencyconverter.network;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.peruukki.simplecurrencyconverter.R;
import com.peruukki.simplecurrencyconverter.models.ConversionRate;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * The background task to use for fetching updated conversion rates. Updates the rates in the
 * database after fetching.
 */
public class FetchConversionRatesTask extends AsyncTask<Void, Void, String> {

    private static final String LOG_TAG = FetchConversionRatesTask.class.getSimpleName();

    private Context mContext;
    private OnConversionRatesFetchedListener mUpdateListener;
    private List<ConversionRate> mConversionRates = new ArrayList<>();

    /**
     * Creates a new background task for fetching updated conversion rates.
     *
     * @param context  application context
     * @param updateListener  listener to notify after successful fetching
     */
    public FetchConversionRatesTask(Context context, OnConversionRatesFetchedListener updateListener) {
        mContext = context;
        mUpdateListener = updateListener;
    }

    @Override
    protected String doInBackground(Void... params) {
        OkHttpClient client = new OkHttpClient();
        Uri uri = Uri.parse("https://free.currencyconverterapi.com/api/v5/convert").buildUpon()
            .appendQueryParameter("q", getCurrencyPairsForApiQuery())
            .build();

        try {
            URL url = new URL(uri.toString());

            Log.i(LOG_TAG, "Fetching conversion rates from " + url);
            Request request = new Request.Builder()
                .url(url)
                .build();
            ResponseBody responseBody = client.newCall(request)
                .execute()
                .body();
            String response = responseBody.string();
            Log.i(LOG_TAG, "Conversion rates response: '" + response + "'");

            List<ConversionRate> conversionRates = parseConversionRates(response);
            for (ConversionRate conversionRate : conversionRates) {
                Log.d(LOG_TAG, "Parsed conversion rate: " + conversionRate.getFixedCurrencyRateString() +
                    " <-> " + conversionRate.getVariableCurrencyRateString());
            }
            storeConversionRates(conversionRates);
            mConversionRates = conversionRates;

            responseBody.close();
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error fetching conversion rates", e);
            return mContext.getString(R.string.failed_to_fetch_conversion_rates);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing conversion rates to JSON", e);
            return mContext.getString(R.string.unexpected_conversion_rate_data);
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "Error converting JSON string to number", e);
            return mContext.getString(R.string.invalid_conversion_rates);
        }
    }

    private String getCurrencyPairsForApiQuery() {
        StringBuilder currencyPairs = new StringBuilder();
        for (ConversionRate conversionRate : ConversionRate.ALL) {
            if (!conversionRate.equals(ConversionRate.ALL.get(0))) {
                currencyPairs.append(",");
            }
            currencyPairs.append(conversionRateToApiKeyName(conversionRate));
        }
        return currencyPairs.toString();
    }

    private String conversionRateToApiKeyName(ConversionRate conversionRate) {
        return conversionRate.getFixedCurrency() + "_" + conversionRate.getVariableCurrency();
    }

    private List<ConversionRate> parseConversionRates(String responseBody) throws JSONException {
        JSONObject responseJson = new JSONObject(responseBody);
        JSONObject results = responseJson.getJSONObject("results");

        List<ConversionRate> conversionRates = new ArrayList<>();
        for (ConversionRate conversionRate : ConversionRate.ALL) {
            JSONObject rate = results.getJSONObject(conversionRateToApiKeyName(conversionRate));
            Float rateValue = Float.valueOf(rate.getString("val"));
            conversionRates.add(new ConversionRate(
                conversionRate.getFixedCurrency(),
                conversionRate.getVariableCurrency(),
                rateValue
            ));
        }
        return conversionRates;
    }

    private void storeConversionRates(List<ConversionRate> conversionRates) {
        for (ConversionRate conversionRate : conversionRates) {
            conversionRate.writeToDb(mContext.getContentResolver());
        }
    }

    @Override
    protected void onPostExecute(String errorMessage) {
        super.onPostExecute(errorMessage);

        if (errorMessage == null) {
            mUpdateListener.onConversionRatesUpdated(mConversionRates);
        } else {
            mUpdateListener.onUpdateFailed(errorMessage);
        }
    }

    public interface OnConversionRatesFetchedListener {
        public void onConversionRatesUpdated(List<ConversionRate> conversionRates);
        public void onUpdateFailed(String errorMessage);
    }
}
