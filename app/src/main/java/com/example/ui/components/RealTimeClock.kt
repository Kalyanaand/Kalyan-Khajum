package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClockState
import java.util.Calendar

/**
 * Taskbar digital clock widget that updates every second in real time.
 * Positioned on the right side of the taskbar (System Tray).
 */
@Composable
fun TaskbarDigitalClock(
    clockState: ClockState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        color = Color.Transparent,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
            .testTag("taskbar_clock_button")
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0x1AFFFFFF))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                // Digital Time Row: Hours:Minutes:Seconds + AM/PM
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = clockState.timeShort,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
                    )

                    if (clockState.showSecondsInTaskbar) {
                        Text(
                            text = ":${clockState.secondsStr}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = Color(0xFF60CDFF),
                            modifier = Modifier.padding(start = 1.dp)
                        )
                    }

                    if (!clockState.is24Hour && clockState.amPm.isNotEmpty()) {
                        Text(
                            text = " ${clockState.amPm}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp
                            ),
                            color = Color(0xCCFFFFFF),
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }

                // Date label underneath: "Wed, Nov 11"
                Text(
                    text = clockState.dateFormatted,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color(0xAAFFFFFF)
                )
            }
        }
    }
}

/**
 * Windows-style Flyout Calendar & Big Clock popup.
 * Opens when user clicks on the Taskbar digital clock.
 */
@Composable
fun WindowsCalendarFlyout(
    isOpen: Boolean,
    clockState: ClockState,
    onClose: () -> Unit,
    onToggleSeconds: () -> Unit,
    onToggle24Hour: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = slideInVertically(initialOffsetY = { it / 3 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it / 3 }) + fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 380.dp)
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xF0182234),
                            Color(0xF50D1525)
                        )
                    )
                )
                .border(1.dp, Color(0x3360CDFF), RoundedCornerShape(14.dp))
                .padding(16.dp)
                .testTag("calendar_flyout")
        ) {
            Column {
                // Header with live big digital clock & Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        // Live Big Digital Clock with pulsing seconds indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = clockState.timeWithSeconds,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                ),
                                color = Color.White
                            )

                            Box(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF60CDFF))
                            )
                        }

                        // Full Date display
                        Text(
                            text = clockState.fullDateFormatted,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF60CDFF),
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("close_calendar_flyout")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Calendar",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                HorizontalDivider(
                    color = Color(0x22FFFFFF),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Calendar Month View
                InteractiveMonthCalendar(currentDay = clockState.dayOfMonth)

                HorizontalDivider(
                    color = Color(0x22FFFFFF),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Quick Clock Settings
                Text(
                    text = "Clock Settings",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color(0x99FFFFFF),
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0xFF60CDFF),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Show seconds in taskbar",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }

                    Switch(
                        checked = clockState.showSecondsInTaskbar,
                        onCheckedChange = { onToggleSeconds() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF0078D4)
                        ),
                        modifier = Modifier.testTag("toggle_seconds_switch")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Today,
                            contentDescription = null,
                            tint = Color(0xFF60CDFF),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "24-hour clock format",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }

                    Switch(
                        checked = clockState.is24Hour,
                        onCheckedChange = { onToggle24Hour() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF0078D4)
                        ),
                        modifier = Modifier.testTag("toggle_24hour_switch")
                    )
                }
            }
        }
    }
}

/**
 * Interactive Monthly Calendar Grid with current day highlighting.
 */
@Composable
fun InteractiveMonthCalendar(
    currentDay: Int,
    modifier: Modifier = Modifier
) {
    val calendar = remember { Calendar.getInstance() }
    var monthOffset by remember { mutableIntStateOf(0) }

    val activeCal = remember(monthOffset) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, monthOffset)
        cal
    }

    val monthYearTitle = remember(monthOffset) {
        val sdf = java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault())
        sdf.format(activeCal.time)
    }

    // Days in active month
    val daysInMonth = remember(monthOffset) {
        val temp = activeCal.clone() as Calendar
        temp.set(Calendar.DAY_OF_MONTH, 1)
        val maxDays = temp.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = temp.get(Calendar.DAY_OF_WEEK) // 1=Sunday, 2=Monday, ...
        Pair(firstDayOfWeek, maxDays)
    }

    val (firstDayOfWeek, maxDays) = daysInMonth
    val daysOfWeek = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")

    Column(modifier = modifier.fillMaxWidth()) {
        // Month Navigation Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = monthYearTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Row {
                IconButton(
                    onClick = { monthOffset-- },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous Month",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = { monthOffset++ },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next Month",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Days of week header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            daysOfWeek.forEach { dayName ->
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0x88FFFFFF),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.width(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Days Grid (6 rows x 7 cols)
        val totalCells = 42
        val offset = firstDayOfWeek - 1

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(180.dp),
            userScrollEnabled = false
        ) {
            items(totalCells) { index ->
                val dayNum = index - offset + 1
                val isValidDay = dayNum in 1..maxDays
                val isToday = monthOffset == 0 && dayNum == currentDay

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(2.dp)
                        .height(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when {
                                isToday -> Color(0xFF0078D4)
                                else -> Color.Transparent
                            }
                        )
                ) {
                    if (isValidDay) {
                        Text(
                            text = dayNum.toString(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (isToday) Color.White else Color(0xDDFFFFFF),
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
