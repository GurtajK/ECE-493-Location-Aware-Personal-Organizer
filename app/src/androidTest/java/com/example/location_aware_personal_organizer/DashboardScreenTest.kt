package com.example.location_aware_personal_organizer.ui.dashboard

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.location_aware_personal_organizer.R
import com.example.location_aware_personal_organizer.services.Authorization
import com.example.location_aware_personal_organizer.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DashboardScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun dashboard_displaysCreateNewTaskButton() {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Create New Task", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule
            .onNodeWithText("Create New Task", useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun dashboard_searchField_interactsCorrectly() {
        val searchField = composeRule.onNodeWithText("Search tasks by title/description")
        searchField.assertIsDisplayed()

        searchField.performTextInput("Meeting")
        searchField.assertTextContains("Meeting")
    }

    @Test
    fun dashboard_displaysWelcomeMessage() {
        val expectedText = "Welcome to your Task Dashboard!"

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(expectedText, useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule
            .onNodeWithText(expectedText, useUnmergedTree = true)
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun dashboard_openAndCloseSearchFilterDialog() {
        // Click filter icon
        composeRule.onNodeWithContentDescription("Search/Filter Tasks")
            .assertExists()
            .performClick()

        // Wait for the dialog to appear
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Apply", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Confirm the Apply and Clear Filters buttons exist
        composeRule.onNodeWithText("Apply", useUnmergedTree = true)
            .assertExists()
            .assertIsDisplayed()

        composeRule.onNodeWithText("Clear Filters", useUnmergedTree = true)
            .assertExists()
            .performClick()
    }

    @Test
    fun createNewTaskButton_navigatesOnClick() {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Create New Task", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Create New Task", useUnmergedTree = true).performClick()

        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodesWithText("Task Create", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule
            .onAllNodesWithText("Task Create", useUnmergedTree = true)
            .onFirst()
            .assertIsDisplayed()

        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodesWithText("Cancel", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Cancel", useUnmergedTree = true).performClick()
    }

    @Test
    fun dropdownMenu_navigatesToSettings() {
        val username = Authorization.getUsername()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(username, useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText(username, useUnmergedTree = true)
            .performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Settings", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Settings", useUnmergedTree = true)
            .performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Notification Settings", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Notification Settings", useUnmergedTree = true)
            .assertIsDisplayed()
    }
}
