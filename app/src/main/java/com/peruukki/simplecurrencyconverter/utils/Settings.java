package com.peruukki.simplecurrencyconverter.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.peruukki.simplecurrencyconverter.R;
import com.peruukki.simplecurrencyconverter.models.ConversionRate;

public class Settings {

    public static ConversionRate readConversionRate(Activity activity) {
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        ConversionRate defaultRate = ConversionRate.getDefaultRate();

        String fixedCurrency = preferences.getString(activity.getString(R.string.fixed_currency_key),
            defaultRate.getFixedCurrency());
        String variableCurrency = preferences.getString(activity.getString(R.string.variable_currency_key),
            defaultRate.getVariableCurrency());
        Float rate = preferences.getFloat(activity.getString(R.string.conversion_rate_key),
            defaultRate.getFixedCurrencyInVariableCurrencyRate());

        return new ConversionRate(fixedCurrency, variableCurrency, rate);
    }

    public static void writeConversionRate(Activity activity, ConversionRate conversionRate) {
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(activity.getString(R.string.fixed_currency_key), conversionRate.getFixedCurrency());
        editor.putString(activity.getString(R.string.variable_currency_key), conversionRate.getVariableCurrency());
        editor.putFloat(activity.getString(R.string.conversion_rate_key),
            conversionRate.getFixedCurrencyInVariableCurrencyRate());
        editor.commit();
    }

}
