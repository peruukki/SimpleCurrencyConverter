package com.simplecurrencyconverter.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

import com.simplecurrencyconverter.R;
import com.simplecurrencyconverter.models.ConversionRate;

public class Settings {

    public static ConversionRate readConversionRate(Fragment fragment) {
        SharedPreferences preferences = fragment.getActivity().getPreferences(Context.MODE_PRIVATE);

        ConversionRate defaultRate = ConversionRate.DEFAULT;
        String fixedCurrency = preferences.getString(fragment.getString(R.string.fixed_currency_key),
            defaultRate.getFixedCurrency());
        String variableCurrency = preferences.getString(fragment.getString(R.string.variable_currency_key),
            defaultRate.getVariableCurrency());

        ConversionRate storedConversionRate = ConversionRate.fromCurrencies(fixedCurrency, variableCurrency);
        return storedConversionRate != null ? storedConversionRate : defaultRate;
    }

    public static void writeConversionRate(Fragment fragment, ConversionRate conversionRate) {
        SharedPreferences preferences = fragment.getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(fragment.getString(R.string.fixed_currency_key), conversionRate.getFixedCurrency());
        editor.putString(fragment.getString(R.string.variable_currency_key), conversionRate.getVariableCurrency());
        editor.commit();
    }

}
