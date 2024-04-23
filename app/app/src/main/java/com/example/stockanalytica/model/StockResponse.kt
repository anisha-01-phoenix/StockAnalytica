package com.example.stockanalytica.model

data class StockResponse(
    val afterHours: Double,
    val close: Double,
    val from: String,
    val high: Double,
    val low: Double,
    val open: Double,
    val preMarket: Double,
    val status: String,
    val symbol: String,
    val volume: Long
)
