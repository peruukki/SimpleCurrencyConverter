package com.peruukki.simplecurrencyconverter.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.peruukki.simplecurrencyconverter.db.ConverterContract.ConversionRateEntry;

/**
 * A {@link android.content.ContentProvider} that provides updated conversion rates from the database.
 */
public class ConverterProvider extends ContentProvider {

    private ConverterDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ConverterDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = mDbHelper.getReadableDatabase()
            .query(ConversionRateEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int updatedRowCount = mDbHelper.getWritableDatabase()
            .update(ConverterContract.ConversionRateEntry.TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return updatedRowCount;
    }
}
