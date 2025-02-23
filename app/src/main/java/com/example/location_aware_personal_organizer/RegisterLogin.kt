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
import com.example.location_aware_personal_organizer.services.Authorization
import com.example.location_aware_personal_organizer.ui.theme.AppTypography

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterLogin(onAuthentication: () -> Unit) {
    var username by rememberSaveable { mutableStateOf("") };
    var password by rememberSaveable { mutableStateOf("") };
    var isRegistering by rememberSaveable { mutableStateOf(false) };
    var email by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") };
    fun updatePage(_registering: Boolean) {
        isRegistering = _registering;
        username = "";
        password = "";
        email = "";
        confirmPassword = "";
    }

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
                        if (isRegistering) "Create an Account" else "Location-Aware\r\nPersonal Organizer",
                        textAlign = TextAlign.Center,
                        style = AppTypography.titleLarge)
                }
                if (isRegistering) {
                    FlowRow(modifier = Modifier.padding(bottom = 10.dp)) {
                        TextField(
                            email,
                            onValueChange = { email = it },
                            label = { Text("Email") }
                        )
                    }
                }
                FlowRow(modifier = Modifier.padding(bottom = 10.dp)) {
                    TextField(
                        username,
                        onValueChange = { username = it },
                        label = { Text("Username") }
                    )
                }
                FlowRow(modifier = Modifier.padding(bottom = if (isRegistering) 10.dp else 0.dp)) {
                    PasswordInput(password, onPasswordChange = { password = it })
                }
                if (isRegistering) {
                    FlowRow(modifier = Modifier.padding(bottom = 20.dp)) {
                        PasswordInput(confirmPassword, onPasswordChange = { confirmPassword = it }, "Confirm Password")
                    }
                } else {
                    FlowRow(
                        modifier = Modifier.fillMaxColumnWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {}) {
                            Text("Forgot Password?", modifier = Modifier.padding(vertical = 0.dp));
                        }
                    }
                }
                FlowRow(
                    modifier = Modifier.fillMaxColumnWidth(),
                ) {
                    if (isRegistering) {
                        Button(
                            onClick = { Authorization.register(username, email, password, confirmPassword) { updatePage(false) } },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Register");
                        }
                    } else {
                        Button(onClick = { Authorization.login(username, password, onAuthentication) }, modifier = Modifier.fillMaxWidth()) {
                            Text("Login");
                        }
                    }
                }
                FlowRow(
                    modifier = Modifier.fillMaxColumnWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = { updatePage(!isRegistering) }) {
                        Text(if (isRegistering) "Already have an account? Login" else "Don't have an account? Register Now");
                    }
                }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterLoginPreview() {
    RegisterLogin {};
}