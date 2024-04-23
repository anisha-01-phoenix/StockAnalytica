package com.example.stockanalytica.repository

import com.example.stockanalytica.model.StockResponse
import com.example.stockanalytica.service.StockApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StockRepository {
    private val apiService = Retrofit.Builder()
        .baseUrl("https://api.polygon.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(StockApiService::class.java)

    suspend fun getStockData(ticker: String, date: String): StockResponse {
        return apiService.getStockData(ticker, date, true, "SIUwMPYvXQwTOfiEKmIHN8s6IFCwyVGT")
    }
}