package com.dicoding.storyapp.data.repository.di

import com.dicoding.storyapp.data.datasource.remote.retrofit.ApiService
import com.dicoding.storyapp.data.pref.UserPreference
import com.dicoding.storyapp.data.repository.StoryPagingSource
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        userPreference: UserPreference,
        apiService: ApiService
    ): UserRepository {
        return UserRepository(userPreference, apiService)
    }

    @Provides
    @Singleton
    fun provideStoryRepository(
        apiService: ApiService
    ) : StoryRepository {
        return StoryRepository(apiService)
    }

    @Provides
    @Singleton
    fun providesStoryPagingSource(
        apiService: ApiService
    ) : StoryPagingSource {
        return StoryPagingSource(apiService)
    }
}