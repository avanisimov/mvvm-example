package ru.practicum.mvvm.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.practicum.mvvm.news.data.NewsRepositoryImpl
import ru.practicum.mvvm.news.domain.repo.NewsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NewsModule {

    @Binds
    @Singleton
    fun bindNewsRepository(
        impl: NewsRepositoryImpl
    ): NewsRepository
}