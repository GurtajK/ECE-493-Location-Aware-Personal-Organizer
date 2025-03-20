package com.example.location_aware_personal_organizer.ui.taskCreation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationInputField(
    taskLocation: String,
    onTaskLocationChange: (String) -> Unit,
    locationSuggestions: List<String>,
    onSuggestionSelected: (String) -> Unit,
    onFetchSuggestions: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) } // Controls dropdown visibility

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = taskLocation,
            onValueChange = {
                onTaskLocationChange(it)
                onFetchSuggestions(it) // Fetch suggestions whenever user types
                expanded = it.isNotEmpty() // Keep dropdown open while typing
            },
            label = { Text("Task Location") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(), // Keeps text field focus
            isError = taskLocation.isBlank(),
            supportingText = { if (taskLocation.isBlank()) Text("Task Location is required.") },
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions(onDone = { expanded = false })
        )

        ExposedDropdownMenu(
            expanded = expanded && locationSuggestions.isNotEmpty(),
            onDismissRequest = { expanded = false }
        ) {
            locationSuggestions.forEach { suggestion ->
                DropdownMenuItem(
                    text = { Text(suggestion) },
                    onClick = {
                        onSuggestionSelected(suggestion) // Set selected location
                        expanded = false // Close dropdown
                    }
                )
            }
        }
    }
}
