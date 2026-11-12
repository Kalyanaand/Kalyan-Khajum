package com.example.data.model

enum class ShortcutType {
    APP,
    WEB_URL,
    SYSTEM_ACTION,
    MINI_APP
}

data class ShortcutItem(
    val id: String,
    val title: String,
    val type: ShortcutType,
    val target: String, // package name or URL or action id
    val iconKey: String = "globe", // globe, star, code, folder, terminal, calc, note, chat, play, etc.
    val colorHex: Long = 0xFF0078D4,
    val isPinnedToTaskbar: Boolean = false,
    val isPinnedToDesktop: Boolean = true,
    val gridCol: Int = 0,
    val gridRow: Int = 0
)

data class InstalledAppItem(
    val packageName: String,
    val activityName: String,
    val label: String,
    val isSystemApp: Boolean = false
)

data class ClockState(
    val timeFormatted: String = "",       // e.g. "10:42 AM" or "10:42:15 AM"
    val timeWithSeconds: String = "",    // e.g. "10:42:15"
    val timeShort: String = "",          // e.g. "10:42"
    val amPm: String = "",               // e.g. "AM" or "PM"
    val secondsStr: String = "00",       // e.g. "15"
    val dateFormatted: String = "",      // e.g. "Wed, Nov 11"
    val fullDateFormatted: String = "",  // e.g. "Wednesday, November 11, 2026"
    val dayOfMonth: Int = 1,
    val monthName: String = "",
    val year: Int = 2026,
    val showSecondsInTaskbar: Boolean = true,
    val is24Hour: Boolean = false
)

enum class WallpaperTheme(
    val displayName: String,
    val startColor: Long,
    val endColor: Long,
    val accentColor: Long,
    val description: String,
    val isLight: Boolean = false
) {
    WINDOWS_BLOOM(
        "Windows Bloom",
        0xFF0A192F,
        0xFF0D2538,
        0xFF0078D4,
        "Classic Windows 11 deep bloom gradient"
    ),
    WINDOWS_BLOOM_LIGHT(
        "Windows Bloom Light",
        0xFFF1F5F9,
        0xFFE2E8F0,
        0xFF0078D4,
        "Clean bright Fluent desktop theme",
        isLight = true
    ),
    CYBER_NIGHT(
        "Cyber Midnight",
        0xFF0F0C29,
        0xFF24243E,
        0xFF7B2CBF,
        "Deep violet and neon purple aesthetic"
    ),
    AURORA_BOREALIS(
        "Aurora Borealis",
        0xFF051923,
        0xFF003554,
        0xFF00A896,
        "Emerald and teal polar glow"
    ),
    SUNSET_HORIZON(
        "Sunset Horizon",
        0xFF1A0B2E,
        0xFF3E1F47,
        0xFFFF6B6B,
        "Warm dusk and orange glow"
    ),
    SLATE_MINIMAL(
        "Slate Minimal",
        0xFF18181B,
        0xFF27272A,
        0xFF38BDF8,
        "Clean dark desktop slate with sky accent"
    ),
    MATRIX_CYBERPUNK(
        "Matrix Cyberpunk",
        0xFF001100,
        0xFF002200,
        0xFF107C41,
        "Matrix terminal aesthetic with green accents"
    ),
    DEEP_OCEAN(
        "Deep Ocean",
        0xFF03071E,
        0xFF0F2027,
        0xFF00A4EF,
        "Aquamarine abyss with cyan lighting"
    )
}

enum class ThemeMode(val displayName: String) {
    DARK("Dark Acrylic"),
    LIGHT("Light Fluent"),
    SYSTEM("Follow System")
}

enum class SkinColor(
    val displayName: String,
    val accentHex: Long,
    val lightAccentHex: Long
) {
    WINDOWS_BLUE("Windows Blue", 0xFF0078D4, 0xFF60CDFF),
    ELECTRIC_CYAN("Electric Cyan", 0xFF00A4EF, 0xFF70E0FF),
    EMERALD_GREEN("Emerald Green", 0xFF107C41, 0xFF4ADE80),
    NEON_PURPLE("Neon Purple", 0xFF7B2CBF, 0xFFC084FC),
    CRIMSON_RED("Crimson Red", 0xFFE81123, 0xFFF87171),
    SUNSET_AMBER("Sunset Amber", 0xFFFF8C00, 0xFFFBBF24),
    CORAL_PINK("Coral Pink", 0xFFFF6B6B, 0xFFFDA4AF),
    SLATE_STEEL("Slate Steel", 0xFF64748B, 0xFF94A3B8)
}

enum class AppFontFamilyOption(
    val displayName: String,
    val description: String
) {
    SYSTEM_DEFAULT("System (Default)", "Standard system font"),
    MONTSERRAT("Montserrat", "Geometric modern Windows typography"),
    OUTFIT("Outfit", "Polished rounded display font"),
    ROBOTO_MONO("Roboto Mono", "Terminal & developer monospace font")
}

enum class FontSizeScale(
    val displayName: String,
    val scaleFactor: Float
) {
    COMPACT("Small (85%)", 0.85f),
    DEFAULT("Normal (100%)", 1.0f),
    LARGE("Large (115%)", 1.15f),
    EXTRA_LARGE("Extra Large (130%)", 1.30f)
}


data class WindowData(
    val id: String,
    val shortcutId: String? = null,
    val title: String,
    val iconKey: String = "globe",
    val type: ShortcutType = ShortcutType.MINI_APP,
    val target: String = "",
    val colorHex: Long = 0xFF0078D4,
    val offsetX: Float = 30f,
    val offsetY: Float = 30f,
    val widthDp: Int = 360,
    val heightDp: Int = 420,
    val isMinimized: Boolean = false,
    val isMaximized: Boolean = false,
    val zIndex: Float = 1f,
    val preMaxOffsetX: Float = 30f,
    val preMaxOffsetY: Float = 30f,
    val preMaxWidthDp: Int = 360,
    val preMaxHeightDp: Int = 420
)

enum class NotificationType {
    SYSTEM,
    SECURITY,
    APP,
    SHORTCUT,
    BATTERY
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestampFormatted: String,
    val type: NotificationType = NotificationType.SYSTEM,
    val iconKey: String = "bell",
    val colorHex: Long = 0xFF0078D4,
    val isPersistent: Boolean = false,
    val actionLabel: String? = null,
    val actionTarget: String? = null
)

