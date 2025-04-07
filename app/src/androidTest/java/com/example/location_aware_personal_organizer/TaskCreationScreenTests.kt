package com.example.location_aware_personal_organizer.ui.taskCreation

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.location_aware_personal_organizer.ui.MainActivity
import com.example.location_aware_personal_organizer.ui.taskCreation.TaskCreationScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskCreationScreenTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun taskCreationScreen_displaysAllInputFields() {
        composeTestRule.activity.setContent {
            TaskCreationScreen(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Task Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Task Description").assertIsDisplayed()
        composeTestRule.onNodeWithText("Please select a date and time").assertIsDisplayed()
        composeTestRule.onNodeWithText("Task Location").assertIsDisplayed()
        composeTestRule.onNodeWithText("15 minutes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create Task").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun taskCreationScreen_typingIntoFields_updatesValues() {
        composeTestRule.activity.setContent {
            TaskCreationScreen(navController = rememberNavController())
        }

        // Task Name
        composeTestRule.onNodeWithText("Task Name").performTextInput("Grocery Shopping")

        // Task Description
        composeTestRule.onNodeWithText("Task Description").performTextInput("Buy fruits and vegetables")

        // Task Location
        composeTestRule.onNodeWithText("Task Location").performTextInput("Walmart")

        // Verify inputs exist
        composeTestRule.onNodeWithText("Grocery Shopping", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithText("Buy fruits and vegetables", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithText("Walmart", useUnmergedTree = true).assertExists()
    }

    @Test
    fun taskCreationScreen_createTask_showsSnackbarOnMissingFields() {
        composeTestRule.activity.setContent {
            TaskCreationScreen(navController = rememberNavController())
        }

        // Click Create Task without filling anything
        composeTestRule.onNodeWithText("Create Task").performClick()

        // Wait for snackbar to appear
        composeTestRule.waitUntil(
            timeoutMillis = 3000
        ) {
            composeTestRule.onAllNodesWithText("Please fill in all required fields").fetchSemanticsNodes().isNotEmpty()
        }

        // Check Snackbar message
        composeTestRule.onNodeWithText("Please fill in all required fields").assertIsDisplayed()
    }

}
