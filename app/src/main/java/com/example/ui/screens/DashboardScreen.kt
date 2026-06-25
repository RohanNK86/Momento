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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.MomentoViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: MomentoViewModel,
    onFocusModeClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()

    val name = profile?.name ?: "Rohan"
    val greeting = viewModel.getGreeting(name)
    val todayQuote = remember { viewModel.getTodayQuote() }

    // Live clock
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) { delay(60_000L); currentTime = System.currentTimeMillis() }
    }
    val timeFmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val timeString = timeFmt.format(Date(currentTime))
    val dateFmt = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
    val dateString = dateFmt.format(Date(currentTime))

    // Stats
    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val todayEnd = todayStart + 86400000L
    val todayTasks = tasks.filter { it.isToday }
    val completedToday = todayTasks.count { it.isCompleted }
    val pendingToday = todayTasks.count { !it.isCompleted }
    val todayEvents = events.count { it.timeMillis in todayStart..todayEnd }
    val habitsToday = habits.count { viewModel.isHabitCompletedToday(it) }
    val goalProgress = if (goals.isEmpty()) 0f else goals.count { it.status == com.example.data.Goal.GoalStatus.COMPLETED }.toFloat() / goals.size
    val upcomingEvents = events.filter { it.timeMillis >= System.currentTimeMillis() }.take(3)
    val highPriorityTasks = tasks.filter { !it.isCompleted && it.priority == com.example.data.Task.Priority.HIGH }.take(3)

    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(100); visible = true }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // ── Greeting Card ─────────────────────────────────────────────────────
        item {
            AnimatedVisibility(visible = visible, enter = fadeIn() + slideInVertically { -40 }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(MomentoPrimary.copy(alpha = 0.85f), MomentoSecondary.copy(alpha = 0.7f))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(greeting, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(dateString, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                            }
                            Text(timeString, style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MiniStatChip("$completedToday/${ todayTasks.size} Tasks", Icons.Default.TaskAlt)
                            MiniStatChip("$habitsToday/${habits.size} Habits", Icons.Default.Loop)
                            MiniStatChip("$todayEvents Events", Icons.Default.CalendarMonth)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onFocusModeClick,
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f), contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Timer, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Start Pomodoro", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // ── Quick Stats ───────────────────────────────────────────────────────
        item {
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(300, 100)) + slideInVertically(tween(300, 100)) { 40 }) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickStatCard(Modifier.weight(1f), "Pending", pendingToday.toString(), Icons.Default.HourglassTop, MomentoError)
                    QuickStatCard(Modifier.weight(1f), "Goals", "${(goalProgress * 100).toInt()}%", Icons.Default.EmojiEvents, MomentoTertiary)
                    QuickStatCard(Modifier.weight(1f), "Events", todayEvents.toString(), Icons.Default.Event, MomentoSecondary)
                }
            }
        }

        // ── Today's Quote ─────────────────────────────────────────────────────
        item {
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(400, 150))) {
                GlassCard(alpha = 0.07f) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.FormatQuote, null, tint = MomentoPrimary, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(todayQuote.first, style = MaterialTheme.typography.bodyMedium, color = MomentoOnSurface, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("— ${todayQuote.second}", style = MaterialTheme.typography.labelSmall, color = MomentoOnSurfaceVariant)
                        }
                    }
                }
            }
        }

        // ── Today's Focus Tasks ───────────────────────────────────────────────
        item {
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(500, 200)) + slideInVertically(tween(400, 200)) { 40 }) {
                GlassCard {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Bolt, null, tint = MomentoError, modifier = Modifier.size(18.dp))
                                Text("High Priority", style = MaterialTheme.typography.titleMedium, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
                            }
                            TextButton(onClick = { onNavigate("tasks") }) {
                                Text("See All", color = MomentoPrimary, fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (highPriorityTasks.isEmpty()) {
                            Text("🎉 All high-priority tasks done!", color = MomentoTertiary, style = MaterialTheme.typography.bodyMedium)
                        } else {
                            highPriorityTasks.forEach { task ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                                        .background(MomentoError.copy(alpha = 0.05f))
                                        .clickable { viewModel.toggleTaskCompletion(task) }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier.size(20.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, MomentoError.copy(alpha = 0.7f), CircleShape)
                                            .background(if (task.isCompleted) MomentoError.copy(alpha = 0.3f) else Color.Transparent),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (task.isCompleted) Icon(Icons.Default.Check, null, tint = MomentoError, modifier = Modifier.size(12.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(task.title, color = MomentoOnSurface, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }
                }
            }
        }

        // ── Habits Today ──────────────────────────────────────────────────────
        item {
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(600, 250)) + slideInVertically(tween(400, 250)) { 40 }) {
                GlassCard {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Loop, null, tint = MomentoTertiary, modifier = Modifier.size(18.dp))
                                Text("Today's Habits", style = MaterialTheme.typography.titleMedium, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
                            }
                            TextButton(onClick = { onNavigate("habits") }) {
                                Text("See All", color = MomentoPrimary, fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(habits.take(5)) { habit ->
                                val done = viewModel.isHabitCompletedToday(habit)
                                val habitColor = try { Color(android.graphics.Color.parseColor(habit.color)) } catch (e: Exception) { MomentoPrimary }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (done) habitColor.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f))
                                        .border(1.dp, if (done) habitColor.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                                        .clickable { viewModel.toggleHabitForToday(habit) }
                                        .padding(horizontal = 14.dp, vertical = 12.dp)
                                ) {
                                    Icon(if (done) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked, null, tint = if (done) habitColor else MomentoOnSurfaceVariant, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(habit.name.take(10), color = if (done) habitColor else MomentoOnSurface, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    Text("🔥 ${habit.currentStreak}", color = MomentoOnSurfaceVariant, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Upcoming Events ───────────────────────────────────────────────────
        item {
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(700, 300)) + slideInVertically(tween(400, 300)) { 40 }) {
                GlassCard {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.CalendarMonth, null, tint = MomentoSecondary, modifier = Modifier.size(18.dp))
                                Text("Upcoming Events", style = MaterialTheme.typography.titleMedium, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
                            }
                            TextButton(onClick = { onNavigate("events") }) {
                                Text("See All", color = MomentoPrimary, fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (upcomingEvents.isEmpty()) {
                            Text("No upcoming events. Add one!", color = MomentoOnSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                        } else {
                            upcomingEvents.forEach { event ->
                                val eventColor = try { Color(android.graphics.Color.parseColor(event.color)) } catch (e: Exception) { MomentoPrimary }
                                val timeFmtE = SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault())
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(4.dp, 36.dp).clip(RoundedCornerShape(2.dp)).background(eventColor))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(event.title, color = MomentoOnSurface, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                        Text(timeFmtE.format(Date(event.timeMillis)), color = MomentoOnSurfaceVariant, fontSize = 11.sp)
                                    }
                                    Box(
                                        modifier = Modifier.clip(RoundedCornerShape(6.dp))
                                            .background(eventColor.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) { Text(event.category, color = eventColor, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Quick Shortcuts ───────────────────────────────────────────────────
        item {
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(800, 350))) {
                Column {
                    Text("Quick Access", style = MaterialTheme.typography.titleMedium, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ShortcutCard(Modifier.weight(1f), "Goals", Icons.Default.EmojiEvents, MomentoTertiary) { onNavigate("goals") }
                        ShortcutCard(Modifier.weight(1f), "Notes", Icons.Default.Notes, MomentoSecondary) { onNavigate("notes") }
                        ShortcutCard(Modifier.weight(1f), "Analytics", Icons.Default.Analytics, MomentoPrimary) { onNavigate("analytics") }
                        ShortcutCard(Modifier.weight(1f), "Search", Icons.Default.Search, MomentoOnSurfaceVariant) { onNavigate("search") }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(90.dp)) }
    }
}

@Composable
private fun MiniStatChip(label: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(12.dp))
        Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun QuickStatCard(modifier: Modifier, label: String, value: String, icon: ImageVector, color: Color) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.height(6.dp))
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(label, color = MomentoOnSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ShortcutCard(modifier: Modifier, label: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null, tint = color, modifier = Modifier.size(18.dp)) }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, color = MomentoOnSurface, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}
