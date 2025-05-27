package ru.practicum.mvvm.news.domain

import kotlinx.coroutines.flow.first
import ru.practicum.mvvm.news.domain.model.NewsArticle
import ru.practicum.mvvm.news.domain.repo.NewsRepository

class GetArticlesUseCase(
    private val newsRepository: NewsRepository
) {

    suspend operator fun invoke(query: String): List<NewsArticle> {
        val articles = newsRepository.search(query.ifBlank { "science" })
        val searchHistory = newsRepository.getSearchHistoryFlow().first()
        newsRepository.saveSearchHistory(
            searchHistory
                .toMutableList()
                .apply {
                    add(query)
                }
                .toSet()
                .take(5)
        )
        return articles
    }
}