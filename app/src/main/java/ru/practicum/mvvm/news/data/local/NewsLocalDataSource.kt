package ru.practicum.mvvm.news.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.mvvm.dataStore
import javax.inject.Inject

class NewsLocalDataSource  @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    companion object {
        val SEARCH_HISTORY_KEY = stringPreferencesKey("SEARCH_HISTORY_KEY")
    }

    suspend fun saveSearchHistory(history: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[SEARCH_HISTORY_KEY] = history
                .joinToString(",")
        }
    }

    fun getSearchHistory(): Flow<List<String>> {
        return context.dataStore.data.map { prefs ->
            val searchHistoryString = prefs[SEARCH_HISTORY_KEY] ?: ""
            searchHistoryString.split(",")
        }
    }


}