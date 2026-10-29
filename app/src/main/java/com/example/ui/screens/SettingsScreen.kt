package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.collectAsState
import com.example.ui.theme.*
import com.example.viewmodel.MomentoViewModel

@Composable
fun SettingsScreen(viewModel: MomentoViewModel, onNavigateBack: () -> Unit) {
    var showClearDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val storedDark by ThemeModeStore.dark.collectAsState()
    val darkTheme = storedDark ?: context.getSharedPreferences("momento_preferences", android.content.Context.MODE_PRIVATE).getBoolean("dark_theme", false)

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All Data", color = MomentoOnSurface) },
            text = { Text("This will permanently delete all tasks, goals, habits, events, expenses, and notes. This cannot be undone.", color = MomentoOnSurfaceVariant) },
            confirmButton = { TextButton(onClick = { viewModel.clearAllData(); showClearDialog = false }) { Text("Delete All", color = MomentoError) } },
            dismissButton = { TextButton(onClick = { showClearDialog = false }) { Text("Cancel", color = MomentoOnSurfaceVariant) } },
            containerColor = MomentoSurfaceContainerHigh
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null, tint = MomentoOnSurface) }
            Text("Settings", style = MaterialTheme.typography.titleLarge, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                SettingsSection("Appearance") {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(MomentoPrimary.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                            Icon(if (darkTheme) Icons.Default.DarkMode else Icons.Default.LightMode, null, tint = MomentoPrimary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Appearance", color = MomentoOnSurface, fontWeight = FontWeight.Medium)
                            Text(if (darkTheme) "Dark theme" else "Light theme", color = MomentoOnSurfaceVariant, fontSize = 12.sp)
                        }
                        Switch(checked = darkTheme, onCheckedChange = { ThemeModeStore.setDark(context, it) })
                    }
                    SettingsInfoRow(Icons.Default.Palette, "Accent Color", "Violet", MomentoSecondary)
                }
            }
            item {
                SettingsSection("Notifications") {
                    SettingsInfoRow(Icons.Default.Notifications, "Task Reminders", "Enabled", MomentoTertiary)
                    SettingsInfoRow(Icons.Default.Loop, "Habit Reminders", "Enabled", MomentoTertiary)
                    SettingsInfoRow(Icons.Default.Event, "Event Alerts", "Enabled", MomentoTertiary)
                }
            }
            item {
                SettingsSection("Data") {
                    SettingsInfoRow(Icons.Default.Storage, "Storage", "Saved on this device", MomentoTertiary)
                    SettingsInfoRow(Icons.Default.CloudOff, "Account", "Not required", MomentoOnSurfaceVariant)
                }
            }
            item {
                SettingsSection("Danger Zone") {
                    SettingsActionRow(Icons.Default.DeleteForever, "Clear All Data", "Permanently delete everything", MomentoError) { showClearDialog = true }
                }
            }
            item {
                SettingsSection("About") {
                    SettingsInfoRow(Icons.Default.Info, "Version", "1.0.0", MomentoOnSurfaceVariant)
                    SettingsInfoRow(Icons.Default.Code, "Built with", "Kotlin + Jetpack Compose", MomentoOnSurfaceVariant)
                    SettingsInfoRow(Icons.Default.Storage, "Database", "Room (Offline First)", MomentoOnSurfaceVariant)
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title.uppercase(), color = MomentoPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
                .background(MomentoSurfaceContainer)
                .border(1.dp, MomentoOutline, RoundedCornerShape(18.dp))
        ) { content() }
    }
}

@Composable
private fun SettingsInfoRow(icon: ImageVector, title: String, value: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(color.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, color = MomentoOnSurface, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
        Text(value, color = MomentoOnSurfaceVariant, fontSize = 12.sp)
    }
}

@Composable
private fun SettingsActionRow(icon: ImageVector, title: String, subtitle: String, color: Color, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(color.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = MomentoOnSurface, fontWeight = FontWeight.Medium)
            Text(subtitle, color = MomentoOnSurfaceVariant, fontSize = 11.sp)
        }
        Icon(Icons.Default.ChevronRight, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(18.dp))
    }
}

