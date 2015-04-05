package com.peruukki.simplecurrencyconverter.models;

import android.content.ContentResolver;
import android.content.ContentValues;

import com.peruukki.simplecurrencyconverter.db.ConverterContract;
import com.peruukki.simplecurrencyconverter.db.ConverterContract.ConversionRateEntry;

import java.util.Arrays;
import java.util.List;

/**
 * A container class for two currencies and their conversion rate.
 */
public class ConversionRate {

    private static ConversionRate[] CONVERSION_RATES = {
        new ConversionRate("EUR", "KRW", 1207.31f),
        new ConversionRate("EUR", "HKD", 8.41f),
        new ConversionRate("EUR", "MOP", 8.66f),
        new ConversionRate("EUR", "CNY", 6.73f)
    };

    /**
     * A list of all available currency pairs and their conversion rates.
     */
    public static List<ConversionRate> ALL = Arrays.asList(CONVERSION_RATES);

    /**
     * The default conversion rate.
     */
    public static ConversionRate DEFAULT = ALL.get(0);

    private String mFixedCurrency;
    private String mVariableCurrency;

    private Float mFixedCurrencyInVariableCurrencyRate;
    private Float mVariableCurrencyInFixedCurrencyRate;

    /**
     * Creates a conversion rate mapping between two currencies.
     *
     * @param fixedCurrency  the name of the currency whose value is one unit
     * @param variableCurrency  the name of the currency whose value is the conversion rate
     * @param rate the value of one unit of the fixed currency in the variable currency
     */
    public ConversionRate(String fixedCurrency, String variableCurrency, Float rate) {
        mFixedCurrency = fixedCurrency;
        mVariableCurrency = variableCurrency;
        mFixedCurrencyInVariableCurrencyRate = rate;
        mVariableCurrencyInFixedCurrencyRate = 1 / rate;
    }

    /**
     * Returns the name of the currency whose value is one unit.
     *
     * @return the name of the currency whose value is one unit
     */
    public String getFixedCurrency() {
        return mFixedCurrency;
    }

    /**
     * Returns the name of the currency whose value is the conversion rate.
     *
     * @return the name of the currency whose value is the conversion rate
     */
    public String getVariableCurrency() {
        return mVariableCurrency;
    }

    /**
     * Returns the value of one unit of the fixed currency in the variable currency. It is equal to
     * the conversion rate of the two currencies.
     *
     * @return the value of one unit of the fixed currency in the variable currency
     */
    public Float getFixedCurrencyInVariableCurrencyRate() {
        return mFixedCurrencyInVariableCurrencyRate;
    }

    /**
     * Returns the value of one unit of the variable currency in the fixed currency. It is equal to
     * the inverse of the conversion rate.
     *
     * @return the value of one unit of the variable currency in the fixed currency
     */
    public Float getVariableCurrencyInFixedCurrencyRate() {
        return mVariableCurrencyInFixedCurrencyRate;
    }

    /**
     * Returns a string representation of the variable currency value that equals one unit of the
     * fixed currency.
     *
     * @return a string describing the variable currency value that equals one unit of the fixed
     *         currency
     */
    public String getVariableCurrencyRateString() {
        return getFixedCurrencyInVariableCurrencyRate().toString() + " " + getVariableCurrency();
    }

    /**
     * Returns a string representation of one unit of the fixed currency.
     *
     * @return a string describing one unit of the fixed currency
     */
    public String getFixedCurrencyRateString() {
        return "1 " + getFixedCurrency();
    }

    /**
     * Updates the conversion rate in the database.
     *
     * @param contentResolver  the {@link android.content.ContentResolver} instance to use for updating
     */
    public void writeToDb(ContentResolver contentResolver) {
        ContentValues values = new ContentValues();
        values.put(ConversionRateEntry.COLUMN_FIXED_CURRENCY, getFixedCurrency());
        values.put(ConversionRateEntry.COLUMN_VARIABLE_CURRENCY, getVariableCurrency());
        values.put(ConversionRateEntry.COLUMN_CONVERSION_RATE, getFixedCurrencyInVariableCurrencyRate());

        String whereClause = ConversionRateEntry.COLUMN_FIXED_CURRENCY + "=?" + " AND " +
            ConversionRateEntry.COLUMN_VARIABLE_CURRENCY + "=?";
        String[] whereArgs = { getFixedCurrency(), getVariableCurrency() };
        contentResolver.update(ConverterContract.BASE_CONTENT_URI, values, whereClause, whereArgs);
    }

    @Override
    public String toString() {
        return getVariableCurrency() + " <-> " + getFixedCurrency();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ConversionRate) && (toString().equals(o.toString()));
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
