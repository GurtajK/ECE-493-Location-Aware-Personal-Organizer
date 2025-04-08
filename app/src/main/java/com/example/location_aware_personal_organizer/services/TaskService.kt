package com.example.location_aware_personal_organizer.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.location_aware_personal_organizer.R
import com.example.location_aware_personal_organizer.data.Task
import com.example.location_aware_personal_organizer.ui.notifyPreference.NotificationPreferencesManager
import com.example.location_aware_personal_organizer.utils.TaskDeadlineReminderReceiver
import com.example.location_aware_personal_organizer.utils.TaskDeadlineReminderReceiver.Companion.scheduleTaskNotification
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date
import kotlinx.coroutines.flow.first

object TaskService {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    // FR 37 Save.Task
    suspend fun createTask(
        title: String,
        description: String,
        deadline: Date,
        location: GeoPoint?,
        locationName: String,
        notify: Int,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(context, R.string.not_logged_in, Toast.LENGTH_SHORT).show()
            return
        }

        val username = user.displayName
        if (username.isNullOrBlank()) {
            Toast.makeText(context, R.string.no_username, Toast.LENGTH_SHORT).show()
            return
        }

        val task = Task(
            title = title,
            description = description,
            deadline = com.google.firebase.Timestamp(deadline),
            location = location,
            locationName = locationName,
            notify = notify,
            complete = false,
            user = username
        )
        val timeEnabled = NotificationPreferencesManager
            .getTimeNotificationState(context)
            .first()

        try {
            db.collection("tasks")
                .add(task)
                .addOnSuccessListener { documentRef ->
                    Log.d("TaskService", "Task successfully added: ${documentRef.id}")

                    if (timeEnabled) {
                        val notifyAtMillis = Calendar.getInstance().apply {
                            time = deadline
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                            add(Calendar.MINUTE, -notify)
                        }.timeInMillis


                        scheduleTaskNotification(
                            context = context,
                            taskId = documentRef.id,
                            notifyAtMillis = notifyAtMillis,
                            taskTitle = title,
                            deadline = deadline
                        )
                    }

                    onSuccess()
                }
                .addOnFailureListener { e ->
                    Log.e("TaskService", "Error adding task", e)
                    onFailure(e)
                }
                .await()
        } catch (e: Exception) {
            Log.e("TaskService", "Exception while adding task", e)
            onFailure(e)
        }
    }

    // FR 39 Update.TaskCreate
    // FR 47 Update.TaskEdit
    // FR 54 Update.TaskDelete
    // FR 70 Prioritization.Update
    suspend fun getTasksForCurrentUser(): List<Task> {
        val user = auth.currentUser
        if (user == null) {
            Log.d("TaskService", "No logged-in user.")
            return emptyList()
        }

        val username = user.displayName
        if (username.isNullOrBlank()) {
            Log.d("TaskService", "User has no displayName set in Firebase Auth.")
            return emptyList()
        }

        return try {
            val snapshot = db.collection("tasks")
                .whereEqualTo("user", username)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
        } catch (e: Exception) {
            Log.e("TaskService", "Error fetching tasks", e)
            emptyList()
        }
    }

    // FR 52 Task.Deleted
    suspend fun deleteTask(taskId: String) {
        try {
            FirebaseFirestore.getInstance()
                .collection("tasks")
                .document(taskId)
                .delete()
                .await()
            Log.d("TaskService", "Task deleted successfully: $taskId")
        } catch (e: Exception) {
            Log.e("TaskService", "Error deleting task", e)
        }
    }

    // FR 62 Complete Task
    fun markTaskAsCompleted(taskId: String) {
        val taskRef = Firebase.firestore.collection("tasks").document(taskId)
        taskRef.update("complete", true)
    }

    // FR 66 Undo.CompletedTask
    fun markTaskAsIncomplete(taskId: String) {
        val taskRef = Firebase.firestore.collection("tasks").document(taskId)
        taskRef.update("complete", false)
    }

    // FR 45 Save.TaskEdit
    fun updateTask(
        taskId: String,
        title: String,
        description: String,
        deadline: Date,
        location: GeoPoint?,
        locationName: String,
        notify: Int,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val taskRef = db.collection("tasks").document(taskId)
        taskRef.update(
            mapOf(
                "title" to title,
                "description" to description,
                "deadline" to com.google.firebase.Timestamp(deadline),
                "location" to location,
                "locationName" to locationName,
                "notify" to notify
            )
        ).addOnSuccessListener {
            Log.d("TaskService", "Task updated: $taskId")

            // Cancel previous alarm
            val intent = Intent(context, TaskDeadlineReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                taskId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)

            // Reschedule notification if enabled
            val notifyAtMillis = Calendar.getInstance().apply {
                time = deadline
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.MINUTE, -notify)
            }.timeInMillis

            if (notifyAtMillis > Calendar.getInstance().timeInMillis) {
                try {
                    scheduleTaskNotification(
                        context = context,
                        taskId = taskId,
                        notifyAtMillis = notifyAtMillis,
                        taskTitle = title,
                        deadline = deadline
                    )
                } catch (e: Exception) {
                    Log.e("TaskService", "Error scheduling notification", e)
                }
            }

            onSuccess()
        }.addOnFailureListener { e ->
            Log.e("TaskService", "Error updating task", e)
            onFailure(e)
        }
    }

    // FR 47 Update.TaskEdit
    suspend fun getTaskById(taskId: String): Task? {
        return try {
            val document = FirebaseFirestore.getInstance()
                .collection("tasks")
                .document(taskId)
                .get()
                .await()

            document.toObject(Task::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            Log.e("TaskService", "Error fetching task by ID", e)
            null
        }
    }
}
