package com.dicoding.storyapp.foundation.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    private fun getInputFormat(): SimpleDateFormat {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    private fun getOutputFormat(): SimpleDateFormat {
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    fun formatDate(dateString: String): String {
        return try {
            val date = getInputFormat().parse(dateString)
            date?.let { getOutputFormat().format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
}