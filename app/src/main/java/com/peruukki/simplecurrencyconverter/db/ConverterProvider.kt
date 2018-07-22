package com.peruukki.simplecurrencyconverter.db

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

import com.peruukki.simplecurrencyconverter.db.ConverterContract.ConversionRateEntry

/**
 * A [android.content.ContentProvider] that provides updated conversion rates from the database.
 */
class ConverterProvider : ContentProvider() {

    private var mDbHelper: ConverterDbHelper? = null

    override fun onCreate(): Boolean {
        mDbHelper = ConverterDbHelper(context!!)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val cursor = mDbHelper!!.readableDatabase
                .query(ConversionRateEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val updatedRowCount = mDbHelper!!.writableDatabase
                .update(ConverterContract.ConversionRateEntry.TABLE_NAME, values, selection, selectionArgs)
        context!!.contentResolver.notifyChange(uri, null)
        return updatedRowCount
    }
}
