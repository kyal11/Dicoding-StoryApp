package com.dicoding.storyapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.pref.UserModel
import com.dicoding.storyapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val userSession: Flow<UserModel> = userRepository.getSession()

    fun updateTheme(isDarkMode: Boolean) {
        viewModelScope.launch {
            try {
                val user = userRepository.getSession().first()
                val updatedUser = user.copy(theme = isDarkMode)
                userRepository.saveSession(updatedUser)
            } catch (e: Exception) {

            }
        }
    }

    fun updateLanguage(isIndonesian: Boolean) {
        viewModelScope.launch {
            try {
                val user = userRepository.getSession().first()
                val updatedUser = user.copy(language = isIndonesian)
                userRepository.saveSession(updatedUser)
            } catch (e: Exception) {

            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                userRepository.logout()
            } catch (e: Exception) {

            }
        }
    }
}
