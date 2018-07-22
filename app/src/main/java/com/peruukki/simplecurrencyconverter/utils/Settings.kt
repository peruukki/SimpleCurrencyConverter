package com.peruukki.simplecurrencyconverter.utils

import android.app.Activity
import android.content.Context

import com.peruukki.simplecurrencyconverter.R
import com.peruukki.simplecurrencyconverter.models.ConversionRate

object Settings {

    fun readConversionRate(activity: Activity): ConversionRate {
        val preferences = activity.getPreferences(Context.MODE_PRIVATE)
        val defaultRate = ConversionRate.defaultRate

        val fixedCurrency = preferences.getString(activity.getString(R.string.fixed_currency_key),
                defaultRate.fixedCurrency)
        val variableCurrency = preferences.getString(activity.getString(R.string.variable_currency_key),
                defaultRate.variableCurrency)
        val rate = preferences.getFloat(activity.getString(R.string.conversion_rate_key),
                defaultRate.fixedCurrencyInVariableCurrencyRate!!)

        return ConversionRate(fixedCurrency!!, variableCurrency!!, rate)
    }

    fun writeConversionRate(activity: Activity, conversionRate: ConversionRate) {
        val preferences = activity.getPreferences(Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(activity.getString(R.string.fixed_currency_key), conversionRate.fixedCurrency)
        editor.putString(activity.getString(R.string.variable_currency_key), conversionRate.variableCurrency)
        editor.putFloat(activity.getString(R.string.conversion_rate_key),
                conversionRate.fixedCurrencyInVariableCurrencyRate!!)
        editor.commit()
    }

}
