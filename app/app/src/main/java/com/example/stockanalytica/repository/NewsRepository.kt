package com.example.stockanalytica.repository

import com.example.stockanalytica.model.NewsFeedResponse
import com.example.stockanalytica.service.NewsApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NewsRepository {
    private val retrofitService = Retrofit.Builder()
        .baseUrl("https://www.alphavantage.co/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NewsApiService::class.java)

    suspend fun fetchNews(ticker: String): NewsFeedResponse {
        return retrofitService.getNews(ticker)
    }
}