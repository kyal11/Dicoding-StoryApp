package com.dicoding.storyapp.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.datasource.remote.response.RegisterResponse
import com.dicoding.storyapp.data.repository.UserRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _registerStatus = MutableStateFlow<Boolean?>(null)
    val registerStatus: StateFlow<Boolean?> = _registerStatus

    private val _registerMessage = MutableStateFlow<String?>(null)
    val registerMessage: StateFlow<String?> = _registerMessage

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.register(name, email, password)
                _registerMessage.value = response.message
                _registerStatus.value = response.error == false
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
                _errorMessage.value = errorBody.message
            }
        }
    }
}
