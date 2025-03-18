package com.example.location_aware_personal_organizer.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.location_aware_personal_organizer.R
import com.example.location_aware_personal_organizer.data.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.util.Date

object TaskService {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun createTask(
        title: String,
        description: String,
        deadline: Date,
        location: String,
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
            notify = notify,
            complete = false,
            user = username
        )

        try {
            db.collection("tasks")
                .add(task)
                .addOnSuccessListener {
                    Log.d("TaskService", "Task successfully added: ${it.id}")
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

    // Fetch tasks for the logged-in user
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
                .whereEqualTo("user", username) // filter by username
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
        } catch (e: Exception) {
            Log.e("TaskService", "Error fetching tasks", e)
            emptyList()
        }
    }

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
}
