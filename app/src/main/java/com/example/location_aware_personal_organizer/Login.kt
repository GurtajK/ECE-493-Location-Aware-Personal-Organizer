package com.example.location_aware_personal_organizer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.absolutePadding
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
import com.example.location_aware_personal_organizer.services.Authorization
import com.example.location_aware_personal_organizer.ui.theme.AppTypography

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Login(onLogin: () -> Unit, signupRedirect: () -> Unit) {
    var username by rememberSaveable { mutableStateOf("") };
    var password by rememberSaveable { mutableStateOf("") };

    val context = LocalContext.current
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
                        style = AppTypography.titleLarge)
                }
                FlowRow(modifier = Modifier.padding(bottom = 10.dp)) {
                    TextField(
                        username,
                        onValueChange = { username = it },
                        label = { Text(stringResource(R.string.username)) }
                    )
                }
                FlowRow(modifier = Modifier.absolutePadding(bottom = 0.dp)) {
                    PasswordInput(password, onPasswordChange = { password = it })
                }
                FlowRow(
                    modifier = Modifier.fillMaxColumnWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {}, modifier = Modifier.padding(vertical = 0.dp)) {
                        Text(stringResource(R.string.forgot_password), modifier = Modifier.padding(vertical = 0.dp));
                    }
                }
                FlowRow(
                    modifier = Modifier.fillMaxColumnWidth(),
                ) {
                    Button(onClick = { Authorization.login(username, password, onLogin, context) }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.login))
                    }
                }
                FlowRow(
                    modifier = Modifier.fillMaxColumnWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = {signupRedirect()}) {
                        Text(stringResource(R.string.no_account));
                    }
                }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    Login({}, {});
}