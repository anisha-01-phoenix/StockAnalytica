package com.example.stockanalytica.fragments

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stockanalytica.R
import com.example.stockanalytica.databinding.FragmentPortfolioOptimisationBinding
import com.example.stockanalytica.model.Value
import com.example.stockanalytica.repository.PortfolioRepository
import com.example.stockanalytica.viewmodels.PortfolioViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PortfolioOptimisationFragment : Fragment() {
    private lateinit var viewModel: PortfolioViewModel
    private lateinit var chart: LineChart
    private lateinit var spinnerIndicatorType: Spinner
    private lateinit var spinnerSeriesType: Spinner
    private lateinit var spinnerTimespan: Spinner
    private lateinit var spinnerTicker: Spinner

    private lateinit var spinnerIndicatorTypeLabel: TextView
    private lateinit var spinnerSeriesTypeLabel: TextView
    private lateinit var spinnerTimespanLabel: TextView
    private lateinit var spinnerTickerLabel: TextView
    private lateinit var submit : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_portfolio_optimisation, container, false)
        viewModel = ViewModelProvider(this).get(PortfolioViewModel::class.java)
        chart = view.findViewById(R.id.chart)
        submit = view.findViewById(R.id.buttonSubmit)
        setupSpinners(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        submit.setOnClickListener {
            val indicator = spinnerIndicatorTypeLabel.text.toString()
            val ticker = spinnerTickerLabel.text.toString()
            val series = spinnerSeriesTypeLabel.text.toString()
            val timespan = spinnerTimespanLabel.text.toString()
            viewModel.fetchIndicatorData(indicator,ticker,series,timespan)
            viewModel.portfolio.observe(viewLifecycleOwner) { response ->
                val chartResponse = response.results.values
                val entries = ArrayList<Entry>()
                for (value in chartResponse.sortedBy { it.timestamp }) {
                    if (value.timestamp < 0 || value.value.isNaN() || value.value.isInfinite()) {
                        println("Invalid data point: timestamp=${value.timestamp}, value=${value.value}")
                    }
                    else
                    entries.add(Entry(value.timestamp.toFloat(), value.value.toFloat()))
                }
                drawGraph(entries)
                Log.i("response", entries.toString())
            }

            viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            }
//            setupObservers()
        }
    }

    private fun drawGraph(entries: ArrayList<Entry>) {
        val dataSet = LineDataSet(entries, "Portfolio").apply {
            color = Color.RED
            valueTextColor = Color.BLACK
            lineWidth = 2f
            setDrawFilled(true)
            val drawable = resources.getDrawable(R.drawable.gradient_chart_fill)
            fillDrawable = drawable
        }
        val lineData = LineData(dataSet)

        chart.data = lineData
        chart.description.isEnabled = false
        val xAxis = chart.xAxis
        xAxis.granularity = 1f  // Ensure granularity is positive and makes sense for your data
        xAxis.axisMinimum = entries.minOf { it.x }  // Ensure axis minimum doesn't cause issues
        xAxis.axisMaximum = entries.maxOf { it.x }  // Ensure axis maximum is correct
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.setVisibleXRangeMinimum(1f)  // Set a reasonable minimum range
        chart.invalidate()

    }

    private fun setupSpinners(view: View) {
        spinnerIndicatorType = view.findViewById(R.id.spinnerIndicator)
        spinnerSeriesType = view.findViewById(R.id.spinnerSeriesType)
        spinnerTimespan = view.findViewById(R.id.spinnerTimespan)
        spinnerTicker = view.findViewById(R.id.spinnerTicker)


        spinnerIndicatorTypeLabel = view.findViewById(R.id.spinnerIndicator_label)
        spinnerSeriesTypeLabel = view.findViewById(R.id.spinnerSeriesType_label)
        spinnerTimespanLabel = view.findViewById(R.id.spinnerTimespan_label)
        spinnerTickerLabel = view.findViewById(R.id.spinnerTicker_label)

        spinnerTicker.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val ticker = parent.getItemAtPosition(position).toString()
                spinnerTickerLabel.text = ticker
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                spinnerTickerLabel.text = "IBM"
            }
        }
        spinnerTimespan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val ticker = parent.getItemAtPosition(position).toString()
                spinnerTimespanLabel.text = ticker
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                spinnerTimespanLabel.text = "day"
            }
        }
        spinnerSeriesType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val ticker = parent.getItemAtPosition(position).toString()
                spinnerSeriesTypeLabel.text = ticker
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                spinnerSeriesTypeLabel.text = "open"
            }
        }
        spinnerIndicatorType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val ticker = parent.getItemAtPosition(position).toString()
                spinnerIndicatorTypeLabel.text = ticker
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                spinnerIndicatorTypeLabel.text = "sma"
            }
        }
    }

    private fun setupObservers() {
        viewModel.lineData.observe(viewLifecycleOwner) { lineData ->
            Log.i("ChartData", lineData.toString())
            (lineData.getDataSetByIndex(0) as? LineDataSet)?.apply {
                fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.gradient_chart_fill)
            }
            chart.data = lineData
            Log.i("chart", lineData.toString())
            chart.invalidate()
        }
    }}


