package com.example.stockanalytica.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.stockanalytica.R

class PredictionScrollFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scrollable_content, container, false)
        view.findViewById<TextView>(R.id.content_text).text = "Stock Prediction"
        return view
    }
}
