package com.example.stockanalytica.service

import com.example.stockanalytica.model.NewsFeedResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("query?function=NEWS_SENTIMENT&apikey=V3EML8GAF3UJ8LMI")
    suspend fun getNews(@Query("tickers") ticker: String): NewsFeedResponse
}