package com.example.location_aware_personal_organizer.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.location_aware_personal_organizer.services.NotificationHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// FR 55 Notify.Deadline
class TaskDeadlineReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Task Reminder"
        val deadline = intent.getStringExtra("deadline") ?: "Unknown time"
        Log.d("TaskReminder", "Alarm triggered for task: $title")

        val message = "Task \"$title\" is due at $deadline"
        NotificationHelper(context).sendNotification(
            title = "Upcoming Task",
            message = message
        )
    }
    companion object {
        @SuppressLint("ScheduleExactAlarm")
        fun scheduleTaskNotification(
            context: Context,
            taskId: String,
            notifyAtMillis: Long,
            taskTitle: String,
            deadline: Date
        ) {
            val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(deadline)

            val intent = Intent(context, TaskDeadlineReminderReceiver::class.java).apply {
                putExtra("title", taskTitle)
                putExtra("deadline", formattedTime)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                taskId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                notifyAtMillis,
                pendingIntent
            )
        }
    }

}
