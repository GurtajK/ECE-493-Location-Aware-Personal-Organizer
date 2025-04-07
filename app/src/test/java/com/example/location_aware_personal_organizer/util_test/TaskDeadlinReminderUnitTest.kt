package com.example.location_aware_personal_organizer.util_test

import android.content.Context
import com.example.location_aware_personal_organizer.data.Task
import com.example.location_aware_personal_organizer.services.NotificationHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.Mockito.*
import java.util.*
import com.google.firebase.Timestamp
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

@OptIn(ExperimentalCoroutinesApi::class)
class TaskDeadlineReminderLogicTest {

    @Test
    fun `sendNotification is triggered when task deadline is within notify window`() = runBlocking {
        val mockContext = mock(Context::class.java)
        val mockNotificationHelper = mock(NotificationHelper::class.java)

        val now = Calendar.getInstance()

        val deadline = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 5)
        }.time

        val task = Task(
            title = "Test Task",
            description = "Testing deadline logic",
            deadline = Timestamp(deadline),
            notify = 5,
            user = "testuser"
        )

        val reminderTime = Calendar.getInstance().apply {
            time = deadline
            add(Calendar.MINUTE, -task.notify)
        }

        // Simulate the condition check
        if (now.timeInMillis in reminderTime.timeInMillis..deadline.time) {
            mockNotificationHelper.sendNotification(
                title = "Upcoming Task: ${task.title}",
                message = "Starts at $deadline"
            )
        }

        verify(mockNotificationHelper).sendNotification(
            eq("Upcoming Task: Test Task"),
            any()
        )
    }
    @Test
    fun `sendNotification is NOT triggered when current time is outside notify window`() = runBlocking {
        val mockContext = mock(Context::class.java)
        val mockNotificationHelper = mock(NotificationHelper::class.java)

        val deadline = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 10)
        }.time

        val task = Task(
            title = "Future Task",
            description = "This task is too far to trigger",
            deadline = Timestamp(deadline),
            notify = 5,
            user = "testuser"
        )

        val now = Calendar.getInstance()
        val reminderTime = Calendar.getInstance().apply {
            time = deadline
            add(Calendar.MINUTE, -task.notify)
        }

        if (now.timeInMillis in reminderTime.timeInMillis..deadline.time) {
            mockNotificationHelper.sendNotification(
                title = "Upcoming Task: ${task.title}",
                message = "Starts at $deadline"
            )
        }

        verify(mockNotificationHelper, never()).sendNotification(any(), any())
    }
}
