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
    
    val homeTimezone: StateFlow<String?> = preferences.homeTimezone
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    
    fun setHomeTimezone(zoneId: String) {
        viewModelScope.launch {
            preferences.setHomeTimezone(zoneId)
            updateWidget()
        }
    }
    
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
    
    fun reorderTimezones(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch {
            preferences.reorderTimezones(fromIndex, toIndex)
            updateWidget()
        }
    }
    
    private suspend fun updateWidget() {
        WorldClockWidget().updateAll(getApplication())
    }
}
