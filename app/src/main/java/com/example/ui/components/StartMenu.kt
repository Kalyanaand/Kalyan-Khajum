package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InstalledAppItem
import com.example.data.model.ShortcutItem
import com.example.ui.viewmodel.MathResult
import com.example.ui.viewmodel.SearchFilter
import com.example.ui.viewmodel.StartMenuTab

@Composable
fun WindowsStartMenu(
    isOpen: Boolean,
    tab: StartMenuTab,
    onTabChange: (StartMenuTab) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    searchFilter: SearchFilter,
    onSearchFilterChange: (SearchFilter) -> Unit,
    mathResult: MathResult,
    filteredApps: List<InstalledAppItem>,
    filteredShortcuts: List<ShortcutItem>,
    allApps: List<InstalledAppItem>,
    pinnedShortcuts: List<ShortcutItem>,
    onShortcutClick: (ShortcutItem) -> Unit,
    onAppClick: (InstalledAppItem) -> Unit,
    onWebSearch: (query: String, engine: String) -> Unit,
    onAddNewShortcutClick: () -> Unit,
    onOpenWallpaperPicker: () -> Unit,
    onOpenSettings: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPowerMenuOpen by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInVertically(initialOffsetY = { it / 3 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it / 3 }) + fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth(0.94f)
                .heightIn(max = 580.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xF2101B2E),
                            Color(0xF80A1220)
                        )
                    )
                )
                .border(1.dp, Color(0x3360CDFF), RoundedCornerShape(16.dp))
                .padding(16.dp)
                .testTag("start_menu_panel")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Search Bar
                QuickSearchContent(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    searchFilter = searchFilter,
                    onFilterChange = onSearchFilterChange,
                    mathResult = mathResult,
                    filteredApps = filteredApps,
                    filteredShortcuts = filteredShortcuts,
                    onAppClick = onAppClick,
                    onShortcutClick = onShortcutClick,
                    onWebSearch = onWebSearch
                )

                // If searching, QuickSearchContent displays the results.
                // If not searching, display Pinned vs All Apps tabs and grid!
                if (searchQuery.isBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // Tabs: Pinned vs All Apps
                    TabRow(
                        selectedTabIndex = if (tab == StartMenuTab.PINNED) 0 else 1,
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFF60CDFF),
                        indicator = { tabPositions ->
                            val currentTab = if (tab == StartMenuTab.PINNED) 0 else 1
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[currentTab]),
                                color = Color(0xFF60CDFF)
                            )
                        },
                        divider = {
                            HorizontalDivider(color = Color(0x22FFFFFF))
                        }
                    ) {
                        Tab(
                            selected = tab == StartMenuTab.PINNED,
                            onClick = { onTabChange(StartMenuTab.PINNED) },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PushPin,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pinned", fontWeight = FontWeight.SemiBold)
                                }
                            },
                            modifier = Modifier.testTag("start_tab_pinned")
                        )
                        Tab(
                            selected = tab == StartMenuTab.ALL_APPS,
                            onClick = { onTabChange(StartMenuTab.ALL_APPS) },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Apps,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("All Apps (${allApps.size})", fontWeight = FontWeight.SemiBold)
                                }
                            },
                            modifier = Modifier.testTag("start_tab_all_apps")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tab Body
                    Box(modifier = Modifier.weight(1f)) {
                        when (tab) {
                            StartMenuTab.PINNED -> {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "PINNED TILES",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                letterSpacing = 1.sp,
                                                color = Color(0x88FFFFFF)
                                            )
                                        )

                                        // Add custom shortcut button
                                        Surface(
                                            onClick = onAddNewShortcutClick,
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0x220078D4),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4460CDFF)),
                                            modifier = Modifier.testTag("add_shortcut_start_button")
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Add shortcut",
                                                    tint = Color(0xFF60CDFF),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "New Shortcut",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    LazyVerticalGrid(
                                        columns = GridCells.Adaptive(minSize = 74.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        items(pinnedShortcuts, key = { it.id }) { shortcut ->
                                            StartMenuTile(
                                                shortcut = shortcut,
                                                onClick = { onShortcutClick(shortcut) }
                                            )
                                        }
                                    }
                                }
                            }

                            StartMenuTab.ALL_APPS -> {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(allApps, key = { it.packageName + it.activityName }) { app ->
                                        AppSearchResultRow(
                                            app = app,
                                            onClick = { onAppClick(app) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(
                    color = Color(0x22FFFFFF),
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                // Bottom User Profile & Power Options Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // User Profile
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {}
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0078D4))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User Profile",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Administrator",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White
                            )
                            Text(
                                text = "Desktop Workspace",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0x88FFFFFF)
                            )
                        }
                    }

                    // System Controls: Wallpaper, Settings, Power
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onOpenWallpaperPicker,
                            modifier = Modifier.testTag("wallpaper_picker_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Change Wallpaper",
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier.testTag("open_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Box {
                            IconButton(
                                onClick = { isPowerMenuOpen = true },
                                modifier = Modifier.testTag("power_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PowerSettingsNew,
                                    contentDescription = "Power Options",
                                    tint = Color(0xFFFF6B6B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = isPowerMenuOpen,
                                onDismissRequest = { isPowerMenuOpen = false },
                                modifier = Modifier
                                    .background(Color(0xFF1E293B))
                                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Restart Launcher", color = Color.White) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            tint = Color(0xFF60CDFF)
                                        )
                                    },
                                    onClick = {
                                        isPowerMenuOpen = false
                                        onClose()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Lock Screen", color = Color.White) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = Color(0xFFFFB900)
                                        )
                                    },
                                    onClick = {
                                        isPowerMenuOpen = false
                                        onClose()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Windows Start Menu App/Shortcut Tile.
 */
@Composable
fun StartMenuTile(
    shortcut: ShortcutItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x18FFFFFF))
            .clickable(onClick = onClick)
            .padding(8.dp)
            .testTag("start_tile_${shortcut.id}")
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(shortcut.colorHex))
        ) {
            Icon(
                imageVector = IconHelper.getIcon(shortcut.iconKey),
                contentDescription = shortcut.title,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = shortcut.title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            ),
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
