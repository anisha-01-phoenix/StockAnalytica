package com.example.stockanalytica.viewmodels

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockanalytica.model.PortfolioResponse
import com.example.stockanalytica.repository.PortfolioRepository
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.launch

class PortfolioViewModel : ViewModel() {
    private val repository = PortfolioRepository()
    val portfolio: MutableLiveData<PortfolioResponse> = MutableLiveData()
    private val _lineData = MutableLiveData<LineData>()
    val lineData: LiveData<LineData> get() = _lineData
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchIndicatorData(
        indicatorType: String,
        ticker: String,
        seriesType: String,
        timespan: String,
        window: Int
    ) {
        viewModelScope.launch {
            try {
                val response = repository.fetchIndicatorData(indicatorType, ticker, seriesType, timespan, window)
                portfolio.postValue(response)
//                Log.i("response", response.toString())
//                val entries = response.results.values.map {
//                    Entry(
//                        it.timestamp.toFloat(),
//                        it.value.toFloat()
//                    )
//                }
//                // Create a LineDataSet from entries; this is where the line properties are set
//                val dataSet = LineDataSet(entries, "Indicator Values").apply {
//                    color = Color.MAGENTA
//                    valueTextColor = Color.BLACK
//                    lineWidth = 1.8f
//                    setDrawCircles(false)
//                    setDrawValues(true)
//                    setDrawFilled(true)
//                }
//                _lineData.postValue(LineData(dataSet))
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to fetch data: ${e.localizedMessage}")
            }
        }
    }
}