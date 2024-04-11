package com.example.stockanalytica.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.example.stockanalytica.R
import java.io.File

class StockAnalysisFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stock_analysis, container, false)
    }

    fun handleCSVFileUpload(file: File) {
//        val parser = CSVParser(file)
//        val data = parser.parse()
//        displayData(data)
    }

    fun displayData(data: List<List<String>>) {
        val rootView = view ?: return // Make sure fragment's view is available
        val scrollView = rootView.findViewById<ScrollView>(R.id.scrollView)
        val container = LinearLayout(requireContext())
        container.orientation = LinearLayout.VERTICAL

        for (row in data) {
            val rowView = LinearLayout(requireContext())
            rowView.orientation = LinearLayout.HORIZONTAL
            for (item in row) {
                val textView = TextView(requireContext())
                textView.text = item
                textView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                rowView.addView(textView)
            }
            container.addView(rowView)
        }
        scrollView.addView(container)
    }

}