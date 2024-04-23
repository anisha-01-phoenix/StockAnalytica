package com.example.stockanalytica.service

import com.example.stockanalytica.model.StockResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StockApiService {
    @GET("v1/open-close/{ticker}/{date}")
    suspend fun getStockData(
        @Path("ticker") ticker: String,
        @Path("date") date: String,
        @Query("adjusted") adjusted: Boolean,
        @Query("apiKey") apiKey: String
    ): StockResponse
}