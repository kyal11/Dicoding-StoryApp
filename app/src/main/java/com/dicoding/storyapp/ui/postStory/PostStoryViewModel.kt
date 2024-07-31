package com.dicoding.storyapp.ui.postStory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.datasource.remote.response.AddNewStoryResponse
import com.dicoding.storyapp.data.repository.StoryRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
@HiltViewModel
class PostStoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _postStatus = MutableSharedFlow<String?>()
    val postStatus: SharedFlow<String?> = _postStatus

    private val _errorMessage = MutableSharedFlow<String?>()
    val errorMessage: SharedFlow<String?> = _errorMessage

    fun postStory(description: RequestBody, photo: MultipartBody.Part, lat: Float?, lon: Float?) {
        viewModelScope.launch {
            try {
                val response = storyRepository.postStories(description, photo, lat, lon)
                _postStatus.emit(response.message)
            } catch (e: IOException) {
                _errorMessage.emit(context.getString(R.string.network_error))
            }
            catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, AddNewStoryResponse::class.java)
                _errorMessage.emit(errorBody.message)
            } catch (e: Exception) {
                _errorMessage.emit(e.message)
            }
        }
    }
}