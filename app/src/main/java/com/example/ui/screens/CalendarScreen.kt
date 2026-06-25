package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Event
import com.example.data.Task
import com.example.ui.components.AddEventDialog
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.MomentoViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(viewModel: MomentoViewModel) {
    val events by viewModel.events.collectAsStateWithLifecycle()
    val tasks  by viewModel.tasks.collectAsStateWithLifecycle()
    var showAddEvent by remember { mutableStateOf(false) }

    if (showAddEvent) {
        AddEventDialog(onDismiss = { showAddEvent = false }) { title, category ->
            viewModel.addEvent(title, System.currentTimeMillis(), category)
        }
    }

    // Calendar state
    val today = remember { Calendar.getInstance() }
    var displayCal by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDay by remember { mutableStateOf(today.get(Calendar.DAY_OF_MONTH)) }
    var selectedMonth by remember { mutableStateOf(today.get(Calendar.MONTH)) }
    var selectedYear by remember { mutableStateOf(today.get(Calendar.YEAR)) }

    fun navigateMonth(delta: Int) {
        val newCal = displayCal.clone() as Calendar
        newCal.add(Calendar.MONTH, delta)
        displayCal = newCal
        selectedMonth = newCal.get(Calendar.MONTH)
        selectedYear = newCal.get(Calendar.YEAR)
        selectedDay = if (selectedMonth == today.get(Calendar.MONTH) && selectedYear == today.get(Calendar.YEAR))
            today.get(Calendar.DAY_OF_MONTH) else 1
    }

    // Selected day bounds
    val selCal = Calendar.getInstance().apply { set(selectedYear, selectedMonth, selectedDay, 0, 0, 0); set(Calendar.MILLISECOND, 0) }
    val dayStart = selCal.timeInMillis
    val dayEnd   = dayStart + 86400000L

    val dayEvents = events.filter { it.timeMillis in dayStart..dayEnd }
    val dayTasks  = tasks.filter {
        val due = it.dueDate
        due != null && due in dayStart..dayEnd
    }

    // Calendar grid
    val calForGrid = displayCal.clone() as Calendar
    calForGrid.set(Calendar.DAY_OF_MONTH, 1)
    val firstDow   = calForGrid.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sun
    val daysInMonth = calForGrid.getActualMaximum(Calendar.DAY_OF_MONTH)
    val todayDay    = today.get(Calendar.DAY_OF_MONTH)
    val isCurrentMonth = today.get(Calendar.MONTH) == displayCal.get(Calendar.MONTH) && today.get(Calendar.YEAR) == displayCal.get(Calendar.YEAR)

    val monthYearStr = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(displayCal.time)

    // Dots: days that have events
    val eventDays = events.mapNotNull { ev ->
        val c = Calendar.getInstance().apply { timeInMillis = ev.timeMillis }
        if (c.get(Calendar.MONTH) == displayCal.get(Calendar.MONTH) && c.get(Calendar.YEAR) == displayCal.get(Calendar.YEAR))
            c.get(Calendar.DAY_OF_MONTH) else null
    }.toSet()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Events", style = MaterialTheme.typography.displaySmall, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
                    Text("$monthYearStr", color = MomentoOnSurfaceVariant, fontSize = 13.sp)
                }
                FloatingActionButton(onClick = { showAddEvent = true }, modifier = Modifier.size(48.dp), containerColor = Color.Transparent, shape = RoundedCornerShape(14.dp)) {
                    Box(modifier = Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(MomentoSecondary, MomentoPrimary)), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, null, tint = Color.White)
                    }
                }
            }
        }

        // ── Calendar Grid ──────────────────────────────────────────────────────
        item {
            GlassCard(alpha = 0.07f) {
                Column {
                    // Month navigation
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navigateMonth(-1) }) { Icon(Icons.Default.ChevronLeft, null, tint = MomentoOnSurface) }
                        Text(monthYearStr, style = MaterialTheme.typography.titleMedium, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { navigateMonth(1) }) { Icon(Icons.Default.ChevronRight, null, tint = MomentoOnSurface) }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // Day of week headers
                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { d ->
                            Text(d, color = MomentoOnSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // Calendar days
                    val totalCells = firstDow + daysInMonth
                    val rows = (totalCells + 6) / 7
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        (0 until rows).forEach { row ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                (0 until 7).forEach { col ->
                                    val cellIdx = row * 7 + col
                                    val day     = cellIdx - firstDow + 1
                                    if (day in 1..daysInMonth) {
                                        val isToday    = isCurrentMonth && day == todayDay
                                        val isSelected = day == selectedDay
                                        val hasEvent   = day in eventDays
                                        Box(
                                            modifier = Modifier.weight(1f).aspectRatio(1f).padding(2.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        isSelected -> MomentoPrimary
                                                        isToday    -> MomentoPrimary.copy(alpha = 0.2f)
                                                        else       -> Color.Transparent
                                                    }
                                                )
                                                .clickable { selectedDay = day; selectedMonth = displayCal.get(Calendar.MONTH); selectedYear = displayCal.get(Calendar.YEAR) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(day.toString(), color = when { isSelected -> Color.White; isToday -> MomentoPrimary; else -> MomentoOnSurface }, fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
                                                if (hasEvent && !isSelected) { Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(MomentoSecondary)) }
                                            }
                                        }
                                    } else {
                                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Selected Day Detail ────────────────────────────────────────────────
        item {
            val selDateStr = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(selCal.time)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Today, null, tint = MomentoPrimary, modifier = Modifier.size(18.dp))
                Text(selDateStr, style = MaterialTheme.typography.titleMedium, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
            }
        }

        if (dayEvents.isEmpty() && dayTasks.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White.copy(alpha = 0.04f)).border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(16.dp)).padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.EventBusy, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Nothing scheduled", color = MomentoOnSurfaceVariant, fontSize = 14.sp)
                    }
                }
            }
        }

        if (dayEvents.isNotEmpty()) {
            item { Text("Events", color = MomentoOnSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp) }
            items(dayEvents, key = { "ev_${it.id}" }) { event -> EventListItem(event) { viewModel.deleteEvent(event) } }
        }
        if (dayTasks.isNotEmpty()) {
            item { Text("TASKS DUE", color = MomentoOnSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp) }
            items(dayTasks, key = { "t_${it.id}" }) { task ->
                Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.04f)).border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(12.dp)).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    val pc = when (task.priority) { Task.Priority.HIGH -> MomentoError; Task.Priority.MEDIUM -> MomentoTertiary; Task.Priority.LOW -> MomentoOnSurfaceVariant }
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(pc))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(task.title, color = MomentoOnSurface, fontWeight = FontWeight.Medium)
                        Text(task.category, color = MomentoOnSurfaceVariant, fontSize = 12.sp)
                    }
                    if (task.isCompleted) Icon(Icons.Default.CheckCircle, null, tint = MomentoTertiary, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        // ── Upcoming Events ────────────────────────────────────────────────────
        val upcomingAll = events.filter { it.timeMillis > System.currentTimeMillis() }.take(6)
        if (upcomingAll.isNotEmpty()) {
            item { Spacer(modifier = Modifier.height(4.dp)); Text("UPCOMING", color = MomentoOnSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp) }
            items(upcomingAll, key = { "up_${it.id}" }) { event -> EventListItem(event) { viewModel.deleteEvent(event) } }
        }
        item { Spacer(modifier = Modifier.height(90.dp)) }
    }
}

@Composable
fun EventListItem(event: Event, onDelete: () -> Unit) {
    val eventColor = try { Color(android.graphics.Color.parseColor(event.color)) } catch (e: Exception) { MomentoPrimary }
    val timeFmt = SimpleDateFormat("MMM d · hh:mm a", Locale.getDefault())
    var showMenu by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(4.dp).height(44.dp).clip(RoundedCornerShape(2.dp)).background(eventColor))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(event.title, color = MomentoOnSurface, fontWeight = FontWeight.SemiBold)
            Text(timeFmt.format(Date(event.timeMillis)), color = MomentoOnSurfaceVariant, fontSize = 12.sp)
            if (!event.location.isNullOrBlank()) Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Icon(Icons.Default.LocationOn, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(11.dp))
                Text(event.location, color = MomentoOnSurfaceVariant, fontSize = 11.sp)
            }
        }
        Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(eventColor.copy(alpha = 0.12f)).padding(horizontal = 8.dp, vertical = 3.dp)) { Text(event.category, color = eventColor, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
        Spacer(modifier = Modifier.width(4.dp))
        Box {
            IconButton(onClick = { showMenu = true }, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.MoreVert, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(16.dp)) }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }, modifier = Modifier.background(MomentoSurfaceContainerHigh)) {
                DropdownMenuItem(text = { Text("Delete", color = MomentoError) }, onClick = { onDelete(); showMenu = false }, leadingIcon = { Icon(Icons.Default.Delete, null, tint = MomentoError) })
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}
