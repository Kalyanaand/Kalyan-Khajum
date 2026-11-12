package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClockState
import com.example.data.model.ShortcutItem
import com.example.data.model.WindowData

/**
 * Windows 11 Desktop Taskbar:
 * - Start Button
 * - Quick Search Input
 * - Active Open Windows (with minimize/restore toggle)
 * - Pinned App Icons
 * - System Tray with Live Digital Clock (updating every second), Battery, Wi-Fi, Sound, Action Center
 */
@Composable
fun WindowsTaskbar(
    clockState: ClockState,
    pinnedShortcuts: List<ShortcutItem>,
    openWindows: List<WindowData>,
    activeWindowId: String?,
    isStartMenuOpen: Boolean,
    isCalendarOpen: Boolean,
    isActionCenterOpen: Boolean,
    isNotificationCenterOpen: Boolean = false,
    notificationCount: Int = 0,
    activeMiniApp: String?,
    batteryPercentage: Int,
    onStartButtonClick: () -> Unit,
    onSearchClick: () -> Unit,
    onShortcutClick: (ShortcutItem) -> Unit,
    onWindowTaskbarClick: (WindowData) -> Unit,
    onClockClick: () -> Unit,
    onActionCenterClick: () -> Unit,
    onNotificationCenterClick: () -> Unit,
    onShowDesktopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xE610192A),
                            Color(0xF2090F1C)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color(0x2BFFFFFF),
                    shape = RoundedCornerShape(0.dp)
                )
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // LEFT / CENTER SECTION: Start Button + Search Bar + Open Windows + Pinned Apps
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    // Windows Start Button
                    StartButton(
                        isOpen = isStartMenuOpen,
                        onClick = onStartButtonClick
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Windows Search Bar / Button
                    TaskbarSearchBar(
                        onClick = onSearchClick
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Open Windows & Pinned shortcuts row
                    LazyRow(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // First: Active Open Windows
                        items(openWindows, key = { it.id }) { win ->
                            val isFocused = win.id == activeWindowId && !win.isMinimized
                            TaskbarWindowIcon(
                                window = win,
                                isFocused = isFocused,
                                onClick = { onWindowTaskbarClick(win) }
                            )
                        }

                        // Next: Pinned shortcuts that are not currently opened as a window
                        val unopenPinned = pinnedShortcuts.filter { sc ->
                            openWindows.none { it.shortcutId == sc.id }
                        }
                        items(unopenPinned, key = { it.id }) { shortcut ->
                            TaskbarAppIcon(
                                shortcut = shortcut,
                                isActive = false,
                                onClick = { onShortcutClick(shortcut) }
                            )
                        }
                    }
                }

                // RIGHT SECTION: System Tray & Clock
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    // Quick Action Toggles (Wi-Fi, Volume, Battery)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onActionCenterClick() }
                            .padding(horizontal = 6.dp, vertical = 6.dp)
                            .testTag("system_tray_quick_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = "Wi-Fi Status",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Volume",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (batteryPercentage > 20) Icons.Default.BatteryFull else Icons.Default.BatteryChargingFull,
                            contentDescription = "Battery $batteryPercentage%",
                            tint = if (batteryPercentage > 20) Color(0xFF60CDFF) else Color(0xFFFF6B6B),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$batteryPercentage%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color(0xCCFFFFFF),
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }

                    // REAL-TIME DIGITAL CLOCK in Taskbar (updates every second)
                    TaskbarDigitalClock(
                        clockState = clockState,
                        onClick = onClockClick
                    )

                    // Notification Center Bell Toggle with Badge
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onNotificationCenterClick() }
                            .padding(horizontal = 6.dp, vertical = 6.dp)
                            .testTag("notification_center_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notification Center",
                            tint = if (isNotificationCenterOpen) Color(0xFF60CDFF) else Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(17.dp)
                        )

                        if (notificationCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF60CDFF))
                            )
                        }
                    }

                    // "Show Desktop" peek strip (far right Windows line)
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .fillMaxHeight(0.6f)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0x33FFFFFF))
                            .clickable { onShowDesktopClick() }
                            .testTag("show_desktop_button")
                    )
                }
            }
        }
    }
}

/**
 * Windows Start Button with authentic 4-pane icon.
 */
@Composable
fun StartButton(
    isOpen: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isOpen) Color(0x4060CDFF) else Color(0x1AFFFFFF))
            .border(
                1.dp,
                if (isOpen) Color(0xFF60CDFF) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .testTag("start_button")
    ) {
        // 4-tile Windows icon
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(Color(0xFF0078D4))
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(Color(0xFF00A4EF))
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(Color(0xFF60CDFF))
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(Color(0xFF0078D4))
                )
            }
        }
    }
}

/**
 * Taskbar search bar in Windows 11 style.
 */
@Composable
fun TaskbarSearchBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(17.dp))
            .background(Color(0x26FFFFFF))
            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(17.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp)
            .testTag("taskbar_search_bar")
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = Color(0xCCFFFFFF),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Type here to search",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = Color(0x88FFFFFF),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Pinned App icon in taskbar with active running indicator line.
 */
@Composable
fun TaskbarAppIcon(
    shortcut: ShortcutItem,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(38.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isActive) Color(0x3360CDFF) else Color(0x10FFFFFF))
            .clickable(onClick = onClick)
            .testTag("taskbar_item_${shortcut.id}")
    ) {
        Icon(
            imageVector = IconHelper.getIcon(shortcut.iconKey),
            contentDescription = shortcut.title,
            tint = Color(shortcut.colorHex),
            modifier = Modifier.size(20.dp)
        )

        // Active indicator line at bottom
        if (isActive) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .width(16.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF60CDFF))
            )
        }
    }
}

/**
 * Active Open Window icon in taskbar with focus indicator and minimize/restore toggle.
 */
@Composable
fun TaskbarWindowIcon(
    window: WindowData,
    isFocused: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(
                when {
                    isFocused -> Color(0x4060CDFF)
                    !window.isMinimized -> Color(0x24FFFFFF)
                    else -> Color(0x10FFFFFF)
                }
            )
            .border(
                1.dp,
                if (isFocused) Color(0xFF60CDFF) else Color(0x22FFFFFF),
                RoundedCornerShape(6.dp)
            )
            .clickable(onClick = onClick)
            .testTag("taskbar_win_${window.id}")
    ) {
        Icon(
            imageVector = IconHelper.getIcon(window.iconKey),
            contentDescription = window.title,
            tint = Color(window.colorHex),
            modifier = Modifier.size(20.dp)
        )

        // Running indicator pill at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(if (isFocused) 18.dp else 8.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (isFocused) Color(0xFF60CDFF) else Color(0x99FFFFFF))
        )
    }
}

