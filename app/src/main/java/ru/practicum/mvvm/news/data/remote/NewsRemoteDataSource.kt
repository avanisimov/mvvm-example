package ru.practicum.mvvm.news.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import ru.practicum.mvvm.news.domain.model.NewsArticle

class NewsRemoteDataSource {

    companion object {
        const val API_KEY = "9f1ad698ae07440ab94571207d6ba9cd"
    }

    private val newsApi: NewsApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://newsapi.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApi::class.java)
    }


    suspend fun search(query: String): NewsApiResponse {
        return newsApi.getEverything(
            query = query,
            from = "2025-04-27",
            sortBy = "publishedAt",
            apiKey = API_KEY
        )
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
        val articles: List<NewsArticle>
    )

}