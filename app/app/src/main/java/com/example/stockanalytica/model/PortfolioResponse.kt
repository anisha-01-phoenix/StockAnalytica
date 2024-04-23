package com.example.stockanalytica.model

data class PortfolioResponse(
    val results: Results,
    val status: String,
    val request_id: String,
    val next_url: String?
)
