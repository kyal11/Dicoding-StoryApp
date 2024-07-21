package com.dicoding.storyapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.pref.UserModel
import com.dicoding.storyapp.data.pref.UserPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreference: UserPreference
) : ViewModel(){

    private val _userSession = MutableSharedFlow<UserModel>()
    val userSession : Flow<UserModel> = _userSession

    init {
        viewModelScope.launch {
            userPreference.getSession().collect { userModel ->
                _userSession.emit(userModel)
            }
        }
    }
}