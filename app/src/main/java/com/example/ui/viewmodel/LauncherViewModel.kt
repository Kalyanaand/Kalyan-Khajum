package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.BatteryManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppFontFamilyOption
import com.example.data.model.ClockState
import com.example.data.model.FontSizeScale
import com.example.data.model.InstalledAppItem
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.ShortcutItem
import com.example.data.model.ShortcutType
import com.example.data.model.SkinColor
import com.example.data.model.ThemeMode
import com.example.data.model.WallpaperTheme
import com.example.data.model.WindowData
import com.example.data.repository.LauncherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class StartMenuTab {
    PINNED,
    ALL_APPS
}

enum class SearchFilter {
    ALL,
    APPS,
    SHORTCUTS,
    WEB
}

data class MathResult(
    val expression: String,
    val result: String,
    val isValid: Boolean
)

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LauncherRepository(application.applicationContext)

    // Clock state - updates every second
    private val _clockState = MutableStateFlow(ClockState())
    val clockState: StateFlow<ClockState> = _clockState.asStateFlow()

    // Apps & Shortcuts
    private val _installedApps = MutableStateFlow<List<InstalledAppItem>>(emptyList())
    val installedApps: StateFlow<List<InstalledAppItem>> = _installedApps.asStateFlow()

    private val _shortcuts = MutableStateFlow<List<ShortcutItem>>(emptyList())
    val shortcuts: StateFlow<List<ShortcutItem>> = _shortcuts.asStateFlow()

    // Personalization & Theming
    private val _themeMode = MutableStateFlow(ThemeMode.DARK)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _skinColor = MutableStateFlow(SkinColor.WINDOWS_BLUE)
    val skinColor: StateFlow<SkinColor> = _skinColor.asStateFlow()

    private val _fontOption = MutableStateFlow(AppFontFamilyOption.SYSTEM_DEFAULT)
    val fontOption: StateFlow<AppFontFamilyOption> = _fontOption.asStateFlow()

    private val _fontSizeScale = MutableStateFlow(FontSizeScale.DEFAULT)
    val fontSizeScale: StateFlow<FontSizeScale> = _fontSizeScale.asStateFlow()

    private val _isPersonalizationDialogOpen = MutableStateFlow(false)
    val isPersonalizationDialogOpen: StateFlow<Boolean> = _isPersonalizationDialogOpen.asStateFlow()

    // Wallpaper
    private val _wallpaperTheme = MutableStateFlow(WallpaperTheme.WINDOWS_BLOOM)
    val wallpaperTheme: StateFlow<WallpaperTheme> = _wallpaperTheme.asStateFlow()

    // Search query & filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchFilter = MutableStateFlow(SearchFilter.ALL)
    val searchFilter: StateFlow<SearchFilter> = _searchFilter.asStateFlow()

    // UI Flyouts and Menus
    private val _isStartMenuOpen = MutableStateFlow(false)
    val isStartMenuOpen: StateFlow<Boolean> = _isStartMenuOpen.asStateFlow()

    private val _startMenuTab = MutableStateFlow(StartMenuTab.PINNED)
    val startMenuTab: StateFlow<StartMenuTab> = _startMenuTab.asStateFlow()

    private val _isCalendarFlyoutOpen = MutableStateFlow(false)
    val isCalendarFlyoutOpen: StateFlow<Boolean> = _isCalendarFlyoutOpen.asStateFlow()

    private val _isActionCenterOpen = MutableStateFlow(false)
    val isActionCenterOpen: StateFlow<Boolean> = _isActionCenterOpen.asStateFlow()

    private val _isAddShortcutDialogOpen = MutableStateFlow(false)
    val isAddShortcutDialogOpen: StateFlow<Boolean> = _isAddShortcutDialogOpen.asStateFlow()

    private val _editingShortcut = MutableStateFlow<ShortcutItem?>(null)
    val editingShortcut: StateFlow<ShortcutItem?> = _editingShortcut.asStateFlow()

    private val _isWallpaperPickerOpen = MutableStateFlow(false)
    val isWallpaperPickerOpen: StateFlow<Boolean> = _isWallpaperPickerOpen.asStateFlow()

    private val _activeMiniApp = MutableStateFlow<String?>(null)
    val activeMiniApp: StateFlow<String?> = _activeMiniApp.asStateFlow()

    // Window Management System
    private val _openWindows = MutableStateFlow<List<WindowData>>(emptyList())
    val openWindows: StateFlow<List<WindowData>> = _openWindows.asStateFlow()

    private val _activeWindowId = MutableStateFlow<String?>(null)
    val activeWindowId: StateFlow<String?> = _activeWindowId.asStateFlow()

    private var nextZIndex = 2f

    // Notification Center System
    private val _isNotificationCenterOpen = MutableStateFlow(false)
    val isNotificationCenterOpen: StateFlow<Boolean> = _isNotificationCenterOpen.asStateFlow()

    private val _isDoNotDisturb = MutableStateFlow(false)
    val isDoNotDisturb: StateFlow<Boolean> = _isDoNotDisturb.asStateFlow()

    private val _notifications = MutableStateFlow(getDefaultNotifications())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Quick notes text
    private val _quickNotesText = MutableStateFlow(
        "Welcome to Windows Launcher for Android!\n- Click any shortcut to launch\n- Open Start menu for all apps\n- Click the taskbar clock for full calendar & seconds view\n- Quick Search calculates math & searches web"
    )
    val quickNotesText: StateFlow<String> = _quickNotesText.asStateFlow()

    // Battery & Volume
    private val _batteryPercentage = MutableStateFlow(85)
    val batteryPercentage: StateFlow<Int> = _batteryPercentage.asStateFlow()

    // Search results combine
    val filteredApps = combine(_installedApps, _searchQuery) { apps, query ->
        if (query.isBlank()) apps else {
            val q = query.trim().lowercase()
            apps.filter { it.label.lowercase().contains(q) || it.packageName.lowercase().contains(q) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredShortcuts = combine(_shortcuts, _searchQuery) { scs, query ->
        if (query.isBlank()) scs else {
            val q = query.trim().lowercase()
            scs.filter { it.title.lowercase().contains(q) || it.target.lowercase().contains(q) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mathResult = combine(_searchQuery) { queryArr ->
        val q = queryArr[0]
        evaluateMath(q)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MathResult("", "", false))

    init {
        loadSettingsAndData()
        startRealTimeClock()
        refreshBattery()
    }

    private fun loadSettingsAndData() {
        _shortcuts.value = repository.loadShortcuts()
        _wallpaperTheme.value = repository.getWallpaperTheme()
        _themeMode.value = repository.getThemeMode()
        _skinColor.value = repository.getSkinColor()
        _fontOption.value = repository.getFontOption()
        _fontSizeScale.value = repository.getFontSizeScale()
        viewModelScope.launch {
            _installedApps.value = repository.getInstalledApps()
        }
    }

    /**
     * Starts the real-time digital clock that updates every single second (1000ms).
     * Calculates 12h/24h strings, seconds, full date, calendar day/month/year.
     */
    private fun startRealTimeClock() {
        viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                updateClock()
                // Sleep until roughly the start of the next second
                val now = System.currentTimeMillis()
                val sleepTime = 1000 - (now % 1000)
                delay(if (sleepTime > 0) sleepTime else 1000)
            }
        }
    }

    private fun updateClock() {
        val calendar = Calendar.getInstance()
        val date = calendar.time
        val showSeconds = repository.getShowSecondsInTaskbar()
        val is24Hour = repository.getIs24Hour()

        val timePattern = when {
            is24Hour && showSeconds -> "HH:mm:ss"
            is24Hour -> "HH:mm"
            showSeconds -> "h:mm:ss a"
            else -> "h:mm a"
        }

        val timeFormatter = SimpleDateFormat(timePattern, Locale.getDefault())
        val withSecondsFormatter = SimpleDateFormat(if (is24Hour) "HH:mm:ss" else "h:mm:ss a", Locale.getDefault())
        val shortFormatter = SimpleDateFormat(if (is24Hour) "HH:mm" else "h:mm", Locale.getDefault())
        val amPmFormatter = SimpleDateFormat("a", Locale.getDefault())
        val secondsFormatter = SimpleDateFormat("ss", Locale.getDefault())
        val dateFormatter = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        val fullDateFormatter = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        val monthFormatter = SimpleDateFormat("MMMM", Locale.getDefault())

        _clockState.value = ClockState(
            timeFormatted = timeFormatter.format(date),
            timeWithSeconds = withSecondsFormatter.format(date),
            timeShort = shortFormatter.format(date),
            amPm = amPmFormatter.format(date),
            secondsStr = secondsFormatter.format(date),
            dateFormatted = dateFormatter.format(date),
            fullDateFormatted = fullDateFormatter.format(date),
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH),
            monthName = monthFormatter.format(date),
            year = calendar.get(Calendar.YEAR),
            showSecondsInTaskbar = showSeconds,
            is24Hour = is24Hour
        )
    }

    fun toggleShowSecondsInTaskbar() {
        val current = repository.getShowSecondsInTaskbar()
        repository.setShowSecondsInTaskbar(!current)
        updateClock()
    }

    fun toggle24HourFormat() {
        val current = repository.getIs24Hour()
        repository.setIs24Hour(!current)
        updateClock()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        // Automatically open start menu if searching
        if (query.isNotEmpty() && !_isStartMenuOpen.value) {
            _isStartMenuOpen.value = true
        }
    }

    fun setSearchFilter(filter: SearchFilter) {
        _searchFilter.value = filter
    }

    fun toggleStartMenu() {
        _isStartMenuOpen.value = !_isStartMenuOpen.value
        if (_isStartMenuOpen.value) {
            _isCalendarFlyoutOpen.value = false
            _isActionCenterOpen.value = false
        }
    }

    fun closeStartMenu() {
        _isStartMenuOpen.value = false
    }

    fun setStartMenuTab(tab: StartMenuTab) {
        _startMenuTab.value = tab
    }

    fun toggleCalendarFlyout() {
        _isCalendarFlyoutOpen.value = !_isCalendarFlyoutOpen.value
        if (_isCalendarFlyoutOpen.value) {
            _isStartMenuOpen.value = false
            _isActionCenterOpen.value = false
        }
    }

    fun closeCalendarFlyout() {
        _isCalendarFlyoutOpen.value = false
    }

    fun toggleActionCenter() {
        _isActionCenterOpen.value = !_isActionCenterOpen.value
        if (_isActionCenterOpen.value) {
            _isStartMenuOpen.value = false
            _isCalendarFlyoutOpen.value = false
        }
    }

    fun closeActionCenter() {
        _isActionCenterOpen.value = false
    }

    fun openAddShortcutDialog(itemToEdit: ShortcutItem? = null) {
        _editingShortcut.value = itemToEdit
        _isAddShortcutDialogOpen.value = true
    }

    fun closeAddShortcutDialog() {
        _editingShortcut.value = null
        _isAddShortcutDialogOpen.value = false
    }

    fun openWallpaperPicker() {
        _isWallpaperPickerOpen.value = true
    }

    fun closeWallpaperPicker() {
        _isWallpaperPickerOpen.value = false
    }

    fun openPersonalizationDialog() {
        _isPersonalizationDialogOpen.value = true
        closeStartMenu()
        closeCalendarFlyout()
        closeActionCenter()
    }

    fun closePersonalizationDialog() {
        _isPersonalizationDialogOpen.value = false
        _isWallpaperPickerOpen.value = false
    }

    fun selectWallpaper(theme: WallpaperTheme) {
        _wallpaperTheme.value = theme
        repository.setWallpaperTheme(theme)
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        repository.setThemeMode(mode)
    }

    fun setSkinColor(skin: SkinColor) {
        _skinColor.value = skin
        repository.setSkinColor(skin)
    }

    fun setFontOption(option: AppFontFamilyOption) {
        _fontOption.value = option
        repository.setFontOption(option)
    }

    fun setFontSizeScale(scale: FontSizeScale) {
        _fontSizeScale.value = scale
        repository.setFontSizeScale(scale)
    }

    fun openMiniApp(appId: String) {
        _activeMiniApp.value = appId
        _isStartMenuOpen.value = false
    }

    fun closeMiniApp() {
        _activeMiniApp.value = null
    }

    fun updateQuickNotes(text: String) {
        _quickNotesText.value = text
    }

    // ==========================================
    // WINDOW MANAGEMENT METHODS
    // ==========================================

    fun openWindow(shortcut: ShortcutItem) {
        closeStartMenu()
        closeCalendarFlyout()
        closeActionCenter()

        val currentList = _openWindows.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.shortcutId == shortcut.id }

        if (existingIndex >= 0) {
            val existing = currentList[existingIndex]
            nextZIndex += 1f
            currentList[existingIndex] = existing.copy(
                isMinimized = false,
                zIndex = nextZIndex
            )
            _openWindows.value = currentList
            _activeWindowId.value = existing.id
            return
        }

        // Stagger window position so multiple windows don't completely overlap
        val openCount = currentList.size
        val startX = 24f + (openCount % 6) * 32f
        val startY = 24f + (openCount % 6) * 28f
        nextZIndex += 1f

        val (initW, initH) = when (shortcut.target) {
            "mini_calc" -> 320 to 420
            "mini_notes" -> 340 to 360
            "mini_terminal" -> 440 to 360
            else -> 360 to 420
        }

        val newWindow = WindowData(
            id = "win_${shortcut.id}_${System.currentTimeMillis()}",
            shortcutId = shortcut.id,
            title = shortcut.title,
            iconKey = shortcut.iconKey,
            type = shortcut.type,
            target = shortcut.target,
            colorHex = shortcut.colorHex,
            offsetX = startX,
            offsetY = startY,
            widthDp = initW,
            heightDp = initH,
            isMinimized = false,
            isMaximized = false,
            zIndex = nextZIndex,
            preMaxOffsetX = startX,
            preMaxOffsetY = startY,
            preMaxWidthDp = initW,
            preMaxHeightDp = initH
        )

        currentList.add(newWindow)
        _openWindows.value = currentList
        _activeWindowId.value = newWindow.id
    }

    fun openWindowForApp(app: InstalledAppItem) {
        closeStartMenu()
        closeCalendarFlyout()
        closeActionCenter()

        val currentList = _openWindows.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.target == app.packageName }

        if (existingIndex >= 0) {
            val existing = currentList[existingIndex]
            nextZIndex += 1f
            currentList[existingIndex] = existing.copy(
                isMinimized = false,
                zIndex = nextZIndex
            )
            _openWindows.value = currentList
            _activeWindowId.value = existing.id
            return
        }

        val openCount = currentList.size
        val startX = 24f + (openCount % 6) * 32f
        val startY = 24f + (openCount % 6) * 28f
        nextZIndex += 1f

        val newWindow = WindowData(
            id = "win_app_${app.packageName.hashCode()}_${System.currentTimeMillis()}",
            shortcutId = null,
            title = app.label,
            iconKey = "apps",
            type = ShortcutType.APP,
            target = app.packageName,
            colorHex = 0xFF0078D4,
            offsetX = startX,
            offsetY = startY,
            widthDp = 360,
            heightDp = 380,
            isMinimized = false,
            isMaximized = false,
            zIndex = nextZIndex,
            preMaxOffsetX = startX,
            preMaxOffsetY = startY,
            preMaxWidthDp = 360,
            preMaxHeightDp = 380
        )

        currentList.add(newWindow)
        _openWindows.value = currentList
        _activeWindowId.value = newWindow.id
    }

    fun closeWindow(windowId: String) {
        _openWindows.value = _openWindows.value.filterNot { it.id == windowId }
        if (_activeWindowId.value == windowId) {
            _activeWindowId.value = _openWindows.value.maxByOrNull { it.zIndex }?.id
        }
    }

    fun minimizeWindow(windowId: String) {
        _openWindows.value = _openWindows.value.map {
            if (it.id == windowId) it.copy(isMinimized = true) else it
        }
        if (_activeWindowId.value == windowId) {
            _activeWindowId.value = _openWindows.value
                .filterNot { it.isMinimized }
                .maxByOrNull { it.zIndex }?.id
        }
    }

    fun restoreWindow(windowId: String) {
        nextZIndex += 1f
        _openWindows.value = _openWindows.value.map {
            if (it.id == windowId) it.copy(isMinimized = false, zIndex = nextZIndex) else it
        }
        _activeWindowId.value = windowId
    }

    fun toggleMaximizeWindow(windowId: String) {
        _openWindows.value = _openWindows.value.map { win ->
            if (win.id == windowId) {
                if (win.isMaximized) {
                    // Restore to previous floating position & dimensions
                    win.copy(
                        isMaximized = false,
                        offsetX = win.preMaxOffsetX,
                        offsetY = win.preMaxOffsetY,
                        widthDp = win.preMaxWidthDp,
                        heightDp = win.preMaxHeightDp
                    )
                } else {
                    // Save current floating position & dimensions before maximizing
                    win.copy(
                        isMaximized = true,
                        preMaxOffsetX = win.offsetX,
                        preMaxOffsetY = win.offsetY,
                        preMaxWidthDp = win.widthDp,
                        preMaxHeightDp = win.heightDp,
                        offsetX = 0f,
                        offsetY = 0f
                    )
                }
            } else win
        }
        bringWindowToFront(windowId)
    }

    fun bringWindowToFront(windowId: String) {
        nextZIndex += 1f
        _openWindows.value = _openWindows.value.map {
            if (it.id == windowId) it.copy(zIndex = nextZIndex, isMinimized = false) else it
        }
        _activeWindowId.value = windowId
    }

    fun updateWindowPosition(windowId: String, deltaX: Float, deltaY: Float) {
        _openWindows.value = _openWindows.value.map { win ->
            if (win.id == windowId && !win.isMaximized) {
                val newX = (win.offsetX + deltaX).coerceAtLeast(0f)
                val newY = (win.offsetY + deltaY).coerceAtLeast(0f)
                win.copy(offsetX = newX, offsetY = newY)
            } else win
        }
    }

    fun updateWindowSize(windowId: String, deltaWidthDp: Int, deltaHeightDp: Int) {
        _openWindows.value = _openWindows.value.map { win ->
            if (win.id == windowId && !win.isMaximized) {
                val newW = (win.widthDp + deltaWidthDp).coerceIn(240, 900)
                val newH = (win.heightDp + deltaHeightDp).coerceIn(200, 900)
                win.copy(widthDp = newW, heightDp = newH)
            } else win
        }
    }

    fun minimizeAllWindows() {
        val anyVisible = _openWindows.value.any { !it.isMinimized }
        _openWindows.value = _openWindows.value.map {
            it.copy(isMinimized = anyVisible)
        }
        if (anyVisible) {
            _activeWindowId.value = null
        } else {
            _activeWindowId.value = _openWindows.value.maxByOrNull { it.zIndex }?.id
        }
    }

    fun launchShortcut(item: ShortcutItem) {
        // Opens the shortcut as a draggable, resizable window in the launcher environment!
        openWindow(item)
    }

    fun launchShortcutExternal(item: ShortcutItem) {
        repository.launchShortcut(item)
    }

    fun launchApp(app: InstalledAppItem) {
        // Opens the application as a desktop window in the launcher environment!
        openWindowForApp(app)
    }

    fun launchAppExternal(packageName: String) {
        repository.launchAppByPackage(packageName)
    }

    fun saveOrUpdateShortcut(
        id: String?,
        title: String,
        type: ShortcutType,
        target: String,
        iconKey: String,
        colorHex: Long,
        pinToTaskbar: Boolean,
        pinToDesktop: Boolean
    ) {
        val currentList = _shortcuts.value.toMutableList()
        val shortcutId = id ?: "sc_${UUID.randomUUID().toString().take(8)}"
        val existingIndex = currentList.indexOfFirst { it.id == shortcutId }

        val updatedItem = ShortcutItem(
            id = shortcutId,
            title = title.ifBlank { "Shortcut" },
            type = type,
            target = target.trim(),
            iconKey = iconKey,
            colorHex = colorHex,
            isPinnedToTaskbar = pinToTaskbar,
            isPinnedToDesktop = pinToDesktop,
            gridCol = 0,
            gridRow = 0
        )

        if (existingIndex >= 0) {
            currentList[existingIndex] = updatedItem
        } else {
            currentList.add(updatedItem)
        }

        _shortcuts.value = currentList
        repository.saveShortcuts(currentList)
        closeAddShortcutDialog()
    }

    fun deleteShortcut(shortcutId: String) {
        val updated = _shortcuts.value.filterNot { it.id == shortcutId }
        _shortcuts.value = updated
        repository.saveShortcuts(updated)
    }

    fun togglePinToTaskbar(shortcutId: String) {
        val updated = _shortcuts.value.map {
            if (it.id == shortcutId) it.copy(isPinnedToTaskbar = !it.isPinnedToTaskbar) else it
        }
        _shortcuts.value = updated
        repository.saveShortcuts(updated)
    }

    fun togglePinToDesktop(shortcutId: String) {
        val updated = _shortcuts.value.map {
            if (it.id == shortcutId) it.copy(isPinnedToDesktop = !it.isPinnedToDesktop) else it
        }
        _shortcuts.value = updated
        repository.saveShortcuts(updated)
    }

    fun executeWebSearch(query: String, engine: String = "google") {
        closeStartMenu()
        val url = when (engine.lowercase()) {
            "bing" -> "https://www.bing.com/search?q=${Uri.encode(query)}"
            "duckduckgo" -> "https://duckduckgo.com/?q=${Uri.encode(query)}"
            else -> "https://www.google.com/search?q=${Uri.encode(query)}"
        }
        repository.launchShortcut(
            ShortcutItem(
                id = "search_exec",
                title = "Search",
                type = ShortcutType.WEB_URL,
                target = url
            )
        )
    }

    fun refreshInstalledApps() {
        viewModelScope.launch {
            _installedApps.value = repository.getInstalledApps()
        }
    }

    private fun refreshBattery() {
        try {
            val batteryStatus: Intent? = getApplication<Application>().registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
            val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            if (level >= 0 && scale > 0) {
                _batteryPercentage.value = (level * 100 / scale.toFloat()).toInt()
            }
        } catch (_: Exception) {}
    }

    // Quick Math solver for the quick search bar
    private fun evaluateMath(rawQuery: String): MathResult {
        val query = rawQuery.trim()
        if (query.isBlank() || !query.any { it in "+-*/%^" }) {
            return MathResult(query, "", false)
        }

        return try {
            val clean = query.replace("x", "*").replace("×", "*").replace("÷", "/")
            val value = simpleEval(clean)
            if (value != null) {
                val formatted = if (value % 1.0 == 0.0) {
                    value.toLong().toString()
                } else {
                    String.format(Locale.US, "%.4f", value).trimEnd('0').trimEnd('.')
                }
                MathResult(query, formatted, true)
            } else {
                MathResult(query, "", false)
            }
        } catch (e: Exception) {
            MathResult(query, "", false)
        }
    }

    private fun simpleEval(expr: String): Double? {
        val tokens = expr.replace(" ", "")
        // Handle basic binary operations: a op b
        val regex = Regex("""^([\d.]+)([\+\-\*/%^])([\d.]+)$""")
        val match = regex.find(tokens) ?: return null
        val (aStr, op, bStr) = match.destructured
        val a = aStr.toDoubleOrNull() ?: return null
        val b = bStr.toDoubleOrNull() ?: return null

        return when (op) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> if (b != 0.0) a / b else null
            "%" -> a % b
            "^" -> Math.pow(a, b)
            else -> null
        }
    }

    // ==========================================
    // NOTIFICATION CENTER ACTIONS
    // ==========================================

    fun toggleNotificationCenter() {
        _isNotificationCenterOpen.value = !_isNotificationCenterOpen.value
        if (_isNotificationCenterOpen.value) {
            _isStartMenuOpen.value = false
            _isCalendarFlyoutOpen.value = false
            _isActionCenterOpen.value = false
        }
    }

    fun closeNotificationCenter() {
        _isNotificationCenterOpen.value = false
    }

    fun openNotificationCenter() {
        _isNotificationCenterOpen.value = true
        _isStartMenuOpen.value = false
        _isCalendarFlyoutOpen.value = false
        _isActionCenterOpen.value = false
    }

    fun dismissNotification(id: String) {
        _notifications.value = _notifications.value.filterNot { it.id == id }
    }

    fun clearAllNonPersistentNotifications() {
        _notifications.value = _notifications.value.filter { it.isPersistent }
    }

    fun toggleDoNotDisturb() {
        _isDoNotDisturb.value = !_isDoNotDisturb.value
    }

    fun postCustomAlert(title: String, message: String, isPersistent: Boolean = false) {
        val timeFmt = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        val newAlert = NotificationItem(
            id = "alert_${UUID.randomUUID().toString().take(6)}",
            title = title.ifBlank { "System Alert" },
            message = message.ifBlank { "Notification event recorded." },
            timestampFormatted = timeFmt,
            type = if (isPersistent) NotificationType.SYSTEM else NotificationType.APP,
            iconKey = "bell",
            colorHex = 0xFF60CDFF,
            isPersistent = isPersistent
        )
        _notifications.value = listOf(newAlert) + _notifications.value
    }

    private fun getDefaultNotifications(): List<NotificationItem> {
        return listOf(
            NotificationItem(
                id = "alert_desktop_ready",
                title = "Desktop Environment Active",
                message = "Windows Launcher is operating in desktop mode. Open shortcuts as floating, resizable windows.",
                timestampFormatted = "Just now",
                type = NotificationType.SYSTEM,
                iconKey = "computer",
                colorHex = 0xFF0078D4,
                isPersistent = true,
                actionLabel = "System Info",
                actionTarget = "settings"
            ),
            NotificationItem(
                id = "alert_security_active",
                title = "Windows Security & Sandboxing",
                message = "Zero-trust sandbox protection is active. Applications run with least-privilege permissions.",
                timestampFormatted = "10m ago",
                type = NotificationType.SECURITY,
                iconKey = "security",
                colorHex = 0xFF107C41,
                isPersistent = true,
                actionLabel = "Security Details",
                actionTarget = "settings"
            ),
            NotificationItem(
                id = "alert_battery_power",
                title = "Power & Performance",
                message = "Battery profile optimized for desktop multitasking.",
                timestampFormatted = "25m ago",
                type = NotificationType.BATTERY,
                iconKey = "battery",
                colorHex = 0xFF60CDFF,
                isPersistent = false,
                actionLabel = "Power Settings",
                actionTarget = "settings"
            ),
            NotificationItem(
                id = "alert_quick_shortcuts",
                title = "Custom Shortcuts Engine",
                message = "Tip: Long-press anywhere on the desktop to add custom web or app shortcuts with color accents.",
                timestampFormatted = "1h ago",
                type = NotificationType.SHORTCUT,
                iconKey = "globe",
                colorHex = 0xFFFFB900,
                isPersistent = false,
                actionLabel = "New Shortcut",
                actionTarget = "new_shortcut"
            )
        )
    }
}

