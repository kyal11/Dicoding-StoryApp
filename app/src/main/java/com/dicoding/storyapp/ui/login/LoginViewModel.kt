package com.dicoding.storyapp.ui.login

import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.datasource.remote.response.LoginResponse
import com.dicoding.storyapp.data.pref.UserModel
import com.dicoding.storyapp.data.repository.UserRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _loginStatus = MutableSharedFlow<Boolean>()
    val loginStatus: SharedFlow<Boolean> = _loginStatus

    private val _loginMessage = MutableSharedFlow<String>()
    val loginMessage: SharedFlow<String> = _loginMessage

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage


    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.login(email, password)
                response.message?.let { _loginMessage.emit(it) }
                if (response.error == false) {
                    response.loginResult?.let { loginResult ->
                        val theme = isDeviceDarkMode()
                        val language = isDeviceLanguageIndonesian()
                        val userModel = UserModel(
                            email = email,
                            token = loginResult.token ?: "",
                            theme = theme,
                            language = language,
                            isLogin = true
                        )
                        saveSession(userModel) {
                            runBlocking {
                                _loginStatus.emit(true)
                            }
                        }
                    }
                } else {
                    _loginStatus.emit(false)
                }
            }catch (e: IOException) {
                _errorMessage.emit(context.getString(R.string.network_error))
            }
            catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, LoginResponse::class.java)
                _errorMessage.emit(errorBody.message.toString())
            }
        }
    }

    private fun isDeviceDarkMode(): Boolean {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    private fun isDeviceLanguageIndonesian(): Boolean {
        val locale = context.resources.configuration.locales[0]
        return locale.language == "in"
    }

    private fun saveSession(userModel: UserModel, onCompleted: () -> Unit) {
        viewModelScope.launch {
            userRepository.saveSession(userModel)
        }.invokeOnCompletion { onCompleted() }
    }
}
