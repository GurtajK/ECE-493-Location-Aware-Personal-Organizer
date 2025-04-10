package com.example.location_aware_personal_organizer.ui.notifyPreference

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "notification_settings")

// FR 49 Notify.PreferencesChange
object NotificationPreferencesManager {
    private val TIME_NOTIF_KEY = booleanPreferencesKey("time_notifications")
    private val PRIORITY_NOTIF_KEY = booleanPreferencesKey("priority_notifications")

    fun getTimeNotificationState(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[TIME_NOTIF_KEY] ?: true }

    fun getPriorityNotificationState(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[PRIORITY_NOTIF_KEY] ?: false }

    suspend fun setTimeNotificationState(context: Context, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[TIME_NOTIF_KEY] = enabled
        }
    }

    suspend fun setPriorityNotificationState(context: Context, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PRIORITY_NOTIF_KEY] = enabled
        }
    }
}
