package com.dicoding.storyapp.ui.detailStory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.datasource.remote.response.GetListStoryResponse
import com.dicoding.storyapp.data.datasource.remote.response.Story
import com.dicoding.storyapp.data.repository.StoryRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class DetailStoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository
) : ViewModel(){
    private val _storyData = MutableStateFlow<Story?>(null)
    var storyData: MutableStateFlow<Story?> = _storyData

    private val _errorMessage = MutableStateFlow<String?>(null)
    var errorMessage: MutableStateFlow<String?> = _errorMessage


    fun getDetailStory(id: String) {
        viewModelScope.launch {
            try {
                val response = storyRepository.getDetailStories(id)
                _storyData.value = response.story
                _errorMessage.value = null
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, GetListStoryResponse::class.java)
                _errorMessage.value = errorBody.message
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
}