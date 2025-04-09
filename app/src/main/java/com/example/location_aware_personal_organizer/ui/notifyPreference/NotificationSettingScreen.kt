package com.example.location_aware_personal_organizer.ui.notifyPreference

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.location_aware_personal_organizer.services.NotificationHelper

// FR 58 Notify.PreferencesChange
@Composable
fun NotificationSettingsScreen(
    navController: NavController,
    viewModel: NotificationSettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val isPermissionGranted by viewModel.isPermissionGranted.collectAsState()
    val timeEnabled by viewModel.timeNotificationEnabled.collectAsState()
    val priorityEnabled by viewModel.priorityNotificationEnabled.collectAsState()

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.updatePermissionStatus(granted)
        Toast.makeText(
            context,
            if (granted) "Notification permission granted!" else "Permission denied!",
            Toast.LENGTH_SHORT
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Notification Settings",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (!isPermissionGranted) {
            Text("To enable notifications, grant system-level permission below:")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }) {
                Text("Enable Notification Permission")
            }
        } else {
            Text("Enable Time Notifications (before deadline)")
            Switch(
                checked = timeEnabled,
                onCheckedChange = { viewModel.toggleTimeNotification(it) },
                modifier = Modifier.testTag("timeSwitch")


            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Enable Prioritized Task Notifications")
            Switch(
                checked = priorityEnabled,
                onCheckedChange = { viewModel.togglePriorityNotification(it) },
                modifier = Modifier.testTag("prioritySwitch")
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = {
                NotificationHelper(context).sendNotification(
                    title = "Test Notification",
                    message = "This is a test notification from your app!"
                )
            }) {
                Text("Send Test Notification")
            }
        }
    }
}
