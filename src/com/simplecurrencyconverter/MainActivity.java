package com.simplecurrencyconverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.widget.EditText;

public class MainActivity extends Activity {
    private static final BigDecimal ONE_EURO_IN_WONS = new BigDecimal(1452.62594);
    private static final BigDecimal ONE_WON_IN_EUROS = new BigDecimal(1).divide(ONE_EURO_IN_WONS, 5, RoundingMode.HALF_EVEN);

    private EditText getKrwEditText() { return (EditText) findViewById(R.id.krw_amount); }
    private EditText getEurEditText() { return (EditText) findViewById(R.id.eur_amount); }

    private boolean ignoreAmountUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addAmountChangedListeners(getKrwEditText());
        addAmountChangedListeners(getEurEditText());
    }

    private void addAmountChangedListeners(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) { updateAmounts(s); }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void updateAmounts(Editable changedText) {
        if (ignoreAmountUpdate) {
            ignoreAmountUpdate = false;
            return;
        }
        ignoreAmountUpdate = true;

        boolean hasKrwChanged = changedText.equals(getKrwEditText().getText());
        BigDecimal multiplier = hasKrwChanged ? ONE_WON_IN_EUROS : ONE_EURO_IN_WONS;
        EditText textToChange = hasKrwChanged ? getEurEditText() : getKrwEditText();

        BigDecimal inputAmount = parseDecimal(changedText.toString());
        BigDecimal outputAmount = inputAmount.multiply(multiplier).setScale(2, RoundingMode.HALF_EVEN);
        String formattedOutputAmount = formatAmount(outputAmount);
        textToChange.setText(formattedOutputAmount);
    }

    private BigDecimal parseDecimal(String s) {
        try {
            BigDecimal decimal = new BigDecimal(s);
            return decimal;
        }
        catch (NumberFormatException e) {
            return new BigDecimal(0);
        }
    }

    private String formatAmount(BigDecimal amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(' ');
        DecimalFormat formatter = new DecimalFormat("###,##0.00", symbols);
        String formattedValue = formatter.format(amount.doubleValue());
        return formattedValue.equals("0.00") ? "" : formattedValue;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
