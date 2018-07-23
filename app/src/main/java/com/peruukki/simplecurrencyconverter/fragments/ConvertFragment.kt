package com.peruukki.simplecurrencyconverter.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView

import com.peruukki.simplecurrencyconverter.R
import com.peruukki.simplecurrencyconverter.models.ConversionRate
import com.peruukki.simplecurrencyconverter.utils.Settings

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.util.*

/**
 * A [Fragment] that contains currency conversion widgets.
 */
/**
 * Creates an instance of the fragment.
 */
class ConvertFragment : Fragment() {

    private val mInputAmountFormatter = getAmountFormatter("###,###.##")
    private val mOutputAmountFormatter = getAmountFormatter("###,##0.00")

    private var mAllowAmountUpdate = true

    private var mConversionRate: ConversionRate? = null

    /**
     * Updates the conversion rate and currencies to show from shared preferences.
     */
    fun updateConversionRate() {
        mConversionRate = Settings.readConversionRate(activity)

        val view = view
        updateLabels(view!!)
        clearAmounts(view)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_convert, container, false)
        mConversionRate = Settings.readConversionRate(activity)
        updateLabels(view)
        addCurrencyListeners(view)
        return view
    }

    private fun updateLabels(view: View) {
        val firstLabel = view.findViewById(R.id.first_currency_label) as TextView
        firstLabel.text = mConversionRate!!.variableCurrency

        val secondLabel = view.findViewById(R.id.second_currency_label) as TextView
        secondLabel.text = mConversionRate!!.fixedCurrency
    }

    private fun clearAmounts(view: View) {
        val firstAmount = view.findViewById(R.id.first_currency_amount) as EditText
        firstAmount.setText("")

        val secondAmount = view.findViewById(R.id.second_currency_amount) as EditText
        secondAmount.setText("")
    }

    private fun addCurrencyListeners(view: View) {
        val firstAmount = view.findViewById(R.id.first_currency_amount) as EditText
        val secondAmount = view.findViewById(R.id.second_currency_amount) as EditText

        addAmountChangedListeners(firstAmount, secondAmount, false)
        addFocusChangedListener(firstAmount)

        addAmountChangedListeners(secondAmount, firstAmount, true)
        addFocusChangedListener(secondAmount)
    }

    private fun addAmountChangedListeners(editText: EditText, otherEditText: EditText,
                                          isFixedCurrency: Boolean) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                updateOtherAmount(s, otherEditText, isFixedCurrency)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    private fun addFocusChangedListener(editText: EditText) {
        editText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                updateLostFocusAmount(v as EditText)
            }
        }
    }

    private fun updateOtherAmount(changedText: Editable, editTextToChange: EditText,
                                  isFixedCurrency: Boolean) {
        if (!mAllowAmountUpdate) {
            mAllowAmountUpdate = true
            return
        }
        mAllowAmountUpdate = false

        val formattedOutputAmount: String
        if (changedText.toString().isEmpty()) {
            formattedOutputAmount = ""
        } else {
            val multiplier = if (isFixedCurrency)
                mConversionRate!!.fixedCurrencyInVariableCurrencyRate
            else
                mConversionRate!!.variableCurrencyInFixedCurrencyRate
            val inputAmount = parseDecimal(changedText.toString())
            val outputAmount = multiplier * inputAmount
            formattedOutputAmount = formatAmount(outputAmount, mOutputAmountFormatter)
        }
        editTextToChange.setText(formattedOutputAmount)
    }

    private fun updateLostFocusAmount(editText: EditText) {
        val amount = parseDecimal(editText.text.toString())
        val formattedOutputAmount = formatAmount(amount, mInputAmountFormatter)
        // Empty the other amount too if the amount that lost focus was emptied
        mAllowAmountUpdate = formattedOutputAmount.isEmpty()
        editText.setText(formattedOutputAmount)
    }

    private fun parseDecimal(amount: String): Float {
        val trimmedAmount = amount.replace(" ".toRegex(), "")
        try {
            return mInputAmountFormatter.parse(trimmedAmount).toFloat()
        } catch (e: NumberFormatException) {
            return 0f
        } catch (e: ParseException) {
            return 0f
        }

    }

    private fun formatAmount(amount: Float, formatter: DecimalFormat): String {
        val formattedValue = formatter.format(amount.toDouble())
        return if (formattedValue == EMPTY_AMOUNT) "" else formattedValue
    }

    private fun getAmountFormatter(formatPattern: String): DecimalFormat {
        val symbols = DecimalFormatSymbols(Locale.US)
        symbols.groupingSeparator = ' '
        return DecimalFormat(formatPattern, symbols)
    }

    companion object {
        private const val EMPTY_AMOUNT = "0"
    }
}
