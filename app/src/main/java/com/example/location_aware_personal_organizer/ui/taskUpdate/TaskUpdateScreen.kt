package com.example.location_aware_personal_organizer.ui.taskUpdate

import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.location_aware_personal_organizer.R
import com.example.location_aware_personal_organizer.data.LocationSuggestion
import com.example.location_aware_personal_organizer.data.Task
import com.example.location_aware_personal_organizer.services.TaskService
import com.example.location_aware_personal_organizer.ui.taskCreation.LocationInputField
import com.example.location_aware_personal_organizer.utils.fetchLocationSuggestions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskUpdateScreen(
    navController: NavController,
    taskId: String,
    latitude: Float,
    longitude: Float
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var task by remember { mutableStateOf<Task?>(null) }

    LaunchedEffect(taskId) {
        if (taskId.isNotBlank()) {
            task = TaskService.getTaskById(taskId)
        }
    }

    task?.let { originalTask ->
        var taskName by remember { mutableStateOf(originalTask.title) }
        var taskDeadline by remember { mutableStateOf<Date?>(originalTask.deadline?.toDate()) }
        var taskDescription by remember { mutableStateOf(originalTask.description) }
        var isTaskNameError by remember { mutableStateOf(taskName.isBlank()) }
        var isTaskDeadlineError by remember { mutableStateOf(taskDeadline == null) }
        var showDatePicker by remember { mutableStateOf(false) }
        var showTimePicker by remember { mutableStateOf(false) }
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        var selectedDate by remember { mutableStateOf<Long?>(taskDeadline?.time) }
        var selectedHour by remember { mutableIntStateOf(taskDeadline?.hours ?: 0) }
        var selectedMinute by remember { mutableIntStateOf(taskDeadline?.minutes ?: 0) }
        var isExpanded by remember { mutableStateOf(false) }
        val interactionSource = remember { MutableInteractionSource() }
        val timeToNotifyOptions = listOf(5, 10, 15, 30, 60)
        val isPressed by interactionSource.collectIsPressedAsState()
        var taskLocation by remember { mutableStateOf(originalTask.locationName) }
        var taskGeoPoint by remember { mutableStateOf(originalTask.location) }
        var timeToNotify by remember { mutableIntStateOf(originalTask.notify) }
        val placesClient = remember(context) { Places.createClient(context) }
        var locationSuggestions by remember { mutableStateOf<List<LocationSuggestion>>(emptyList()) }

        if (isPressed) {
            showDatePicker = true
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(title = { Text("Update Task") })
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = taskName,
                    onValueChange = {
                        taskName = it
                        isTaskNameError = taskName.isBlank()
                    },
                    label = { Text("Task Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    isError = isTaskNameError,
                    supportingText = {
                        if (isTaskNameError) Text("Task name is required")
                    }
                )

                OutlinedTextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = taskDeadline?.let {
                        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(it)
                    } ?: "Select Date/Time",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Task Deadline") },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    interactionSource = interactionSource,
                    isError = isTaskDeadlineError,
                    supportingText = {
                        if (isTaskDeadlineError) Text("Deadline is required")
                    }
                )

                LocationInputField(
                    taskLocation = taskLocation,
                    onTaskLocationChange = { taskLocation = it },
                    locationSuggestions = locationSuggestions,
                    onSuggestionSelected = { selectedLocation ->
                        taskLocation = selectedLocation.name
                        locationSuggestions = emptyList()
                        val request = FetchPlaceRequest.builder(selectedLocation.placeId, listOf(Place.Field.LAT_LNG)).build()
                        placesClient.fetchPlace(request).addOnSuccessListener { response ->
                            response.place.latLng?.let {
                                taskGeoPoint = GeoPoint(it.latitude, it.longitude)
                            }
                        }
                    },
                    onFetchSuggestions = { query ->
                        scope.launch {
                            fetchLocationSuggestions(query, placesClient, latitude, longitude) {
                                locationSuggestions = it
                            }
                        }
                    }
                )

                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    OutlinedTextField(
                        value = "$timeToNotify minutes",
                        label = { Text("Notify Before") },
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
                                text = { Text("$option minutes") },
                                onClick = {
                                    timeToNotify = option
                                    isExpanded = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        if (taskName.isNotBlank() && taskDeadline != null && taskLocation.isNotBlank()) {
                            val finalDeadline = Calendar.getInstance().apply {
                                timeInMillis = selectedDate ?: System.currentTimeMillis()
                                set(Calendar.HOUR_OF_DAY, selectedHour)
                                set(Calendar.MINUTE, selectedMinute)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }.time

//                            scope.launch {
//                                TaskService.updateTask(
//                                    taskId = originalTask.id!!,
//                                    title = taskName,
//                                    description = taskDescription,
//                                    deadline = finalDeadline,
//                                    location = taskGeoPoint,
//                                    locationName = taskLocation,
//                                    notify = timeToNotify,
//                                    context = context,
//                                    onSuccess = {
//                                        snackbarHostState.showSnackbar("Task updated successfully")
//                                        navController.popBackStack()
//                                    },
//                                    onFailure = {
//                                        snackbarHostState.showSnackbar("Update failed: ${it.message}")
//                                    }
//                                )
//                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Please fill in all required fields")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text("Update Task")
                }

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = {
                    isTaskDeadlineError = true
                    showDatePicker = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                        selectedDate = datePickerState.selectedDateMillis?.let { utcMillis ->
                            val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                timeInMillis = utcMillis
                            }
                            val localCalendar = Calendar.getInstance().apply {
                                set(Calendar.YEAR, utcCalendar.get(Calendar.YEAR))
                                set(Calendar.MONTH, utcCalendar.get(Calendar.MONTH))
                                set(Calendar.DAY_OF_MONTH, utcCalendar.get(Calendar.DAY_OF_MONTH))
                            }
                            localCalendar.timeInMillis
                        }

                        showTimePicker = true
                        isTaskDeadlineError = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        isTaskDeadlineError = true
                        showDatePicker = false
                    }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showTimePicker) {
            val timePickerState = rememberTimePickerState(initialHour = selectedHour, initialMinute = selectedMinute)
            DatePickerDialog(
                onDismissRequest = {
                    isTaskDeadlineError = true
                    showTimePicker = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        showTimePicker = false
                        selectedHour = timePickerState.hour
                        selectedMinute = timePickerState.minute

                        val calendar = Calendar.getInstance()
                        selectedDate?.let {
                            calendar.timeInMillis = it
                            calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                            calendar.set(Calendar.MINUTE, selectedMinute)
                            calendar.set(Calendar.SECOND, 0)
                            calendar.set(Calendar.MILLISECOND, 0)
                            taskDeadline = calendar.time
                        }
                        isTaskDeadlineError = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        isTaskDeadlineError = true
                        showTimePicker = false
                    }) {
                        Text("Cancel")
                    }
                }
            ) {
                TimePicker(state = timePickerState)
            }
        }
    }
}
