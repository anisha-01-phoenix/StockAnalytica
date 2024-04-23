package com.example.stockanalytica.model

data class Results (
    val underlying: Underlying,
    val values: List<Value>
)