package com.example.stockanalytica.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stockanalytica.R
import com.example.stockanalytica.adapters.NewsAdapter
import com.example.stockanalytica.viewmodels.HomeViewModel

class HomeFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinnerCompanies: Spinner
    private lateinit var selectedCompany: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        recyclerView = view.findViewById(R.id.news_rv)
        recyclerView.layoutManager = LinearLayoutManager(context)
        spinnerCompanies = view.findViewById(R.id.ticker_spinner)
        selectedCompany = view.findViewById(R.id.ticker_label)
        spinnerCompanies.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val ticker = parent.getItemAtPosition(position).toString()
                selectedCompany.text = ticker
                viewModel.fetchNews(ticker)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedCompany.text = "IBM"
            }
        }
        viewModel.newsFeed.observe(viewLifecycleOwner) { newsList ->
            Log.i("check", newsList.toString())
            if (newsList != null) {
                recyclerView.adapter = NewsAdapter(newsList)
            } else {
                Log.e("HomeFragment", "newsList is null")
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (msg != null)
                Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
        }

        return view
    }
}