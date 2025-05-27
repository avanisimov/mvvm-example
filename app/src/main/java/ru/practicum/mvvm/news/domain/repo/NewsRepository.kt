package ru.practicum.mvvm.news.domain.repo

import kotlinx.coroutines.flow.Flow
import ru.practicum.mvvm.news.domain.model.NewsArticle

interface NewsRepository {

    suspend fun search(query: String): List<NewsArticle>

    suspend fun saveSearchHistory(history: List<String>)

    fun getSearchHistoryFlow(): Flow<List<String>>
}