package com.example.location_aware_personal_organizer.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.location_aware_personal_organizer.data.Task
import com.example.location_aware_personal_organizer.services.PriorityService
import com.example.location_aware_personal_organizer.services.TaskService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// FR 39 Update.TaskCreate
// FR 47 Update.TaskEdit
// FR 54 Update.TaskDelete
// FR 70 Prioritization.Update
class TaskViewModel : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    fun fetchTasks() {
        viewModelScope.launch {
            _tasks.value = TaskService.getTasksForCurrentUser() // Fetch tasks from Firestore
            PriorityService.prioritizeTasks(_tasks.value) // Prioritize tasks
        }
    }
}
