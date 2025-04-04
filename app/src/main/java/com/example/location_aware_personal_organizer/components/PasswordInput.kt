package com.example.location_aware_personal_organizer.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.location_aware_personal_organizer.ui.theme.errorLight

@Composable
fun PasswordInput(
    password: String,
    onPasswordChange: (String) -> Unit,
    label: String = "Password",
    validate: (String) -> Boolean = {true},
    errorMessage: String = ""
) {
    var showPassword by rememberSaveable { mutableStateOf(false) };
    var valid by rememberSaveable { mutableStateOf(validate(password)) };
    TextField(
        password,
        onValueChange = {
            onPasswordChange(it);
            valid = validate(it);
        },
        label = { Text(label) },
        singleLine = true,
        supportingText = {
            if (!valid)
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 0.dp),
                    text = errorMessage,
                    color = errorLight
                )
        },
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val icon = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
            val desc = if (showPassword) "Hide Password" else "Show Password"
            IconButton(onClick = { showPassword = !showPassword }) {
                Icon(imageVector = icon, contentDescription = desc)
            }
        },
        isError = !valid
    )
}

@Preview(showBackground = true)
@Composable
fun PasswordInputPreview() {
    PasswordInput("", {}, "Password", {true}, "Invalid Password")
}