package com.simplecurrencyconverter.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.simplecurrencyconverter.R;
import com.simplecurrencyconverter.utils.ConversionRate;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

/**
 * A {@link Fragment} that contains currency conversion widgets.
 */
public class ConvertFragment extends Fragment {

    private final static String EMPTY_AMOUNT = "0";

    private final DecimalFormat mInputAmountFormatter = getAmountFormatter("###,###.##");
    private final DecimalFormat mOutputAmountFormatter = getAmountFormatter("###,##0.00");

    private boolean mAllowAmountUpdate = true;

    private ConversionRate mConversionRate;

    /**
     * Creates an instance of the fragment.
     */
    public ConvertFragment() {
    }

    /**
     * Updates the conversion rate and currencies to show from shared preferences.
     */
    public void updateConversionRate() {
        mConversionRate = readConversionRate();

        View view = getView();
        updateLabels(view);
        clearAmounts(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_convert, container, false);
        mConversionRate = readConversionRate();
        updateLabels(view);
        addCurrencyListeners(view);
        return view;
    }

    private ConversionRate readConversionRate() {
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        ConversionRate defaultRate = ConversionRate.DEFAULT;
        String fixedCurrency = preferences.getString(getString(R.string.fixed_currency_key),
            defaultRate.getFixedCurrency());
        String variableCurrency = preferences.getString(getString(R.string.variable_currency_key),
            defaultRate.getVariableCurrency());

        ConversionRate storedConversionRate = ConversionRate.fromCurrencies(fixedCurrency, variableCurrency);
        return storedConversionRate != null ? storedConversionRate : defaultRate;
    }

    private void updateLabels(View view) {
        TextView firstLabel = (TextView) view.findViewById(R.id.first_currency_label);
        firstLabel.setText(mConversionRate.getVariableCurrency());

        TextView secondLabel = (TextView) view.findViewById(R.id.second_currency_label);
        secondLabel.setText(mConversionRate.getFixedCurrency());
    }

    private void clearAmounts(View view) {
        EditText firstAmount = (EditText) view.findViewById(R.id.first_currency_amount);
        firstAmount.setText("");

        EditText secondAmount = (EditText) view.findViewById(R.id.second_currency_amount);
        secondAmount.setText("");
    }

    private void addCurrencyListeners(View view) {
        EditText firstAmount = (EditText) view.findViewById(R.id.first_currency_amount);
        EditText secondAmount = (EditText) view.findViewById(R.id.second_currency_amount);

        addAmountChangedListeners(firstAmount, secondAmount, false);
        addFocusChangedListener(firstAmount);

        addAmountChangedListeners(secondAmount, firstAmount, true);
        addFocusChangedListener(secondAmount);
    }

    private void addAmountChangedListeners(final EditText editText, final EditText otherEditText,
                                           final boolean isFixedCurrency) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updateOtherAmount(s, otherEditText, isFixedCurrency);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private void addFocusChangedListener(EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    updateLostFocusAmount((EditText) v);
                }
            }
        });
    }

    private void updateOtherAmount(Editable changedText, EditText editTextToChange,
                                   boolean isFixedCurrency) {
        if (!mAllowAmountUpdate) {
            mAllowAmountUpdate = true;
            return;
        }
        mAllowAmountUpdate = false;

        String formattedOutputAmount;
        if (changedText.toString().length() == 0) {
            formattedOutputAmount = "";
        }
        else {
            Float multiplier = isFixedCurrency ?
                mConversionRate.getFixedCurrencyInVariableCurrencyRate() :
                mConversionRate.getVariableCurrencyInFixedCurrencyRate();
            Float inputAmount = parseDecimal(changedText.toString());
            Float outputAmount = multiplier * inputAmount;
            formattedOutputAmount = formatAmount(outputAmount, mOutputAmountFormatter);
        }
        editTextToChange.setText(formattedOutputAmount);
    }

    private void updateLostFocusAmount(EditText editText) {
        Float amount = parseDecimal(editText.getText().toString());
        String formattedOutputAmount = formatAmount(amount, mInputAmountFormatter);
        // Empty the other amount too if the amount that lost focus was emptied
        mAllowAmountUpdate = formattedOutputAmount.length() == 0;
        editText.setText(formattedOutputAmount);
    }

    private Float parseDecimal(String amount) {
        String trimmedAmount = amount.replaceAll(" ", "");
        try {
            return mInputAmountFormatter.parse(trimmedAmount).floatValue();
        }
        catch (NumberFormatException | ParseException e) {
            return 0f;
        }
    }

    private String formatAmount(Float amount, DecimalFormat formatter) {
        String formattedValue = formatter.format(amount.doubleValue());
        return formattedValue.equals(EMPTY_AMOUNT) ? "" : formattedValue;
    }

    private DecimalFormat getAmountFormatter(String formatPattern) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(' ');
        return new DecimalFormat(formatPattern, symbols);
    }
}
