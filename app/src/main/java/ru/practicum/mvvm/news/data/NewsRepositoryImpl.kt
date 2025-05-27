package ru.practicum.mvvm.news.data

import kotlinx.coroutines.flow.Flow
import ru.practicum.mvvm.news.data.local.NewsLocalDataSource
import ru.practicum.mvvm.news.data.remote.NewsRemoteDataSource
import ru.practicum.mvvm.news.domain.model.NewsArticle
import ru.practicum.mvvm.news.domain.repo.NewsRepository

class NewsRepositoryImpl(
    private val remoteDataSource: NewsRemoteDataSource,
    private val localDataSource: NewsLocalDataSource,
) : NewsRepository {
    override suspend fun search(query: String): List<NewsArticle> {
        return remoteDataSource.search(query).also {
            println("NewsRepositoryImpl $it")
        }.articles
    }

    override suspend fun saveSearchHistory(history: List<String>) {
        localDataSource.saveSearchHistory(history)
    }

    override fun getSearchHistoryFlow(): Flow<List<String>> {
        return localDataSource.getSearchHistory()
    }
}