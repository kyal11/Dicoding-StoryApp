package com.dicoding.storyapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
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
            userRepository.getSession().collect {
                _isCurrentTheme.value = it.theme
                _isCurrentLanguage.value = it.language
            }
        }
    }
    val isUserLoggedIn: Flow<Boolean> = userRepository.getSession()
        .map { it.token.isNotEmpty() }
        .distinctUntilChanged()
}