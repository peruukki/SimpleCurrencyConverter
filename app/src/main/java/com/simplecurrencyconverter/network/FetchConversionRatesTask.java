package com.simplecurrencyconverter.network;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.simplecurrencyconverter.db.ConverterDbHelper;
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
public class FetchConversionRatesTask extends AsyncTask<Context, Void, Void> {

    private static String LOG_TAG = FetchConversionRatesTask.class.getSimpleName();

    @Override
    protected Void doInBackground(Context... params) {
        Context context = params[0];
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
            storeConversionRates(context, conversionRates);

            responseBody.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error fetching conversion rates", e);
        }
        return null;
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

    private List<ConversionRate> parseConversionRates(String responseBody) {
        List<ConversionRate> conversionRates = new ArrayList<>();
        try {
            JSONObject responseJson = new JSONObject(responseBody);
            JSONArray rates = responseJson.getJSONObject("query")
                .getJSONObject("results")
                .getJSONArray("rate");

            for (int i = 0; i < rates.length(); i++) {
                JSONObject rate = rates.getJSONObject(i);
                String[] currencies = rate.getString("Name").split("/");
                Float conversionRate = Float.valueOf(rate.getString("Rate"));
                conversionRates.add(new ConversionRate(currencies[0], currencies[1], conversionRate));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing conversion rates to JSON", e);
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "Error converting JSON string to number", e);
        }
        return conversionRates;
    }

    private void storeConversionRates(Context context, List<ConversionRate> conversionRates) {
        SQLiteDatabase db = new ConverterDbHelper(context)
            .getWritableDatabase();
        for (ConversionRate conversionRate : conversionRates) {
            conversionRate.writeToDb(db);
        }
        db.close();
    }
}
