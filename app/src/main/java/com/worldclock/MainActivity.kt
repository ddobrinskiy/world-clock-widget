package com.worldclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.worldclock.ui.TimezonePickerScreen
import com.worldclock.ui.theme.WorldClockTheme
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorldClockTheme {
                WorldClockApp()
            }
        }
    }
}

@Composable
fun WorldClockApp(viewModel: MainViewModel = viewModel()) {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                viewModel = viewModel,
                onAddClick = { navController.navigate("picker") }
            )
        }
        composable("picker") {
            val timezones by viewModel.timezones.collectAsState()
            TimezonePickerScreen(
                selectedTimezones = timezones,
                onTimezoneSelected = { viewModel.addTimezone(it) },
                onBackPressed = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel, onAddClick: () -> Unit) {
    val timezones by viewModel.timezones.collectAsState()
    var refreshKey by remember { mutableStateOf(0) }
    
    // Auto-refresh times every minute
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(60_000)
            refreshKey++
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("World Clock") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add timezone")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(timezones, key = { it }) { zoneId ->
                key(refreshKey, zoneId) {
                    TimezoneCard(
                        zoneId = zoneId,
                        onDelete = { viewModel.removeTimezone(zoneId) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimezoneCard(zoneId: String, onDelete: () -> Unit) {
    val zone = ZoneId.of(zoneId)
    val now = ZonedDateTime.now(zone)
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val offset = now.offset.toString()
    val formattedOffset = if (offset == "Z") "UTC+00:00" else "UTC$offset"
    
    val dismissState = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart) {
                onDelete()
                true
            } else false
        }
    )
    
    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 4.dp)
            )
        },
        dismissContent = {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = zoneId.substringAfterLast("/").replace("_", " "),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formattedOffset,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = now.format(timeFormatter),
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }
    )
}

