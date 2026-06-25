package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Habit
import com.example.ui.components.AddHabitDialog
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.MomentoViewModel

@Composable
fun HabitsScreen(viewModel: MomentoViewModel) {
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Habit?>(null) }

    if (showAddDialog) {
        AddHabitDialog(onDismiss = { showAddDialog = false }) { name, desc, freq, color ->
            viewModel.addHabit(name, desc, freq, color)
        }
    }
    showDeleteDialog?.let { habit ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Habit", color = MomentoOnSurface) },
            text = { Text("Remove \"${habit.name}\"? Your streak will be lost.", color = MomentoOnSurfaceVariant) },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteHabit(habit); showDeleteDialog = null }) {
                    Text("Delete", color = MomentoError)
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = null }) { Text("Cancel", color = MomentoOnSurfaceVariant) } },
            containerColor = MomentoSurfaceContainerHigh
        )
    }

    val completedCount = habits.count { viewModel.isHabitCompletedToday(it) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(8.dp))

        // Header
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Habits", style = MaterialTheme.typography.displaySmall, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
                Text("Build routines, one day at a time.", style = MaterialTheme.typography.bodySmall, color = MomentoOnSurfaceVariant)
            }
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.size(48.dp),
                containerColor = Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(MomentoPrimary, MomentoSecondary)), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Add, null, tint = Color.White) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Today progress card
        GlassCard {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Today's Progress", style = MaterialTheme.typography.titleMedium, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$completedCount of ${habits.size} habits done", color = MomentoOnSurfaceVariant, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { if (habits.isEmpty()) 0f else completedCount.toFloat() / habits.size },
                        modifier = Modifier.width(180.dp).height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = MomentoTertiary,
                        trackColor = Color.White.copy(alpha = 0.08f)
                    )
                }
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp)) {
                    val animProgress by animateFloatAsState(
                        targetValue = if (habits.isEmpty()) 0f else completedCount.toFloat() / habits.size,
                        animationSpec = tween(800, easing = EaseOutCubic), label = "habitProgress"
                    )
                    CircularProgressIndicator(
                        progress = { animProgress },
                        modifier = Modifier.fillMaxSize(),
                        color = MomentoTertiary,
                        trackColor = Color.White.copy(alpha = 0.06f),
                        strokeWidth = 6.dp
                    )
                    Text("${(animProgress * 100).toInt()}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MomentoOnSurface)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("All Habits", style = MaterialTheme.typography.titleMedium, color = MomentoOnSurface, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(10.dp))

        if (habits.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Loop, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No habits yet", color = MomentoOnSurfaceVariant, style = MaterialTheme.typography.bodyLarge)
                    Text("Tap + to add your first habit", color = MomentoOnSurfaceVariant, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                items(habits, key = { it.id }) { habit ->
                    HabitCard(habit = habit, viewModel = viewModel, onDelete = { showDeleteDialog = habit })
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun HabitCard(habit: Habit, viewModel: MomentoViewModel, onDelete: () -> Unit) {
    val done = viewModel.isHabitCompletedToday(habit)
    val last7 = viewModel.getHabitLast7Days(habit)
    val habitColor = try { Color(android.graphics.Color.parseColor(habit.color)) } catch (e: Exception) { MomentoPrimary }

    var expanded by remember { mutableStateOf(false) }
    val animBorder by animateColorAsState(
        targetValue = if (done) habitColor.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.08f),
        animationSpec = tween(400), label = "habitBorder"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (done) habitColor.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.04f))
            .border(1.dp, animBorder, RoundedCornerShape(20.dp))
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            // Check button
            Box(
                modifier = Modifier.size(44.dp)
                    .clip(CircleShape)
                    .background(if (done) habitColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.06f))
                    .border(2.dp, if (done) habitColor else Color.White.copy(alpha = 0.15f), CircleShape)
                    .clickable { viewModel.toggleHabitForToday(habit) },
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(targetState = done, label = "check") { isDone ->
                    if (isDone) Icon(Icons.Default.Check, null, tint = habitColor, modifier = Modifier.size(22.dp))
                    else Icon(Icons.Default.Circle, null, tint = Color.Transparent, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(habit.name, color = MomentoOnSurface, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🔥 ${habit.currentStreak} day streak", color = MomentoOnSurfaceVariant, fontSize = 12.sp)
                    Text("• ${habit.frequency}", color = habitColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Best", color = MomentoOnSurfaceVariant, fontSize = 10.sp)
                Text("${habit.longestStreak}d", color = habitColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.DeleteOutline, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(16.dp))
            }
        }

        // 7-day heatmap
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            val days = listOf("M", "T", "W", "T", "F", "S", "S")
            last7.forEachIndexed { i, completed ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier.size(28.dp).clip(RoundedCornerShape(6.dp))
                            .background(if (completed) habitColor.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.06f))
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(days[i], color = MomentoOnSurfaceVariant, fontSize = 9.sp)
                }
            }
        }

        // Expanded description
        AnimatedVisibility(visible = expanded && habit.description.isNotBlank()) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                Spacer(modifier = Modifier.height(8.dp))
                Text(habit.description, color = MomentoOnSurfaceVariant, fontSize = 13.sp)
            }
        }
    }
}
