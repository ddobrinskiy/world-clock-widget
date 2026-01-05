package com.worldclock.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class WorldClockWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WorldClockWidget()

    companion object {
        private const val TAG = "WorldClockWidget"
        private const val ACTION_UPDATE_TIME = "com.worldclock.ACTION_UPDATE_TIME"
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        Log.d(TAG, "onReceive: action=${intent.action}")
        
        when (intent.action) {
            ACTION_UPDATE_TIME -> {
                Log.d(TAG, "Updating widget time...")
                // Update all widgets when alarm fires
                runBlocking {
                    WorldClockWidget().updateAll(context)
                }
                // Schedule next update
                scheduleNextUpdate(context)
            }
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                // Also schedule updates when system triggers update
                scheduleNextUpdate(context)
            }
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Log.d(TAG, "onEnabled: First widget added, starting updates")
        scheduleNextUpdate(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        Log.d(TAG, "onDisabled: Last widget removed, canceling updates")
        cancelUpdates(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Log.d(TAG, "onUpdate: widgetIds=${appWidgetIds.contentToString()}")
        scheduleNextUpdate(context)
    }

    private fun scheduleNextUpdate(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Check if we can schedule exact alarms on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Log.w(TAG, "Cannot schedule exact alarms, using inexact")
            scheduleInexactUpdate(context, alarmManager)
            return
        }
        
        val intent = Intent(context, WorldClockWidgetReceiver::class.java).apply {
            action = ACTION_UPDATE_TIME
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule for the start of the next minute
        val calendar = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC,
                calendar.timeInMillis,
                pendingIntent
            )
            Log.d(TAG, "Scheduled next update for ${calendar.time}")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException scheduling exact alarm, using inexact", e)
            scheduleInexactUpdate(context, alarmManager)
        }
    }
    
    private fun scheduleInexactUpdate(context: Context, alarmManager: AlarmManager) {
        val intent = Intent(context, WorldClockWidgetReceiver::class.java).apply {
            action = ACTION_UPDATE_TIME
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Use inexact alarm as fallback (updates within ~1 minute)
        alarmManager.set(
            AlarmManager.RTC,
            System.currentTimeMillis() + 60_000,
            pendingIntent
        )
        Log.d(TAG, "Scheduled inexact update in ~1 minute")
    }

    private fun cancelUpdates(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WorldClockWidgetReceiver::class.java).apply {
            action = ACTION_UPDATE_TIME
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Canceled all updates")
    }
}
