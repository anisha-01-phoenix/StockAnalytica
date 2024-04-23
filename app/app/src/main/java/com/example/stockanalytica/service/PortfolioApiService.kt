package com.example.stockanalytica.service

import com.example.stockanalytica.model.PortfolioResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PortfolioApiService {
    @GET("v1/indicators/{indicatorType}/{ticker}")
    suspend fun getIndicatorData(
        @Path("indicatorType") indicatorType: String,
        @Path("ticker") ticker: String,
        @Query("series_type") seriesType: String,
        @Query("timespan") timespan: String,
        @Query("adjusted") adjusted: Boolean,
        @Query("apiKey") apiKey: String,
        @Query("window") window: Int,
        @Query("order") order: String
    ): PortfolioResponse
}