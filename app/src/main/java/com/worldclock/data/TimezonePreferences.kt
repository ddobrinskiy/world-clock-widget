package com.worldclock.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "timezones")

class TimezonePreferences(private val context: Context) {
    
    companion object {
        private val TIMEZONES_KEY = stringPreferencesKey("selected_timezones")
        private val HOME_TIMEZONE_KEY = stringPreferencesKey("home_timezone")
        private const val SEPARATOR = "|||"
    }

    val homeTimezone: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[HOME_TIMEZONE_KEY]
    }

    val timezones: Flow<List<String>> = context.dataStore.data.map { preferences ->
        val stored = preferences[TIMEZONES_KEY] ?: ""
        if (stored.isEmpty()) {
            // Default timezones
            listOf("America/New_York", "Europe/London", "Asia/Tokyo", "Australia/Sydney")
        } else {
            stored.split(SEPARATOR)
        }
    }

    suspend fun addTimezone(zoneId: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[TIMEZONES_KEY] ?: ""
            val list = if (current.isEmpty()) {
                listOf("America/New_York", "Europe/London", "Asia/Tokyo", "Australia/Sydney")
            } else {
                current.split(SEPARATOR)
            }
            
            if (!list.contains(zoneId)) {
                val updated = list + zoneId
                preferences[TIMEZONES_KEY] = updated.joinToString(SEPARATOR)
            }
        }
    }

    suspend fun removeTimezone(zoneId: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[TIMEZONES_KEY] ?: ""
            val list = if (current.isEmpty()) {
                listOf("America/New_York", "Europe/London", "Asia/Tokyo", "Australia/Sydney")
            } else {
                current.split(SEPARATOR)
            }
            val updated = list.filter { it != zoneId }
            preferences[TIMEZONES_KEY] = updated.joinToString(SEPARATOR)
        }
    }

    suspend fun reorderTimezones(fromIndex: Int, toIndex: Int) {
        context.dataStore.edit { preferences ->
            val current = preferences[TIMEZONES_KEY] ?: ""
            val list = if (current.isEmpty()) {
                mutableListOf("America/New_York", "Europe/London", "Asia/Tokyo", "Australia/Sydney")
            } else {
                current.split(SEPARATOR).toMutableList()
            }
            
            if (fromIndex in list.indices && toIndex in list.indices) {
                val item = list.removeAt(fromIndex)
                list.add(toIndex, item)
                preferences[TIMEZONES_KEY] = list.joinToString(SEPARATOR)
            }
        }
    }

    suspend fun setHomeTimezone(zoneId: String?) {
        context.dataStore.edit { preferences ->
            if (zoneId == null) {
                preferences.remove(HOME_TIMEZONE_KEY)
            } else {
                // Toggle: if already home, unset it; otherwise set it
                val current = preferences[HOME_TIMEZONE_KEY]
                if (current == zoneId) {
                    preferences.remove(HOME_TIMEZONE_KEY)
                } else {
                    preferences[HOME_TIMEZONE_KEY] = zoneId
                }
            }
        }
    }
}
