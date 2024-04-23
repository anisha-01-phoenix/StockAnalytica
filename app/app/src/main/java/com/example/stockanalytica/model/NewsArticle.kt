package com.example.stockanalytica.model

data class NewsArticle(
    val title: String,
    val url: String,
    val time_published: String,
    val authors: List<String>,
    val summary: String,
    val banner_image: String?,
    val source: String,
    val category_within_source: String,
    val source_domain: String,
    val topics: List<Topic>,
    val overall_sentiment_score: Double,
    val overall_sentiment_label: String
)