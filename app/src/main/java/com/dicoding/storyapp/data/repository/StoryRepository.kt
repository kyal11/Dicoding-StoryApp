package com.dicoding.storyapp.data.repository

import com.dicoding.storyapp.data.datasource.remote.retrofit.ApiService
import okhttp3.MultipartBody
import javax.inject.Inject

class StoryRepository @Inject constructor(
    private val apiService: ApiService
){
    suspend fun getStories() = apiService.getStories()

    suspend fun getDetailStories(token: String, id: String) = apiService.getDetailStories(id)

    suspend fun postStories(description: String, photo: MultipartBody.Part, lat: Float?, lon: Float?) = apiService.postStories(description, photo, lat, lon)
}