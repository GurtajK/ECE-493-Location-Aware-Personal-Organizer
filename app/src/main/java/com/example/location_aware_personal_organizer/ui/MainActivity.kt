package com.example.location_aware_personal_organizer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.location_aware_personal_organizer.services.Authorization
import com.example.location_aware_personal_organizer.ui.dashboard.DashboardScreen
import com.example.location_aware_personal_organizer.ui.login.LoginScreen
import com.example.location_aware_personal_organizer.ui.notifyPreference.NotificationSettingsScreen
import com.example.location_aware_personal_organizer.ui.register.RegisterScreen
import com.example.location_aware_personal_organizer.ui.taskCreation.TaskCreationScreen
import com.example.location_aware_personal_organizer.ui.theme.AppTheme
import com.google.android.libraries.places.api.Places
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import android.content.Context
import androidx.work.*
import com.example.location_aware_personal_organizer.utils.TaskDeadlineReminder
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        Authorization.getInstance()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Retrieve API key securely from the manifest
        val ai: ApplicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val apiKey = ai.metaData.getString("com.google.android.geo.API_KEY")

        if (!Places.isInitialized() && apiKey != null) {
            Places.initialize(applicationContext, apiKey)
        }
        scheduleTaskDeadlineReminder(applicationContext)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController: NavHostController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = if (auth.currentUser != null) Screen.Dashboard.route else Screen.Login.route
                    ) {
                        composable(Screen.Login.route) {
                            LoginScreen(navController)
                        }
                        composable(Screen.Register.route) {
                            RegisterScreen(navController)
                        }
                        composable(Screen.Dashboard.route) {
                            DashboardScreen(navController)
                        }
                        composable(Screen.TaskCreation.route) {
                            TaskCreationScreen(navController)
                        }
                        composable(Screen.NotificationSettings.route) {
                            NotificationSettingsScreen(navController)
                        }
                    }
                }
            }
        }
    }
}

fun scheduleTaskDeadlineReminder(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<TaskDeadlineReminder>(
        3, TimeUnit.MINUTES
    ).build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "task_deadline_reminder",
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest
    )
}
