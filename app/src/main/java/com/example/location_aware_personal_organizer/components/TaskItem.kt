package com.example.location_aware_personal_organizer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.location_aware_personal_organizer.data.Task
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TaskItem(task: Task) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
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
                val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(deadlineDate)
                Text("Due: $formattedDate", style = MaterialTheme.typography.bodySmall)
            }

            // Task Location (Stored as String)
            if (task.location?.isNotBlank() == true) {
                Text("Location: ${task.location}", style = MaterialTheme.typography.bodySmall)
            } else {
                Text("Location: Not specified", style = MaterialTheme.typography.bodySmall)
            }

            // Task Notification Time (Time to Notify)
            if (task.notify > 0) {
                Text("Notify: ${task.notify} minutes before deadline", style = MaterialTheme.typography.bodySmall)
            } else {
                Text("Notify: No reminders set", style = MaterialTheme.typography.bodySmall)
            }

            // Task Completion Status
            Text(
                text = if (task.complete) "Status: Completed" else "Status: Pending",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
