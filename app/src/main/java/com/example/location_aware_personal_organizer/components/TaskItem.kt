package com.example.location_aware_personal_organizer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.location_aware_personal_organizer.data.Task
import com.example.location_aware_personal_organizer.services.TaskService
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TaskItem(task: Task, onTaskDeleted: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) } // Track if the dialog is open

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

            // Delete Task Button at the Top Right
            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Task",
                    tint = MaterialTheme.colorScheme.error
                )
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Task Title
                Text(task.title, style = MaterialTheme.typography.titleMedium)

                // Task Description
                if (task.description.isNotBlank()) {
                    Text(task.description, style = MaterialTheme.typography.bodyMedium)
                }

                // Task Deadline
                task.deadline?.toDate()?.let { deadlineDate ->
                    val formattedDate =
                        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(
                            deadlineDate
                        )
                    Text("Due: $formattedDate", style = MaterialTheme.typography.bodySmall)
                }

                // Task Location (Stored as GeoPoint in Firestore)
                Text("Location: ${task.locationName}", style = MaterialTheme.typography.bodySmall)

                // Task Notification Time (Time to Notify)
                if (task.notify > 0) {
                    Text(
                        "Notify: ${task.notify} minutes before deadline",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Text("Notify: No reminders set", style = MaterialTheme.typography.bodySmall)
                }

                // Task Completion Status
                Text(
                    text = if (task.complete) "Status: Completed" else "Status: Pending",
                    style = MaterialTheme.typography.bodySmall
                )

                // Delete Confirmation Dialog
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Confirm Delete") },
                        text = { Text("Are you sure you want to delete this task? This action cannot be undone.") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    coroutineScope.launch {
                                        TaskService.deleteTask(task.id!!) // Delete task from Firestore
                                        showDialog = false
                                        onTaskDeleted() // Refresh UI
                                    }
                                }
                            ) {
                                Text("Delete", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }
}
