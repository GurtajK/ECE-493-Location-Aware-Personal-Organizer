package com.example.location_aware_personal_organizer.ui.notifyPreference

import android.Manifest
import android.app.Application
import android.app.job.JobScheduler
import android.app.job.JobService.JOB_SCHEDULER_SERVICE
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat

// FR 49 Notify.PreferencesChange
class NotificationSettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private val _isPermissionGranted = MutableStateFlow(
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    )
    val isPermissionGranted: StateFlow<Boolean> = _isPermissionGranted

    private val _timeNotificationEnabled = MutableStateFlow(true)
    val timeNotificationEnabled: StateFlow<Boolean> = _timeNotificationEnabled

    private val _priorityNotificationEnabled = MutableStateFlow(false)
    val priorityNotificationEnabled: StateFlow<Boolean> = _priorityNotificationEnabled

    init {
        viewModelScope.launch {
            NotificationPreferencesManager.getTimeNotificationState(context)
                .collect { _timeNotificationEnabled.value = it }
        }

        viewModelScope.launch {
            NotificationPreferencesManager.getPriorityNotificationState(context)
                .collect { _priorityNotificationEnabled.value = it }
        }
    }

    fun updatePermissionStatus(granted: Boolean) {
        _isPermissionGranted.value = granted
    }

    fun toggleTimeNotification(enabled: Boolean) {
        _timeNotificationEnabled.value = enabled
        viewModelScope.launch {
            NotificationPreferencesManager.setTimeNotificationState(context, enabled)
        }
    }

    fun togglePriorityNotification(enabled: Boolean) {
        if (!enabled) {
            val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.cancel(1)
            Log.d("bg job", "cancelled job")
        }
        _priorityNotificationEnabled.value = enabled
        viewModelScope.launch {
            NotificationPreferencesManager.setPriorityNotificationState(context, enabled)
        }
    }
}
