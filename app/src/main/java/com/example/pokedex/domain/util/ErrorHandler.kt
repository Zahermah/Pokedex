package com.example.pokedex.domain.util

import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class ErrorHandler @Inject constructor() {
    fun handleError(throwable: Throwable): String {
        return when (throwable) {
            is IOException -> "Network error occurred. Please check your internet connection."
            is HttpException -> "Server error occurred. Please try again later."
            else -> "An unexpected error occurred. Please try again."
        }
    }
}