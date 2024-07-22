package com.dicoding.storyapp.data.pref

data class UserModel(
    val email: String,
    val token: String,
    val theme: Boolean = false, // false: Light, true: Dark
    val language: Boolean = false, // false: English, true: Indonesian
    val isLogin: Boolean = false
)