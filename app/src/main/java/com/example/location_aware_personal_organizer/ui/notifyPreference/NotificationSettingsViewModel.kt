package com.example.location_aware_personal_organizer.ui.notifyPreference

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat

class NotificationSettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _isPermissionGranted = MutableStateFlow(
        ContextCompat.checkSelfPermission(
            application, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    )

    val isPermissionGranted: StateFlow<Boolean> = _isPermissionGranted

    // Function to update permission state
    fun updatePermissionStatus(granted: Boolean) {
        _isPermissionGranted.value = granted
    }
}
