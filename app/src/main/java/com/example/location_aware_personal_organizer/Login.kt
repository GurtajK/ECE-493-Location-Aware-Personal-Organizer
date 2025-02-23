package com.example.location_aware_personal_organizer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.location_aware_personal_organizer.components.PasswordInput
import com.example.location_aware_personal_organizer.ui.theme.AppTypography

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Login() {
    var username by rememberSaveable { mutableStateOf("") };
    var password by rememberSaveable { mutableStateOf("") };

    Surface(
        modifier = Modifier.fillMaxSize().padding(all = 8.dp)
    ) {
        FlowColumn(
            modifier = Modifier.fillMaxHeight().padding(all = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.Center
        ) {
                FlowRow(
                    modifier = Modifier.fillMaxColumnWidth().padding(bottom = 40.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Location-Aware\r\nPersonal Organizer",
                        textAlign = TextAlign.Center,
                        style = AppTypography.titleLarge)
                }
                FlowRow(modifier = Modifier.padding(bottom = 10.dp)) {
                    TextField(
                        username,
                        onValueChange = { username = it },
                        label = { Text("Username") }
                    )
                }
                FlowRow {
                    PasswordInput(password, onPasswordChange = { password = it })
                }
                FlowRow(
                    modifier = Modifier.fillMaxColumnWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {}) {
                        Text("Forgot Password?", modifier = Modifier.padding(vertical = 0.dp));
                    }
                }
                FlowRow(
                    modifier = Modifier.fillMaxColumnWidth(),
                ) {
                    Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                        Text("Login");
                    }
                }
                FlowRow(
                    modifier = Modifier.fillMaxColumnWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = {}) {
                        Text("Don't have an account? Register Now");
                    }
                }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    Login();
}