package com.example.location_aware_personal_organizer.ui.taskCreation

import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.location_aware_personal_organizer.R
import com.example.location_aware_personal_organizer.services.RequestLocationPermission
import com.example.location_aware_personal_organizer.services.TaskService
import com.example.location_aware_personal_organizer.utils.fetchLocationSuggestions
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreationScreen(navController: NavController) {
    RequestLocationPermission()
    Log.d("TaskCreationScreen", "Composing TaskCreationScreen")

    var taskName by remember { mutableStateOf("") }
    var taskDeadline by remember { mutableStateOf<Date?>(null) }
    var timeToNotify by remember { mutableStateOf(15) } // Default to 15 minutes
    var taskDescription by remember { mutableStateOf("") }
    var isTaskNameError by remember { mutableStateOf(false) }
    var isTaskDeadlineError by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedHour by remember { mutableStateOf(0) }
    var selectedMinute by remember { mutableStateOf(0) }
    var isExpanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val timeToNotifyOptions = listOf(5, 10, 15, 30, 60)
    val isPressed by interactionSource.collectIsPressedAsState()
    var taskLocation by remember { mutableStateOf("") }
    val context = LocalContext.current
    val placesClient = remember(context) { Places.createClient(context) }
    var locationSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLocationError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    if (isPressed) {
        showDatePicker = true
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.create_task)) })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Task Name Field
            OutlinedTextField(
                value = taskName,
                onValueChange = {
                    taskName = it
                    isTaskNameError = taskName.isBlank()
                },
                label = { Text(stringResource(R.string.task_name)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                isError = isTaskNameError,
                supportingText = {
                    if (isTaskNameError) {
                        Text(text = stringResource(R.string.task_name_required))
                    }
                }
            )

            // Task Description Field
            OutlinedTextField(
                value = taskDescription,
                onValueChange = { taskDescription = it },
                label = { Text(stringResource(R.string.task_description)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Task Deadline Field
            OutlinedTextField(
                value = taskDeadline?.let {
                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(it)
                } ?: stringResource(R.string.select_date_time),
                onValueChange = { },
                readOnly = true,
                label = { Text(stringResource(R.string.task_deadline)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar), // Replace with your calendar icon
                        contentDescription = stringResource(R.string.select_date_time)
                    )
                },
                isError = isTaskDeadlineError,
                supportingText = {
                    if (isTaskDeadlineError) {
                        Text(text = stringResource(R.string.task_deadline_required))
                    }
                },
                interactionSource = interactionSource
            )

            // Location Field
            OutlinedTextField(
                value = taskLocation,
                onValueChange = { newText ->
                    taskLocation = newText
                    isLocationError = taskLocation.isBlank()
                    coroutineScope.launch {
                        fetchLocationSuggestions(newText, placesClient) { suggestions ->
                            locationSuggestions = suggestions
                        }
                    }
                },
                label = { Text("Task Location") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                isError = isLocationError,
                supportingText = {
                    if (isLocationError) {
                        Text(" Task Location is required.")
                    }
                }
            )

            // Display suggestions
            DropdownMenu(
                expanded = locationSuggestions.isNotEmpty(),
                onDismissRequest = { locationSuggestions = emptyList() }
            ) {
                locationSuggestions.forEach { suggestion ->
                    DropdownMenuItem(
                        text = { Text(suggestion) },
                        onClick = {
                            taskLocation = suggestion
                            locationSuggestions = emptyList()
                        }
                    )
                }
            }


            // Time to Notify Field
            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = { isExpanded = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                OutlinedTextField(
                    value = "$timeToNotify ${stringResource(R.string.minutes)}",
                    label = { Text(stringResource(R.string.time_to_notify)) },
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }
                ) {
                    timeToNotifyOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text("$option ${stringResource(R.string.minutes)}") },
                            onClick = {
                                timeToNotify = option
                                isExpanded = false
                            }
                        )
                    }
                }
            }

            // Create Task and Cancel Buttons
            Button(
                onClick = {
                    if (taskName.isBlank()) {
                        isTaskNameError = true
                    }
                    if (taskDeadline == null) {
                        isTaskDeadlineError = true
                    }

                    if (taskLocation.isBlank()) {
                        isLocationError = true
                    }

                    if (taskName.isNotBlank() && taskDeadline != null && taskLocation.isNotBlank()) {
                        scope.launch {
                            TaskService.createTask(
                                title = taskName,
                                description = taskDescription,
                                deadline = taskDeadline!!,
                                location = taskLocation,
                                notify = timeToNotify,
                                context = context,
                                onSuccess = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Task Created Successfully")
                                        navController.popBackStack()
                                    }
                                },
                                onFailure = { e ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Task Creation Failed: ${e.message}")
                                    }
                                }
                            )
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar( "Please fill in all required fields")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(stringResource(R.string.create_task))
            }
            Button(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    }
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = {
                isTaskDeadlineError = true
                showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    selectedDate = datePickerState.selectedDateMillis
                    showTimePicker = true
                    isTaskDeadlineError = false
                }) {
                    Text(text = stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    isTaskDeadlineError = true
                    showDatePicker = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(initialHour = 0, initialMinute = 0)
        DatePickerDialog(
            onDismissRequest = {
                isTaskDeadlineError = true
                showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showTimePicker = false
                    selectedHour = timePickerState.hour
                    selectedMinute = timePickerState.minute
                    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                    if (selectedDate != null) {
                        calendar.timeInMillis = selectedDate!!
                    }

                    val localCalendar = Calendar.getInstance()
                    localCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
                    localCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                    localCalendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))

                    localCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                    localCalendar.set(Calendar.MINUTE, selectedMinute)
                    taskDeadline = localCalendar.time
                    isTaskDeadlineError = false
                }) {
                    Text(text = stringResource(R.string.ok))
                }

            },
            dismissButton = {
                TextButton(onClick = {
                    isTaskDeadlineError = true
                    showTimePicker = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

