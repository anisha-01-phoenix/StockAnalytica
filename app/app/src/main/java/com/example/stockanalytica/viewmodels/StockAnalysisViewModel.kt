package com.example.stockanalytica.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockanalytica.model.StockResponse
import com.example.stockanalytica.repository.StockRepository
import kotlinx.coroutines.launch

class StockAnalysisViewModel : ViewModel() {
    private val repository = StockRepository()
    val stockData: MutableLiveData<StockResponse> = MutableLiveData()
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchStockData(ticker: String, date: String) {
        viewModelScope.launch {
            try {
                val response = repository.getStockData(ticker, date)
                stockData.value = response
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to load data: ${e.localizedMessage}")
            }
        }
    }
}