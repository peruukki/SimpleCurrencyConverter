package com.peruukki.simplecurrencyconverter.models

import android.content.ContentResolver
import android.content.ContentValues
import com.peruukki.simplecurrencyconverter.db.ConverterContract
import com.peruukki.simplecurrencyconverter.db.ConverterContract.ConversionRateEntry

/**
 * A container class for two currencies and their conversion rate.
 */
class ConversionRate
/**
 * Creates a conversion rate mapping between two currencies.
 *
 * @param fixedCurrency  the name of the currency whose value is one unit
 * @param variableCurrency  the name of the currency whose value is the conversion rate
 * @param rate the value of one unit of the fixed currency in the variable currency
 */
(
        /**
         * The name of the currency whose value is one unit.
         */
        val fixedCurrency: String,
        /**
         * The name of the currency whose value is the conversion rate.
         */
        val variableCurrency: String,
        /**
         * The value of one unit of the fixed currency in the variable currency. It is equal to
         * the conversion rate of the two currencies.
         */
        val fixedCurrencyInVariableCurrencyRate: Float?) {

    /**
     * The value of one unit of the variable currency in the fixed currency. It is equal to
     * the inverse of the conversion rate.
     */
    val variableCurrencyInFixedCurrencyRate: Float?

    /**
     * A string representation of the variable currency value that equals one unit of the
     * fixed currency.
     */
    val variableCurrencyRateString: String
        get() = fixedCurrencyInVariableCurrencyRate!!.toString() + " " + variableCurrency

    /**
     * A string representation of one unit of the fixed currency.
     */
    val fixedCurrencyRateString: String
        get() = "1 $fixedCurrency"

    init {
        variableCurrencyInFixedCurrencyRate = 1 / fixedCurrencyInVariableCurrencyRate!!
    }

    /**
     * Updates the conversion rate in the database.
     *
     * @param contentResolver  the [android.content.ContentResolver] instance to use for updating
     */
    fun writeToDb(contentResolver: ContentResolver) {
        val values = ContentValues()
        values.put(ConversionRateEntry.COLUMN_FIXED_CURRENCY, fixedCurrency)
        values.put(ConversionRateEntry.COLUMN_VARIABLE_CURRENCY, variableCurrency)
        values.put(ConversionRateEntry.COLUMN_CONVERSION_RATE, fixedCurrencyInVariableCurrencyRate)

        val whereClause = ConversionRateEntry.COLUMN_FIXED_CURRENCY + "=?" + " AND " +
                ConversionRateEntry.COLUMN_VARIABLE_CURRENCY + "=?"
        val whereArgs = arrayOf(fixedCurrency, variableCurrency)
        contentResolver.update(ConverterContract.BASE_CONTENT_URI, values, whereClause, whereArgs)
    }

    override fun toString(): String {
        return "$variableCurrency <-> $fixedCurrency"
    }

    override fun equals(other: Any?): Boolean {
        return other is ConversionRate && toString() == other.toString()
    }

    override fun hashCode(): Int {
        return toString().hashCode()
    }

    companion object {

        private val CONVERSION_RATES = listOf(
                ConversionRate("EUR", "HKD", 9.17f),
                ConversionRate("EUR", "MOP", 9.44f)
        )

        /**
         * A list of all available currency pairs and their conversion rates.
         */
        @JvmStatic
        val allRates = CONVERSION_RATES

        /**
         * The default conversion rate.
         */
        @JvmStatic
        val defaultRate = allRates[0]
    }
}
