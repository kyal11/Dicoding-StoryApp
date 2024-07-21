package com.dicoding.storyapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.datasource.remote.response.LoginResponse
import com.dicoding.storyapp.data.pref.UserModel
import com.dicoding.storyapp.data.repository.UserRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginStatus = MutableStateFlow<String?>(null)
    val loginStatus: StateFlow<String?> = _loginStatus

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.login(email, password)
                _loginStatus.value = response.message
                if (response.error == false) {
                    response.loginResult?.let { loginResult ->
                        val userModel = UserModel(email, loginResult.token ?: "", true)
                        saveSession(userModel)
                    }
                }
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, LoginResponse::class.java)
                _errorMessage.value = errorBody.message
            }
        }
    }

    private fun saveSession(userModel: UserModel) {
        viewModelScope.launch {
            userRepository.saveSession(userModel)
        }
    }
}
