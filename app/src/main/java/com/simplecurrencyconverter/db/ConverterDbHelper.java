package com.simplecurrencyconverter.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.simplecurrencyconverter.db.ConverterContract.ConversionRateEntry;

public class ConverterDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "converter.db";

    private static final String PRIMARY_KEY_AUTO_INCR = " INTEGER PRIMARY KEY AUTOINCREMENT";
    private static final String TEXT = " TEXT NOT NULL";
    private static final String REAL = " REAL NOT NULL";

    private static final String CREATE = "CREATE TABLE ";
    private static final String DROP = "DROP TABLE IF EXISTS ";

    public ConverterDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableSql = CREATE + ConversionRateEntry.TABLE_NAME + " (" +
            ConversionRateEntry._ID + PRIMARY_KEY_AUTO_INCR + "," +
            ConversionRateEntry.COLUMN_FIXED_CURRENCY + TEXT + "," +
            ConversionRateEntry.COLUMN_VARIABLE_CURRENCY + TEXT + "," +
            ConversionRateEntry.COLUMN_CONVERSION_RATE + REAL +
            ")";
        db.execSQL(createTableSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP + ConversionRateEntry.TABLE_NAME);
        onCreate(db);
    }
}
