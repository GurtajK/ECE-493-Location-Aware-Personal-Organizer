package com.example.location_aware_personal_organizer.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.location_aware_personal_organizer.services.NotificationHelper
import com.example.location_aware_personal_organizer.services.TaskService
import com.example.location_aware_personal_organizer.ui.notifyPreference.NotificationPreferencesManager
import kotlinx.coroutines.flow.first
import java.util.Calendar


class TaskDeadlineReminder(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {


    override suspend fun doWork(): Result {
        val timeEnabled = NotificationPreferencesManager
            .getTimeNotificationState(context)
            .first()
        Log.d("TaskReminder", "TaskDeadlineReminder worker started")
        if (!timeEnabled) {
            return Result.success()
        }

        return checkAndNotifyUpcomingDeadlines()
    }

    private suspend fun checkAndNotifyUpcomingDeadlines(): Result {
        val now = Calendar.getInstance()
        val tasks = TaskService.getTasksForCurrentUser()

        tasks.forEach { task ->
            val deadline = task.deadline?.toDate()
            val timeToNotify = task.notify
            if (deadline != null) {
                val reminderTime = Calendar.getInstance().apply {
                    time = deadline
                    add(Calendar.MINUTE, -timeToNotify)
                }

                if (now.timeInMillis in reminderTime.timeInMillis..deadline.time) {
                    NotificationHelper(context).sendNotification(
                        title = "Upcoming Task: ${task.title}",
                        message = "Starts at ${task.deadline.toDate()}"
                    )
                }
            }
        }

        return Result.success()
    }
}
