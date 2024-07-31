package com.dicoding.storyapp.ui.maps

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.datasource.remote.response.GetListStoryResponse
import com.dicoding.storyapp.data.repository.StoryRepository
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
class MapsViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    @ApplicationContext private val context: Context
) : ViewModel(){
    private val _isStoryData = MutableStateFlow<GetListStoryResponse?>(null)
    val isStoryData: StateFlow<GetListStoryResponse?> = _isStoryData

    private val _errorMessage = MutableSharedFlow<String?>()
    val errorMessage: SharedFlow<String?> = _errorMessage

    init {
        fetchStoriesWithLocation()
    }
    private fun fetchStoriesWithLocation() {
        viewModelScope.launch {
            try {
                storyRepository.getStoriesWithLocation()
                    .collect { data ->
                        _isStoryData.value = data
                }
                _errorMessage.emit(null)
            } catch (e: IOException) {
                _errorMessage.emit(context.getString(R.string.network_error))
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, GetListStoryResponse::class.java)
                _errorMessage.emit(errorBody.message)
            } catch (e: Exception) {
                _errorMessage.emit(e.message)
                Log.d("Exception Maps", e.message.toString())
            }
        }
    }
}