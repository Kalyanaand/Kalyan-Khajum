package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ShortcutItem

/**
 * Desktop Icon Grid:
 * Displays customizable desktop shortcuts in classic desktop icon layout.
 * Supports clicking to launch, and long-press for context menu.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DesktopWorkspace(
    desktopShortcuts: List<ShortcutItem>,
    onShortcutClick: (ShortcutItem) -> Unit,
    onEditShortcut: (ShortcutItem) -> Unit,
    onDeleteShortcut: (String) -> Unit,
    onTogglePinTaskbar: (String) -> Unit,
    onNewShortcutClick: () -> Unit,
    onOpenWallpaperPicker: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isDesktopContextMenuOpen by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .combinedClickable(
                onClick = { /* clicks on empty space deselect */ },
                onLongClick = { isDesktopContextMenuOpen = true }
            )
            .testTag("desktop_workspace")
    ) {
        // Desktop Grid
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 84.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(desktopShortcuts, key = { it.id }) { shortcut ->
                DesktopShortcutItem(
                    shortcut = shortcut,
                    onClick = { onShortcutClick(shortcut) },
                    onEdit = { onEditShortcut(shortcut) },
                    onDelete = { onDeleteShortcut(shortcut.id) },
                    onTogglePin = { onTogglePinTaskbar(shortcut.id) }
                )
            }
        }

        // Desktop Right-Click / Long-Press Context Menu
        DropdownMenu(
            expanded = isDesktopContextMenuOpen,
            onDismissRequest = { isDesktopContextMenuOpen = false },
            modifier = Modifier
                .background(Color(0xF0182234))
                .border(1.dp, Color(0x3360CDFF), RoundedCornerShape(8.dp))
        ) {
            DropdownMenuItem(
                text = { Text("New Shortcut", color = Color.White) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color(0xFF60CDFF)
                    )
                },
                onClick = {
                    isDesktopContextMenuOpen = false
                    onNewShortcutClick()
                }
            )

            DropdownMenuItem(
                text = { Text("Personalize Wallpaper", color = Color.White) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = Color(0xFFFFB900)
                    )
                },
                onClick = {
                    isDesktopContextMenuOpen = false
                    onOpenWallpaperPicker()
                }
            )

            HorizontalDivider(color = Color(0x22FFFFFF))

            DropdownMenuItem(
                text = { Text("System Settings", color = Color.White) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = Color(0xCCFFFFFF)
                    )
                },
                onClick = {
                    isDesktopContextMenuOpen = false
                    onOpenSettings()
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DesktopShortcutItem(
    shortcut: ShortcutItem,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTogglePin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuOpen by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = { isMenuOpen = true }
                )
                .padding(8.dp)
                .testTag("desktop_shortcut_${shortcut.id}")
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(shortcut.colorHex))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = IconHelper.getIcon(shortcut.iconKey),
                    contentDescription = shortcut.title,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = shortcut.title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black,
                        blurRadius = 4f
                    )
                ),
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Context Menu for individual shortcut
        DropdownMenu(
            expanded = isMenuOpen,
            onDismissRequest = { isMenuOpen = false },
            modifier = Modifier
                .background(Color(0xF0182234))
                .border(1.dp, Color(0x3360CDFF), RoundedCornerShape(8.dp))
        ) {
            DropdownMenuItem(
                text = { Text("Open", color = Color.White) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Launch,
                        contentDescription = null,
                        tint = Color(0xFF60CDFF)
                    )
                },
                onClick = {
                    isMenuOpen = false
                    onClick()
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        if (shortcut.isPinnedToTaskbar) "Unpin from Taskbar" else "Pin to Taskbar",
                        color = Color.White
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = null,
                        tint = if (shortcut.isPinnedToTaskbar) Color(0xFFFF6B6B) else Color(0xFF60CDFF)
                    )
                },
                onClick = {
                    isMenuOpen = false
                    onTogglePin()
                }
            )

            DropdownMenuItem(
                text = { Text("Edit Shortcut", color = Color.White) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color(0xFFFFB900)
                    )
                },
                onClick = {
                    isMenuOpen = false
                    onEdit()
                }
            )

            HorizontalDivider(color = Color(0x22FFFFFF))

            DropdownMenuItem(
                text = { Text("Delete Shortcut", color = Color(0xFFFF6B6B)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFFF6B6B)
                    )
                },
                onClick = {
                    isMenuOpen = false
                    onDelete()
                }
            )
        }
    }
}
