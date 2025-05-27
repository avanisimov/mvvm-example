package ru.practicum.mvvm.news.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import retrofit2.http.GET
import retrofit2.http.Query
import ru.practicum.mvvm.dataStore
import ru.practicum.mvvm.news.data.NewsRepositoryImpl
import ru.practicum.mvvm.news.data.local.NewsLocalDataSource
import ru.practicum.mvvm.news.data.local.NewsLocalDataSource.Companion.SEARCH_HISTORY_KEY
import ru.practicum.mvvm.news.data.remote.NewsRemoteDataSource
import ru.practicum.mvvm.news.domain.GetArticlesUseCase
import ru.practicum.mvvm.news.domain.GetSearchHistoryFlowUseCase
import ru.practicum.mvvm.news.domain.repo.NewsRepository
import java.text.SimpleDateFormat

const val API_KEY = "9f1ad698ae07440ab94571207d6ba9cd"


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: NewsViewModel = viewModel(
        factory = NewsViewModelFactory(context)
    )
    val dateFormatter = remember {
        SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM)
    }

    val state by viewModel.state.collectAsState(NewsState.Data(emptyList()))
    val stateLocal = state
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (stateLocal is NewsState.Search) {
                        TextField(
                            value = stateLocal.searchQuery,
                            onValueChange = {
                                viewModel.onSearchChanged(it)
                            },
                            placeholder = { Text("Поиск...") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                errorContainerColor = Color.Transparent
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    viewModel.onSearchDone()
                                }
                            )
                        )
                    } else {
                        Text(text = "Главная")
                    }
                },
                navigationIcon = {
                    if (stateLocal is NewsState.Search) {
                        IconButton(onClick = {
                            viewModel.onSearchCancel()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                        }
                    }
                },
                actions = {
                    if (stateLocal is NewsState.Data) {
                        IconButton(onClick = {
                            viewModel.onSearchClick()
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Поиск")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        when (stateLocal) {
            is NewsState.Data -> {
                LazyColumn(
                    modifier = Modifier.padding(innerPadding),
                ) {
                    items(stateLocal.articles) {
                        println("article = $it")
                        Column(modifier = Modifier.padding(16.dp)) {
                            it.author?.run {
                                Text(
                                    text = this,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Text(
                                text = dateFormatter.format(it.publishedAt), //dateFormatter.format(it.publishedAt),
                                style = MaterialTheme.typography.labelSmall
                            )
                            AsyncImage(
                                model = it.urlToImage,
                                contentDescription = null
                            )
                            Text(
                                text = it.title,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = it.description,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
            is NewsState.Search -> {
                LazyColumn(
                    modifier = Modifier,
                ) {
                    items(stateLocal.history) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable {
                                    viewModel.onSearchHistoryClick(it)
                                }
                        )
                    }
                }
            }
        }
    }
}

class NewsViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val newsRepository = NewsRepositoryImpl(
            remoteDataSource = NewsRemoteDataSource(),
            localDataSource = NewsLocalDataSource(context)
        )
        return NewsViewModel(
            getArticlesUseCase =  GetArticlesUseCase(
                newsRepository = newsRepository
            ),
            getSearchHistoryFlowUseCase =  GetSearchHistoryFlowUseCase(
                newsRepository = newsRepository
            ),
        ) as T
    }
}

