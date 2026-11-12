package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.FilterNone
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.example.data.model.ShortcutType
import com.example.data.model.WindowData
import kotlin.math.roundToInt

/**
 * Draggable and Resizable Desktop Window.
 * Implements window title bar dragging, corner resizing, minimize, maximize, and custom contents.
 */
@Composable
fun DesktopWindowItem(
    window: WindowData,
    isActive: Boolean,
    onFocus: () -> Unit,
    onDrag: (deltaX: Float, deltaY: Float) -> Unit,
    onResize: (deltaWidthDp: Int, deltaHeightDp: Int) -> Unit,
    onMinimize: () -> Unit,
    onToggleMaximize: () -> Unit,
    onClose: () -> Unit,
    onLaunchExternal: () -> Unit,
    quickNotesText: String,
    onUpdateQuickNotes: (String) -> Unit,
    onOpenAndroidSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (window.isMinimized) return

    val density = LocalDensity.current

    val windowModifier = if (window.isMaximized) {
        modifier
            .fillMaxSize()
            .zIndex(window.zIndex)
    } else {
        modifier
            .offset { IntOffset(window.offsetX.roundToInt(), window.offsetY.roundToInt()) }
            .size(width = window.widthDp.dp, height = window.heightDp.dp)
            .zIndex(window.zIndex)
    }

    Box(
        modifier = windowModifier
            .shadow(if (isActive) 24.dp else 12.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xF5182234),
                        Color(0xFA0D1525)
                    )
                )
            )
            .border(
                width = if (isActive) 1.5.dp else 1.dp,
                color = if (isActive) Color(0xFF60CDFF) else Color(0x33FFFFFF),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onFocus()
            }
            .testTag("window_${window.id}")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // WINDOW TITLE BAR (Draggable)
            WindowTitleBar(
                title = window.title,
                iconKey = window.iconKey,
                colorHex = window.colorHex,
                isActive = isActive,
                isMaximized = window.isMaximized,
                onDrag = onDrag,
                onMinimize = onMinimize,
                onToggleMaximize = onToggleMaximize,
                onClose = onClose,
                onFocus = onFocus
            )

            // WINDOW CLIENT AREA CONTENT
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (window.type) {
                    ShortcutType.MINI_APP -> {
                        when (window.target) {
                            "mini_calc" -> WindowCalculatorContent()
                            "mini_notes" -> WindowNotesContent(
                                text = quickNotesText,
                                onTextChange = onUpdateQuickNotes
                            )
                            "mini_terminal" -> WindowTerminalContent(onClose = onClose)
                            else -> WindowGenericAppContent(
                                window = window,
                                onLaunchExternal = onLaunchExternal
                            )
                        }
                    }
                    ShortcutType.WEB_URL -> {
                        WindowWebBrowserContent(
                            url = window.target,
                            onLaunchExternal = onLaunchExternal
                        )
                    }
                    ShortcutType.APP -> {
                        WindowAppRunnerContent(
                            window = window,
                            onLaunchExternal = onLaunchExternal,
                            onOpenSettings = onOpenAndroidSettings
                        )
                    }
                    ShortcutType.SYSTEM_ACTION -> {
                        WindowControlPanelContent(
                            window = window,
                            onOpenSettings = onOpenAndroidSettings
                        )
                    }
                }
            }
        }

        // RESIZE HANDLE (Bottom-Right Corner) - disabled when maximized
        if (!window.isMaximized) {
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
                    .pointerInput(window.id) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val deltaWidthDp = with(density) { dragAmount.x.toDp().value.toInt() }
                            val deltaHeightDp = with(density) { dragAmount.y.toDp().value.toInt() }
                            onResize(deltaWidthDp, deltaHeightDp)
                        }
                    }
                    .testTag("resize_handle_${window.id}")
            ) {
                // Diagonal resize marks
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(12.dp)
                ) {
                    val strokeWidth = 2.dp.toPx()
                    val color = if (isActive) Color(0xFF60CDFF) else Color(0x66FFFFFF)
                    drawLine(color, androidx.compose.ui.geometry.Offset(size.width, 0f), androidx.compose.ui.geometry.Offset(0f, size.height), strokeWidth)
                    drawLine(color, androidx.compose.ui.geometry.Offset(size.width, size.height * 0.5f), androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height), strokeWidth)
                }
            }
        }
    }
}

/**
 * Windows Title Bar with Drag Gesture Detection and Min/Max/Close Buttons.
 */
@Composable
fun WindowTitleBar(
    title: String,
    iconKey: String,
    colorHex: Long,
    isActive: Boolean,
    isMaximized: Boolean,
    onDrag: (deltaX: Float, deltaY: Float) -> Unit,
    onMinimize: () -> Unit,
    onToggleMaximize: () -> Unit,
    onClose: () -> Unit,
    onFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp)
            .background(
                if (isActive) Color(0xFF101B2E) else Color(0xFF0B1220)
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { onFocus() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x, dragAmount.y)
                    }
                )
            }
            .padding(horizontal = 10.dp)
            .testTag("window_titlebar")
    ) {
        // App Icon and Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(colorHex))
            ) {
                Icon(
                    imageVector = IconHelper.getIcon(iconKey),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 12.sp
                ),
                color = if (isActive) Color.White else Color(0xAAFFFFFF),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Window Control Buttons (Minimize, Maximize / Restore, Close)
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Minimize Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onMinimize() }
                    .testTag("window_btn_minimize")
            ) {
                Icon(
                    imageVector = Icons.Default.Minimize,
                    contentDescription = "Minimize",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(14.dp)
                )
            }

            // Maximize / Restore Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onToggleMaximize() }
                    .testTag("window_btn_maximize")
            ) {
                Icon(
                    imageVector = if (isMaximized) Icons.Default.FilterNone else Icons.Default.CropSquare,
                    contentDescription = if (isMaximized) "Restore" else "Maximize",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(13.dp)
                )
            }

            // Close Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onClose() }
                    .testTag("window_btn_close")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color(0xFFFF6B6B),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

/**
 * Embedded Web Browser Window Content.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WindowWebBrowserContent(
    url: String,
    onLaunchExternal: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentUrl by remember { mutableStateOf(url) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        // Browser Navigation Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            IconButton(
                onClick = { webViewRef?.reload() },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reload",
                    tint = Color(0xFF60CDFF),
                    modifier = Modifier.size(16.dp)
                )
            }

            OutlinedTextField(
                value = currentUrl,
                onValueChange = { currentUrl = it },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color(0xCCFFFFFF),
                    focusedContainerColor = Color(0x33000000),
                    unfocusedContainerColor = Color(0x22000000),
                    focusedBorderColor = Color(0xFF0078D4),
                    unfocusedBorderColor = Color(0x33FFFFFF)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
            )

            IconButton(
                onClick = onLaunchExternal,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInBrowser,
                    contentDescription = "Open in Chrome/Browser",
                    tint = Color(0xFF60CDFF),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // WebView Frame
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                isLoading = true
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                                if (url != null) currentUrl = url
                            }
                        }
                        loadUrl(url)
                        webViewRef = this
                    }
                },
                update = { webView ->
                    if (webView.url != currentUrl && !isLoading) {
                        // webView.loadUrl(currentUrl)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            if (isLoading) {
                CircularProgressIndicator(
                    color = Color(0xFF60CDFF),
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                )
            }
        }
    }
}

/**
 * Windows Calculator Window Content.
 */
@Composable
fun WindowCalculatorContent() {
    var display by remember { mutableStateOf("0") }
    var storedVal by remember { mutableStateOf<Double?>(null) }
    var operation by remember { mutableStateOf<String?>(null) }
    var resetOnNext by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        // LCD Display
        Surface(
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {
            Text(
                text = display,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    fontSize = 24.sp
                ),
                color = Color.White,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            )
        }

        val buttons = listOf(
            listOf("C", "±", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "⌫", "=")
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                row.forEach { label ->
                    val isOp = label in listOf("÷", "×", "-", "+", "=")
                    val isClear = label in listOf("C", "⌫", "±", "%")
                    Button(
                        onClick = {
                            when (label) {
                                "C" -> {
                                    display = "0"
                                    storedVal = null
                                    operation = null
                                }
                                "⌫" -> {
                                    display = if (display.length > 1) display.dropLast(1) else "0"
                                }
                                "±" -> {
                                    display = if (display.startsWith("-")) display.drop(1) else "-$display"
                                }
                                "%" -> {
                                    val v = display.toDoubleOrNull() ?: 0.0
                                    display = (v / 100.0).toString().trimEnd('0').trimEnd('.')
                                }
                                "+", "-", "×", "÷" -> {
                                    storedVal = display.toDoubleOrNull()
                                    operation = label
                                    resetOnNext = true
                                }
                                "=" -> {
                                    val second = display.toDoubleOrNull()
                                    val first = storedVal
                                    val op = operation
                                    if (first != null && second != null && op != null) {
                                        val res = when (op) {
                                            "+" -> first + second
                                            "-" -> first - second
                                            "×" -> first * second
                                            "÷" -> if (second != 0.0) first / second else 0.0
                                            else -> second
                                        }
                                        display = if (res % 1.0 == 0.0) res.toLong().toString() else res.toString()
                                        storedVal = null
                                        operation = null
                                        resetOnNext = true
                                    }
                                }
                                else -> {
                                    if (resetOnNext || display == "0") {
                                        display = label
                                        resetOnNext = false
                                    } else {
                                        display += label
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                label == "=" -> Color(0xFF0078D4)
                                isOp -> Color(0xFF2563EB)
                                isClear -> Color(0xFF334155)
                                else -> Color(0xFF1E293B)
                            }
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Sticky Notes Window Content.
 */
@Composable
fun WindowNotesContent(
    text: String,
    onTextChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFFFF7D6),
                unfocusedContainerColor = Color(0xFFFFF9E6),
                focusedTextColor = Color(0xFF1E1E1E),
                unfocusedTextColor = Color(0xFF1E1E1E),
                focusedBorderColor = Color(0xFFFFD166),
                unfocusedBorderColor = Color(0xFFFFE3A3)
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Auto-saved to Windows Launcher storage.",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0x88FFFFFF)
        )
    }
}

/**
 * Terminal Prompt Window Content.
 */
@Composable
fun WindowTerminalContent(onClose: () -> Unit) {
    var commandInput by remember { mutableStateOf("") }
    val history = remember {
        mutableStateListOf(
            "Windows PowerShell [Version 10.0.22631]",
            "(c) Microsoft Corporation. Android Desktop Subsystem.",
            "Type 'help' or 'info' for available commands.\n"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0C0C))
            .padding(10.dp)
    ) {
        androidx.compose.foundation.lazy.LazyColumn(modifier = Modifier.weight(1f)) {
            items(history.size) { idx ->
                Text(
                    text = history[idx],
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    ),
                    color = Color(0xFFCCCCCC)
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "PS C:\\> ",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF60CDFF),
                    fontSize = 11.sp
                )
            )
            OutlinedTextField(
                value = commandInput,
                onValueChange = { commandInput = it },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color(0xFF60CDFF)
                ),
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    val cmd = commandInput.trim().lowercase()
                    history.add("PS C:\\> $commandInput")
                    when (cmd) {
                        "help" -> history.add("Commands: help, date, time, system, clear, exit")
                        "date", "time" -> history.add(java.util.Date().toString())
                        "system" -> history.add("OS: Windows Launcher Desktop Subsystem")
                        "clear", "cls" -> history.clear()
                        "exit" -> onClose()
                        else -> history.add("'$cmd' is not recognized as an internal or external command.")
                    }
                    commandInput = ""
                },
                modifier = Modifier.height(30.dp)
            ) {
                Text("Run", fontSize = 10.sp)
            }
        }
    }
}

/**
 * Window App Runner Content for Installed Applications.
 */
@Composable
fun WindowAppRunnerContent(
    window: WindowData,
    onLaunchExternal: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(window.colorHex))
        ) {
            Icon(
                imageVector = IconHelper.getIcon(window.iconKey),
                contentDescription = window.title,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = window.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )

        Text(
            text = window.target,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0x88FFFFFF)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onLaunchExternal,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0078D4)),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Icon(imageVector = Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Launch in Android")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onOpenSettings,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0x22FFFFFF)),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("App Details / Permissions")
        }
    }
}

/**
 * Control Panel Window Content for System Actions.
 */
@Composable
fun WindowControlPanelContent(
    window: WindowData,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Windows Control Panel",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Text(
            text = "Adjust computer and system configuration",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0x88FFFFFF)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onOpenSettings,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0078D4)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open Android System Settings")
        }
    }
}

/**
 * Fallback Generic App Content.
 */
@Composable
fun WindowGenericAppContent(
    window: WindowData,
    onLaunchExternal: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = window.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onLaunchExternal) {
            Text("Launch")
        }
    }
}
