package com.simplecurrencyconverter;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {
    private static final BigDecimal ONE_EURO_IN_WONS = new BigDecimal(1452.62594);
    private static final BigDecimal ONE_WON_IN_EUROS = new BigDecimal(1).divide(ONE_EURO_IN_WONS, 5, RoundingMode.HALF_EVEN);

    private EditText getKrwEditText() { return (EditText) findViewById(R.id.krw_amount); }
    private EditText getEurEditText() { return (EditText) findViewById(R.id.eur_amount); }

    private final DecimalFormat inputAmountFormatter = getAmountFormatter("###,###.##");
    private final DecimalFormat outputAmountFormatter = getAmountFormatter("###,##0.00");
    private DecimalFormat getAmountFormatter(String formatPattern) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(' ');
        return new DecimalFormat(formatPattern, symbols);
    }
    private final String emptyAmount = "0";

    private boolean allowAmountUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText krwEditText = getKrwEditText();
        EditText eurEditText = getEurEditText();

        addAmountChangedListeners(krwEditText, eurEditText, ONE_WON_IN_EUROS);
        addFocusChangedListener(krwEditText);

        addAmountChangedListeners(eurEditText, krwEditText, ONE_EURO_IN_WONS);
        addFocusChangedListener(eurEditText);
    }


    private void addAmountChangedListeners(final EditText editText, final EditText otherEditText, final BigDecimal multiplier) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) { updateOtherAmount(s, otherEditText, multiplier); }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
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

    private void updateOtherAmount(Editable changedText, EditText editTextToChange, BigDecimal multiplier) {
        if (!allowAmountUpdate) {
            allowAmountUpdate = true;
            return;
        }
        allowAmountUpdate = false;

        String formattedOutputAmount;
        if (changedText.toString().length() == 0) {
            formattedOutputAmount = "";
        }
        else {
            BigDecimal inputAmount = parseDecimal(changedText.toString());
            BigDecimal outputAmount = inputAmount.multiply(multiplier).setScale(2, RoundingMode.HALF_EVEN);
            formattedOutputAmount = formatAmount(outputAmount, outputAmountFormatter);
        }
        editTextToChange.setText(formattedOutputAmount);
    }

    private void updateLostFocusAmount(EditText editText) {
        BigDecimal amount = parseDecimal(editText.getText().toString());
        String formattedOutputAmount = formatAmount(amount, inputAmountFormatter);
        // Empty the other amount too if the amount that lost focus was emptied
        allowAmountUpdate = formattedOutputAmount.length() == 0;
        editText.setText(formattedOutputAmount);
    }

    private BigDecimal parseDecimal(String s) {
        try {
            return new BigDecimal(inputAmountFormatter.parse(s).doubleValue());
        }
        catch (NumberFormatException e) {
            return new BigDecimal(0);
        }
        catch (ParseException e) {
            return new BigDecimal(0);
        }
    }

    private String formatAmount(BigDecimal amount, DecimalFormat formatter) {
        String formattedValue = formatter.format(amount.doubleValue());
        return formattedValue.equals(emptyAmount) ? "" : formattedValue;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
