package ru.practicum.mvvm.news.domain

import kotlinx.coroutines.flow.Flow
import ru.practicum.mvvm.news.domain.repo.NewsRepository

class GetSearchHistoryFlowUseCase(
    private val newsRepository: NewsRepository
) {

    operator fun invoke():Flow<List<String>> = newsRepository.getSearchHistoryFlow()
}