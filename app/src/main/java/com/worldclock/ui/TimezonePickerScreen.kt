package com.worldclock.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimezonePickerScreen(
    selectedTimezones: List<String>,
    onTimezoneSelected: (String) -> Unit,
    onBackPressed: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    val allTimezones = remember {
        ZoneId.getAvailableZoneIds()
            .filter { it.contains("/") }
            .sorted()
            .toList()
    }
    
    val filteredTimezones = remember(searchQuery, allTimezones) {
        if (searchQuery.isEmpty()) {
            allTimezones
        } else {
            allTimezones.filter { 
                it.contains(searchQuery, ignoreCase = true) ||
                it.replace("_", " ").contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Timezone") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search timezones...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true
            )
            
            // Timezone list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredTimezones, key = { it }) { zoneId ->
                    val isSelected = selectedTimezones.contains(zoneId)
                    TimezoneListItem(
                        zoneId = zoneId,
                        isSelected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                onTimezoneSelected(zoneId)
                                onBackPressed()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TimezoneListItem(
    zoneId: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val zone = ZoneId.of(zoneId)
    val now = ZonedDateTime.now(zone)
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val offset = now.offset.toString()
    val formattedOffset = if (offset == "Z") "UTC+00:00" else "UTC$offset"
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick, enabled = !isSelected),
        color = if (isSelected) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = zoneId.replace("_", " "),
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$formattedOffset · ${now.format(timeFormatter)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    Divider()
}
