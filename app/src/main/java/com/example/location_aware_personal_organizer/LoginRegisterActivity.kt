package com.example.location_aware_personal_organizer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.location_aware_personal_organizer.services.Authorization
import com.example.location_aware_personal_organizer.ui.theme.AppTheme

class ComposeActivity : ComponentActivity() {
    private fun onLogin() {
        startActivity(Intent(this, deprecatedMainActivity::class.java))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        Authorization.getInstance();
        super.onCreate(savedInstanceState)
        enableEdgeToEdge();
        setContent {
            // A surface container using the 'background' color from the theme
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RegisterLogin()
                }
            }
        }
    }
    @Composable
    fun RegisterLogin() {
        var isRegistering by rememberSaveable { mutableStateOf(false) };
        if (isRegistering) {
            Register(redirectLogin={isRegistering = false})
        } else {
            Login(onLogin={onLogin()}, signupRedirect={ isRegistering = true })
        }
    }
}

