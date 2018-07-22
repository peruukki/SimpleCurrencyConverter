package com.peruukki.simplecurrencyconverter.db

import android.net.Uri
import android.provider.BaseColumns

object ConverterContract {

    private val CONTENT_AUTHORITY = "com.peruukki.simplecurrencyconverter"

    @JvmField
    val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")

    class ConversionRateEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = "conversionRates"

            const val COLUMN_FIXED_CURRENCY = "fixed"
            const val COLUMN_VARIABLE_CURRENCY = "variable"
            const val COLUMN_CONVERSION_RATE = "rate"
        }
    }
}
