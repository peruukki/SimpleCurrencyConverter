package com.simplecurrencyconverter.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simplecurrencyconverter.R;
import com.simplecurrencyconverter.db.ConverterDbHelper;
import com.simplecurrencyconverter.models.ConversionRate;

/**
 * A {@link android.support.v4.widget.CursorAdapter} that renders
 * {@link com.simplecurrencyconverter.models.ConversionRate} items.
 */
public class ConversionRateListAdapter extends CursorAdapter {

    public ConversionRateListAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_currencies, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ConversionRate conversionRate = ConverterDbHelper.conversionRateFromCursor(cursor);

        TextView nameLabel = (TextView) view.findViewById(R.id.list_item_first_currency);
        nameLabel.setText(conversionRate.getVariableCurrencyRateString());
        TextView rateLabel = (TextView) view.findViewById(R.id.list_item_second_currency);
        rateLabel.setText(conversionRate.getFixedCurrencyRateString());
    }
}
