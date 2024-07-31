package com.dicoding.storyapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dicoding.storyapp.data.datasource.StoryRemoteMediator
import com.dicoding.storyapp.data.datasource.local.StoryDatabase
import com.dicoding.storyapp.data.datasource.remote.response.GetListStoryResponse
import com.dicoding.storyapp.data.datasource.remote.response.ListStoryItem
import com.dicoding.storyapp.data.datasource.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class StoryRepository @Inject constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase
){
    fun getStories() : Flow<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).flow
    }
    fun getStoriesDetailWithDb(id: String) : Flow<ListStoryItem> {
        return storyDatabase.storyDao().getStoryById(id)
    }
    fun getStoriesWithLocation(): Flow<GetListStoryResponse> = flow {
        val response = apiService.getStoriesWithLocation()
        emit(response)
    }
    suspend fun getDetailStories(id: String) = apiService.getDetailStories(id)

    suspend fun postStories(description: RequestBody, photo: MultipartBody.Part, lat: Float?, lon: Float?) = apiService.postStories(description, photo, lat, lon)
}