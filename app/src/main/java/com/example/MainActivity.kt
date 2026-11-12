package com.example

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ShortcutItem
import com.example.data.model.ShortcutType
import com.example.data.model.WindowData
import com.example.ui.components.AddEditShortcutDialog
import com.example.ui.components.DesktopWindowItem
import com.example.ui.components.DesktopWorkspace
import com.example.ui.components.PersonalizationDialog
import com.example.ui.components.WallpaperPickerDialog
import com.example.ui.components.WindowsActionCenterFlyout
import com.example.ui.components.WindowsCalculatorMiniApp
import com.example.ui.components.WindowsCalendarFlyout
import com.example.ui.components.WindowsNotificationCenterOverlay
import com.example.ui.components.WindowsStartMenu
import com.example.ui.components.WindowsStickyNotesMiniApp
import com.example.ui.components.WindowsTaskbar
import com.example.ui.components.WindowsTerminalMiniApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val skinColor by viewModel.skinColor.collectAsStateWithLifecycle()
            val fontOption by viewModel.fontOption.collectAsStateWithLifecycle()
            val fontSizeScale by viewModel.fontSizeScale.collectAsStateWithLifecycle()

            MyApplicationTheme(
                themeMode = themeMode,
                skinColor = skinColor,
                fontOption = fontOption,
                fontSizeScale = fontSizeScale
            ) {
                WinLauncherApp(
                    viewModel = viewModel,
                    onOpenAndroidSettings = {
                        startActivity(Intent(Settings.ACTION_SETTINGS))
                    }
                )
            }
        }
    }
}

@Composable
fun WinLauncherApp(
    viewModel: LauncherViewModel,
    onOpenAndroidSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clockState by viewModel.clockState.collectAsStateWithLifecycle()
    val shortcuts by viewModel.shortcuts.collectAsStateWithLifecycle()
    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
    val wallpaperTheme by viewModel.wallpaperTheme.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchFilter by viewModel.searchFilter.collectAsStateWithLifecycle()
    val mathResult by viewModel.mathResult.collectAsStateWithLifecycle()
    val filteredApps by viewModel.filteredApps.collectAsStateWithLifecycle()
    val filteredShortcuts by viewModel.filteredShortcuts.collectAsStateWithLifecycle()

    val isStartMenuOpen by viewModel.isStartMenuOpen.collectAsStateWithLifecycle()
    val startMenuTab by viewModel.startMenuTab.collectAsStateWithLifecycle()
    val isCalendarOpen by viewModel.isCalendarFlyoutOpen.collectAsStateWithLifecycle()
    val isActionCenterOpen by viewModel.isActionCenterOpen.collectAsStateWithLifecycle()
    val isAddShortcutDialogOpen by viewModel.isAddShortcutDialogOpen.collectAsStateWithLifecycle()
    val editingShortcut by viewModel.editingShortcut.collectAsStateWithLifecycle()
    val isWallpaperPickerOpen by viewModel.isWallpaperPickerOpen.collectAsStateWithLifecycle()
    val isPersonalizationDialogOpen by viewModel.isPersonalizationDialogOpen.collectAsStateWithLifecycle()

    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val skinColor by viewModel.skinColor.collectAsStateWithLifecycle()
    val fontOption by viewModel.fontOption.collectAsStateWithLifecycle()
    val fontSizeScale by viewModel.fontSizeScale.collectAsStateWithLifecycle()

    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val isNotificationCenterOpen by viewModel.isNotificationCenterOpen.collectAsStateWithLifecycle()
    val isDoNotDisturb by viewModel.isDoNotDisturb.collectAsStateWithLifecycle()

    val openWindows by viewModel.openWindows.collectAsStateWithLifecycle()
    val activeWindowId by viewModel.activeWindowId.collectAsStateWithLifecycle()
    val activeMiniApp by viewModel.activeMiniApp.collectAsStateWithLifecycle()
    val quickNotesText by viewModel.quickNotesText.collectAsStateWithLifecycle()
    val batteryPercentage by viewModel.batteryPercentage.collectAsStateWithLifecycle()

    val desktopShortcuts = remember(shortcuts) {
        shortcuts.filter { it.isPinnedToDesktop }
    }
    val pinnedTaskbarShortcuts = remember(shortcuts) {
        shortcuts.filter { it.isPinnedToTaskbar }
    }

    // Handle back button to dismiss open Windows menus, notification center, or active windows
    BackHandler(enabled = isPersonalizationDialogOpen || isWallpaperPickerOpen || isStartMenuOpen || isCalendarOpen || isActionCenterOpen || isNotificationCenterOpen || openWindows.any { !it.isMinimized } || activeMiniApp != null) {
        when {
            isPersonalizationDialogOpen || isWallpaperPickerOpen -> viewModel.closePersonalizationDialog()
            isNotificationCenterOpen -> viewModel.closeNotificationCenter()
            isStartMenuOpen -> viewModel.closeStartMenu()
            isCalendarOpen -> viewModel.closeCalendarFlyout()
            isActionCenterOpen -> viewModel.closeActionCenter()
            activeMiniApp != null -> viewModel.closeMiniApp()
            activeWindowId != null -> viewModel.closeWindow(activeWindowId!!)
            else -> {
                openWindows.filterNot { it.isMinimized }.maxByOrNull { it.zIndex }?.let {
                    viewModel.closeWindow(it.id)
                }
            }
        }
    }

    val dismissAllPopups = {
        if (isNotificationCenterOpen) viewModel.closeNotificationCenter()
        if (isStartMenuOpen) viewModel.closeStartMenu()
        if (isCalendarOpen) viewModel.closeCalendarFlyout()
        if (isActionCenterOpen) viewModel.closeActionCenter()
    }

    // Windows Desktop Environment Screen
    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                // Windows Bloom / Aurora Desktop Wallpaper Background
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(wallpaperTheme.accentColor).copy(alpha = 0.45f),
                            Color(wallpaperTheme.startColor),
                            Color(wallpaperTheme.endColor)
                        ),
                        center = Offset(size.width * 0.5f, size.height * 0.4f),
                        radius = size.maxDimension * 0.85f
                    )
                )

                // Atmospheric Windows Bloom decorative glow lines
                drawCircle(
                    color = Color(wallpaperTheme.accentColor).copy(alpha = 0.12f),
                    center = Offset(size.width * 0.75f, size.height * 0.25f),
                    radius = size.width * 0.4f
                )
            }
    ) {
        // Main Desktop Area
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                // Windows 11 Bottom Taskbar with Live Second Digital Clock
                WindowsTaskbar(
                    clockState = clockState,
                    pinnedShortcuts = pinnedTaskbarShortcuts,
                    openWindows = openWindows,
                    activeWindowId = activeWindowId,
                    isStartMenuOpen = isStartMenuOpen,
                    isCalendarOpen = isCalendarOpen,
                    isActionCenterOpen = isActionCenterOpen,
                    isNotificationCenterOpen = isNotificationCenterOpen,
                    notificationCount = notifications.size,
                    activeMiniApp = activeMiniApp,
                    batteryPercentage = batteryPercentage,
                    onStartButtonClick = { viewModel.toggleStartMenu() },
                    onSearchClick = {
                        viewModel.toggleStartMenu()
                    },
                    onShortcutClick = { shortcut ->
                        viewModel.launchShortcut(shortcut)
                    },
                    onWindowTaskbarClick = { win ->
                        if (win.isMinimized) {
                            viewModel.restoreWindow(win.id)
                        } else if (win.id == activeWindowId) {
                            viewModel.minimizeWindow(win.id)
                        } else {
                            viewModel.bringWindowToFront(win.id)
                        }
                    },
                    onClockClick = { viewModel.toggleCalendarFlyout() },
                    onActionCenterClick = { viewModel.toggleActionCenter() },
                    onNotificationCenterClick = { viewModel.toggleNotificationCenter() },
                    onShowDesktopClick = { viewModel.minimizeAllWindows() }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        dismissAllPopups()
                    }
            ) {
                // Desktop Workspace & Shortcut Icons
                DesktopWorkspace(
                    desktopShortcuts = desktopShortcuts,
                    onShortcutClick = { shortcut ->
                        dismissAllPopups()
                        viewModel.launchShortcut(shortcut)
                    },
                    onEditShortcut = { shortcut ->
                        dismissAllPopups()
                        viewModel.openAddShortcutDialog(shortcut)
                    },
                    onDeleteShortcut = { id ->
                        viewModel.deleteShortcut(id)
                    },
                    onTogglePinTaskbar = { id ->
                        viewModel.togglePinToTaskbar(id)
                    },
                    onNewShortcutClick = {
                        dismissAllPopups()
                        viewModel.openAddShortcutDialog(null)
                    },
                    onOpenWallpaperPicker = {
                        dismissAllPopups()
                        viewModel.openPersonalizationDialog()
                    },
                    onOpenSettings = {
                        dismissAllPopups()
                        onOpenAndroidSettings()
                    },
                    modifier = Modifier.statusBarsPadding()
                )

                // OPEN DRAGGABLE & RESIZABLE DESKTOP WINDOWS
                openWindows.forEach { window ->
                    DesktopWindowItem(
                        window = window,
                        isActive = window.id == activeWindowId,
                        onFocus = { viewModel.bringWindowToFront(window.id) },
                        onDrag = { dx, dy -> viewModel.updateWindowPosition(window.id, dx, dy) },
                        onResize = { dw, dh -> viewModel.updateWindowSize(window.id, dw, dh) },
                        onMinimize = { viewModel.minimizeWindow(window.id) },
                        onToggleMaximize = { viewModel.toggleMaximizeWindow(window.id) },
                        onClose = { viewModel.closeWindow(window.id) },
                        onLaunchExternal = {
                            if (window.type == ShortcutType.APP) {
                                viewModel.launchAppExternal(window.target)
                            } else {
                                viewModel.launchShortcutExternal(
                                    ShortcutItem(
                                        id = window.id,
                                        title = window.title,
                                        type = window.type,
                                        target = window.target
                                    )
                                )
                            }
                        },
                        quickNotesText = quickNotesText,
                        onUpdateQuickNotes = { viewModel.updateQuickNotes(it) },
                        onOpenAndroidSettings = onOpenAndroidSettings
                    )
                }

                // FLOATING START MENU (above Start button)
                WindowsStartMenu(
                    isOpen = isStartMenuOpen,
                    tab = startMenuTab,
                    onTabChange = { viewModel.setStartMenuTab(it) },
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    searchFilter = searchFilter,
                    onSearchFilterChange = { viewModel.setSearchFilter(it) },
                    mathResult = mathResult,
                    filteredApps = filteredApps,
                    filteredShortcuts = filteredShortcuts,
                    allApps = installedApps,
                    pinnedShortcuts = shortcuts.filter { it.isPinnedToTaskbar || it.isPinnedToDesktop },
                    onShortcutClick = { shortcut ->
                        viewModel.launchShortcut(shortcut)
                    },
                    onAppClick = { app ->
                        viewModel.launchApp(app)
                    },
                    onWebSearch = { query, engine ->
                        viewModel.executeWebSearch(query, engine)
                    },
                    onAddNewShortcutClick = {
                        viewModel.closeStartMenu()
                        viewModel.openAddShortcutDialog(null)
                    },
                    onOpenWallpaperPicker = {
                        viewModel.closeStartMenu()
                        viewModel.openPersonalizationDialog()
                    },
                    onOpenSettings = {
                        viewModel.closeStartMenu()
                        onOpenAndroidSettings()
                    },
                    onClose = { viewModel.closeStartMenu() },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 12.dp, bottom = 8.dp)
                )

                // FLOATING CALENDAR & BIG CLOCK FLYOUT (above taskbar clock)
                WindowsCalendarFlyout(
                    isOpen = isCalendarOpen,
                    clockState = clockState,
                    onClose = { viewModel.closeCalendarFlyout() },
                    onToggleSeconds = { viewModel.toggleShowSecondsInTaskbar() },
                    onToggle24Hour = { viewModel.toggle24HourFormat() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 8.dp)
                )

                // FLOATING ACTION CENTER FLYOUT (above system tray)
                WindowsActionCenterFlyout(
                    isOpen = isActionCenterOpen,
                    batteryPercentage = batteryPercentage,
                    onClose = { viewModel.closeActionCenter() },
                    onOpenSettings = {
                        viewModel.closeActionCenter()
                        onOpenAndroidSettings()
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 8.dp)
                )

                // NOTIFICATION CENTER OVERLAY (slides in from right edge of screen)
                WindowsNotificationCenterOverlay(
                    isOpen = isNotificationCenterOpen,
                    notifications = notifications,
                    isDoNotDisturb = isDoNotDisturb,
                    onDismissNotification = { id -> viewModel.dismissNotification(id) },
                    onClearAll = { viewModel.clearAllNonPersistentNotifications() },
                    onToggleDoNotDisturb = { viewModel.toggleDoNotDisturb() },
                    onPostCustomAlert = { title, msg, isPersist ->
                        viewModel.postCustomAlert(title, msg, isPersist)
                    },
                    onActionClick = { notif ->
                        viewModel.closeNotificationCenter()
                        when (notif.actionTarget) {
                            "settings" -> onOpenAndroidSettings()
                            "new_shortcut" -> viewModel.openAddShortcutDialog(null)
                            else -> onOpenAndroidSettings()
                        }
                    },
                    onClose = { viewModel.closeNotificationCenter() },
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }

        // DIALOGS
        if (isAddShortcutDialogOpen) {
            AddEditShortcutDialog(
                itemToEdit = editingShortcut,
                installedApps = installedApps,
                onDismiss = { viewModel.closeAddShortcutDialog() },
                onSave = { id, title, type, target, iconKey, colorHex, pinToTaskbar, pinToDesktop ->
                    viewModel.saveOrUpdateShortcut(
                        id,
                        title,
                        type,
                        target,
                        iconKey,
                        colorHex,
                        pinToTaskbar,
                        pinToDesktop
                    )
                }
            )
        }

        if (isPersonalizationDialogOpen || isWallpaperPickerOpen) {
            PersonalizationDialog(
                themeMode = themeMode,
                skinColor = skinColor,
                wallpaperTheme = wallpaperTheme,
                fontOption = fontOption,
                fontSizeScale = fontSizeScale,
                onThemeModeChange = { viewModel.setThemeMode(it) },
                onSkinColorChange = { viewModel.setSkinColor(it) },
                onWallpaperChange = { viewModel.selectWallpaper(it) },
                onFontOptionChange = { viewModel.setFontOption(it) },
                onFontSizeScaleChange = { viewModel.setFontSizeScale(it) },
                onDismiss = { viewModel.closePersonalizationDialog() }
            )
        }
    }
}
