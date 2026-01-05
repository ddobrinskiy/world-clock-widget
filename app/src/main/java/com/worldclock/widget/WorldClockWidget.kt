package com.worldclock.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.worldclock.MainActivity
import com.worldclock.data.TimezonePreferences
import kotlinx.coroutines.flow.first
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class WorldClockWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val preferences = TimezonePreferences(context)
        val timezones = preferences.timezones.first()

        provideContent {
            WorldClockWidgetContent(timezones)
        }
    }

    @Composable
    private fun WorldClockWidgetContent(timezones: List<String>) {
        val size = LocalSize.current

        val intent = Intent(LocalContext.current, MainActivity::class.java)
        
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(16.dp)
                .background(GlanceTheme.colors.background)
                .clickable(actionStartActivity(intent)),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "World Clock",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = GlanceTheme.colors.onBackground
                ),
                modifier = GlanceModifier.padding(bottom = 12.dp)
            )

            if (timezones.isEmpty()) {
                Text(
                    text = "No timezones added",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = ColorProvider(Color.Gray)
                    )
                )
            } else {
                val maxTimezones = calculateMaxTimezones(size.height)
                
                timezones.take(maxTimezones).forEach { zoneId ->
                    TimezoneRow(zoneId)
                    Spacer(modifier = GlanceModifier.height(8.dp))
                }

                if (timezones.size > maxTimezones) {
                    Text(
                        text = "+${timezones.size - maxTimezones} more",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = ColorProvider(Color.Gray)
                        ),
                        modifier = GlanceModifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun TimezoneRow(zoneId: String) {
        val zone = ZoneId.of(zoneId)
        val now = ZonedDateTime.now(zone)
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val offset = now.offset.toString()
        val formattedOffset = if (offset == "Z") "UTC+00:00" else "UTC$offset"
        
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Horizontal.Start,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Column(
                modifier = GlanceModifier.defaultWeight()
            ) {
                Text(
                    text = zoneId.substringAfterLast("/").replace("_", " "),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = GlanceTheme.colors.onBackground
                    )
                )
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text(
                    text = formattedOffset,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = ColorProvider(Color.Gray)
                    )
                )
            }

            Text(
                text = now.format(timeFormatter),
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    color = GlanceTheme.colors.onBackground
                )
            )
        }
    }

    private fun calculateMaxTimezones(height: androidx.compose.ui.unit.Dp): Int {
        val availableHeight = height.value - 60
        val rowHeight = 44
        return (availableHeight / rowHeight).toInt().coerceAtLeast(2)
    }
}
