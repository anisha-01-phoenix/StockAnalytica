package com.example.stockanalytica.model

data class NewsFeedResponse(
    val items: String,
    val sentiment_score_definition: String,
    val relevance_score_definition: String,
    val feed: List<NewsArticle>
)
