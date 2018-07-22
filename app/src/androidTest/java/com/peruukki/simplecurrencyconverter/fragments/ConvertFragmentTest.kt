package com.peruukki.simplecurrencyconverter.fragments

import android.test.ActivityInstrumentationTestCase2
import android.test.ViewAsserts
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView

import com.peruukki.simplecurrencyconverter.MainActivity
import com.peruukki.simplecurrencyconverter.R
import com.peruukki.simplecurrencyconverter.models.ConversionRate
import com.peruukki.simplecurrencyconverter.utils.Settings

import junit.framework.Assert

class ConvertFragmentTest : ActivityInstrumentationTestCase2<MainActivity>(MainActivity::class.java) {

    private var mMainActivity: MainActivity? = null
    private var mRootView: View? = null

    private var mFirstCurrencyLabel: TextView? = null
    private var mSecondCurrencyLabel: TextView? = null

    private var mFirstCurrencyAmount: EditText? = null
    private var mSecondCurrencyAmount: EditText? = null

    private var mConversionRate: ConversionRate? = null

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        setActivityInitialTouchMode(false)

        mMainActivity = activity

        mRootView = mMainActivity!!.window.decorView

        mFirstCurrencyLabel = mRootView!!.findViewById(R.id.first_currency_label) as TextView
        mSecondCurrencyLabel = mRootView!!.findViewById(R.id.second_currency_label) as TextView

        mFirstCurrencyAmount = mRootView!!.findViewById(R.id.first_currency_amount) as EditText
        mSecondCurrencyAmount = mRootView!!.findViewById(R.id.second_currency_amount) as EditText

        mConversionRate = ConversionRate("FIX", "VAR", 5.9f)
    }

    fun testPreconditions() {
        Assert.assertNotNull("1st currency label is null", mFirstCurrencyLabel)
        Assert.assertNotNull("2nd currency label is null", mSecondCurrencyLabel)
        Assert.assertNotNull("1st currency amount is null", mFirstCurrencyAmount)
        Assert.assertNotNull("2nd currency amount is null", mSecondCurrencyAmount)
    }


    fun testConvertFragment_initialLabels() {
        resetCurrencies()
        assertLabel(mFirstCurrencyLabel, "1st currency", mConversionRate!!.variableCurrency)
        assertLabel(mSecondCurrencyLabel, "2nd currency", mConversionRate!!.fixedCurrency)
    }

    private fun assertLabel(label: TextView?, labelDescription: String, expectedLabel: String) {
        ViewAsserts.assertOnScreen(mRootView, label)
        Assert.assertEquals("$labelDescription has unexpected label", expectedLabel, label!!.text)
    }

    fun testConvertFragment_initialTextFields() {
        assertAmount(mFirstCurrencyAmount, "1st currency")
        assertAmount(mSecondCurrencyAmount, "2nd currency")
    }

    private fun assertAmount(amount: EditText?, fieldDescription: String) {
        ViewAsserts.assertOnScreen(mRootView, amount)
        Assert.assertEquals("$fieldDescription has unexpected amount", "", amount!!.text.toString())
        Assert.assertEquals("$fieldDescription has unexpected hint", "0", amount.hint)
    }

    fun testConvertFragment_changeOneAmountAndExpectOtherAmountToUpdate() {
        resetCurrencies()
        Assert.assertEquals("1st currency amount is not empty", "", mFirstCurrencyAmount!!.text.toString())
        Assert.assertEquals("2nd currency amount is not empty", "", mSecondCurrencyAmount!!.text.toString())

        // Set the first currency amount to 10000
        mMainActivity!!.runOnUiThread { mFirstCurrencyAmount!!.requestFocus() }
        sendKeys(KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_0)
        instrumentation.waitForIdleSync()

        Assert.assertEquals("1st currency has unexpected amount", "10000", mFirstCurrencyAmount!!.text.toString())
        Assert.assertEquals("2nd currency has unexpected amount", "1 694.92", mSecondCurrencyAmount!!.text.toString())

        // Delete the last digit from the second currency amount
        mMainActivity!!.runOnUiThread {
            mSecondCurrencyAmount!!.requestFocus()
            // Move the caret to the end of the text view
            mSecondCurrencyAmount!!.setSelection(mSecondCurrencyAmount!!.text.length)
        }
        sendKeys(KeyEvent.KEYCODE_DEL)
        instrumentation.waitForIdleSync()

        Assert.assertEquals("2nd currency has unexpected amount", "1 694.9", mSecondCurrencyAmount!!.text.toString())
        Assert.assertEquals("1st currency has unexpected amount", "9 999.91", mFirstCurrencyAmount!!.text.toString())
    }


    private fun resetCurrencies() {
        Settings.writeConversionRate(mMainActivity!!, mConversionRate!!)
        mMainActivity!!.runOnUiThread { mMainActivity!!.onCurrenciesSelected() }
    }
}
