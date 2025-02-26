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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.location_aware_personal_organizer.components.PasswordInput
import com.example.location_aware_personal_organizer.components.ValidateInput
import com.example.location_aware_personal_organizer.services.Authorization
import com.example.location_aware_personal_organizer.ui.theme.AppTypography

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Register(redirectLogin: () -> Unit) {
    var username by rememberSaveable { mutableStateOf("") };
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") };
    var confirmPassword by rememberSaveable { mutableStateOf("") };
    var context = LocalContext.current;

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
                        stringResource(R.string.create_account),
                        textAlign = TextAlign.Center,
                        style = AppTypography.titleLarge)
                }
                FlowRow(modifier = Modifier.padding(bottom = 5.dp)) {
                    ValidateInput(
                        email,
                        {email=it},
                        stringResource(R.string.email),
                        {Authorization.isValidEmail(it)},
                        stringResource(R.string.invalid_email)
                    )
                }
                FlowRow(modifier = Modifier.padding(bottom = 10.dp)) {
                    TextField(
                        username,
                        onValueChange = { username = it },
                        label = { Text(stringResource(R.string.username)) }
                    )
                }
                FlowRow(modifier = Modifier.padding(bottom = 4.dp)) {
                    PasswordInput(
                        password,
                        onPasswordChange = { password = it },
                        validate = {Authorization.isValidPassword(it)},
                        errorMessage = stringResource(R.string.invalid_password)
                    )
                }
                FlowRow(modifier = Modifier.fillMaxColumnWidth().padding(bottom = 10.dp, start = 4.dp)) {
                    Text(stringResource(R.string.password_standards), style = AppTypography.bodySmall)
                }
                FlowRow(modifier = Modifier.padding(bottom = 20.dp)) {
                    PasswordInput(
                        confirmPassword,
                        onPasswordChange = { confirmPassword = it },
                        label = stringResource(R.string.confirm_password),
                        validate = {it == password},
                        errorMessage = stringResource(R.string.password_match)
                    )
                }
                FlowRow(
                    modifier = Modifier.fillMaxColumnWidth(),
                ) {
                    Button(onClick = {Authorization.register(username, email, password, confirmPassword, redirectLogin, context)}, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.sign_up))
                    }
                }
                FlowRow(
                    modifier = Modifier.fillMaxColumnWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = {redirectLogin()}) {
                        Text(stringResource(R.string.have_account))
                    }
                }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    Register {};
}