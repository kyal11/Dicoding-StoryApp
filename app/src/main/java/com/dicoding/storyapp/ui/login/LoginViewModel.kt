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

    private val _loginStatus = MutableStateFlow<Boolean?>(null)
    val loginStatus: StateFlow<Boolean?> = _loginStatus

    private val _loginMassage = MutableStateFlow<String?>(null)
    val loginMassage: StateFlow<String?> = _loginMassage

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.login(email, password)
                _loginMassage.value = response.message
                if (response.error == false) {
                    response.loginResult?.let { loginResult ->
                        val userModel = UserModel(email, loginResult.token ?: "")
                        saveSession(userModel)
                        _loginStatus.value = true
                    }
                }
                else {
                    _loginStatus.value = false
                }
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, LoginResponse::class.java)
                _errorMessage.value = errorBody.message
                _loginStatus.value = false
            }
        }
    }

    private fun saveSession(userModel: UserModel) {
        viewModelScope.launch {
            userRepository.saveSession(userModel)
        }
    }
}
