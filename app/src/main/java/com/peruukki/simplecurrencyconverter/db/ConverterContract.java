package com.peruukki.simplecurrencyconverter.db;

import android.net.Uri;
import android.provider.BaseColumns;

public abstract class ConverterContract {

    private static final String CONTENT_AUTHORITY = "com.peruukki.simplecurrencyconverter";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class ConversionRateEntry implements BaseColumns {
        public static final String TABLE_NAME = "conversionRates";

        public static final String COLUMN_FIXED_CURRENCY = "fixed";
        public static final String COLUMN_VARIABLE_CURRENCY = "variable";
        public static final String COLUMN_CONVERSION_RATE = "rate";
    }
}
