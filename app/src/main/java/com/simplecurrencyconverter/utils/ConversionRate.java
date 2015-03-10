package com.simplecurrencyconverter.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ConversionRate {

    private String mFixedCurrency;
    private String mVariableCurrency;

    private BigDecimal mFixedCurrencyInVariableCurrencyRate;
    private BigDecimal mVariableCurrencyInFixedCurrencyRate;

    /**
     * Creates a conversion rate mapping between two currencies.
     *
     * @param fixedCurrency  the name of the currency whose value is one unit
     * @param variableCurrency  the name of the currency whose value is the conversion rate
     * @param rate the value of one unit of the fixed currency in the variable currency
     */
    public ConversionRate(String fixedCurrency, String variableCurrency, BigDecimal rate) {
        mFixedCurrency = fixedCurrency;
        mVariableCurrency = variableCurrency;
        mFixedCurrencyInVariableCurrencyRate = rate;
        mVariableCurrencyInFixedCurrencyRate = new BigDecimal(1).divide(rate, 5, RoundingMode.HALF_EVEN);
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
    public BigDecimal getFixedCurrencyInVariableCurrencyRate() {
        return mFixedCurrencyInVariableCurrencyRate;
    }

    /**
     * Returns the value of one unit of the variable currency in the fixed currency. It is equal to
     * the inverse of the conversion rate.
     *
     * @return the value of one unit of the variable currency in the fixed currency
     */
    public BigDecimal getVariableCurrencyInFixedCurrencyRate() {
        return mVariableCurrencyInFixedCurrencyRate;
    }
}
