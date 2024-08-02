package com.dicoding.storyapp.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.datasource.remote.response.GetListStoryResponse
import com.dicoding.storyapp.data.datasource.remote.response.ListStoryItem
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.repository.UserRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _isListStory = MutableStateFlow<PagingData<ListStoryItem>?>(null)
    val isListStory: StateFlow<PagingData<ListStoryItem>?> = _isListStory

    private val _errorMessage = MutableSharedFlow<String?>()
    val errorMessage: SharedFlow<String?> = _errorMessage

    private val _isLoading = MutableSharedFlow<Boolean>()
    val isLoading: SharedFlow<Boolean> get() = _isLoading

    init {
        getListStory()
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
     fun getListStory() {
        viewModelScope.launch {
            _isLoading.emit(true)
            try {
                storyRepository.getStories()
                    .cachedIn(viewModelScope)
                    .collect { pagingData ->
                        _isListStory.emit(pagingData)
                        _isLoading.emit(false)
                    }
                _errorMessage.emit(null)
            } catch (e: IOException) {
                _errorMessage.emit(context.getString(R.string.network_error))
            }
            catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, GetListStoryResponse::class.java)
                _errorMessage.emit(errorBody.message)
                _isLoading.emit(false)
            } catch (e: Exception) {
                _errorMessage.emit(e.message)
                _isLoading.emit(false)
            }
        }
    }

}
