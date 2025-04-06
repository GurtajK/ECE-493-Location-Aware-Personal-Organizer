package com.example.location_aware_personal_organizer.util_test

import android.content.Context
import android.content.Intent
import com.example.location_aware_personal_organizer.services.NotificationHelper
import com.example.location_aware_personal_organizer.utils.TaskDeadlineReminderReceiver
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class TaskDeadlineReminderReceiverUnitTest {
    @Test
    fun `onReceive triggers notification with correct title and message`() {
        val mockContext = mock(Context::class.java)
        val intent = Intent().apply {
            putExtra("title", "Mock Task")
            putExtra("deadline", "09:30 AM")
        }

        val mockHelper = mock(NotificationHelper::class.java)

        val receiver = TaskDeadlineReminderReceiver()

        // Temporarily override NotificationHelper if possible
        val helperField = NotificationHelper::class.java.getDeclaredField("INSTANCE")
        helperField.isAccessible = true
        helperField.set(null, mockHelper)

        receiver.onReceive(mockContext, intent)

        verify(mockHelper).sendNotification(
            eq("Upcoming Task"),
            eq("Task \"Mock Task\" is due at 09:30 AM")
        )
    }

}