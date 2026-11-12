package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType

/**
 * Windows Notification Center Overlay:
 * Slides in smoothly from the right edge of the screen to display
 * persistent messages and system alerts with quick actions.
 */
@Composable
fun WindowsNotificationCenterOverlay(
    isOpen: Boolean,
    notifications: List<NotificationItem>,
    isDoNotDisturb: Boolean,
    onDismissNotification: (String) -> Unit,
    onClearAll: () -> Unit,
    onToggleDoNotDisturb: () -> Unit,
    onPostCustomAlert: (title: String, message: String, isPersistent: Boolean) -> Unit,
    onActionClick: (NotificationItem) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isComposeAlertOpen by remember { mutableStateOf(false) }
    var newAlertTitle by remember { mutableStateOf("") }
    var newAlertMessage by remember { mutableStateOf("") }
    var isNewAlertPersistent by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 400.dp)
                .fillMaxWidth(0.92f)
                .testTag("notification_center_overlay")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xF2101A2C),
                                Color(0xF80B1220)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0x3360CDFF),
                        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // HEADER ROW
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = Color(0xFF60CDFF),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Notifications",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = Color.White
                            )

                            if (notifications.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color(0xFF0078D4))
                                        .padding(horizontal = 7.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${notifications.size}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (notifications.any { !it.isPersistent }) {
                                TextButton(
                                    onClick = onClearAll,
                                    modifier = Modifier.testTag("clear_all_notifications_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ClearAll,
                                        contentDescription = null,
                                        tint = Color(0xAAFFFFFF),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Clear all",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xAAFFFFFF)
                                    )
                                }
                            }

                            IconButton(
                                onClick = onClose,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("close_notification_center_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Notifications",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // FOCUS ASSIST / DO NOT DISTURB TOGGLE
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x18FFFFFF))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isDoNotDisturb) Icons.Default.NotificationsOff else Icons.Default.DoNotDisturb,
                                contentDescription = null,
                                tint = if (isDoNotDisturb) Color(0xFFFF6B6B) else Color(0xFF60CDFF),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Focus Assist / DND",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.White
                                )
                                Text(
                                    text = if (isDoNotDisturb) "Priority alerts only" else "All alerts active",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0x88FFFFFF)
                                )
                            }
                        }

                        Switch(
                            checked = isDoNotDisturb,
                            onCheckedChange = { onToggleDoNotDisturb() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF0078D4)
                            ),
                            modifier = Modifier.testTag("dnd_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // SEND TEST ALERT TRIGGER ROW
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x120078D4))
                            .border(1.dp, Color(0x3360CDFF), RoundedCornerShape(8.dp))
                            .clickable { isComposeAlertOpen = !isComposeAlertOpen }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AddAlert,
                                contentDescription = null,
                                tint = Color(0xFF60CDFF),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Simulate System Alert",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFF60CDFF)
                            )
                        }

                        Text(
                            text = if (isComposeAlertOpen) "Hide" else "Create",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0x88FFFFFF)
                        )
                    }

                    // COMPOSE TEST ALERT CARD
                    AnimatedVisibility(visible = isComposeAlertOpen) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x22000000))
                                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            OutlinedTextField(
                                value = newAlertTitle,
                                onValueChange = { newAlertTitle = it },
                                placeholder = { Text("Alert Title (e.g. Device Connected)", fontSize = 11.sp) },
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFF0078D4),
                                    unfocusedBorderColor = Color(0x33FFFFFF)
                                ),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = newAlertMessage,
                                onValueChange = { newAlertMessage = it },
                                placeholder = { Text("Alert message content...", fontSize = 11.sp) },
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFF0078D4),
                                    unfocusedBorderColor = Color(0x33FFFFFF)
                                ),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Checkbox(
                                    checked = isNewAlertPersistent,
                                    onCheckedChange = { isNewAlertPersistent = it },
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0078D4))
                                )
                                Text("Persistent Alert", style = MaterialTheme.typography.labelSmall, color = Color.White)

                                Spacer(modifier = Modifier.weight(1f))

                                Button(
                                    onClick = {
                                        if (newAlertTitle.isNotBlank() || newAlertMessage.isNotBlank()) {
                                            onPostCustomAlert(
                                                newAlertTitle.ifBlank { "System Alert" },
                                                newAlertMessage.ifBlank { "Alert dispatched successfully." },
                                                isNewAlertPersistent
                                            )
                                            newAlertTitle = ""
                                            newAlertMessage = ""
                                            isComposeAlertOpen = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0078D4)),
                                    modifier = Modifier.height(32.dp).testTag("send_test_alert_btn")
                                ) {
                                    Text("Post Alert", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    HorizontalDivider(
                        color = Color(0x22FFFFFF),
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    // NOTIFICATIONS LIST
                    if (notifications.isEmpty()) {
                        // Empty State
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = Color(0x44FFFFFF),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No new notifications",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = Color(0x88FFFFFF)
                            )
                            Text(
                                text = "System alerts and persistent messages will appear here",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0x55FFFFFF),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        // Priority Persistent Messages & System Alerts
                        val persistentList = notifications.filter { it.isPersistent }
                        val standardList = notifications.filter { !it.isPersistent }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            if (persistentList.isNotEmpty()) {
                                item {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 2.dp, top = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PushPin,
                                            contentDescription = null,
                                            tint = Color(0xFF60CDFF),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "PERSISTENT SYSTEM MESSAGES",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.8.sp,
                                                color = Color(0xFF60CDFF)
                                            )
                                        )
                                    }
                                }

                                items(persistentList, key = { it.id }) { item ->
                                    NotificationCard(
                                        item = item,
                                        onDismiss = { onDismissNotification(item.id) },
                                        onAction = { onActionClick(item) }
                                    )
                                }
                            }

                            if (standardList.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "RECENT ALERTS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.8.sp,
                                            color = Color(0x88FFFFFF)
                                        ),
                                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                                    )
                                }

                                items(standardList, key = { it.id }) { item ->
                                    NotificationCard(
                                        item = item,
                                        onDismiss = { onDismissNotification(item.id) },
                                        onAction = { onActionClick(item) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Windows Notification Card item.
 */
@Composable
fun NotificationCard(
    item: NotificationItem,
    onDismiss: () -> Unit,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0x20FFFFFF),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (item.isPersistent) Color(0x4460CDFF) else Color(0x22FFFFFF)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("notification_card_${item.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(item.colorHex))
                    ) {
                        Icon(
                            imageVector = IconHelper.getIcon(item.iconKey),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.timestampFormatted,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Color(0x88FFFFFF)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(24.dp)
                            .testTag("dismiss_notification_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Message Body
            Text(
                text = item.message,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                color = Color(0xDDFFFFFF)
            )

            // Optional Action Button
            if (item.actionLabel != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        onClick = onAction,
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0x260078D4),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x5560CDFF)),
                        modifier = Modifier.testTag("action_btn_${item.id}")
                    ) {
                        Text(
                            text = item.actionLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF60CDFF)
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
