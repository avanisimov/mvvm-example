package ru.practicum.mvvm.news

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import ru.practicum.mvvm.dataStore
import java.text.SimpleDateFormat
import java.util.Date

const val API_KEY = "9f1ad698ae07440ab94571207d6ba9cd"
val SEARCH_HISTORY_KEY = stringPreferencesKey("SEARCH_HISTORY_KEY")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val newsApi: NewsApi = remember {
        Retrofit.Builder()
            .baseUrl("https://newsapi.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApi::class.java)
    }
    val viewModel: NewsViewModel = viewModel(
        factory = NewsViewModelFactory(newsApi)
    )
    val dateFormatter = remember {
        SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM)
    }
    val scope = rememberCoroutineScope()
    val dataStore = context.dataStore
    val searchHistory: List<String> by dataStore.data
        .map { prefs ->
            val searchHistoryString = prefs[SEARCH_HISTORY_KEY] ?: ""
            searchHistoryString.split(",")
        }
        .collectAsState(emptyList())

    var isSearchMode by remember { mutableStateOf(false) }
    var tempSearchQuery by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchMode) {
                        TextField(
                            value = tempSearchQuery,
                            onValueChange = {
                                tempSearchQuery = it
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
                                    searchQuery = tempSearchQuery
                                    scope.launch {
                                        val searchHistoryToSave = searchHistory.toMutableList()
                                        searchHistoryToSave.add(0, searchQuery)
                                        dataStore.edit { preferences ->
                                            preferences[SEARCH_HISTORY_KEY] = searchHistoryToSave
                                                .toSet()
                                                .take(5)
                                                .joinToString(",")
                                        }
                                    }
                                    isSearchMode = false
                                }
                            )
                        )
                    } else {
                        Text(text = "Главная")
                    }
                },
                navigationIcon = {
                    if (isSearchMode) {
                        IconButton(onClick = {
                            isSearchMode = false
                            tempSearchQuery = ""
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                        }
                    }
                },
                actions = {
                    if (!isSearchMode) {
                        IconButton(onClick = {
                            isSearchMode = true
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
        var articles: List<Article> by remember { mutableStateOf(emptyList<Article>()) }

        LaunchedEffect(searchQuery) {
            val newsApiResponse = newsApi.getEverything(
                query = searchQuery.ifEmpty {
                    "science"
                },
                from = "2025-05-12",
                sortBy = "popularity",
                apiKey = API_KEY
            )
            Log.d("NEWS_API", "newsApiResponse = ${newsApiResponse.articles.size}")
            articles = newsApiResponse.articles
        }

        if (isSearchMode) {
            LazyColumn(
                modifier = Modifier,
            ) {
                items(searchHistory) {
                    println("searchHistory = $it")
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable {
                                searchQuery = it
                                scope.launch {
                                    val searchHistoryToSave = searchHistory.toMutableList()
                                    searchHistoryToSave.add(0, searchQuery)
                                    dataStore.edit { preferences ->
                                        preferences[SEARCH_HISTORY_KEY] = searchHistoryToSave
                                            .toSet()
                                            .take(5)
                                            .joinToString(",")
                                    }
                                }
                                isSearchMode = false
                            }
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
            ) {
                items(articles) {
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

    }
}

class NewsViewModelFactory(
    private val newsApi: NewsApi
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(newsApi) as T
    }
}

interface NewsApi {

    @GET("/v2/everything")
    suspend fun getEverything(
        @Query("q") query: String,
        @Query("from") from: String,
        @Query("sortBy") sortBy: String,
        @Query("apiKey") apiKey: String
    ): NewsApiResponse
}

data class NewsApiResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)

data class Article(
    val author: String?,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: Date,
    val content: String,
)