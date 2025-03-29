package com.example.location_aware_personal_organizer.ui.completedTasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.location_aware_personal_organizer.components.CompletedTaskItem
import com.example.location_aware_personal_organizer.services.TaskService
import com.example.location_aware_personal_organizer.ui.components.TaskItem
import com.example.location_aware_personal_organizer.viewmodels.TaskViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletedTasksScreen(navController: NavController, showSnackbar: Boolean = false) {
    val taskViewModel: TaskViewModel = viewModel()
    val tasks = taskViewModel.tasks.collectAsStateWithLifecycle().value
    val completedTasks = tasks.filter { it.complete }
    val snackbarHostState = remember { SnackbarHostState() } // Snackbar state
    val coroutineScope = rememberCoroutineScope() // CoroutineScope to show Snackbar

    LaunchedEffect(Unit, showSnackbar) {
        taskViewModel.fetchTasks()
        if (showSnackbar) {
            snackbarHostState.showSnackbar("Task successfully added to completed task list!")
        }

    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Completed Tasks") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (completedTasks.isEmpty()) {
                Text("No completed tasks.")
            } else {
                LazyColumn {
                    items(completedTasks) { task ->
                        CompletedTaskItem(
                            task = task,
                            onTaskDeleted = {
                                taskViewModel.fetchTasks()
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Task Deleted Successfully",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            onUndoClicked = {
                                coroutineScope.launch {
                                    TaskService.markTaskAsIncomplete(task.id!!)
                                    taskViewModel.fetchTasks()
                                    snackbarHostState.showSnackbar("Task successfully moved back to Dashboard!")
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Back to Dashboard")
            }
        }
    }
}
