package com.dicoding.storyapp.ui.postStory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.datasource.remote.response.AddNewStoryResponse
import com.dicoding.storyapp.data.repository.StoryRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import javax.inject.Inject
@HiltViewModel
class PostStoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _postStatus = MutableStateFlow<String?>(null)
    val postStatus: StateFlow<String?> = _postStatus

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun postStory(description: RequestBody, photo: MultipartBody.Part, lat: Float?, lon: Float?) {
        viewModelScope.launch {
            try {
                val response = storyRepository.postStories(description, photo, lat, lon)
                _postStatus.value = response.message
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, AddNewStoryResponse::class.java)
                _errorMessage.value = errorBody.message
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
}