package com.simplecurrencyconverter.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Contains available currency information.
 */
public class Currencies {

    private static ConversionRate[] CONVERSION_RATES_ARRAY = {
        new ConversionRate("EUR", "KRW", 1209.15f),
        new ConversionRate("EUR", "HKD", 8.33f),
        new ConversionRate("EUR", "MOP", 8.58f),
        new ConversionRate("EUR", "CNY", 6.73f)
    };

    /**
     * A list of all available currency pairs and their conversion rates.
     */
    public static List<ConversionRate> CONVERSION_RATES = Arrays.asList(CONVERSION_RATES_ARRAY);
}
