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

        val task = Task(
            title = title,
            description = description,
            deadline = com.google.firebase.Timestamp(deadline),
            location = location,
            notify = notify,
            complete = false
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
}
