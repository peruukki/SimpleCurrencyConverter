package com.simplecurrencyconverter.db;

import android.provider.BaseColumns;

public abstract class ConverterContract {

    public static final class ConversionRateEntry implements BaseColumns {
        public static final String TABLE_NAME = "conversionRates";

        public static final String COLUMN_FIXED_CURRENCY = "fixed";
        public static final String COLUMN_VARIABLE_CURRENCY = "variable";
        public static final String COLUMN_CONVERSION_RATE = "rate";
    }
}
