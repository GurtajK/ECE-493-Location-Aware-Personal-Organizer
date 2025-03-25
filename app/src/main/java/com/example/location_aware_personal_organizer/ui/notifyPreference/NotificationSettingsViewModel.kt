package com.example.location_aware_personal_organizer.ui.notifyPreference

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NotificationSettingsViewModel(application: Application) : AndroidViewModel(application) {

    // Runtime permission state
    private val _isPermissionGranted = MutableStateFlow(
        ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    )
    val isPermissionGranted: StateFlow<Boolean> = _isPermissionGranted

    fun updatePermissionStatus(granted: Boolean) {
        _isPermissionGranted.value = granted
    }

    // App-specific notification preferences
    private val _timeNotificationEnabled = MutableStateFlow(true)
    val timeNotificationEnabled: StateFlow<Boolean> = _timeNotificationEnabled

    private val _priorityNotificationEnabled = MutableStateFlow(false)
    val priorityNotificationEnabled: StateFlow<Boolean> = _priorityNotificationEnabled

    fun toggleTimeNotification(enabled: Boolean) {
        _timeNotificationEnabled.value = enabled
    }

    fun togglePriorityNotification(enabled: Boolean) {
        _priorityNotificationEnabled.value = enabled
    }
}
