package com.worldclock

import android.app.Application
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.worldclock.data.TimezonePreferences
import com.worldclock.widget.WorldClockWidget
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val preferences = TimezonePreferences(application)
    
    val timezones: StateFlow<List<String>> = preferences.timezones
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun addTimezone(zoneId: String) {
        viewModelScope.launch {
            preferences.addTimezone(zoneId)
            updateWidget()
        }
    }
    
    fun removeTimezone(zoneId: String) {
        viewModelScope.launch {
            preferences.removeTimezone(zoneId)
            updateWidget()
        }
    }
    
    private suspend fun updateWidget() {
        WorldClockWidget().updateAll(getApplication())
    }
}
