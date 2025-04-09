package com.example.location_aware_personal_organizer.ui.login

import android.app.job.JobScheduler
import android.app.job.JobService.JOB_SCHEDULER_SERVICE
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.location_aware_personal_organizer.R
import com.example.location_aware_personal_organizer.components.PasswordInput
import com.example.location_aware_personal_organizer.services.Authorization
import com.example.location_aware_personal_organizer.ui.Screen
import com.example.location_aware_personal_organizer.ui.theme.AppTypography
import kotlinx.coroutines.launch

// FR 1 Request.Registration
// FR 12 Default.Login
// FR 13 Login.Form
// FR 14 Validate.Required
// FR 15 Login.Button
// FR 17 Redirect.Login
// FR 18 Forgot.Password
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginScreen(navController: NavController) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isUsernameBlank by remember { mutableStateOf(true) }
    var isPasswordBlank by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // cancel any ongoing priority update jobs on logout
    val jobScheduler = LocalContext.current.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
    jobScheduler.cancel(1)

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 8.dp)
        ) {
            FlowColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(all = 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalArrangement = Arrangement.Center
            ) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxColumnWidth()
                        .padding(bottom = 40.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        stringResource(R.string.app_name),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(0.7f),
                        style = AppTypography.titleLarge
                    )
                }
                FlowRow(modifier = Modifier.padding(bottom = 10.dp)) {
                    TextField(
                        username,
                        onValueChange = {
                            username = it
                            isUsernameBlank = username.isBlank()
                        },
                        label = { Text(stringResource(R.string.username)) },
                    )
                }
                FlowRow(modifier = Modifier.absolutePadding(bottom = 0.dp)) {
                    PasswordInput(
                        password,
                        onPasswordChange = {
                            password = it
                            isPasswordBlank = password.isBlank()
                        },
                        validate = { true },    // this is for visual errors only which we don't want here
                    )
                }
                FlowRow(
                    modifier = Modifier.fillMaxColumnWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { navController.navigate(Screen.ForgotPassword.route)}, modifier = Modifier.padding(vertical = 0.dp)) {
                        Text(
                            stringResource(R.string.forgot_password),
                            modifier = Modifier.padding(vertical = 0.dp)
                        );
                    }
                }
                FlowRow(
                    modifier = Modifier.fillMaxColumnWidth(),
                ) {
                    Button(
                        onClick = {

                            if (!isUsernameBlank && !isPasswordBlank) {
                                Authorization.login(
                                    username,
                                    password,
                                    { navController.navigate(Screen.Dashboard.route) },
                                    context
                                )
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar( "Please fill in all required fields")
                                }
                            }

                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    {
                        Text(stringResource(R.string.login))
                    }
                }
                FlowRow(
                    modifier = Modifier.fillMaxColumnWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                        Text(stringResource(R.string.no_account));
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(10.dp)
        )
    }
}