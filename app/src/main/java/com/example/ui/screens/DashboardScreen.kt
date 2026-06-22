package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.MomentoViewModel

@Composable
fun DashboardScreen(viewModel: MomentoViewModel) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            GlassCard {
                Column {
                    Text("Thursday, Oct 26", style = MaterialTheme.typography.labelSmall, color = MomentoOnSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Good morning, Alex.", style = MaterialTheme.typography.displayLarge, color = MomentoOnSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("You have ${tasks.count { !it.isCompleted }} high-priority tasks and ${events.size} event today.", style = MaterialTheme.typography.bodyMedium, color = MomentoOnSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MomentoPrimaryContainer, contentColor = MomentoOnPrimaryContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Focus Mode", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(modifier = Modifier.weight(1f), icon = Icons.Default.TaskAlt, title = "Tasks", value = tasks.size.toString(), subText = "↑ 2", color = MomentoPrimary)
                StatCard(modifier = Modifier.weight(1f), icon = Icons.Default.EmojiEvents, title = "Goals", value = goals.size.toString(), subText = "On track", color = MomentoTertiary)
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(modifier = Modifier.weight(1f), icon = Icons.Default.CalendarMonth, title = "Events", value = events.size.toString(), subText = "Today", color = MomentoSecondary)
                StatCard(modifier = Modifier.weight(1f), icon = Icons.Default.Payments, title = "Expenses", value = "$1.2k", subText = "↓ 5%", color = MomentoError)
            }
        }

        item {
            GlassCard(alpha = 0.1f) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Today's Focus", style = MaterialTheme.typography.headlineMedium, color = MomentoOnSurface)
                        Text("View All", style = MaterialTheme.typography.labelSmall, color = MomentoPrimary)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        tasks.take(3).forEach { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(20.dp).border(2.dp, MomentoPrimary, RoundedCornerShape(4.dp)))
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(task.title, style = MaterialTheme.typography.bodyLarge, color = MomentoOnSurface)
                                    Text("Priority: ${task.priority.name}", style = MaterialTheme.typography.labelSmall, color = MomentoOnSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, icon: ImageVector, title: String, value: String, subText: String, color: Color) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(16.dp)).background(color.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.labelSmall, color = MomentoOnSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Text(value, style = MaterialTheme.typography.headlineMedium, color = MomentoOnSurface)
                Text(subText, style = MaterialTheme.typography.labelSmall, color = color)
            }
        }
    }
}
