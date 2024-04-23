package com.example.stockanalytica.repository

import com.example.stockanalytica.model.PortfolioResponse
import com.example.stockanalytica.service.PortfolioApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PortfolioRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.polygon.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(PortfolioApiService::class.java)

    suspend fun fetchIndicatorData(
        indicatorType: String,
        ticker: String,
        seriesType: String,
        timespan: String
    ): PortfolioResponse {
        return apiService.getIndicatorData(
            indicatorType,
            ticker,
            seriesType,
            timespan,
            true,
            "SIUwMPYvXQwTOfiEKmIHN8s6IFCwyVGT",
            50,
            "desc"
        )
    }
}
