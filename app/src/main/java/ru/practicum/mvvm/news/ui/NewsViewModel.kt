package ru.practicum.mvvm.news.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.practicum.mvvm.news.domain.GetArticlesUseCase
import ru.practicum.mvvm.news.domain.GetSearchHistoryFlowUseCase
import ru.practicum.mvvm.news.domain.model.NewsArticle
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getArticlesUseCase: GetArticlesUseCase,
    private val getSearchHistoryFlowUseCase: GetSearchHistoryFlowUseCase,
) : ViewModel() {

    private val searchHistory = getSearchHistoryFlowUseCase()
    private val _state = MutableStateFlow<NewsState>(NewsState.Data(emptyList()))
    val state: Flow<NewsState> = combine(_state, searchHistory) { state, history ->
        if (state is NewsState.Search) {
            state.copy(history = history)
        } else {
            state
        }
    }

    init {
        viewModelScope.launch {
            val articles = getArticlesUseCase("")
            _state.update {
                NewsState.Data(articles)
            }
        }
    }

    fun onSearchClick() {
        viewModelScope.launch {
            _state.update {
                NewsState.Search(
                    searchQuery = "",
                    history = emptyList()
                )
            }
        }
    }

    fun onSearchChanged(query: String) {
        viewModelScope.launch {
            _state.update {
                if (it is NewsState.Search) {
                    it.copy(searchQuery = query)
                } else {
                    it
                }

            }
        }
    }

    fun onSearchDone() {
        viewModelScope.launch {
            val state = _state.value
            if (state is NewsState.Search) {
                val articles = getArticlesUseCase(state.searchQuery)
                _state.update {
                    NewsState.Data(articles)
                }
            } else {
                _state.update {
                    NewsState.Data(emptyList())
                }
            }
        }
    }

    fun onSearchHistoryClick(query: String) {
        viewModelScope.launch {
            val articles = getArticlesUseCase(query)
            _state.update {
                NewsState.Data(articles)
            }
        }
    }

    fun onSearchCancel() {
        viewModelScope.launch {
            val articles = getArticlesUseCase("")
            _state.update {
                NewsState.Data(articles)
            }
        }
    }
}

sealed class NewsState {
    data class Data(
        val articles: List<NewsArticle>
    ) : NewsState()

    data class Search(
        val searchQuery: String,
        val history: List<String>
    ) : NewsState()
}