package ru.practicum.mvvm.news.domain

import kotlinx.coroutines.flow.Flow
import ru.practicum.mvvm.news.domain.repo.NewsRepository
import javax.inject.Inject

class GetSearchHistoryFlowUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {

    operator fun invoke(): Flow<List<String>> = newsRepository.getSearchHistoryFlow()
}