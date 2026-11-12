package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/**
 * Windows-like Window Frame with Title Bar and Min/Max/Close controls.
 */
@Composable
fun WindowFrame(
    title: String,
    iconKey: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xF5182234),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4460CDFF)),
            shadowElevation = 16.dp,
            modifier = modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth(0.96f)
                .testTag("window_frame_$title")
        ) {
            Column {
                // Windows Title Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .background(Color(0xFF0F172A))
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = IconHelper.getIcon(iconKey),
                            contentDescription = null,
                            tint = Color(0xFF60CDFF),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.White
                        )
                    }

                    // Window controls: Minimize, Maximize, Close
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onClose() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Minimize,
                                contentDescription = "Minimize",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(4.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.CropSquare,
                                contentDescription = "Maximize",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0x33FF6B6B))
                                .clickable { onClose() }
                                .testTag("window_close_button")
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

                // Window Body
                Box(modifier = Modifier.padding(14.dp)) {
                    content()
                }
            }
        }
    }
}

/**
 * Built-in Windows Calculator Window.
 */
@Composable
fun WindowsCalculatorMiniApp(onClose: () -> Unit) {
    var display by remember { mutableStateOf("0") }
    var storedVal by remember { mutableStateOf<Double?>(null) }
    var operation by remember { mutableStateOf<String?>(null) }
    var resetOnNext by remember { mutableStateOf(false) }

    WindowFrame(
        title = "Windows Calculator",
        iconKey = "calc",
        onClose = onClose
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // LCD Display
            Surface(
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = display,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    ),
                    color = Color.White,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                        .testTag("calc_display")
                )
            }

            // Keypad Grid
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
                        .padding(vertical = 3.dp),
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
                                .height(44.dp)
                                .testTag("calc_btn_$label")
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Built-in Sticky Notes Window.
 */
@Composable
fun WindowsStickyNotesMiniApp(
    text: String,
    onTextChange: (String) -> Unit,
    onClose: () -> Unit
) {
    WindowFrame(
        title = "Sticky Notes",
        iconKey = "note",
        onClose = onClose
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .testTag("sticky_notes_input"),
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Notes are saved automatically.",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0x88FFFFFF)
            )
        }
    }
}

/**
 * Built-in Quick Terminal Shell Window.
 */
@Composable
fun WindowsTerminalMiniApp(onClose: () -> Unit) {
    var commandInput by remember { mutableStateOf("") }
    val history = remember {
        mutableStateListOf(
            "Windows PowerShell [Version 10.0.22631]",
            "(c) Microsoft Corporation. Android Desktop Subsystem.",
            "Type 'help' or 'info' for available commands.\n"
        )
    }

    WindowFrame(
        title = "Terminal - Windows Command Prompt",
        iconKey = "terminal",
        onClose = onClose
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color(0xFF0C0C0C), RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(history) { line ->
                    Text(
                        text = line,
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
                    text = "PS C:\\Users\\Desktop> ",
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
                        history.add("PS C:\\Users\\Desktop> $commandInput")
                        when (cmd) {
                            "help" -> history.add("Commands: help, date, time, system, clear, exit")
                            "date", "time" -> history.add(java.util.Date().toString())
                            "system" -> history.add("OS: Android Desktop Engine (WinLauncher v1.0)")
                            "clear", "cls" -> history.clear()
                            "exit" -> onClose()
                            else -> history.add("'$cmd' is not recognized as an internal or external command.")
                        }
                        commandInput = ""
                    },
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Run", fontSize = 10.sp)
                }
            }
        }
    }
}
