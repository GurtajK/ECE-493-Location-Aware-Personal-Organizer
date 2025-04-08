package com.example.location_aware_personal_organizer.ui.notifyPreference

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.location_aware_personal_organizer.services.Authorization
import com.example.location_aware_personal_organizer.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationSettingsScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun navigateToSettingsScreen() {
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

    @Test
    fun grantNotificationPermission_buttonExistsAndCanBeClicked() {
        navigateToSettingsScreen()

        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodesWithText("Enable Notification Permission", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Enable Notification Permission", useUnmergedTree = true)
            .assertExists()
            .performClick()

        // Wait to give you time to manually grant permission in system settings to continue the next 2 tests
        println("Waiting 15 seconds for you to manually enable notification permission...")
        Thread.sleep(15_000)
    }

    @Test
    fun toggleTimeNotifications_enableAndDisable() {
        navigateToSettingsScreen()

        composeRule.onNodeWithText("Enable Time Notifications (before deadline)", useUnmergedTree = true)
            .assertExists()

        val timeSwitch = composeRule.onNodeWithTag("timeSwitch", useUnmergedTree = true)
        timeSwitch.assertExists().assertIsEnabled()

        timeSwitch.performClick()
        Thread.sleep(1000)
        composeRule.waitForIdle()

        timeSwitch.performClick()
    }

    @Test
    fun togglePriorityNotifications_enableAndDisable() {
        navigateToSettingsScreen()

        composeRule.onNodeWithText("Enable Prioritized Task Notifications", useUnmergedTree = true)
            .assertExists()

        val prioritySwitch = composeRule.onNodeWithTag("prioritySwitch", useUnmergedTree = true)
        prioritySwitch.assertExists().assertIsEnabled()

        prioritySwitch.performClick()
        Thread.sleep(1000)
        composeRule.waitForIdle()

        prioritySwitch.performClick()
    }


    @Test
    fun backButton_navigatesBackToDashboard() {
        navigateToSettingsScreen()

        // Click the back arrow
        composeRule.onNodeWithContentDescription("Back", useUnmergedTree = true)
            .assertExists()
            .performClick()

        // Wait for Dashboard screen elements (like welcome message or create button)
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Welcome to your Task Dashboard!", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Welcome to your Task Dashboard!", useUnmergedTree = true)
            .assertIsDisplayed()
    }
}
