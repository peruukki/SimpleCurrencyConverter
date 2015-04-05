package com.peruukki.simplecurrencyconverter.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.peruukki.simplecurrencyconverter.db.ConverterContract.ConversionRateEntry;
import com.peruukki.simplecurrencyconverter.models.ConversionRate;

public class ConverterDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "converter.db";

    /* If you change table column order, update these indices as well. */
    private static final int COL_INDEX_FIXED_CURRENCY = 1;
    private static final int COL_INDEX_VARIABLE_CURRENCY = 2;
    private static final int COL_INDEX_CONVERSION_RATE = 3;

    private static final String PRIMARY_KEY_AUTO_INCR = " INTEGER PRIMARY KEY AUTOINCREMENT";
    private static final String TEXT = " TEXT NOT NULL";
    private static final String REAL = " REAL NOT NULL";

    private static final String CREATE = "CREATE TABLE ";
    private static final String DROP = "DROP TABLE IF EXISTS ";

    /**
     * Returns a {@link com.peruukki.simplecurrencyconverter.models.ConversionRate} instance from given
     * database cursor.
     *
     * @param cursor  database cursor pointing to a table row
     * @return a ConversionRate instance from given table row data
     */
    public static ConversionRate conversionRateFromCursor(Cursor cursor) {
        String fixedCurrency = cursor.getString(COL_INDEX_FIXED_CURRENCY);
        String variableCurrency = cursor.getString(COL_INDEX_VARIABLE_CURRENCY);
        Float rate = cursor.getFloat(COL_INDEX_CONVERSION_RATE);
        return new ConversionRate(fixedCurrency, variableCurrency, rate);
    }

    public ConverterDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table
        String createTableSql = CREATE + ConversionRateEntry.TABLE_NAME + " (" +
            ConversionRateEntry._ID + PRIMARY_KEY_AUTO_INCR + "," +
            ConversionRateEntry.COLUMN_FIXED_CURRENCY + TEXT + "," +
            ConversionRateEntry.COLUMN_VARIABLE_CURRENCY + TEXT + "," +
            ConversionRateEntry.COLUMN_CONVERSION_RATE + REAL +
            ")";
        db.execSQL(createTableSql);

        // Insert initial conversion rates
        for (ConversionRate conversionRate : ConversionRate.ALL) {
            ContentValues values = new ContentValues();
            values.put(ConversionRateEntry.COLUMN_FIXED_CURRENCY, conversionRate.getFixedCurrency());
            values.put(ConversionRateEntry.COLUMN_VARIABLE_CURRENCY, conversionRate.getVariableCurrency());
            values.put(ConversionRateEntry.COLUMN_CONVERSION_RATE,
                conversionRate.getFixedCurrencyInVariableCurrencyRate());
            db.insert(ConversionRateEntry.TABLE_NAME, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP + ConversionRateEntry.TABLE_NAME);
        onCreate(db);
    }
}
