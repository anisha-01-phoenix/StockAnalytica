package com.example.stockanalytica.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.get
import androidx.core.view.marginStart
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.stockanalytica.R
import com.example.stockanalytica.model.StockResponse
import com.example.stockanalytica.viewmodels.StockAnalysisViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StockAnalysisFragment : Fragment() {

    private val viewModel: StockAnalysisViewModel by viewModels()
    private lateinit var ticker_spinner: Spinner
    private lateinit var date_input: EditText
    private lateinit var response_table: TableLayout
    private lateinit var submit_button: Button
    private lateinit var ticker_label: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stock_analysis, container, false)
        ticker_spinner = view.findViewById(R.id.ticker_spinner_stock)
        date_input = view.findViewById(R.id.date_input)
        response_table = view.findViewById(R.id.response_table)
        submit_button = view.findViewById(R.id.submit_button)
        ticker_label = view.findViewById(R.id.ticker_label_stock)
        return view
    }

    private fun validateInputs(ticker: String, date: String): Boolean {
        if (ticker.isBlank()) {
            Toast.makeText(context, "Please select a ticker.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (date.isBlank()) {
            Toast.makeText(context, "Please enter a date.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!isValidDate(date)) {
            Toast.makeText(
                context,
                "Please enter a valid date in YYYY-MM-DD format.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinner()
        setupDatePicker()

        submit_button.setOnClickListener {
            val selectedTicker = ticker_label.text.toString()
            val selectedDate = date_input.text.toString()

            if (validateInputs(selectedTicker, selectedDate)) {
                val response = viewModel.fetchStockData(selectedTicker, selectedDate)
            }
        }
        viewModel.stockData.observe(viewLifecycleOwner, Observer { stock ->
            stock?.let { displayStockData(it) } ?: run {
                Toast.makeText(context, "Failed to retrieve data.", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (msg != null)
                Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.company_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            ticker_spinner.adapter = adapter
        }

        ticker_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedTicker = parent.getItemAtPosition(position).toString()
                ticker_label.text = selectedTicker
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                ticker_label.text = "IBM"
            }
        }
    }

    private fun isValidDate(dateStr: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        dateFormat.isLenient = false
        try {
            dateFormat.parse(dateStr)
        } catch (e: ParseException) {
            return false
        }
        return true
    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        date_input.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    date_input.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun displayStockData(stock: StockResponse) {
        Log.i("stock", stock.toString())
        response_table.visibility = View.VISIBLE
        response_table.removeAllViews()
        addTableRow("Symbol", stock.symbol)
        addTableRow("Date", stock.from)
        addTableRow("Open", stock.open.toString())
        addTableRow("High", stock.high.toString())
        addTableRow("Low", stock.low.toString())
        addTableRow("Close", stock.close.toString())
        addTableRow("Volume", stock.volume.toString())
        addTableRow("After Hours", stock.afterHours.toString())
        addTableRow("Pre-Market", stock.preMarket.toString())
    }

    private fun addTableRow(label: String, value: String) {
        val row = TableRow(context).apply {
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
        }

        val labelView = TextView(context).apply {
            text = "$label:"
            setTextAppearance(R.style.TableHeader)
        }

        val valueView = TextView(context).apply {
            text = value
            setTextAppearance(R.style.TableData)
        }

        row.addView(labelView)
        row.addView(valueView)
        row.setPadding(0, 10, 0, 6)
        row.gravity = Gravity.CENTER
        labelView.gravity = Gravity.START
        valueView.gravity = Gravity.END
        response_table.addView(row)
    }
}