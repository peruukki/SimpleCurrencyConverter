package com.peruukki.simplecurrencyconverter.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

import com.peruukki.simplecurrencyconverter.db.ConverterContract.ConversionRateEntry
import com.peruukki.simplecurrencyconverter.models.ConversionRate

class ConverterDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Create table
        val createTableSql = CREATE + ConversionRateEntry.TABLE_NAME + " (" +
                BaseColumns._ID + PRIMARY_KEY_AUTO_INCR + "," +
                ConversionRateEntry.COLUMN_FIXED_CURRENCY + TEXT + "," +
                ConversionRateEntry.COLUMN_VARIABLE_CURRENCY + TEXT + "," +
                ConversionRateEntry.COLUMN_CONVERSION_RATE + REAL +
                ")"
        db.execSQL(createTableSql)

        // Insert initial conversion rates
        for (conversionRate in ConversionRate.allRates) {
            val values = ContentValues()
            values.put(ConversionRateEntry.COLUMN_FIXED_CURRENCY, conversionRate.fixedCurrency)
            values.put(ConversionRateEntry.COLUMN_VARIABLE_CURRENCY, conversionRate.variableCurrency)
            values.put(ConversionRateEntry.COLUMN_CONVERSION_RATE,
                    conversionRate.fixedCurrencyInVariableCurrencyRate)
            db.insert(ConversionRateEntry.TABLE_NAME, null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(DROP + ConversionRateEntry.TABLE_NAME)
        onCreate(db)
    }

    companion object {

        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "converter.db"

        /* If you change table column order, update these indices as well. */
        private val COL_INDEX_FIXED_CURRENCY = 1
        private val COL_INDEX_VARIABLE_CURRENCY = 2
        private val COL_INDEX_CONVERSION_RATE = 3

        private val PRIMARY_KEY_AUTO_INCR = " INTEGER PRIMARY KEY AUTOINCREMENT"
        private val TEXT = " TEXT NOT NULL"
        private val REAL = " REAL NOT NULL"

        private val CREATE = "CREATE TABLE "
        private val DROP = "DROP TABLE IF EXISTS "

        /**
         * Returns a [com.peruukki.simplecurrencyconverter.models.ConversionRate] instance from given
         * database cursor.
         *
         * @param cursor  database cursor pointing to a table row
         * @return a ConversionRate instance from given table row data
         */
        @JvmStatic
        fun conversionRateFromCursor(cursor: Cursor): ConversionRate {
            val fixedCurrency = cursor.getString(COL_INDEX_FIXED_CURRENCY)
            val variableCurrency = cursor.getString(COL_INDEX_VARIABLE_CURRENCY)
            val rate = cursor.getFloat(COL_INDEX_CONVERSION_RATE)
            return ConversionRate(fixedCurrency, variableCurrency, rate)
        }
    }
}
