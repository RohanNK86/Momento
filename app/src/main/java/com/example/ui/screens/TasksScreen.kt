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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Task
import com.example.ui.components.AddTaskDialog
import com.example.ui.components.AddActionButton
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.MomentoViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TasksScreen(viewModel: MomentoViewModel) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<Task?>(null) }
    var editTarget by remember { mutableStateOf<Task?>(null) }

    if (showAddDialog) {
        AddTaskDialog(onDismiss = { showAddDialog = false }) { title, priority, description, category, dueDate ->
            viewModel.addTask(title, priority, description, category, dueDate)
        }
    }
    editTarget?.let { task ->
        var title by remember(task.id) { mutableStateOf(task.title) }
        var priority by remember(task.id) { mutableStateOf(task.priority) }
        AlertDialog(
            onDismissRequest = { editTarget = null },
            title = { Text("Edit task", color = MomentoOnSurface) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Task title") }, singleLine = true, colors = momentoTextFieldColors())
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Task.Priority.entries.forEach { option ->
                            FilterChip(selected = priority == option, onClick = { priority = option }, label = { Text(option.name) })
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { if (title.isNotBlank()) viewModel.updateTask(task.copy(title = title.trim(), priority = priority)); editTarget = null }) { Text("Save") } },
            dismissButton = { TextButton(onClick = { editTarget = null }) { Text("Cancel") } },
            containerColor = MomentoSurfaceContainerHigh
        )
    }
    deleteTarget?.let { task ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete Task", color = MomentoOnSurface) },
            text = { Text("Remove \"${task.title}\"?", color = MomentoOnSurfaceVariant) },
            confirmButton = { TextButton(onClick = { viewModel.deleteTask(task); deleteTarget = null }) { Text("Delete", color = MomentoError) } },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("Cancel", color = MomentoOnSurfaceVariant) } },
            containerColor = MomentoSurfaceContainerHigh
        )
    }

    val filtered = tasks.filter {
        (searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true)) &&
        when (selectedTab) {
            "Pending"   -> !it.isCompleted
            "Done"      -> it.isCompleted
            "High"      -> it.priority == Task.Priority.HIGH
            else        -> true
        }
    }
    val completion = if (tasks.isEmpty()) 0f else tasks.count { it.isCompleted }.toFloat() / tasks.size
    val animComp by animateFloatAsState(completion, tween(800, easing = EaseOutCubic), label = "comp")

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Tasks", style = MaterialTheme.typography.headlineMedium, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
                Text("${tasks.count { it.isCompleted }}/${tasks.size} completed", color = MomentoOnSurfaceVariant, fontSize = 13.sp)
            }
            AddActionButton(onClick = { showAddDialog = true }, description = "Add task")
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Completion bar
        GlassCard(alpha = 0.07f) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Completion Rate", color = MomentoOnSurface, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { animComp },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = MomentoPrimary, trackColor = MomentoSurfaceContainer
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("${(animComp * 100).toInt()}%", color = MomentoPrimary, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Search
        OutlinedTextField(
            value = searchQuery, onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
            placeholder = { Text("Search tasksâ€¦", color = MomentoOnSurfaceVariant) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = MomentoOnSurfaceVariant) },
            trailingIcon = { if (searchQuery.isNotEmpty()) IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Clear, null, tint = MomentoOnSurfaceVariant) } },
            colors = momentoTextFieldColors(), shape = RoundedCornerShape(12.dp), singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Filter tabs
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("All", "Pending", "Done", "High").forEach { tab ->
                val sel = selectedTab == tab
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp))
                        .background(if (sel) MomentoPrimary else MomentoSurfaceContainer)
                        .border(1.dp, if (sel) MomentoPrimary else MomentoSurfaceContainer, RoundedCornerShape(20.dp))
                        .clickable { selectedTab = tab }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) { Text(tab, color = if (sel) MaterialTheme.colorScheme.onPrimary else MomentoOnSurfaceVariant, fontSize = 12.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal) }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.TaskAlt, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(if (searchQuery.isNotBlank()) "No results for \"$searchQuery\"" else "No tasks here", color = MomentoOnSurfaceVariant)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                items(filtered, key = { it.id }) { task ->
                    TaskItemCard(task = task, onToggle = { viewModel.toggleTaskCompletion(task) }, onEdit = { editTarget = task }, onDelete = { deleteTarget = task }, onArchive = { viewModel.archiveTask(task) })
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun TaskItemCard(task: Task, onToggle: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit, onArchive: () -> Unit) {
    val priorityColor = when (task.priority) { Task.Priority.HIGH -> MomentoError; Task.Priority.MEDIUM -> MomentoTertiary; Task.Priority.LOW -> MomentoOnSurfaceVariant }
    var showMenu by remember { mutableStateOf(false) }
    val dueFmt = SimpleDateFormat("MMM d", Locale.getDefault())

    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (task.isCompleted) MomentoSurfaceContainer else MomentoSurfaceBase)
            .border(1.dp, if (task.isCompleted) MomentoOutline else priorityColor.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox
        Box(
            modifier = Modifier.size(22.dp).clip(CircleShape)
                .border(2.dp, if (task.isCompleted) MomentoTertiary else priorityColor.copy(alpha = 0.7f), CircleShape)
                .background(if (task.isCompleted) MomentoTertiary.copy(alpha = 0.15f) else Color.Transparent)
                .clickable(onClick = onToggle),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.animation.AnimatedVisibility(visible = task.isCompleted, enter = scaleIn(), exit = scaleOut()) {
                Icon(Icons.Default.Check, null, tint = MomentoTertiary, modifier = Modifier.size(14.dp))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                task.title, color = if (task.isCompleted) MomentoOnSurfaceVariant else MomentoOnSurface,
                fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(priorityColor.copy(alpha = 0.12f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text(task.priority.name, color = priorityColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Text(task.category, color = MomentoOnSurfaceVariant, fontSize = 11.sp)
                task.dueDate?.let { Text("Due ${dueFmt.format(Date(it))}", color = if (it < System.currentTimeMillis() && !task.isCompleted) MomentoError else MomentoOnSurfaceVariant, fontSize = 11.sp) }
            }
        }
        Box {
            IconButton(onClick = { showMenu = true }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.MoreVert, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(16.dp))
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }, modifier = Modifier.background(MomentoSurfaceContainerHigh)) {
                DropdownMenuItem(text = { Text("Edit", color = MomentoOnSurface) }, onClick = { onEdit(); showMenu = false }, leadingIcon = { Icon(Icons.Default.Edit, null, tint = MomentoPrimary) })
                DropdownMenuItem(text = { Text(if (task.isCompleted) "Mark Pending" else "Mark Done", color = MomentoOnSurface) }, onClick = { onToggle(); showMenu = false }, leadingIcon = { Icon(Icons.Default.TaskAlt, null, tint = MomentoTertiary) })
                DropdownMenuItem(text = { Text("Archive", color = MomentoOnSurface) }, onClick = { onArchive(); showMenu = false }, leadingIcon = { Icon(Icons.Default.Archive, null, tint = MomentoOnSurface) })
                DropdownMenuItem(text = { Text("Delete", color = MomentoError) }, onClick = { onDelete(); showMenu = false }, leadingIcon = { Icon(Icons.Default.Delete, null, tint = MomentoError) })
            }
        }
    }
}

