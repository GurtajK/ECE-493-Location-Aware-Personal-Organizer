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

import android.Manifest
import android.content.Context
import android.content.Intent
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
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.navArgument
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.location_aware_personal_organizer.ui.completedTasks.CompletedTasksScreen
import com.example.location_aware_personal_organizer.utils.TaskDeadlineReminder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth = Firebase.auth

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var latitude by Delegates.notNull<Float>()
    private var longitude by Delegates.notNull<Float>()

    override fun onCreate(savedInstanceState: Bundle?) {
        Authorization.getInstance();
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
//        scheduleTaskDeadlineReminder(applicationContext)

        // Retrieve API key securely from the manifest
        val ai: ApplicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val apiKey = ai.metaData.getString("com.google.android.geo.API_KEY")

        if (!Places.isInitialized() && apiKey != null) {
            Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey);
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
                            TaskCreationScreen(navController, latitude, longitude)
                        }
                        composable(Screen.NotificationSettings.route) {
                            NotificationSettingsScreen(navController)
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
                    }
                }
            }
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        getCurrentLocation()
    }

    private fun getCurrentLocation()
    {

        if(checkPermissions())
        {
            if(isLocationEnabled())
            {
                // final latitude and longitude code here
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: android.location.Location? = task.result
                    if (location == null) {
                        Toast.makeText(this, "Null Received", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(this, "Get Success", Toast.LENGTH_SHORT).show()
                        latitude = location.latitude.toFloat()
                        longitude = location.longitude.toFloat()
                    }
                }

            }
            else
            {
                // setting open here
                Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)

            }
        }
        else
        {
            // request permission here

            requestPermission()

        }


    }

    private fun isLocationEnabled(): Boolean{
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100

    }

    private fun checkPermissions(): Boolean {
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
            ==PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            return true
        }

        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

        if(requestCode == PERMISSION_REQUEST_ACCESS_LOCATION)
        {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }
            else
            {
                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

fun scheduleTaskDeadlineReminder(context: Context) {
    // Run immediately once
    val immediateWork = OneTimeWorkRequestBuilder<TaskDeadlineReminder>().build()
    WorkManager.getInstance(context).enqueue(immediateWork)

    // Then every 15 minutes
    val periodicWork = PeriodicWorkRequestBuilder<TaskDeadlineReminder>(
        1, TimeUnit.MINUTES
    ).build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "task_deadline_reminder",
        ExistingPeriodicWorkPolicy.KEEP,
        periodicWork
    )
}
