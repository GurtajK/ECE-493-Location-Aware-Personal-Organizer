package com.example.location_aware_personal_organizer.ui.forgotpassword

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var isEmailBlank by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Forgot Password") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = email,
                onValueChange = {
                    email = it
                    isEmailBlank = email.isBlank()
                },
                label = { Text("Email") },
                placeholder = { Text("Enter your registered email") },
                isError = isEmailBlank,
                supportingText = {
                    if (isEmailBlank) Text("Email is required!")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isEmailBlank) {
                        scope.launch {
                            snackbarHostState.showSnackbar( "Please fill in all required fields")
                        }
                    } else {
                        // TODO: Implement email submission to Firebase/Auth service
                        navController.popBackStack() // Navigate back to login
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send a One-Time Password")
            }
        }
    }
}
