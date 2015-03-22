package com.simplecurrencyconverter.models;


import java.util.Arrays;
import java.util.List;

/**
 * A container class for two currencies and their conversion rate.
 */
public class ConversionRate {

    private static ConversionRate[] CONVERSION_RATES = {
        new ConversionRate("EUR", "KRW", 1209.15f),
        new ConversionRate("EUR", "HKD", 8.33f),
        new ConversionRate("EUR", "MOP", 8.58f),
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

    @Override
    public String toString() {
        return getVariableCurrency() + " <-> " + getFixedCurrency();
    }
}
