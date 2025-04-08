package com.example.location_aware_personal_organizer.ui.register

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.location_aware_personal_organizer.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterScreenTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun registerScreen_displaysAllFields() {
        composeTestRule.activity.setContent {
            RegisterScreen(navController = androidx.navigation.compose.rememberNavController())
        }

        // Check all fields are visible
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Username").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Confirm Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign Up").assertIsDisplayed()
        composeTestRule.onNodeWithText("Already have an account? Login").assertIsDisplayed()
    }

    @Test
    fun registerScreen_typingInputsUpdatesState() {
        composeTestRule.activity.setContent {
            RegisterScreen(navController = androidx.navigation.compose.rememberNavController())
        }

        // Type email
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")

        // Type username
        composeTestRule.onNodeWithText("Username").performTextInput("testuser")

        // Type password
        composeTestRule.onNodeWithText("Password").performTextInput("Password123!")

        // Type confirm password
        composeTestRule.onNodeWithText("Confirm Password").performTextInput("Password123!")

        // Confirm inputs are updated
        composeTestRule.onNodeWithText("test@example.com", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithText("testuser", useUnmergedTree = true).assertExists()
    }

    @Test
    fun registerScreen_showPasswordValidationError_whenInvalidPasswordEntered() {
        composeTestRule.activity.setContent {
            RegisterScreen(navController = androidx.navigation.compose.rememberNavController())
        }

        // Enter invalid password (e.g., too short, no number, etc.)
        composeTestRule.onNodeWithText("Password").performTextInput("abc")

        // Try to move focus away to trigger validation
        composeTestRule.onNodeWithText("Confirm Password").performClick()

        // Password error message should appear
        composeTestRule.onNodeWithText("Invalid password").assertExists()
    }

    @Test
    fun registerScreen_showConfirmPasswordValidationError_whenPasswordsMismatch() {
        composeTestRule.activity.setContent {
            RegisterScreen(navController = androidx.navigation.compose.rememberNavController())
        }

        // Enter valid password
        composeTestRule.onNodeWithText("Password").performTextInput("Password123!")

        // Enter different confirm password
        composeTestRule.onNodeWithText("Confirm Password").performTextInput("Password321!")

        // Try to move focus away to trigger validation
        composeTestRule.onNodeWithText("Email").performClick()

        // Confirm Password mismatch error should appear
        composeTestRule.onNodeWithText("Password does not match").assertExists()
    }

}
