package ru.practicum.mvvm.news.domain.model

import java.util.Date

data class NewsArticle(
    val author: String?,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: Date,
    val content: String,
)