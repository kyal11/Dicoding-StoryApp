package com.dicoding.storyapp.data.datasource.remote.retrofit

import com.dicoding.storyapp.data.datasource.remote.response.AddNewStoryResponse
import com.dicoding.storyapp.data.datasource.remote.response.GetDetailStoryResponse
import com.dicoding.storyapp.data.datasource.remote.response.GetListStoryResponse
import com.dicoding.storyapp.data.datasource.remote.response.LoginResponse
import com.dicoding.storyapp.data.datasource.remote.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ) : LoginResponse

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int? = 0
    ): GetListStoryResponse
    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location : Int = 1,
    ): GetListStoryResponse

    @GET("stories/{id}")
    suspend fun getDetailStories(
        @Path("id") id: String
    ) : GetDetailStoryResponse
    @Multipart
    @POST("stories")
    suspend fun postStories(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?
    ) : AddNewStoryResponse
}