package com.simplecurrencyconverter.network;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextSwitcher;

import com.simplecurrencyconverter.models.ConversionRate;
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

    private static final long STATUS_RESET_DELAY_MS = 1000;

    private Handler mTimerHandler = new Handler();

    private Context mContext;
    private View mUpdateButton;
    private TextSwitcher mUpdateTextSwitcher;

    /**
     * Creates a new background task for fetching updated conversion rates.
     *
     * @param context  application context
     * @param updateButton  update button to enable after the data has been fetched
     * @param updateTextSwitcher  status text view to update after the data has been fetched
     */
    public FetchConversionRatesTask(Context context, View updateButton, TextSwitcher updateTextSwitcher) {
        mContext = context;
        mUpdateButton = updateButton;
        mUpdateTextSwitcher = updateTextSwitcher;
    }

    @Override
    protected String doInBackground(Void... params) {
        OkHttpClient client = new OkHttpClient();
        Uri uri = Uri.parse("https://query.yahooapis.com/v1/public/yql").buildUpon()
            .appendQueryParameter("q", "select * from yahoo.finance.xchange where pair in (" + getCurrencyPairsForApiQuery() + ")")
            .appendQueryParameter("format", "json")
            .appendQueryParameter("env", "store://datatables.org/alltableswithkeys")
            .appendQueryParameter("callback", "")
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

            responseBody.close();
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error fetching conversion rates", e);
            return "Failed to fetch conversion rates.";
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing conversion rates to JSON", e);
            return "Received unexpected conversion rate data.";
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "Error converting JSON string to number", e);
            return "Received invalid conversion rates.";
        }
    }

    private String getCurrencyPairsForApiQuery() {
        StringBuilder currencyPairs = new StringBuilder();
        for (ConversionRate conversionRate : ConversionRate.ALL) {
            if (!conversionRate.equals(ConversionRate.ALL.get(0))) {
                currencyPairs.append(", ");
            }
            currencyPairs.append("'")
                .append(conversionRate.getFixedCurrency())
                .append(conversionRate.getVariableCurrency())
                .append("'");
        }
        return currencyPairs.toString();
    }

    private List<ConversionRate> parseConversionRates(String responseBody) throws JSONException {
        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray rates = responseJson.getJSONObject("query")
            .getJSONObject("results")
            .getJSONArray("rate");

        List<ConversionRate> conversionRates = new ArrayList<>();
        for (int i = 0; i < rates.length(); i++) {
            JSONObject rate = rates.getJSONObject(i);
            String[] currencies = rate.getString("Name").split("/");
            Float conversionRate = Float.valueOf(rate.getString("Rate"));
            conversionRates.add(new ConversionRate(currencies[0], currencies[1], conversionRate));
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

        boolean isError = errorMessage != null;
        String statusMessage = isError ? errorMessage : "Updated.";
        mUpdateTextSwitcher.setText(statusMessage);
        clearUpdateStatusAfterDelay(!isError);
    }

    private void clearUpdateStatusAfterDelay(final boolean resetStatusText) {
        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                mUpdateButton.setEnabled(true);
                if (resetStatusText) {
                    mUpdateTextSwitcher.setText("");
                }
            }
        };
        mTimerHandler.postDelayed(timerRunnable, STATUS_RESET_DELAY_MS);
    }
}
