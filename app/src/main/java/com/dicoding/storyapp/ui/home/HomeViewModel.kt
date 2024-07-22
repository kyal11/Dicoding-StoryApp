package com.dicoding.storyapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapp.data.datasource.remote.response.GetListStoryResponse
import com.dicoding.storyapp.data.datasource.remote.response.ListStoryItem
import com.dicoding.storyapp.data.repository.StoryRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storyRepository: StoryRepository
) : ViewModel() {
    private val _isListStory = MutableStateFlow<PagingData<ListStoryItem>?>(null)
    val isListStory : StateFlow<PagingData<ListStoryItem>?> = _isListStory

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        getListStory()
    }
    private fun getListStory() {
        viewModelScope.launch {
            try {
                storyRepository.getStories()
                    .cachedIn(viewModelScope)
                    .collect { pagingData ->
                        _isListStory.value = pagingData
                    }
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