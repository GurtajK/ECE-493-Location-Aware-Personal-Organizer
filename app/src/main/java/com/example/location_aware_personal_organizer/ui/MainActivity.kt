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
import android.widget.Toast
import androidx.navigation.navArgument
import com.example.location_aware_personal_organizer.ui.completedTasks.CompletedTasksScreen
import com.example.location_aware_personal_organizer.ui.login.ForgotPasswordScreen
import com.example.location_aware_personal_organizer.ui.taskUpdate.TaskUpdateScreen
import com.example.location_aware_personal_organizer.utils.LocationHelper
import com.example.location_aware_personal_organizer.services.PriorityService
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth = Firebase.auth

    // FR 12 Default.Login
    override fun onCreate(savedInstanceState: Bundle?) {
        Authorization.getInstance()
        PriorityService.getInstance()
        LocationHelper.getInstance()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Retrieve API key securely from the manifest
        val ai: ApplicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val apiKey = ai.metaData.getString("com.google.android.geo.API_KEY")

        if (!Places.isInitialized() && apiKey != null) {
            Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)
        }

        LocationHelper.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        LocationHelper.updateCurrentLocation(this)

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
                        composable(
                            route = "task_update?id={id}",
                            arguments = listOf(navArgument("id") { defaultValue = "" })
                        ) { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getString("id") ?: ""
                            TaskUpdateScreen(
                                navController = navController,
                                taskId = taskId
                            )
                        }
                        composable(
                            route = "completed_tasks?taskCompleted={taskCompleted}",
                            arguments = listOf(navArgument("taskCompleted") {
                                defaultValue = "false"
                            })
                        ) { backStackEntry ->
                            val showSnackbar = backStackEntry.arguments?.getString("taskCompleted").toBoolean()
                            CompletedTasksScreen(navController, showSnackbar = showSnackbar)
                        }

                        composable(Screen.ForgotPassword.route) {
                            ForgotPasswordScreen(navController)
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

        if(requestCode == LocationHelper.PERMISSION_REQUEST_ACCESS_LOCATION)
        {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
                LocationHelper.updateCurrentLocation(this)
            }
            else
            {
                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
