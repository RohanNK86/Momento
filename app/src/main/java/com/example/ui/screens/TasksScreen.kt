package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
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
import com.example.data.Task
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.MomentoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(viewModel: MomentoViewModel) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("All") }

    val filteredTasks = tasks.filter {
        it.title.contains(searchQuery, ignoreCase = true) &&
        when (selectedTab) {
            "Pending" -> !it.isCompleted
            "Completed" -> it.isCompleted
            "Priority" -> it.priority == Task.Priority.HIGH
            else -> true
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        Text("Tasks", style = MaterialTheme.typography.displayLarge, color = MomentoOnSurface)
        Text("Manage your deep focus priorities.", style = MaterialTheme.typography.bodyMedium, color = MomentoOnSurfaceVariant)
        
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
            placeholder = { Text("Search tasks...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                focusedIndicatorColor = MomentoPrimary,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        GlassCard(alpha = 0.1f) {
            Column {
                val completion = if (tasks.isEmpty()) 0f else tasks.count { it.isCompleted }.toFloat() / tasks.size
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Column {
                        Text("Daily Completion", style = MaterialTheme.typography.headlineMedium, color = MomentoOnSurface)
                        Text("You're on track for today.", style = MaterialTheme.typography.labelSmall, color = MomentoOnSurfaceVariant)
                    }
                    Text("${(completion * 100).toInt()}%", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = MomentoPrimary)
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { completion },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = MomentoPrimary,
                    trackColor = Color.White.copy(alpha = 0.05f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("All", "Pending", "Completed", "Priority").forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) Color.White.copy(alpha = 0.1f) else Color.Transparent)
                        .border(1.dp, if (isSelected) Color.White.copy(alpha = 0.15f) else Color.Transparent, RoundedCornerShape(16.dp))
                        .clickable { selectedTab = tab }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(tab, color = if (isSelected) MomentoPrimary else MomentoOnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(filteredTasks, key = { it.id }) { task ->
                TaskItem(task) { viewModel.toggleTaskCompletion(task) }
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onToggle: () -> Unit) {
    val borderColor = when (task.priority) {
        Task.Priority.HIGH -> MomentoError
        Task.Priority.MEDIUM -> MomentoTertiary
        Task.Priority.LOW -> MomentoOutline
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
            .clickable { onToggle() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, if (task.isCompleted) MomentoPrimary else MomentoOutline, RoundedCornerShape(4.dp))
                .background(if (task.isCompleted) MomentoPrimary.copy(alpha = 0.2f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            if (task.isCompleted) {
                Icon(Icons.Default.Check, contentDescription = null, tint = MomentoPrimary, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (task.isCompleted) MomentoOnSurfaceVariant else MomentoOnSurface,
                textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(task.priority.name, fontSize = 10.sp, color = borderColor, modifier = Modifier
                    .border(1.dp, borderColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp))
            }
        }
    }
}
