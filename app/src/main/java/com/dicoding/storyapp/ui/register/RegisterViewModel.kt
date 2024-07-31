package com.dicoding.storyapp.ui.register

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.datasource.remote.response.RegisterResponse
import com.dicoding.storyapp.data.repository.UserRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _registerStatus = MutableSharedFlow<Boolean?>()
    val registerStatus: SharedFlow<Boolean?> = _registerStatus

    private val _registerMessage = MutableSharedFlow<String?>()
    val registerMessage: SharedFlow<String?> = _registerMessage

    private val _errorMessage = MutableSharedFlow<String?>()
    val errorMessage: SharedFlow<String?> = _errorMessage

    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.register(name, email, password)
                _registerMessage.emit(response.message)
                _registerStatus.emit(response.error == false)
            } catch (e: IOException) {
                _errorMessage.emit(context.getString(R.string.network_error))
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
                _errorMessage.emit(errorBody.message.toString())
            }
        }
    }
}
