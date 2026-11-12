package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InstalledAppItem
import com.example.data.model.ShortcutItem
import com.example.ui.viewmodel.MathResult
import com.example.ui.viewmodel.SearchFilter

@Composable
fun QuickSearchContent(
    query: String,
    onQueryChange: (String) -> Unit,
    searchFilter: SearchFilter,
    onFilterChange: (SearchFilter) -> Unit,
    mathResult: MathResult,
    filteredApps: List<InstalledAppItem>,
    filteredShortcuts: List<ShortcutItem>,
    onAppClick: (InstalledAppItem) -> Unit,
    onShortcutClick: (ShortcutItem) -> Unit,
    onWebSearch: (query: String, engine: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current

    Column(modifier = modifier.fillMaxWidth()) {
        // Quick Search Input Bar
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    text = "Search apps, shortcuts, web, or math (e.g. 15 * 8)...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0x77FFFFFF)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF60CDFF),
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (query.isNotBlank()) {
                        onWebSearch(query, "google")
                    }
                }
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0x33000000),
                unfocusedContainerColor = Color(0x22000000),
                focusedBorderColor = Color(0xFF0078D4),
                unfocusedBorderColor = Color(0x33FFFFFF),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFF60CDFF)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("quick_search_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Search category filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(SearchFilter.values()) { filter ->
                val isSelected = searchFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { onFilterChange(filter) },
                    label = {
                        Text(
                            text = when (filter) {
                                SearchFilter.ALL -> "All Results"
                                SearchFilter.APPS -> "Apps (${filteredApps.size})"
                                SearchFilter.SHORTCUTS -> "Shortcuts (${filteredShortcuts.size})"
                                SearchFilter.WEB -> "Web Search"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0078D4),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0x1AFFFFFF),
                        labelColor = Color(0xCCFFFFFF)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = Color(0x33FFFFFF),
                        selectedBorderColor = Color(0xFF60CDFF)
                    ),
                    modifier = Modifier.testTag("search_filter_${filter.name}")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Live Math Calculation Card
        AnimatedVisibility(
            visible = mathResult.isValid,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                color = Color(0x330078D4),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x5560CDFF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("math_result_card")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0078D4))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${mathResult.expression} =",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xAAFFFFFF)
                            )
                            Text(
                                text = mathResult.result,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF60CDFF)
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(mathResult.result))
                        },
                        modifier = Modifier.testTag("copy_math_result")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy result",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Search Results List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 280.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // SHORTCUTS SECTION
            if (searchFilter == SearchFilter.ALL || searchFilter == SearchFilter.SHORTCUTS) {
                if (filteredShortcuts.isNotEmpty()) {
                    item {
                        Text(
                            text = "CUSTOM SHORTCUTS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp,
                                color = Color(0xFF60CDFF)
                            ),
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp, start = 4.dp)
                        )
                    }

                    items(filteredShortcuts, key = { it.id }) { shortcut ->
                        ShortcutSearchResultRow(
                            shortcut = shortcut,
                            onClick = { onShortcutClick(shortcut) }
                        )
                    }
                }
            }

            // APPS SECTION
            if (searchFilter == SearchFilter.ALL || searchFilter == SearchFilter.APPS) {
                if (filteredApps.isNotEmpty()) {
                    item {
                        Text(
                            text = "APPLICATIONS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp,
                                color = Color(0xFF60CDFF)
                            ),
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp, start = 4.dp)
                        )
                    }

                    items(filteredApps, key = { it.packageName + it.activityName }) { app ->
                        AppSearchResultRow(
                            app = app,
                            onClick = { onAppClick(app) }
                        )
                    }
                }
            }

            // WEB SEARCH ENGINE TRIGGERS
            if (query.isNotBlank()) {
                item {
                    Text(
                        text = "WEB SEARCH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            color = Color(0xFF60CDFF)
                        ),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp, start = 4.dp)
                    )
                }

                item {
                    WebSearchEngineRow(
                        title = "Search Google for \"$query\"",
                        engine = "google",
                        icon = Icons.Default.Search,
                        onClick = { onWebSearch(query, "google") }
                    )
                }

                item {
                    WebSearchEngineRow(
                        title = "Search Bing for \"$query\"",
                        engine = "bing",
                        icon = Icons.Default.OpenInBrowser,
                        onClick = { onWebSearch(query, "bing") }
                    )
                }

                item {
                    WebSearchEngineRow(
                        title = "Search DuckDuckGo for \"$query\"",
                        engine = "duckduckgo",
                        icon = Icons.Default.OpenInBrowser,
                        onClick = { onWebSearch(query, "duckduckgo") }
                    )
                }
            }
        }
    }
}

@Composable
fun ShortcutSearchResultRow(
    shortcut: ShortcutItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x18FFFFFF))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .testTag("search_result_shortcut_${shortcut.id}")
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(shortcut.colorHex))
        ) {
            Icon(
                imageVector = IconHelper.getIcon(shortcut.iconKey),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = shortcut.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = shortcut.target,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0x88FFFFFF),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector = Icons.Default.Launch,
            contentDescription = "Open",
            tint = Color(0x66FFFFFF),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun AppSearchResultRow(
    app: InstalledAppItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x12FFFFFF))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .testTag("search_result_app_${app.packageName}")
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF0078D4))
        ) {
            Text(
                text = app.label.take(1).uppercase(),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = app.label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = app.packageName,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0x66FFFFFF),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector = Icons.Default.Launch,
            contentDescription = "Launch app",
            tint = Color(0x66FFFFFF),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun WebSearchEngineRow(
    title: String,
    engine: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x0EFFFFFF))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp)
            .testTag("web_search_$engine")
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF60CDFF),
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Normal),
            color = Color(0xEEFFFFFF),
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Icon(
            imageVector = Icons.Default.OpenInBrowser,
            contentDescription = null,
            tint = Color(0x66FFFFFF),
            modifier = Modifier.size(14.dp)
        )
    }
}
