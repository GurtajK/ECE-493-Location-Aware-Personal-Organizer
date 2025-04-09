package com.example.location_aware_personal_organizer.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.location_aware_personal_organizer.services.Authorization
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// FR 19 Email.Field
// FR 20 Validate.Email
// FR 21 Email.Button
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var isEmailBlank by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val db = Firebase.firestore



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
                        scope.launch {
                            try {
                                val result = db.collection("users")
                                    .whereEqualTo("email", email)
                                    .get()
                                    .await()

                                if (!result.isEmpty) {
                                    Authorization.requestPasswordReset(email) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Password Reset sent to $email")
                                            navController.navigate("login")
                                        }
                                    }
                                } else {
                                    snackbarHostState.showSnackbar("This email is not registered.")
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Something went wrong: ${e.message}")
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send Password Reset Email")
            }
        }
    }
}
