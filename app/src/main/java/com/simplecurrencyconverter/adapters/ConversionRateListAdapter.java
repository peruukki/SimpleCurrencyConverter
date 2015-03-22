package com.simplecurrencyconverter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.simplecurrencyconverter.R;
import com.simplecurrencyconverter.models.ConversionRate;

import java.util.List;

/**
 * An {@link android.widget.ArrayAdapter} that renders
 * {@link com.simplecurrencyconverter.models.ConversionRate} items.
 */
public class ConversionRateListAdapter extends ArrayAdapter<ConversionRate> {

    private int mResource;

    public ConversionRateListAdapter(Context context, int resource, int textViewResourceId, List<ConversionRate> objects) {
        super(context, resource, textViewResourceId, objects);
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = (convertView != null) ? convertView :
            LayoutInflater.from(getContext()).inflate(mResource, parent, false);

        ConversionRate conversionRate = getItem(position);
        TextView nameLabel = (TextView) view.findViewById(R.id.list_item_first_currency);
        nameLabel.setText(conversionRate.getVariableCurrencyRateString());
        TextView rateLabel = (TextView) view.findViewById(R.id.list_item_second_currency);
        rateLabel.setText(conversionRate.getFixedCurrencyRateString());

        return view;
    }
}
