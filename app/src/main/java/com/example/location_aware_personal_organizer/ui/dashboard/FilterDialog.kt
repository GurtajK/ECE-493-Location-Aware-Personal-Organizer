package com.example.location_aware_personal_organizer.ui.dashboard

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.location_aware_personal_organizer.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

// FR 60 Filter.Location
// FR 61 Filter.Default
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    initialLocation: String,
    initialDeadline: LocalDate?,
    onApplyFilters: (String, LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    var location by remember { mutableStateOf(initialLocation) }
    var selectedDate by remember { mutableStateOf(initialDeadline) }
    var showDatePicker by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    val dateFormatter = remember {
        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Tasks") },
        text = {
            Column {

                Spacer(modifier = Modifier.height(8.dp))

                // Filter by Location
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Filter by location name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Filter by Deadline
                OutlinedTextField(
                    value = selectedDate?.format(dateFormatter) ?: "",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Filter by deadline") },
                    placeholder = { Text("Select a date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar),
                            contentDescription = stringResource(R.string.select_date_time)
                        )
                    },
                    interactionSource = interactionSource
                )

                val isPressed by interactionSource.collectIsPressedAsState()
                if (isPressed) {
                    showDatePicker = true
                }

                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState()
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                val millis = datePickerState.selectedDateMillis
                                selectedDate = millis?.let {
                                    Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
                                }
                                showDatePicker = false
                            }) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onApplyFilters(location, selectedDate)
                    onDismiss()
                }
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                // Reset filters and dismiss
                onApplyFilters("", null)
                onDismiss()
            }) {
                Text("Clear Filters")
            }
        }
    )
}
