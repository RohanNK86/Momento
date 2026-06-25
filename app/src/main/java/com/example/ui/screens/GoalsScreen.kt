package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Goal
import com.example.ui.components.AddGoalDialog
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.MomentoViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GoalsScreen(viewModel: MomentoViewModel) {
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var progressTarget by remember { mutableStateOf<Goal?>(null) }
    var deleteTarget by remember { mutableStateOf<Goal?>(null) }

    if (showAddDialog) {
        AddGoalDialog(onDismiss = { showAddDialog = false }) { title ->
            viewModel.addGoal(title)
        }
    }
    progressTarget?.let { goal ->
        var newProgress by remember(goal) { mutableStateOf(goal.currentValue) }
        AlertDialog(
            onDismissRequest = { progressTarget = null },
            title = { Text("Update Progress", color = MomentoOnSurface) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${goal.title} — ${newProgress.toInt()}%", color = MomentoOnSurfaceVariant)
                    Slider(value = newProgress, onValueChange = { newProgress = it }, valueRange = 0f..goal.targetValue, colors = SliderDefaults.colors(thumbColor = MomentoTertiary, activeTrackColor = MomentoTertiary))
                }
            },
            confirmButton = { Button(onClick = { viewModel.updateGoalProgress(goal, newProgress); progressTarget = null }, colors = ButtonDefaults.buttonColors(containerColor = MomentoTertiary)) { Text("Update") } },
            dismissButton = { TextButton(onClick = { progressTarget = null }) { Text("Cancel", color = MomentoOnSurfaceVariant) } },
            containerColor = MomentoSurfaceContainerHigh
        )
    }
    deleteTarget?.let { goal ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete Goal", color = MomentoOnSurface) },
            text = { Text("Remove \"${goal.title}\"?", color = MomentoOnSurfaceVariant) },
            confirmButton = { TextButton(onClick = { viewModel.deleteGoal(goal); deleteTarget = null }) { Text("Delete", color = MomentoError) } },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("Cancel", color = MomentoOnSurfaceVariant) } },
            containerColor = MomentoSurfaceContainerHigh
        )
    }

    val active    = goals.filter { it.status != Goal.GoalStatus.COMPLETED }
    val completed = goals.filter { it.status == Goal.GoalStatus.COMPLETED }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Goals", style = MaterialTheme.typography.displaySmall, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
                Text("${completed.size}/${goals.size} achieved", color = MomentoOnSurfaceVariant, fontSize = 13.sp)
            }
            FloatingActionButton(onClick = { showAddDialog = true }, modifier = Modifier.size(48.dp), containerColor = Color.Transparent, shape = RoundedCornerShape(14.dp)) {
                Box(modifier = Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(MomentoTertiary, MomentoPrimary)), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.weight(1f)) {
            items(active, key = { it.id }) { goal ->
                GoalCard(goal, onUpdateProgress = { progressTarget = goal }, onDelete = { deleteTarget = goal })
            }
            if (completed.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.CheckCircle, null, tint = MomentoTertiary, modifier = Modifier.size(16.dp))
                        Text("ACHIEVED", color = MomentoTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                    }
                }
                items(completed, key = { it.id }) { goal ->
                    AchievedGoalItem(goal, onDelete = { deleteTarget = goal })
                }
            }
            if (goals.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.EmojiEvents, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No goals yet", color = MomentoOnSurfaceVariant, style = MaterialTheme.typography.bodyLarge)
                            Text("Tap + to set your first goal", color = MomentoOnSurfaceVariant, fontSize = 13.sp)
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun GoalCard(goal: Goal, onUpdateProgress: () -> Unit, onDelete: () -> Unit) {
    val statusColor = when (goal.status) { Goal.GoalStatus.ON_TRACK -> MomentoTertiary; Goal.GoalStatus.AT_RISK -> MomentoError; Goal.GoalStatus.COMPLETED -> MomentoPrimary }
    val animP by animateFloatAsState(goal.progress, tween(1000, easing = EaseOutCubic), label = "gp_${goal.id}")
    val daysLeft = ((goal.targetDateMillis - System.currentTimeMillis()) / 86400000L).toInt()
    val dateFmt = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(Color.White.copy(alpha = 0.05f)).border(1.dp, statusColor.copy(alpha = 0.25f), RoundedCornerShape(22.dp)).padding(20.dp)) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(statusColor.copy(alpha = 0.12f)).border(1.dp, statusColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                        Text(goal.status.name.replace("_", " "), color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(goal.title, style = MaterialTheme.typography.titleLarge, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
                    if (goal.description.isNotBlank()) Text(goal.description, color = MomentoOnSurfaceVariant, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Target: ${dateFmt.format(Date(goal.targetDateMillis))}", color = MomentoOnSurfaceVariant, fontSize = 12.sp)
                }
                Row {
                    IconButton(onClick = onUpdateProgress, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Edit, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(16.dp)) }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Delete, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(16.dp)) }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                // Animated arc
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val sw = 9.dp.toPx(); val inset = sw / 2
                        drawArc(color = statusColor.copy(alpha = 0.1f), -90f, 360f, false, topLeft = Offset(inset, inset), size = Size(size.width - sw, size.height - sw), style = Stroke(sw, cap = StrokeCap.Round))
                        drawArc(color = statusColor, -90f, animP * 360f, false, topLeft = Offset(inset, inset), size = Size(size.width - sw, size.height - sw), style = Stroke(sw, cap = StrokeCap.Round))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${(animP * 100).toInt()}%", color = statusColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(if (daysLeft >= 0) daysLeft.toString() else "0", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = MomentoOnSurface)
                    Text(if (daysLeft >= 0) "Days Left" else "Overdue", color = MomentoOnSurfaceVariant, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onUpdateProgress, modifier = Modifier.fillMaxWidth().height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = statusColor.copy(alpha = 0.15f), contentColor = statusColor), shape = RoundedCornerShape(10.dp)) {
                Icon(Icons.Default.TrendingUp, null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Update Progress", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun AchievedGoalItem(goal: Goal, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MomentoTertiary.copy(alpha = 0.05f)).border(1.dp, MomentoTertiary.copy(alpha = 0.15f), RoundedCornerShape(16.dp)).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(MomentoTertiary.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.EmojiEvents, null, tint = MomentoTertiary, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(goal.title, color = MomentoOnSurface, fontWeight = FontWeight.SemiBold)
            Text("100% Complete 🎉", color = MomentoTertiary, fontSize = 12.sp)
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Delete, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(14.dp)) }
    }
}
