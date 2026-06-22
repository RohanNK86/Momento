package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Videocam
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
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.MomentoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CalendarScreen(viewModel: MomentoViewModel) {
    val events by viewModel.events.collectAsStateWithLifecycle()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Column {
                Text("Calendar", style = MaterialTheme.typography.displayLarge, color = MomentoOnSurface)
                Text("Manage your time and upcoming events.", style = MaterialTheme.typography.bodyLarge, color = MomentoOnSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            GlassCard(alpha = 0.1f) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("October 2026", style = MaterialTheme.typography.headlineMedium, color = MomentoOnSurface)
                        Row {
                            Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = MomentoOnSurfaceVariant)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MomentoOnSurfaceVariant)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                            Text(day, style = MaterialTheme.typography.labelSmall, color = MomentoOnSurfaceVariant, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    val days = (1..31).toList()
                    Column {
                        for (i in 0 until 5) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                for (j in 0 until 7) {
                                    val index = i * 7 + j
                                    if (index < days.size) {
                                        val day = days[index]
                                        val isToday = day == 10
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .padding(2.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isToday) MomentoPrimary.copy(alpha = 0.2f) else Color.Transparent)
                                                .border(
                                                    if (isToday) 1.dp else 0.dp,
                                                    if (isToday) MomentoPrimary else Color.Transparent,
                                                    RoundedCornerShape(8.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(day.toString(), color = if (isToday) MomentoPrimary else MomentoOnSurface, fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal)
                                        }
                                    } else {
                                        Box(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Today, contentDescription = null, tint = MomentoPrimary)
                Text("Today's Schedule", style = MaterialTheme.typography.headlineMedium, color = MomentoOnSurface)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(events.take(2)) { event ->
            EventItem(event)
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Upcoming", style = MaterialTheme.typography.headlineMedium, color = MomentoOnSurface)
                Text("View All", style = MaterialTheme.typography.labelSmall, color = MomentoPrimary)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(events.drop(2)) { event ->
            UpcomingEventItem(event)
            Spacer(modifier = Modifier.height(8.dp))
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun EventItem(event: Event) {
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val timeString = formatter.format(Date(event.timeMillis))
    
    val color = when(event.category) {
        "Personal" -> MomentoTertiary
        "Work" -> MomentoPrimary
        "Health" -> MomentoError
        else -> MomentoSecondary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(4.dp).height(40.dp).clip(RoundedCornerShape(2.dp)).background(color))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(event.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MomentoOnSurface)
                Text(timeString, style = MaterialTheme.typography.labelSmall, color = MomentoOnSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(if (event.category == "Work") Icons.Default.Videocam else Icons.Default.LocationOn, contentDescription = null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(14.dp))
                Text(event.category, style = MaterialTheme.typography.labelSmall, color = MomentoOnSurfaceVariant)
            }
        }
    }
}

@Composable
fun UpcomingEventItem(event: Event) {
    val formatter = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
    val timeString = formatter.format(Date(event.timeMillis))

    val color = when(event.category) {
        "Personal" -> MomentoSecondary
        "Work" -> MomentoPrimary
        "Health" -> MomentoTertiary
        else -> MomentoError
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
            .border(width = 4.dp, color = color, shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)) // Faux left border
            .padding(16.dp)
    ) {
        Text(event.title, style = MaterialTheme.typography.bodyLarge, color = MomentoOnSurface)
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(timeString, style = MaterialTheme.typography.labelSmall, color = MomentoOnSurfaceVariant)
            Text(event.category, style = MaterialTheme.typography.labelSmall, color = MomentoOnSurfaceVariant)
        }
    }
}
