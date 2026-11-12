package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

object IconHelper {
    fun getIcon(key: String): ImageVector {
        return when (key.lowercase()) {
            "globe", "browser", "web" -> Icons.Default.Language
            "code", "github" -> Icons.Default.Code
            "folder", "files" -> Icons.Default.Folder
            "calc", "calculator" -> Icons.Default.Calculate
            "note", "notes", "sticky" -> Icons.Default.Note
            "terminal", "cmd", "cli" -> Icons.Default.Terminal
            "play", "video", "youtube" -> Icons.Default.PlayArrow
            "music" -> Icons.Default.MusicNote
            "game", "games" -> Icons.Default.SportsEsports
            "settings" -> Icons.Default.Settings
            "bookmark" -> Icons.Default.Bookmark
            "computer", "pc" -> Icons.Default.Computer
            "apps" -> Icons.Default.Apps
            "bell", "notification", "notifications" -> Icons.Default.Notifications
            "security", "shield" -> Icons.Default.Security
            "battery" -> Icons.Default.BatteryFull
            "warning", "alert" -> Icons.Default.Warning
            else -> Icons.Default.Star
        }
    }

    val availableIcons = listOf(
        "globe" to "Web Browser",
        "calc" to "Calculator",
        "note" to "Sticky Notes",
        "code" to "Development / Code",
        "folder" to "Folder / Files",
        "terminal" to "Terminal / Shell",
        "play" to "Media / YouTube",
        "music" to "Music Player",
        "game" to "Games",
        "settings" to "Settings",
        "star" to "Favorite",
        "bookmark" to "Bookmark"
    )

    val availableColors = listOf(
        0xFF0078D4 to "Windows Blue",
        0xFF107C41 to "Office Green",
        0xFFFFB900 to "Amber Gold",
        0xFFE81123 to "Crimson Red",
        0xFF7B2CBF to "Neon Purple",
        0xFF00A4EF to "Cyan Sky",
        0xFFFF6B6B to "Coral Pink",
        0xFF24292E to "Midnight Black"
    )
}
