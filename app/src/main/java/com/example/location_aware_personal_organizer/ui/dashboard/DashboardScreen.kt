package com.example.location_aware_personal_organizer.ui.dashboard

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobService.JOB_SCHEDULER_SERVICE
import android.content.ComponentName
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.location_aware_personal_organizer.R
import com.example.location_aware_personal_organizer.services.Authorization
import com.example.location_aware_personal_organizer.services.PriorityService
import com.example.location_aware_personal_organizer.ui.Screen
import com.example.location_aware_personal_organizer.ui.components.TaskItem
import com.example.location_aware_personal_organizer.ui.theme.AppTypography
import com.example.location_aware_personal_organizer.viewmodels.TaskViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val taskViewModel: TaskViewModel = viewModel() // Initialize ViewModel
    val tasks by taskViewModel.tasks.collectAsStateWithLifecycle() // Observe tasks
    val snackbarHostState = remember { SnackbarHostState() } // Snackbar state
    val coroutineScope = rememberCoroutineScope() // CoroutineScope to show Snackbar
    var searchQuery by remember { mutableStateOf("") }
    var isFilterDialogOpen by remember { mutableStateOf(false) }
    var locationFilter by remember { mutableStateOf("") }
    var deadlineFilter by remember { mutableStateOf<LocalDate?>(null) }

    // start the priority service job schedule
    val jobInfo = JobInfo
        .Builder(1, ComponentName(LocalContext.current, PriorityService::class.java))
        .setPeriodic(30*60*1000)

    val jobScheduler = LocalContext.current.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
    jobScheduler.schedule(jobInfo.build())

    val filteredTasks = tasks.filter { task ->
        val matchesQuery = searchQuery.isBlank() || task.title.contains(searchQuery, ignoreCase = true) ||
                task.description.contains(searchQuery, ignoreCase = true)

        val matchesLocation =
            locationFilter.isBlank() || task.locationName.contains(locationFilter, ignoreCase = true)

        val matchesDeadline = deadlineFilter == null || task.deadline?.toDate()
            ?.toInstant()
            ?.atZone(ZoneId.systemDefault())
            ?.toLocalDate() == deadlineFilter


        !task.complete && matchesQuery && matchesLocation && matchesDeadline
    // on the main task dashboard, we should order it by priority (descending)
    } .sortedBy { it.priority }

    LaunchedEffect(Unit) {
        taskViewModel.fetchTasks() // Fetch tasks when screen loads
    }

    var menuExpanded by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    TextButton(onClick = { menuExpanded = !menuExpanded }) {
                        Text(Authorization.getUsername())
                    }
                    IconButton(onClick = { isFilterDialogOpen = true }) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Search/Filter Tasks")
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                menuExpanded = false
                                navController.navigate(Screen.NotificationSettings.route) // Navigate to Settings
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = { menuExpanded = false; Authorization.logout {
                                navController.navigate(Screen.Login.route)
                                // cancel any ongoing priority update jobs on logout
                                jobScheduler.cancel(1)
                            } }
                        )
                    }

                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.welcome_dashboard),
                    style = AppTypography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search tasks by title/description") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search Icon")
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear Search")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(50),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    if (filteredTasks.isEmpty()) {
                        Text(
                            text = "No matching tasks found.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredTasks) { task ->
                                TaskItem(
                                    task = task, // Display each task
                                    onTaskDeleted = {
                                        taskViewModel.fetchTasks() // Refresh task list after deletion
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "Task Deleted Successfully",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    },

                                    onMarkedCompleted = {
                                        // Navigate to the Completed Tasks screen
                                        navController.navigate(Screen.CompletedTasks.withTaskCompletedFlag())
                                    },
                                    onClick = {
                                        navController.navigate("task_update?id=${task.id}")
                                    }

                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                CreateNewTaskButton(
                    navController = navController,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
    if (isFilterDialogOpen) {
        FilterDialog(
            initialLocation = locationFilter,
            initialDeadline = deadlineFilter,
            onApplyFilters = {location, deadline ->
                locationFilter = location
                deadlineFilter = deadline
            },
            onDismiss = { isFilterDialogOpen = false }
        )
    }

}



@Composable
fun CreateNewTaskButton(navController: NavController, modifier: Modifier = Modifier) {
    Button(
        onClick = {
            navController.navigate(Screen.TaskCreation.route)
        },
        modifier = modifier
    ) {
        Text(text = stringResource(R.string.create_new_task))
    }
}