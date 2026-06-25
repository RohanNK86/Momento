package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.viewmodel.MomentoViewModel

@Composable
fun SearchScreen(viewModel: MomentoViewModel, onNavigate: (String) -> Unit) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()

    var query by remember { mutableStateOf("") }
    val q = query.trim()

    val matchedTasks  = if (q.isBlank()) emptyList() else tasks.filter  { it.title.contains(q, true) || it.description.contains(q, true) || it.category.contains(q, true) }
    val matchedEvents = if (q.isBlank()) emptyList() else events.filter { it.title.contains(q, true) || it.category.contains(q, true) }
    val matchedGoals  = if (q.isBlank()) emptyList() else goals.filter  { it.title.contains(q, true) || it.description.contains(q, true) }
    val matchedHabits = if (q.isBlank()) emptyList() else habits.filter { it.name.contains(q, true) }
    val matchedNotes  = if (q.isBlank()) emptyList() else notes.filter  { it.title.contains(q, true) || it.content.contains(q, true) || it.tags.contains(q, true) }

    val totalResults = matchedTasks.size + matchedEvents.size + matchedGoals.size + matchedHabits.size + matchedNotes.size

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        Text("Search", style = MaterialTheme.typography.displaySmall, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
        Text("Find anything across Momento", color = MomentoOnSurfaceVariant, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = query, onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
            placeholder = { Text("Tasks, events, notes, goals…", color = MomentoOnSurfaceVariant) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = MomentoPrimary) },
            trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { query = "" }) { Icon(Icons.Default.Clear, null, tint = MomentoOnSurfaceVariant) } },
            colors = momentoTextFieldColors(), shape = RoundedCornerShape(16.dp), singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {
            q.isBlank() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Search, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Type to search", color = MomentoOnSurfaceVariant, style = MaterialTheme.typography.bodyLarge)
                        Text("Tasks · Events · Goals · Habits · Notes", color = MomentoOnSurfaceVariant, fontSize = 12.sp)
                    }
                }
            }
            totalResults == 0 -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SearchOff, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No results for \"$q\"", color = MomentoOnSurface, style = MaterialTheme.typography.bodyLarge)
                        Text("Try a different keyword", color = MomentoOnSurfaceVariant, fontSize = 12.sp)
                    }
                }
            }
            else -> {
                Text("$totalResults result${if (totalResults == 1) "" else "s"} for \"$q\"", color = MomentoOnSurfaceVariant, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (matchedTasks.isNotEmpty()) {
                        item { SearchSectionHeader("Tasks", Icons.Default.TaskAlt, MomentoPrimary, matchedTasks.size) }
                        items(matchedTasks.take(5)) { task ->
                            SearchResultCard(
                                title = task.title, subtitle = "${task.priority.name} · ${task.category}",
                                icon = Icons.Default.TaskAlt, color = MomentoPrimary,
                                badge = if (task.isCompleted) "Done" else "Pending"
                            ) { onNavigate("tasks") }
                        }
                    }
                    if (matchedEvents.isNotEmpty()) {
                        item { SearchSectionHeader("Events", Icons.Default.Event, MomentoSecondary, matchedEvents.size) }
                        items(matchedEvents.take(5)) { event ->
                            val evColor = try { Color(android.graphics.Color.parseColor(event.color)) } catch (e: Exception) { MomentoSecondary }
                            SearchResultCard(
                                title = event.title, subtitle = event.category,
                                icon = Icons.Default.Event, color = evColor, badge = event.category
                            ) { onNavigate("events") }
                        }
                    }
                    if (matchedGoals.isNotEmpty()) {
                        item { SearchSectionHeader("Goals", Icons.Default.EmojiEvents, MomentoTertiary, matchedGoals.size) }
                        items(matchedGoals.take(5)) { goal ->
                            SearchResultCard(
                                title = goal.title, subtitle = "${(goal.progress * 100).toInt()}% complete",
                                icon = Icons.Default.EmojiEvents, color = MomentoTertiary, badge = goal.status.name
                            ) { onNavigate("goals") }
                        }
                    }
                    if (matchedHabits.isNotEmpty()) {
                        item { SearchSectionHeader("Habits", Icons.Default.Loop, Color(0xFFF59E0B), matchedHabits.size) }
                        items(matchedHabits.take(5)) { habit ->
                            val hc = try { Color(android.graphics.Color.parseColor(habit.color)) } catch (e: Exception) { MomentoPrimary }
                            SearchResultCard(
                                title = habit.name, subtitle = "🔥 ${habit.currentStreak} day streak · ${habit.frequency}",
                                icon = Icons.Default.Loop, color = hc, badge = habit.frequency
                            ) { onNavigate("habits") }
                        }
                    }
                    if (matchedNotes.isNotEmpty()) {
                        item { SearchSectionHeader("Notes", Icons.Default.Notes, MomentoOnSurfaceVariant, matchedNotes.size) }
                        items(matchedNotes.take(5)) { note ->
                            SearchResultCard(
                                title = note.title, subtitle = note.content.take(60),
                                icon = Icons.Default.Notes, color = MomentoOnSurfaceVariant,
                                badge = if (note.isPinned) "Pinned" else ""
                            ) { onNavigate("notes") }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun SearchSectionHeader(title: String, icon: ImageVector, color: Color, count: Int) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(icon, null, tint = color, modifier = Modifier.size(14.dp))
        Text(title.uppercase(), color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
        Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.15f)).padding(horizontal = 6.dp, vertical = 1.dp)) {
            Text(count.toString(), color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SearchResultCard(title: String, subtitle: String, icon: ImageVector, color: Color, badge: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(color.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = MomentoOnSurface, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, color = MomentoOnSurfaceVariant, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        if (badge.isNotBlank()) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(badge, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(color.copy(alpha = 0.1f)).padding(horizontal = 6.dp, vertical = 2.dp))
        }
        Icon(Icons.Default.ChevronRight, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(16.dp))
    }
}
