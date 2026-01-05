package com.worldclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.worldclock.ui.TimezonePickerScreen
import com.worldclock.ui.theme.WorldClockTheme
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
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
    val homeTimezone by viewModel.homeTimezone.collectAsState()
    var refreshKey by remember { mutableStateOf(0) }
    val hapticFeedback = LocalHapticFeedback.current
    
    // Local mutable list for drag operations
    var localTimezones by remember(timezones) { mutableStateOf(timezones) }
    
    // Sync when external data changes
    LaunchedEffect(timezones) {
        localTimezones = timezones
    }
    
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        localTimezones = localTimezones.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        
        // Persist reorder
        viewModel.reorderTimezones(from.index, to.index)
    }
    
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
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(localTimezones, key = { it }) { zoneId ->
                ReorderableItem(reorderableLazyListState, key = zoneId) { isDragging ->
                    key(refreshKey, zoneId, homeTimezone) {
                        TimezoneCard(
                            zoneId = zoneId,
                            isHome = zoneId == homeTimezone,
                            onDelete = { viewModel.removeTimezone(zoneId) },
                            onSetHome = { viewModel.setHomeTimezone(zoneId) },
                            isDragging = isDragging,
                            dragModifier = Modifier.draggableHandle(
                                onDragStarted = {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                                onDragStopped = {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimezoneCard(
    zoneId: String,
    isHome: Boolean = false,
    onDelete: () -> Unit,
    onSetHome: () -> Unit,
    isDragging: Boolean = false,
    dragModifier: Modifier = Modifier
) {
    val zone = ZoneId.of(zoneId)
    val now = ZonedDateTime.now(zone)
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val offset = now.offset.toString()
    val formattedOffset = if (offset == "Z") "UTC+00:00" else "UTC$offset"
    
    val elevation by animateDpAsState(if (isDragging) 8.dp else 2.dp, label = "elevation")
    
    val dismissState = rememberDismissState(
        confirmValueChange = { value ->
            when (value) {
                DismissValue.DismissedToStart -> {
                    onDelete()
                    true
                }
                DismissValue.DismissedToEnd -> {
                    onSetHome()
                    false // Don't actually dismiss, just trigger the action
                }
                else -> false
            }
        }
    )
    
    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart, DismissDirection.StartToEnd),
        background = {
            val direction = dismissState.dismissDirection
            val backgroundColor = when (direction) {
                DismissDirection.EndToStart -> MaterialTheme.colorScheme.errorContainer
                DismissDirection.StartToEnd -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
            val contentAlignment = when (direction) {
                DismissDirection.EndToStart -> Alignment.CenterEnd
                DismissDirection.StartToEnd -> Alignment.CenterStart
                else -> Alignment.Center
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = contentAlignment
            ) {
                when (direction) {
                    DismissDirection.EndToStart -> {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.padding(end = 16.dp),
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    DismissDirection.StartToEnd -> {
                        Text(
                            text = "🏠",
                            modifier = Modifier.padding(start = 16.dp),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    else -> {}
                }
            }
        },
        dismissContent = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = elevation),
                colors = if (isHome) {
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                } else {
                    CardDefaults.cardColors()
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isHome) {
                                Text(
                                    text = "🏠 ",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Text(
                                text = zoneId.substringAfterLast("/").replace("_", " "),
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isHome) MaterialTheme.colorScheme.onPrimaryContainer 
                                       else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formattedOffset,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isHome) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = now.format(timeFormatter),
                            style = MaterialTheme.typography.headlineLarge,
                            color = if (isHome) MaterialTheme.colorScheme.onPrimaryContainer 
                                   else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        modifier = dragModifier,
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DragHandle,
                            contentDescription = "Drag to reorder",
                            tint = if (isHome) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                  else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    )
}
