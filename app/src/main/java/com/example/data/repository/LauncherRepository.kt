package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import com.example.data.model.AppFontFamilyOption
import com.example.data.model.FontSizeScale
import com.example.data.model.InstalledAppItem
import com.example.data.model.ShortcutItem
import com.example.data.model.ShortcutType
import com.example.data.model.SkinColor
import com.example.data.model.ThemeMode
import com.example.data.model.WallpaperTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class LauncherRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("win_launcher_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SHORTCUTS = "key_custom_shortcuts"
        private const val KEY_WALLPAPER = "key_wallpaper_theme"
        private const val KEY_SHOW_SECONDS = "key_show_seconds"
        private const val KEY_24_HOUR = "key_is_24_hour"
        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_SKIN_COLOR = "key_skin_color"
        private const val KEY_FONT_OPTION = "key_font_option"
        private const val KEY_FONT_SIZE_SCALE = "key_font_size_scale"
    }

    suspend fun getInstalledApps(): List<InstalledAppItem> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = pm.queryIntentActivities(intent, 0)
        resolveInfos.mapNotNull { resolveInfo ->
            val pkg = resolveInfo.activityInfo.packageName
            val activity = resolveInfo.activityInfo.name
            val label = resolveInfo.loadLabel(pm).toString()
            if (pkg.isNotEmpty()) {
                InstalledAppItem(
                    packageName = pkg,
                    activityName = activity,
                    label = label,
                    isSystemApp = (resolveInfo.activityInfo.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                )
            } else null
        }.sortedBy { it.label.lowercase() }
    }

    fun loadShortcuts(): List<ShortcutItem> {
        val jsonString = prefs.getString(KEY_SHORTCUTS, null)
        if (jsonString.isNullOrEmpty()) {
            val defaults = getDefaultShortcuts()
            saveShortcuts(defaults)
            return defaults
        }

        return try {
            val array = JSONArray(jsonString)
            val list = mutableListOf<ShortcutItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ShortcutItem(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        title = obj.optString("title", "Shortcut"),
                        type = ShortcutType.valueOf(obj.optString("type", ShortcutType.WEB_URL.name)),
                        target = obj.optString("target", "https://google.com"),
                        iconKey = obj.optString("iconKey", "globe"),
                        colorHex = obj.optLong("colorHex", 0xFF0078D4),
                        isPinnedToTaskbar = obj.optBoolean("isPinnedToTaskbar", false),
                        isPinnedToDesktop = obj.optBoolean("isPinnedToDesktop", true),
                        gridCol = obj.optInt("gridCol", 0),
                        gridRow = obj.optInt("gridRow", 0)
                    )
                )
            }
            list
        } catch (e: Exception) {
            val defaults = getDefaultShortcuts()
            saveShortcuts(defaults)
            defaults
        }
    }

    fun saveShortcuts(shortcuts: List<ShortcutItem>) {
        val array = JSONArray()
        shortcuts.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("type", item.type.name)
                put("target", item.target)
                put("iconKey", item.iconKey)
                put("colorHex", item.colorHex)
                put("isPinnedToTaskbar", item.isPinnedToTaskbar)
                put("isPinnedToDesktop", item.isPinnedToDesktop)
                put("gridCol", item.gridCol)
                put("gridRow", item.gridRow)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_SHORTCUTS, array.toString()).apply()
    }

    fun getWallpaperTheme(): WallpaperTheme {
        val saved = prefs.getString(KEY_WALLPAPER, WallpaperTheme.WINDOWS_BLOOM.name)
        return try {
            WallpaperTheme.valueOf(saved ?: WallpaperTheme.WINDOWS_BLOOM.name)
        } catch (e: Exception) {
            WallpaperTheme.WINDOWS_BLOOM
        }
    }

    fun setWallpaperTheme(theme: WallpaperTheme) {
        prefs.edit().putString(KEY_WALLPAPER, theme.name).apply()
    }

    fun getShowSecondsInTaskbar(): Boolean {
        return prefs.getBoolean(KEY_SHOW_SECONDS, true)
    }

    fun setShowSecondsInTaskbar(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_SECONDS, show).apply()
    }

    fun getIs24Hour(): Boolean {
        return prefs.getBoolean(KEY_24_HOUR, false)
    }

    fun setIs24Hour(is24: Boolean) {
        prefs.edit().putBoolean(KEY_24_HOUR, is24).apply()
    }

    fun getThemeMode(): ThemeMode {
        val saved = prefs.getString(KEY_THEME_MODE, ThemeMode.DARK.name)
        return try {
            ThemeMode.valueOf(saved ?: ThemeMode.DARK.name)
        } catch (e: Exception) {
            ThemeMode.DARK
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    fun getSkinColor(): SkinColor {
        val saved = prefs.getString(KEY_SKIN_COLOR, SkinColor.WINDOWS_BLUE.name)
        return try {
            SkinColor.valueOf(saved ?: SkinColor.WINDOWS_BLUE.name)
        } catch (e: Exception) {
            SkinColor.WINDOWS_BLUE
        }
    }

    fun setSkinColor(skin: SkinColor) {
        prefs.edit().putString(KEY_SKIN_COLOR, skin.name).apply()
    }

    fun getFontOption(): AppFontFamilyOption {
        val saved = prefs.getString(KEY_FONT_OPTION, AppFontFamilyOption.SYSTEM_DEFAULT.name)
        return try {
            AppFontFamilyOption.valueOf(saved ?: AppFontFamilyOption.SYSTEM_DEFAULT.name)
        } catch (e: Exception) {
            AppFontFamilyOption.SYSTEM_DEFAULT
        }
    }

    fun setFontOption(option: AppFontFamilyOption) {
        prefs.edit().putString(KEY_FONT_OPTION, option.name).apply()
    }

    fun getFontSizeScale(): FontSizeScale {
        val saved = prefs.getString(KEY_FONT_SIZE_SCALE, FontSizeScale.DEFAULT.name)
        return try {
            FontSizeScale.valueOf(saved ?: FontSizeScale.DEFAULT.name)
        } catch (e: Exception) {
            FontSizeScale.DEFAULT
        }
    }

    fun setFontSizeScale(scale: FontSizeScale) {
        prefs.edit().putString(KEY_FONT_SIZE_SCALE, scale.name).apply()
    }

    fun launchShortcut(item: ShortcutItem) {
        try {
            when (item.type) {
                ShortcutType.APP -> {
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(item.target)
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(launchIntent)
                    } else {
                        Toast.makeText(context, "App not installed: ${item.target}", Toast.LENGTH_SHORT).show()
                    }
                }
                ShortcutType.WEB_URL -> {
                    var url = item.target.trim()
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "https://$url"
                    }
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
                ShortcutType.SYSTEM_ACTION -> {
                    val intent = when (item.target) {
                        "settings" -> Intent(Settings.ACTION_SETTINGS)
                        "wifi" -> Intent(Settings.ACTION_WIFI_SETTINGS)
                        "display" -> Intent(Settings.ACTION_DISPLAY_SETTINGS)
                        "sound" -> Intent(Settings.ACTION_SOUND_SETTINGS)
                        "apps" -> Intent(Settings.ACTION_APPLICATION_SETTINGS)
                        else -> Intent(Settings.ACTION_SETTINGS)
                    }.apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
                ShortcutType.MINI_APP -> {
                    // Handled inside ViewModel/UI for floating widgets
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Could not launch: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchAppByPackage(packageName: String) {
        try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
            } else {
                Toast.makeText(context, "Cannot open package: $packageName", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Launch error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun openAppDetails(packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open app details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDefaultShortcuts(): List<ShortcutItem> {
        return listOf(
            ShortcutItem(
                id = "sc_google",
                title = "Google Search",
                type = ShortcutType.WEB_URL,
                target = "https://www.google.com",
                iconKey = "globe",
                colorHex = 0xFF4285F4,
                isPinnedToTaskbar = true,
                isPinnedToDesktop = true,
                gridCol = 0,
                gridRow = 0
            ),
            ShortcutItem(
                id = "sc_calc",
                title = "Calculator",
                type = ShortcutType.MINI_APP,
                target = "mini_calc",
                iconKey = "calc",
                colorHex = 0xFF0078D4,
                isPinnedToTaskbar = true,
                isPinnedToDesktop = true,
                gridCol = 0,
                gridRow = 1
            ),
            ShortcutItem(
                id = "sc_notes",
                title = "Sticky Notes",
                type = ShortcutType.MINI_APP,
                target = "mini_notes",
                iconKey = "note",
                colorHex = 0xFFFFB900,
                isPinnedToTaskbar = true,
                isPinnedToDesktop = true,
                gridCol = 0,
                gridRow = 2
            ),
            ShortcutItem(
                id = "sc_github",
                title = "GitHub",
                type = ShortcutType.WEB_URL,
                target = "https://github.com",
                iconKey = "code",
                colorHex = 0xFF24292E,
                isPinnedToTaskbar = false,
                isPinnedToDesktop = true,
                gridCol = 0,
                gridRow = 3
            ),
            ShortcutItem(
                id = "sc_youtube",
                title = "YouTube",
                type = ShortcutType.WEB_URL,
                target = "https://youtube.com",
                iconKey = "play",
                colorHex = 0xFFFF0000,
                isPinnedToTaskbar = true,
                isPinnedToDesktop = true,
                gridCol = 1,
                gridRow = 0
            ),
            ShortcutItem(
                id = "sc_sys_settings",
                title = "Windows Settings",
                type = ShortcutType.SYSTEM_ACTION,
                target = "settings",
                iconKey = "settings",
                colorHex = 0xFF60CDFF,
                isPinnedToTaskbar = true,
                isPinnedToDesktop = true,
                gridCol = 1,
                gridRow = 1
            ),
            ShortcutItem(
                id = "sc_terminal",
                title = "Quick Command",
                type = ShortcutType.MINI_APP,
                target = "mini_terminal",
                iconKey = "terminal",
                colorHex = 0xFF107C41,
                isPinnedToTaskbar = false,
                isPinnedToDesktop = true,
                gridCol = 1,
                gridRow = 2
            ),
            ShortcutItem(
                id = "sc_wiki",
                title = "Wikipedia",
                type = ShortcutType.WEB_URL,
                target = "https://wikipedia.org",
                iconKey = "bookmark",
                colorHex = 0xFF9E9E9E,
                isPinnedToTaskbar = false,
                isPinnedToDesktop = true,
                gridCol = 1,
                gridRow = 3
            )
        )
    }
}
