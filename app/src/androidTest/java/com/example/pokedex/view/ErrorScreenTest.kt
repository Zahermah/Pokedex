package com.example.pokedex.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pokedex.ui.theme.PokedexTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ErrorScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun errorMessage_isDisplayed() {
        composeTestRule.setContent {
            PokedexTheme {
                ErrorScreen(message = "Something went wrong", onRetryClick = {})
            }
        }

        composeTestRule.onNodeWithText("Something went wrong").assertIsDisplayed()
    }

    @Test
    fun retryButton_isDisplayed() {
        composeTestRule.setContent {
            PokedexTheme {
                ErrorScreen(message = "Error", onRetryClick = {})
            }
        }

        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun errorIcon_isDisplayed() {
        composeTestRule.setContent {
            PokedexTheme {
                ErrorScreen(message = "Error", onRetryClick = {})
            }
        }

        composeTestRule.onNodeWithContentDescription("Error").assertIsDisplayed()
    }

    @Test
    fun retryButton_click_invokesCallback() {
        var clicked = false

        composeTestRule.setContent {
            PokedexTheme {
                ErrorScreen(message = "Error", onRetryClick = { clicked = true })
            }
        }

        composeTestRule.onNodeWithText("Retry").performClick()

        assertTrue(clicked)
    }
}