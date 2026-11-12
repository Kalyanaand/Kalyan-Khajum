package com.example

import com.example.data.model.ShortcutItem
import com.example.data.model.ShortcutType
import com.example.data.model.WindowData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExampleUnitTest {

    @Test
    fun `time formatting with seconds produces expected format`() {
        val date = Date(1773321600000L) // Fixed timestamp
        val formatter = SimpleDateFormat("h:mm:ss a", Locale.US)
        val formatted = formatter.format(date)
        assertTrue(formatted.contains(":"))
        assertTrue(formatted.endsWith("AM") || formatted.endsWith("PM"))
    }

    @Test
    fun `24 hour time formatting produces two colons with seconds`() {
        val date = Date(1773321600000L)
        val formatter = SimpleDateFormat("HH:mm:ss", Locale.US)
        val formatted = formatter.format(date)
        val colonCount = formatted.count { it == ':' }
        assertEquals(2, colonCount)
    }

    @Test
    fun `shortcut model equality and properties test`() {
        val item = ShortcutItem(
            id = "test_1",
            title = "Google Search",
            type = ShortcutType.WEB_URL,
            target = "https://google.com",
            iconKey = "globe",
            colorHex = 0xFF4285F4,
            isPinnedToTaskbar = true,
            isPinnedToDesktop = true
        )
        assertEquals("Google Search", item.title)
        assertEquals(ShortcutType.WEB_URL, item.type)
        assertTrue(item.isPinnedToTaskbar)
        assertTrue(item.isPinnedToDesktop)
    }

    @Test
    fun `window data dragging and resizing state manipulation`() {
        var win = WindowData(
            id = "win_calc",
            title = "Calculator",
            iconKey = "calc",
            type = ShortcutType.MINI_APP,
            target = "mini_calc",
            offsetX = 30f,
            offsetY = 30f,
            widthDp = 320,
            heightDp = 420
        )

        // Dragging
        val deltaX = 15f
        val deltaY = 25f
        win = win.copy(offsetX = win.offsetX + deltaX, offsetY = win.offsetY + deltaY)
        assertEquals(45f, win.offsetX, 0.001f)
        assertEquals(55f, win.offsetY, 0.001f)

        // Resizing
        val deltaW = 50
        val deltaH = 40
        win = win.copy(widthDp = win.widthDp + deltaW, heightDp = win.heightDp + deltaH)
        assertEquals(370, win.widthDp)
        assertEquals(460, win.heightDp)

        // Maximizing
        val savedX = win.offsetX
        val savedY = win.offsetY
        val savedW = win.widthDp
        val savedH = win.heightDp

        win = win.copy(
            isMaximized = true,
            preMaxOffsetX = savedX,
            preMaxOffsetY = savedY,
            preMaxWidthDp = savedW,
            preMaxHeightDp = savedH,
            offsetX = 0f,
            offsetY = 0f
        )
        assertTrue(win.isMaximized)
        assertEquals(0f, win.offsetX, 0.001f)

        // Restoring from Maximize
        win = win.copy(
            isMaximized = false,
            offsetX = win.preMaxOffsetX,
            offsetY = win.preMaxOffsetY,
            widthDp = win.preMaxWidthDp,
            heightDp = win.preMaxHeightDp
        )
        assertFalse(win.isMaximized)
        assertEquals(savedX, win.offsetX, 0.001f)
        assertEquals(savedY, win.offsetY, 0.001f)
        assertEquals(savedW, win.widthDp)
        assertEquals(savedH, win.heightDp)
    }

    @Test
    fun `notification center persistent vs transient alerts and clear all logic`() {
        val persistentAlert = com.example.data.model.NotificationItem(
            id = "sys_alert_1",
            title = "Desktop Environment Active",
            message = "Multitasking active",
            timestampFormatted = "Now",
            type = com.example.data.model.NotificationType.SYSTEM,
            isPersistent = true
        )
        val transientAlert = com.example.data.model.NotificationItem(
            id = "app_alert_1",
            title = "Download Finished",
            message = "File ready",
            timestampFormatted = "1m ago",
            type = com.example.data.model.NotificationType.APP,
            isPersistent = false
        )

        val notifications = listOf(persistentAlert, transientAlert)
        assertEquals(2, notifications.size)

        // Clear all non-persistent
        val remaining = notifications.filter { it.isPersistent }
        assertEquals(1, remaining.size)
        assertEquals("sys_alert_1", remaining[0].id)
        assertTrue(remaining[0].isPersistent)
    }

    @Test
    fun `personalization theme modes and skins provide valid color tokens`() {
        assertEquals(3, com.example.data.model.ThemeMode.values().size)
        assertEquals("Dark Acrylic", com.example.data.model.ThemeMode.DARK.displayName)
        assertEquals("Light Fluent", com.example.data.model.ThemeMode.LIGHT.displayName)

        val skins = com.example.data.model.SkinColor.values()
        assertEquals(8, skins.size)
        skins.forEach { skin ->
            assertTrue(skin.accentHex != 0L)
            assertTrue(skin.lightAccentHex != 0L)
            assertTrue(skin.displayName.isNotBlank())
        }
    }

    @Test
    fun `font options and font size scales compute expected scaled sizes`() {
        val fonts = com.example.data.model.AppFontFamilyOption.values()
        assertEquals(4, fonts.size)

        val scales = com.example.data.model.FontSizeScale.values()
        assertEquals(4, scales.size)
        assertEquals(0.85f, com.example.data.model.FontSizeScale.COMPACT.scaleFactor, 0.001f)
        assertEquals(1.0f, com.example.data.model.FontSizeScale.DEFAULT.scaleFactor, 0.001f)
        assertEquals(1.15f, com.example.data.model.FontSizeScale.LARGE.scaleFactor, 0.001f)
        assertEquals(1.30f, com.example.data.model.FontSizeScale.EXTRA_LARGE.scaleFactor, 0.001f)

        // Base text 14sp scaled to extra large should be 18.2sp
        val baseSp = 14f
        val scaledSp = baseSp * com.example.data.model.FontSizeScale.EXTRA_LARGE.scaleFactor
        assertEquals(18.2f, scaledSp, 0.01f)
    }

    @Test
    fun `wallpaper themes contain valid gradient and accent colors`() {
        val wallpapers = com.example.data.model.WallpaperTheme.values()
        assertTrue(wallpapers.size >= 8)
        wallpapers.forEach { wp ->
            assertTrue(wp.startColor != 0L)
            assertTrue(wp.endColor != 0L)
            assertTrue(wp.accentColor != 0L)
            assertTrue(wp.displayName.isNotBlank())
        }
    }
}

