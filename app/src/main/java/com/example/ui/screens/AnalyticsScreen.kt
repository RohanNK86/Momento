package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Goal
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.MomentoViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AnalyticsScreen(viewModel: MomentoViewModel) {
    val tasks  by viewModel.tasks.collectAsStateWithLifecycle()
    val goals  by viewModel.goals.collectAsStateWithLifecycle()
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()

    val completionRate = if (tasks.isEmpty()) 0f else tasks.count { it.isCompleted }.toFloat() / tasks.size
    val goalsDoneRate  = if (goals.isEmpty()) 0f else goals.count { it.status == Goal.GoalStatus.COMPLETED }.toFloat() / goals.size
    val habitsToday    = habits.count { viewModel.isHabitCompletedToday(it) }
    val habitRate      = if (habits.isEmpty()) 0f else habitsToday.toFloat() / habits.size
    val overdueTasks   = tasks.filter { !it.isCompleted && it.dueDate != null && it.dueDate < System.currentTimeMillis() }
    val totalExpenses  = expenses.sumOf { it.amount }

    // Weekly task completion (last 7 days bars)
    val sdf7 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val cal7 = Calendar.getInstance()
    val weekBars = (6 downTo 0).map { daysAgo ->
        cal7.time = Date(); cal7.add(Calendar.DAY_OF_YEAR, -daysAgo)
        val dayStr = sdf7.format(cal7.time)
        val dayLabel = SimpleDateFormat("EEE", Locale.getDefault()).format(cal7.time)
        val done = tasks.count { it.isCompleted && it.createdAt != 0L && sdf7.format(Date(it.createdAt)) == dayStr }
        dayLabel to done
    }
    val maxBar = weekBars.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item {
            Text("Analytics", style = MaterialTheme.typography.displaySmall, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
            Text("Your productivity at a glance", color = MomentoOnSurfaceVariant, fontSize = 13.sp)
        }

        // ── Animated Arc Stats Row ─────────────────────────────────────────────
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AnimatedArcCard(Modifier.weight(1f), "Tasks", completionRate, MomentoPrimary, "${tasks.count { it.isCompleted }}/${tasks.size}")
                AnimatedArcCard(Modifier.weight(1f), "Goals", goalsDoneRate, MomentoTertiary, "${goals.count { it.status == Goal.GoalStatus.COMPLETED }}/${goals.size}")
                AnimatedArcCard(Modifier.weight(1f), "Habits", habitRate, MomentoSecondary, "$habitsToday/${habits.size}")
            }
        }

        // ── Weekly Activity ────────────────────────────────────────────────────
        item {
            GlassCard {
                Column {
                    Text("Weekly Task Completion", style = MaterialTheme.typography.titleMedium, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
                        weekBars.forEach { (day, count) ->
                            val animH by animateFloatAsState(targetValue = count.toFloat() / maxBar, animationSpec = tween(800, easing = EaseOutCubic), label = "bar_$day")
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(if (count > 0) count.toString() else "", color = MomentoPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(3.dp))
                                Box(
                                    modifier = Modifier.width(28.dp).height((animH * 80).dp.coerceAtLeast(4.dp))
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(if (count > 0) MomentoPrimary.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.06f))
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(day, color = MomentoOnSurfaceVariant, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        // ── Summary Cards ──────────────────────────────────────────────────────
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryStatCard(Modifier.weight(1f), "Total Tasks", tasks.size.toString(), Icons.Default.TaskAlt, MomentoPrimary)
                SummaryStatCard(Modifier.weight(1f), "Overdue", overdueTasks.size.toString(), Icons.Default.Warning, MomentoError)
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryStatCard(Modifier.weight(1f), "Events", events.size.toString(), Icons.Default.Event, MomentoSecondary)
                SummaryStatCard(Modifier.weight(1f), "Expenses", "₹${String.format(java.util.Locale.US, "%.0f", totalExpenses)}", Icons.Default.Payments, Color(0xFFF59E0B))
            }
        }

        // ── Overdue Tasks ──────────────────────────────────────────────────────
        if (overdueTasks.isNotEmpty()) {
            item {
                GlassCard {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Warning, null, tint = MomentoError, modifier = Modifier.size(18.dp))
                            Text("Overdue Tasks", style = MaterialTheme.typography.titleMedium, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        overdueTasks.take(5).forEach { task ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(MomentoError))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(task.title, color = MomentoOnSurface, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    task.dueDate?.let { Text("Due: ${SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(it))}", color = MomentoError, fontSize = 11.sp) }
                                }
                                Text(task.priority.name, color = MomentoError, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(MomentoError.copy(alpha = 0.1f)).padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }

        // ── Goal Progress Breakdown ────────────────────────────────────────────
        if (goals.isNotEmpty()) {
            item {
                GlassCard {
                    Column {
                        Text("Goal Breakdown", style = MaterialTheme.typography.titleMedium, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        goals.forEach { goal ->
                            val gc = when (goal.status) {
                                Goal.GoalStatus.COMPLETED -> MomentoTertiary
                                Goal.GoalStatus.AT_RISK -> MomentoError
                                Goal.GoalStatus.ON_TRACK -> MomentoPrimary
                            }
                            val animP by animateFloatAsState(targetValue = goal.progress, animationSpec = tween(800, easing = EaseOutCubic), label = "goal_${goal.id}")
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(goal.title, color = MomentoOnSurface, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                                        Text("${(animP * 100).toInt()}%", color = gc, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { animP }, modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                                        color = gc, trackColor = Color.White.copy(alpha = 0.06f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Expense Category Breakdown ─────────────────────────────────────────
        if (expenses.isNotEmpty()) {
            item {
                GlassCard {
                    Column {
                        Text("Expense by Category", style = MaterialTheme.typography.titleMedium, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        val catColors = listOf(MomentoPrimary, MomentoSecondary, MomentoTertiary, MomentoError, Color(0xFFF59E0B))
                        expenses.groupBy { it.category }.entries.forEachIndexed { idx, (cat, items) ->
                            val catTotal = items.sumOf { it.amount }
                            val ratio = (catTotal / totalExpenses).toFloat()
                            val cc = catColors[idx % catColors.size]
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(cc))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(cat, color = MomentoOnSurface, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                Text("₹${String.format(java.util.Locale.US, "%.0f", catTotal)}", color = cc, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            LinearProgressIndicator(
                                progress = { ratio }, modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)).padding(start = 18.dp),
                                color = cc, trackColor = Color.White.copy(alpha = 0.06f)
                            )
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(90.dp)) }
    }
}

@Composable
private fun AnimatedArcCard(modifier: Modifier, label: String, progress: Float, color: Color, countLabel: String) {
    val animP by animateFloatAsState(targetValue = progress, animationSpec = tween(1000, easing = EaseOutCubic), label = "arc_$label")
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(68.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeW = 8.dp.toPx()
                val inset = strokeW / 2
                drawArc(color = color.copy(alpha = 0.12f), startAngle = -90f, sweepAngle = 360f, useCenter = false, topLeft = Offset(inset, inset), size = Size(size.width - strokeW, size.height - strokeW), style = Stroke(strokeW, cap = StrokeCap.Round))
                drawArc(color = color, startAngle = -90f, sweepAngle = animP * 360f, useCenter = false, topLeft = Offset(inset, inset), size = Size(size.width - strokeW, size.height - strokeW), style = Stroke(strokeW, cap = StrokeCap.Round))
            }
            Text("${(animP * 100).toInt()}%", color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, color = MomentoOnSurface, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Text(countLabel, color = MomentoOnSurfaceVariant, fontSize = 11.sp)
    }
}

@Composable
private fun SummaryStatCard(modifier: Modifier, label: String, value: String, icon: ImageVector, color: Color) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        }
        Column {
            Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(label, color = MomentoOnSurfaceVariant, fontSize = 11.sp)
        }
    }
}
