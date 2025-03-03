package com.example.location_aware_personal_organizer.ui

import android.os.Bundle
import android.util.Log
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
import com.example.location_aware_personal_organizer.ui.register.RegisterScreen
import com.example.location_aware_personal_organizer.ui.taskCreation.TaskCreationScreen
import com.example.location_aware_personal_organizer.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Authorization.getInstance();
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController: NavHostController = rememberNavController()
                    Log.d("MainActivity", "NavController in MainActivity: $navController") // Add this line
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.route
                    ) {
                        composable(Screen.Login.route) {
                            Log.d("MainActivity", "Navigating to LoginScreen")
                            LoginScreen(navController)
                        }
                        composable(Screen.Register.route) {
                            Log.d("MainActivity", "Navigating to RegisterScreen")
                            RegisterScreen(navController)
                        }
                        composable(Screen.Dashboard.route) {
                            Log.d("MainActivity", "Navigating to DashboardScreen")
                            DashboardScreen(navController)
                        }
                        composable(Screen.TaskCreation.route) {
                            Log.d("MainActivity", "Navigating to TaskCreationScreen") // Add this line
                            TaskCreationScreen(navController)
                        }
                    }
                }
            }
        }
    }
}