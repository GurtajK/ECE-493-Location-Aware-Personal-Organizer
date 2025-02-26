package com.example.location_aware_personal_organizer.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.location_aware_personal_organizer.ui.theme.errorLight

@Composable
fun ValidateInput(input: String, onInputChange: (String) -> Unit, label: String, validate: (String) -> Boolean, errorMessage: String) {
    var valid by rememberSaveable { mutableStateOf(true) }
    TextField(
        input,
        onValueChange = {
            onInputChange(it)
            valid = validate(it) || it.isEmpty()
        },
        label = {
            Text(label)
        },
        supportingText = {
            if (!valid)
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = errorMessage,
                        color = errorLight
                    )
        },
        trailingIcon = {
            if (!valid)
                Icon(Icons.Filled.Error,"error", tint = errorLight)
        },
        isError = !valid
    )
}