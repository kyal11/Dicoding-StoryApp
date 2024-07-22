package com.dicoding.storyapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dicoding.storyapp.data.datasource.remote.response.ListStoryItem
import com.dicoding.storyapp.data.datasource.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class StoryRepository @Inject constructor(
    private val apiService: ApiService
){
    fun getStories() : Flow<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPagingSource(apiService) }
        ).flow
    }
    suspend fun getDetailStories(id: String) = apiService.getDetailStories(id)

    suspend fun postStories(description: String, photo: MultipartBody.Part, lat: Float?, lon: Float?) = apiService.postStories(description, photo, lat, lon)
}