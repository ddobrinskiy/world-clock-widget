package com.worldclock.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
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

    companion object {
        // Define size breakpoints for responsive widget
        private val TINY = DpSize(180.dp, 80.dp)
        private val SMALL = DpSize(180.dp, 150.dp)
        private val MEDIUM = DpSize(180.dp, 250.dp)
        private val LARGE = DpSize(180.dp, 350.dp)
        private val EXTRA_LARGE = DpSize(180.dp, 500.dp)
        private val HUGE = DpSize(180.dp, 700.dp)
    }

    // Use responsive size mode to re-render at different sizes
    override val sizeMode = SizeMode.Responsive(
        setOf(TINY, SMALL, MEDIUM, LARGE, EXTRA_LARGE, HUGE)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val preferences = TimezonePreferences(context)
        val timezones = preferences.timezones.first()
        val homeTimezone = preferences.homeTimezone.first()

        provideContent {
            WorldClockWidgetContent(timezones, homeTimezone)
        }
    }

    @Composable
    private fun WorldClockWidgetContent(timezones: List<String>, homeTimezone: String?) {
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
                    TimezoneRow(zoneId, isHome = zoneId == homeTimezone)
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
    private fun TimezoneRow(zoneId: String, isHome: Boolean = false) {
        val zone = ZoneId.of(zoneId)
        val now = ZonedDateTime.now(zone)
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val offset = now.offset.toString()
        val formattedOffset = if (offset == "Z") "UTC+00:00" else "UTC$offset"
        
        // Accent color for home timezone
        val accentColor = ColorProvider(Color(0xFF6B9FFF)) // Light blue accent
        val textColor = if (isHome) accentColor else GlanceTheme.colors.onBackground
        val cityName = if (isHome) {
            "🏠 ${zoneId.substringAfterLast("/").replace("_", " ")}"
        } else {
            zoneId.substringAfterLast("/").replace("_", " ")
        }
        
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Horizontal.Start,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Column(
                modifier = GlanceModifier.defaultWeight()
            ) {
                Text(
                    text = cityName,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = if (isHome) FontWeight.Bold else FontWeight.Medium,
                        color = textColor
                    )
                )
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text(
                    text = formattedOffset,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = if (isHome) accentColor else ColorProvider(Color.Gray)
                    )
                )
            }

            Text(
                text = now.format(timeFormatter),
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = if (isHome) FontWeight.Bold else FontWeight.Normal,
                    color = textColor
                )
            )
        }
    }

    private fun calculateMaxTimezones(height: androidx.compose.ui.unit.Dp): Int {
        // Header "World Clock" takes ~44dp (16sp text + 12dp bottom padding + margin)
        // Each timezone row takes ~44dp (city name 14sp + offset 12sp + spacer 8dp)
        val headerHeight = 44f
        val rowHeight = 44f
        val availableHeight = height.value - headerHeight
        val maxItems = (availableHeight / rowHeight).toInt()
        // Show at least 2, but scale up with available space
        return maxItems.coerceIn(2, 12)
    }
}
