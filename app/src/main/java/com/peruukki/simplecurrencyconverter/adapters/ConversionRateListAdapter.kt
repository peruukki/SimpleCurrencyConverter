package com.peruukki.simplecurrencyconverter.adapters

import android.content.Context
import android.database.Cursor
import android.support.v4.widget.CursorAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.peruukki.simplecurrencyconverter.R
import com.peruukki.simplecurrencyconverter.db.ConverterDbHelper

/**
 * A [android.support.v4.widget.CursorAdapter] that renders
 * [com.peruukki.simplecurrencyconverter.models.ConversionRate] items.
 */
class ConversionRateListAdapter(context: Context, cursor: Cursor?, flags: Int) : CursorAdapter(context, cursor, flags) {

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.list_item_currencies, parent, false)
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val conversionRate = ConverterDbHelper.conversionRateFromCursor(cursor)

        val nameLabel = view.findViewById(R.id.list_item_first_currency) as TextView
        nameLabel.text = conversionRate.variableCurrencyRateString
        val rateLabel = view.findViewById(R.id.list_item_second_currency) as TextView
        rateLabel.text = conversionRate.fixedCurrencyRateString
    }
}
