package com.example.location_aware_personal_organizer.ui.dashboard

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.location_aware_personal_organizer.R
import com.example.location_aware_personal_organizer.ui.Screen
import com.example.location_aware_personal_organizer.ui.components.TaskItem
import com.example.location_aware_personal_organizer.ui.theme.AppTypography
import com.example.location_aware_personal_organizer.viewmodels.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val taskViewModel: TaskViewModel = viewModel() // Initialize ViewModel
    val tasks by taskViewModel.tasks.collectAsStateWithLifecycle() // Observe tasks

    LaunchedEffect(Unit) {
        taskViewModel.fetchTasks() // Fetch tasks when screen loads
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Dashboard") })
        }
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

                Spacer(modifier = Modifier.height(8.dp)) // Adds vertical spacing between column elements

                // Wrap LazyColumn inside another Column with weight
                Column(
                    modifier = Modifier.weight(1f) // Ensures list takes available space but doesn't push button away
                ) {
                    if (tasks.isEmpty()) {
                        Text(
                            text = "No tasks available.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(tasks) { task ->
                                TaskItem(task = task) // Display each task
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp)) // Add spacing

                // Place button outside LazyColumn so it stays at the bottom
                CreateNewTaskButton(
                    navController = navController,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
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