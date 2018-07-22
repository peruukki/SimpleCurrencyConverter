package com.peruukki.simplecurrencyconverter.fragments;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.peruukki.simplecurrencyconverter.MainActivity;
import com.peruukki.simplecurrencyconverter.R;
import com.peruukki.simplecurrencyconverter.models.ConversionRate;
import com.peruukki.simplecurrencyconverter.utils.Settings;

public class ConvertFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mMainActivity;
    private View mRootView;

    private TextView mFirstCurrencyLabel;
    private TextView mSecondCurrencyLabel;

    private EditText mFirstCurrencyAmount;
    private EditText mSecondCurrencyAmount;

    private ConversionRate mConversionRate;


    public ConvertFragmentTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);

        mMainActivity = getActivity();

        mRootView = mMainActivity.getWindow().getDecorView();

        mFirstCurrencyLabel = (TextView) mRootView.findViewById(R.id.first_currency_label);
        mSecondCurrencyLabel = (TextView) mRootView.findViewById(R.id.second_currency_label);

        mFirstCurrencyAmount = (EditText) mRootView.findViewById(R.id.first_currency_amount);
        mSecondCurrencyAmount = (EditText) mRootView.findViewById(R.id.second_currency_amount);

        mConversionRate = new ConversionRate("FIX", "VAR", 5.9f);
    }

    public void testPreconditions() {
        assertNotNull("1st currency label is null", mFirstCurrencyLabel);
        assertNotNull("2nd currency label is null", mSecondCurrencyLabel);
        assertNotNull("1st currency amount is null", mFirstCurrencyAmount);
        assertNotNull("2nd currency amount is null", mSecondCurrencyAmount);
    }


    public void testConvertFragment_initialLabels() {
        resetCurrencies();
        assertLabel(mFirstCurrencyLabel, "1st currency", mConversionRate.getVariableCurrency());
        assertLabel(mSecondCurrencyLabel, "2nd currency", mConversionRate.getFixedCurrency());
    }
    private void assertLabel(TextView label, String labelDescription, String expectedLabel) {
        ViewAsserts.assertOnScreen(mRootView, label);
        assertEquals(labelDescription + " has unexpected label", expectedLabel, label.getText());
    }

    public void testConvertFragment_initialTextFields() {
        assertAmount(mFirstCurrencyAmount, "1st currency");
        assertAmount(mSecondCurrencyAmount, "2nd currency");
    }
    private void assertAmount(EditText amount, String fieldDescription) {
        ViewAsserts.assertOnScreen(mRootView, amount);
        assertEquals(fieldDescription + " has unexpected amount", "", amount.getText().toString());
        assertEquals(fieldDescription + " has unexpected hint", "0", amount.getHint());
    }

    public void testConvertFragment_changeOneAmountAndExpectOtherAmountToUpdate() {
        resetCurrencies();
        assertEquals("1st currency amount is not empty", "", mFirstCurrencyAmount.getText().toString());
        assertEquals("2nd currency amount is not empty", "", mSecondCurrencyAmount.getText().toString());

        // Set the first currency amount to 10000
        mMainActivity.runOnUiThread(mFirstCurrencyAmount::requestFocus);
        sendKeys(KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_0);
        getInstrumentation().waitForIdleSync();

        assertEquals("1st currency has unexpected amount", "10000", mFirstCurrencyAmount.getText().toString());
        assertEquals("2nd currency has unexpected amount", "1 694.92", mSecondCurrencyAmount.getText().toString());

        // Delete the last digit from the second currency amount
        mMainActivity.runOnUiThread(() -> {
            mSecondCurrencyAmount.requestFocus();
            // Move the caret to the end of the text view
            mSecondCurrencyAmount.setSelection(mSecondCurrencyAmount.getText().length());
        });
        sendKeys(KeyEvent.KEYCODE_DEL);
        getInstrumentation().waitForIdleSync();

        assertEquals("2nd currency has unexpected amount", "1 694.9", mSecondCurrencyAmount.getText().toString());
        assertEquals("1st currency has unexpected amount", "9 999.91", mFirstCurrencyAmount.getText().toString());
    }


    private void resetCurrencies() {
        Settings.INSTANCE.writeConversionRate(mMainActivity, mConversionRate);
        mMainActivity.runOnUiThread(mMainActivity::onCurrenciesSelected);
    }
}
