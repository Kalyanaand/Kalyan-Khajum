package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.InstalledAppItem
import com.example.data.model.ShortcutItem
import com.example.data.model.ShortcutType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditShortcutDialog(
    itemToEdit: ShortcutItem?,
    installedApps: List<InstalledAppItem>,
    onDismiss: () -> Unit,
    onSave: (
        id: String?,
        title: String,
        type: ShortcutType,
        target: String,
        iconKey: String,
        colorHex: Long,
        pinToTaskbar: Boolean,
        pinToDesktop: Boolean
    ) -> Unit
) {
    var title by remember { mutableStateOf(itemToEdit?.title ?: "") }
    var selectedType by remember { mutableStateOf(itemToEdit?.type ?: ShortcutType.WEB_URL) }
    var target by remember { mutableStateOf(itemToEdit?.target ?: "") }
    var selectedIcon by remember { mutableStateOf(itemToEdit?.iconKey ?: "globe") }
    var selectedColor by remember { mutableLongStateOf(itemToEdit?.colorHex ?: 0xFF0078D4) }
    var pinToTaskbar by remember { mutableStateOf(itemToEdit?.isPinnedToTaskbar ?: false) }
    var pinToDesktop by remember { mutableStateOf(itemToEdit?.isPinnedToDesktop ?: true) }

    var isAppDropdownOpen by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF182234),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3360CDFF)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("add_shortcut_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (itemToEdit == null) "Create Custom Shortcut" else "Edit Shortcut",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Shortcut Name") },
                    placeholder = { Text("e.g. My Website, Calculator") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF0078D4),
                        unfocusedBorderColor = Color(0x33FFFFFF)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("shortcut_name_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Shortcut Type selector
                Text(
                    text = "Type",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0x88FFFFFF)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    listOf(
                        ShortcutType.WEB_URL to "Web URL",
                        ShortcutType.APP to "Android App",
                        ShortcutType.SYSTEM_ACTION to "System"
                    ).forEach { (type, label) ->
                        val isSelected = selectedType == type
                        Surface(
                            onClick = { selectedType = type },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFF0078D4) else Color(0x18FFFFFF),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFF60CDFF) else Color(0x22FFFFFF)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Target Input
                when (selectedType) {
                    ShortcutType.WEB_URL -> {
                        OutlinedTextField(
                            value = target,
                            onValueChange = { target = it },
                            label = { Text("Web URL") },
                            placeholder = { Text("https://example.com") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF0078D4),
                                unfocusedBorderColor = Color(0x33FFFFFF)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("shortcut_url_input")
                        )
                    }

                    ShortcutType.APP -> {
                        ExposedDropdownMenuBox(
                            expanded = isAppDropdownOpen,
                            onExpandedChange = { isAppDropdownOpen = !isAppDropdownOpen }
                        ) {
                            OutlinedTextField(
                                value = target,
                                onValueChange = { target = it },
                                label = { Text("Select or enter Package Name") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isAppDropdownOpen) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFF0078D4),
                                    unfocusedBorderColor = Color(0x33FFFFFF)
                                ),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = isAppDropdownOpen,
                                onDismissRequest = { isAppDropdownOpen = false },
                                modifier = Modifier.background(Color(0xFF1E293B))
                            ) {
                                installedApps.take(15).forEach { app ->
                                    DropdownMenuItem(
                                        text = { Text(app.label, color = Color.White) },
                                        onClick = {
                                            target = app.packageName
                                            if (title.isBlank()) title = app.label
                                            isAppDropdownOpen = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    ShortcutType.SYSTEM_ACTION -> {
                        OutlinedTextField(
                            value = target,
                            onValueChange = { target = it },
                            label = { Text("Action (settings, wifi, sound, display)") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF0078D4),
                                unfocusedBorderColor = Color(0x33FFFFFF)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    ShortcutType.MINI_APP -> {
                        Text(
                            text = "Target Mini App: $target",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xAAFFFFFF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Icon Selection
                Text(
                    text = "Select Icon",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0x88FFFFFF)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    items(IconHelper.availableIcons) { (key, name) ->
                        val isSelected = selectedIcon == key
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(selectedColor) else Color(0x22FFFFFF))
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFF60CDFF) else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedIcon = key }
                        ) {
                            Icon(
                                imageVector = IconHelper.getIcon(key),
                                contentDescription = name,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Color Selection
                Text(
                    text = "Tile Color",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0x88FFFFFF)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    items(IconHelper.availableColors) { (hex, name) ->
                        val isSelected = selectedColor == hex
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(hex))
                                .border(
                                    2.dp,
                                    if (isSelected) Color.White else Color.Transparent,
                                    CircleShape
                                )
                                .clickable { selectedColor = hex }
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Pin Toggles
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = pinToDesktop,
                        onCheckedChange = { pinToDesktop = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF0078D4),
                            uncheckedColor = Color(0x66FFFFFF)
                        )
                    )
                    Text("Pin to Desktop", style = MaterialTheme.typography.bodySmall, color = Color.White)

                    Spacer(modifier = Modifier.width(16.dp))

                    Checkbox(
                        checked = pinToTaskbar,
                        onCheckedChange = { pinToTaskbar = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF0078D4),
                            uncheckedColor = Color(0x66FFFFFF)
                        )
                    )
                    Text("Pin to Taskbar", style = MaterialTheme.typography.bodySmall, color = Color.White)
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color(0x88FFFFFF))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank() && target.isNotBlank()) {
                                onSave(
                                    itemToEdit?.id,
                                    title,
                                    selectedType,
                                    target,
                                    selectedIcon,
                                    selectedColor,
                                    pinToTaskbar,
                                    pinToDesktop
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0078D4)),
                        modifier = Modifier.testTag("save_shortcut_button")
                    ) {
                        Text("Save Shortcut")
                    }
                }
            }
        }
    }
}
