package com.dicoding.storyapp.data.datasource.remote.retrofit.di

import com.dicoding.storyapp.data.datasource.remote.retrofit.ApiConfig
import com.dicoding.storyapp.data.datasource.remote.retrofit.ApiService
import com.dicoding.storyapp.data.pref.UserPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiService(userPreference: UserPreference): ApiService {
        return ApiConfig.getApiService(userPreference)
    }
}
