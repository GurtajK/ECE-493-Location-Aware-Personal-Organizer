package com.example.location_aware_personal_organizer.ui.completedTasks

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.location_aware_personal_organizer.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CompletedTasksScreenTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()


    @Test
    fun completedTasksScreen_showsBacktoDashboardButton() {
        composeTestRule.activity.setContent {
            CompletedTasksScreen(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Back to Dashboard").assertIsDisplayed()
        composeTestRule.onNodeWithText("Back to Dashboard").performClick()
    }

}
