package com.example.location_aware_personal_organizer.ui.taskUpdate

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.location_aware_personal_organizer.services.TaskService
import com.example.location_aware_personal_organizer.ui.MainActivity
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar
import com.example.location_aware_personal_organizer.data.Task
import java.util.UUID
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*

@RunWith(AndroidJUnit4::class)
class TaskUpdateScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var createdTaskTitle: String

    @Before
    fun setupTask() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val now = Calendar.getInstance().apply { add(Calendar.HOUR, 1) }.time
        createdTaskTitle = "UITestTask-${UUID.randomUUID()}"

        runBlocking {
            TaskService.createTask(
                title = createdTaskTitle,
                description = "Task created for UI test",
                deadline = now,
                location = GeoPoint(0.0, 0.0),
                locationName = "Test Location",
                notify = 5,
                context = context,
                onSuccess = {},
                onFailure = { throw it }
            )
        }
    }

    @Test
    fun clickFirstTaskNavigatesToUpdateScreen() {
        // Wait for task to appear
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithTag("TaskItem", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Click first TaskItem
        composeRule.onAllNodesWithTag("TaskItem", useUnmergedTree = true)
            .onFirst()
            .assertExists()
            .performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Update Task", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Update Task", useUnmergedTree = true)
            .assertIsDisplayed()
    }

    @Test
    fun fillFormAndSubmitTaskUpdate() {
        // Wait for the task item with the test tag to show up
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithTag("TaskItem", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onAllNodesWithTag("TaskItem", useUnmergedTree = true)
            .onFirst()
            .assertExists()
            .performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Update Task", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        val taskNameNode = composeRule.onNodeWithTag("taskNameInput", useUnmergedTree = true)
        taskNameNode.assertExists()
        taskNameNode.performTextClearance()
        taskNameNode.performTextInput("Updated Task Title")

        val descriptionNode = composeRule.onNodeWithTag("descriptionInput", useUnmergedTree = true)
        descriptionNode.assertExists()
        descriptionNode.performTextClearance()
        descriptionNode.performTextInput("This task was updated via UI test.")

        composeRule.onNodeWithTag("deadlineInput", useUnmergedTree = true)
            .performClick()

        composeRule.onNodeWithText("OK", useUnmergedTree = true)
            .assertExists()
            .performClick()

        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodesWithText("OK", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("OK", useUnmergedTree = true)
            .performClick()

        composeRule.onNodeWithText("Notify Before", useUnmergedTree = true)
            .performClick()
        composeRule.onNodeWithText("10 minutes", useUnmergedTree = true)
            .performClick()

        composeRule.onNodeWithText("Update Task", useUnmergedTree = true)
            .performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Task updated successfully", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Task updated successfully", useUnmergedTree = true)
            .assertIsDisplayed()
    }

}

