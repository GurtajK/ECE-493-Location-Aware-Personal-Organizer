//package com.example.location_aware_personal_organizer.ui
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.ui.Modifier
//import androidx.core.content.ContextCompat
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.example.location_aware_personal_organizer.services.Authorization
//import com.example.location_aware_personal_organizer.services.NotificationHelper
//import com.example.location_aware_personal_organizer.ui.dashboard.DashboardScreen
//import com.example.location_aware_personal_organizer.ui.login.LoginScreen
//import com.example.location_aware_personal_organizer.ui.notifyPreference.NotificationSettingsScreen
//import com.example.location_aware_personal_organizer.ui.register.RegisterScreen
//import com.example.location_aware_personal_organizer.ui.taskCreation.TaskCreationScreen
//import com.example.location_aware_personal_organizer.ui.theme.AppTheme
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        Authorization.getInstance();
//        enableEdgeToEdge()
//        super.onCreate(savedInstanceState)
////        requestNotificationPermission()
//
////        val notificationHelper = NotificationHelper(this)
////        notificationHelper.sendNotification("Welcome!", "You have opened the app.")
//
//        setContent {
//            AppTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    val navController: NavHostController = rememberNavController()
//                    NavHost(
//                        navController = navController,
//                        startDestination = Screen.Login.route
//                    ) {
//                        composable(Screen.Login.route) {
//                            LoginScreen(navController)
//                        }
//                        composable(Screen.Register.route) {
//                            RegisterScreen(navController)
//                        }
//                        composable(Screen.Dashboard.route) {
//                            DashboardScreen(navController)
//                        }
//                        composable(Screen.TaskCreation.route) {
//                            TaskCreationScreen(navController)
//                        }
//                        composable(Screen.NotificationSettings.route) {
//                            NotificationSettingsScreen(navController)
//                        }
//                    }
//                }
//            }
//        }
//    }
////    // Corrected method for requesting notification permission
////    private fun requestNotificationPermission() {
////        if (ContextCompat.checkSelfPermission(
////                this, Manifest.permission.POST_NOTIFICATIONS
////            ) != PackageManager.PERMISSION_GRANTED
////        ) {
////            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
////        }
////    }
////
////    private val requestPermissionLauncher =
////        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
////            if (isGranted) {
////
////            } else {
////                // Permission denied: Handle accordingly (e.g., show a message)
////            }
////        }
//
//}

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

class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        Authorization.getInstance();
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Retrieve API key securely from the manifest
        val ai: ApplicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val apiKey = ai.metaData.getString("com.google.android.geo.API_KEY")

        if (!Places.isInitialized() && apiKey != null) {
            Places.initialize(applicationContext, apiKey)
        }
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