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
import com.example.ui.theme.*
import com.example.viewmodel.MomentoViewModel

@Composable
fun SettingsScreen(viewModel: MomentoViewModel, onNavigateBack: () -> Unit) {
    var showClearDialog by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All Data", color = MomentoOnSurface) },
            text = { Text("This will permanently delete all tasks, goals, habits, events, expenses, and notes. This cannot be undone.", color = MomentoOnSurfaceVariant) },
            confirmButton = { TextButton(onClick = { showClearDialog = false }) { Text("Cancel", color = MomentoOnSurfaceVariant) } },
            dismissButton = { TextButton(onClick = { showClearDialog = false }) { Text("Delete All", color = MomentoError) } },
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
                    SettingsInfoRow(Icons.Default.DarkMode, "Theme", "Dark (Default)", MomentoPrimary)
                    SettingsInfoRow(Icons.Default.Palette, "Accent Color", "Indigo / Purple", MomentoSecondary)
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
                    SettingsActionRow(Icons.Default.Backup, "Backup Data", "Export to local storage", MomentoPrimary) {}
                    SettingsActionRow(Icons.Default.Restore, "Restore Data", "Import from backup", MomentoPrimary) {}
                    SettingsActionRow(Icons.Default.TableChart, "Export to CSV", "Export all data", MomentoTertiary) {}
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
                .background(Color.White.copy(alpha = 0.04f))
                .border(1.dp, Color.White.copy(alpha = 0.07f), RoundedCornerShape(18.dp))
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
