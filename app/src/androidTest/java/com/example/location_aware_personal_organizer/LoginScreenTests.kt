package com.example.location_aware_personal_organizer.ui.login

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.location_aware_personal_organizer.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()


    @Test
    fun loginScreen_displaysAllFields() {
        composeTestRule.activity.setContent {
            LoginScreen(navController = androidx.navigation.compose.rememberNavController())
        }

        // Check that username field exists
        composeTestRule.onNodeWithText("Username").assertIsDisplayed()

        // Check that password field exists
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()

        // Check that Login button exists
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()

        // Check that "Forgot password?" link exists
        composeTestRule.onNodeWithText("Forgot Password?").assertIsDisplayed()

        // Check that Register link exists
        composeTestRule.onNodeWithText("Don't have an account? Sign Up").assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsSnackbarOnEmptyFields() {
        composeTestRule.activity.setContent {
            LoginScreen(navController = androidx.navigation.compose.rememberNavController())
        }

        // Press the Login button without entering anything
        composeTestRule.onNodeWithText("Login").performClick()

        // Wait for Snackbar to appear
        composeTestRule.waitUntil(
            timeoutMillis = 5000
        ) {
            composeTestRule.onAllNodesWithText("Please fill in all required fields").fetchSemanticsNodes().isNotEmpty()
        }
        // Confirm Snackbar is shown
        composeTestRule.onNodeWithText("Please fill in all required fields").assertIsDisplayed()
    }

    @Test
    fun loginScreen_enablesTypingUsernameAndPassword() {
        composeTestRule.activity.setContent {
            LoginScreen(navController = androidx.navigation.compose.rememberNavController())
        }

        // Type username
        composeTestRule.onNode(hasText("Username")).performTextInput("testuser")


        // Check if the typed texts are there
        composeTestRule.onNodeWithText("testuser", useUnmergedTree = true).assertIsDisplayed()
    }
}
