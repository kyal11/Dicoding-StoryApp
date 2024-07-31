package com.dicoding.storyapp

import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) : ViewModel(){

    private val _isCurrentTheme: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isCurrentTheme: StateFlow<Boolean> = _isCurrentTheme

    private val _isCurrentLanguage: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isCurrentLanguage: StateFlow<Boolean> = _isCurrentLanguage

    init {
        getPreferences()
    }

    private fun getPreferences() {
        viewModelScope.launch {
            userRepository.getSession().collect { session ->
                if (session.token.isEmpty()) {
                    _isCurrentTheme.value = isDeviceDarkMode()
                    _isCurrentLanguage.value = isDeviceLanguageIndonesian()
                } else {
                    _isCurrentTheme.value = session.theme
                    _isCurrentLanguage.value = session.language
                }
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

    val isUserLoggedIn: Flow<Boolean> = userRepository.getSession()
        .map { it.token.isNotEmpty() }
        .distinctUntilChanged()
}
