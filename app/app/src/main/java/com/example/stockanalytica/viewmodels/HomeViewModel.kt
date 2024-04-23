package com.example.stockanalytica.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockanalytica.model.NewsArticle
import com.example.stockanalytica.repository.NewsRepository
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel()  {
    private val repository = NewsRepository()
    val newsFeed = MutableLiveData<List<NewsArticle>>()
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchNews(ticker: String) {
        viewModelScope.launch {
            try {
                val response = repository.fetchNews(ticker)
                newsFeed.postValue(response.feed)
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to load data: ${e.localizedMessage}")
            }
        }
    }
}