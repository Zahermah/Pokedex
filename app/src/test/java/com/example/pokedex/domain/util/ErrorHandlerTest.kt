package com.example.pokedex.domain.util

import io.mockk.mockk
import okio.IOException
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

class ErrorHandlerTest {

    private lateinit var errorHandler: ErrorHandler

    @Before
    fun setup() {
        errorHandler = ErrorHandler()
    }

    @Test
    fun `handleError with IOException returns network error message`() {
        val result = errorHandler.handleError(IOException("network error"))
        assertEquals("Network error occurred. Please check your internet connection.", result)
    }

    @Test
    fun `handleError with HttpException returns server error message`() {
        val result = errorHandler.handleError(mockk<HttpException>())
        assertEquals("Server error occurred. Please try again later.", result)
    }

    @Test
    fun `handleError with RuntimeException returns unexpected error message`() {
        val result = errorHandler.handleError(RuntimeException("unexpected"))
        assertEquals("An unexpected error occurred. Please try again.", result)
    }

    @Test
    fun `handleError with IllegalStateException returns unexpected error message`() {
        val result = errorHandler.handleError(IllegalStateException("state error"))
        assertEquals("An unexpected error occurred. Please try again.", result)
    }
}
