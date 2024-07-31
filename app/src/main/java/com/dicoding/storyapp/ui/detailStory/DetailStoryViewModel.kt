package com.dicoding.storyapp.ui.detailStory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.datasource.remote.response.GetListStoryResponse
import com.dicoding.storyapp.data.datasource.remote.response.ListStoryItem
import com.dicoding.storyapp.data.datasource.remote.response.Story
import com.dicoding.storyapp.data.repository.StoryRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DetailStoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    @ApplicationContext private val context: Context
) : ViewModel(){
    private val _storyData = MutableSharedFlow<Story?>()
    var storyData: SharedFlow<Story?> = _storyData

    private val _errorMessage = MutableSharedFlow<String?>()
    var errorMessage: SharedFlow<String?> = _errorMessage

    private val _storyDataDb = MutableSharedFlow<ListStoryItem?>()
    var storyDataDb: SharedFlow<ListStoryItem?> = _storyDataDb

    fun getDetailStory(id: String) {
        viewModelScope.launch {
            try {
                val response = storyRepository.getDetailStories(id)
                _storyData.emit(response.story)
                _errorMessage.emit(null)
            } catch(e: IOException) {
                _errorMessage.emit(context.getString(R.string.network_error))
                val story = storyRepository.getStoriesDetailWithDb(id)
                _storyDataDb.emit(story.first())
            }
            catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, GetListStoryResponse::class.java)
                _errorMessage.emit(errorBody.message)
            } catch (e: Exception) {
                _errorMessage.emit(e.message)
            }
        }
    }
}