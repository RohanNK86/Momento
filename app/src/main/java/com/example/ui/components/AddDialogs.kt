package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.window.Dialog
import com.example.data.Task
import com.example.ui.theme.*

// ── Shared text field colors ─────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun dialogTextFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor   = Color.White.copy(alpha = 0.05f),
    unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
    focusedTextColor        = Color(0xFFF1F5F9),
    unfocusedTextColor      = Color(0xFFF1F5F9),
    focusedIndicatorColor   = Color(0xFF6366F1),
    unfocusedIndicatorColor = Color.White.copy(alpha = 0.15f),
    cursorColor             = Color(0xFF6366F1),
    focusedLabelColor       = Color(0xFF94A3B8),
    unfocusedLabelColor     = Color(0xFF94A3B8)
)

// ── Add Task Dialog ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onAdd: (String, Task.Priority) -> Unit) {
    var title    by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Task.Priority.MEDIUM) }
    var desc     by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Personal") }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MomentoSurfaceContainerHigh)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("New Task", style = MaterialTheme.typography.titleLarge, color = MomentoOnSurface)
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title *") }, modifier = Modifier.fillMaxWidth(), colors = dialogTextFieldColors(), singleLine = true)
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), colors = dialogTextFieldColors(), maxLines = 3)
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth(), colors = dialogTextFieldColors(), singleLine = true)
                Text("Priority", color = MomentoOnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Task.Priority.entries.forEach { p ->
                        val pc = when (p) { Task.Priority.HIGH -> MomentoError; Task.Priority.MEDIUM -> MomentoTertiary; Task.Priority.LOW -> MomentoOnSurfaceVariant }
                        FilterChip(selected = priority == p, onClick = { priority = p },
                            label = { Text(p.name, color = if (priority == p) Color.White else MomentoOnSurfaceVariant) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = pc, containerColor = Color.White.copy(alpha = 0.05f)))
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = MomentoOnSurfaceVariant) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { if (title.isNotBlank()) { onAdd(title.trim(), priority); onDismiss() } }, colors = ButtonDefaults.buttonColors(containerColor = MomentoPrimary)) { Text("Add Task") }
                }
            }
        }
    }
}

// ── Add Goal Dialog ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalDialog(onDismiss: () -> Unit, onAdd: (String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var desc  by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MomentoSurfaceContainerHigh)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("New Goal", style = MaterialTheme.typography.titleLarge, color = MomentoOnSurface)
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Goal Title *") }, modifier = Modifier.fillMaxWidth(), colors = dialogTextFieldColors(), singleLine = true)
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), colors = dialogTextFieldColors(), maxLines = 2)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = MomentoOnSurfaceVariant) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { if (title.isNotBlank()) { onAdd(title.trim()); onDismiss() } }, colors = ButtonDefaults.buttonColors(containerColor = MomentoTertiary)) { Text("Add Goal") }
                }
            }
        }
    }
}

// ── Add Event Dialog ─────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var title    by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Personal") }
    var location by remember { mutableStateOf("") }

    val categories = listOf("Personal", "Work", "Health", "Social", "Other")

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MomentoSurfaceContainerHigh)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("New Event", style = MaterialTheme.typography.titleLarge, color = MomentoOnSurface)
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Event Title *") }, modifier = Modifier.fillMaxWidth(), colors = dialogTextFieldColors(), singleLine = true)
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location (optional)") }, modifier = Modifier.fillMaxWidth(), colors = dialogTextFieldColors(), singleLine = true)
                Text("Category", color = MomentoOnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.forEach { cat ->
                        FilterChip(selected = category == cat, onClick = { category = cat },
                            label = { Text(cat, color = if (category == cat) Color.White else MomentoOnSurfaceVariant) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MomentoSecondary, containerColor = Color.White.copy(alpha = 0.05f)))
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = MomentoOnSurfaceVariant) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { if (title.isNotBlank()) { onAdd(title.trim(), category); onDismiss() } }, colors = ButtonDefaults.buttonColors(containerColor = MomentoSecondary)) { Text("Add Event") }
                }
            }
        }
    }
}

// ── Add Expense Dialog ───────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(onDismiss: () -> Unit, onAdd: (String, Double, String) -> Unit) {
    var title    by remember { mutableStateOf("") }
    var amount   by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Food") }

    val categories = listOf("Food", "Transport", "Housing", "Health", "Shopping", "Other")

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MomentoSurfaceContainerHigh)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Add Expense", style = MaterialTheme.typography.titleLarge, color = MomentoOnSurface)
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Description *") }, modifier = Modifier.fillMaxWidth(), colors = dialogTextFieldColors(), singleLine = true)
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount (₹) *") }, modifier = Modifier.fillMaxWidth(), colors = dialogTextFieldColors(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number), singleLine = true)
                Text("Category", color = MomentoOnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories.size) { i ->
                        val cat = categories[i]
                        FilterChip(selected = category == cat, onClick = { category = cat },
                            label = { Text(cat, color = if (category == cat) Color.White else MomentoOnSurfaceVariant) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MomentoError, containerColor = Color.White.copy(alpha = 0.05f)))
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = MomentoOnSurfaceVariant) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val amt = amount.toDoubleOrNull()
                        if (title.isNotBlank() && amt != null && amt > 0) { onAdd(title.trim(), amt, category); onDismiss() }
                    }, colors = ButtonDefaults.buttonColors(containerColor = MomentoError)) { Text("Add") }
                }
            }
        }
    }
}

// ── Add Habit Dialog ─────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitDialog(onDismiss: () -> Unit, onAdd: (String, String, String, String) -> Unit) {
    var name      by remember { mutableStateOf("") }
    var desc      by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("DAILY") }
    var color     by remember { mutableStateOf("#6366F1") }

    val colorOptions = listOf("#6366F1" to "Indigo", "#A855F7" to "Purple", "#10B981" to "Green", "#F43F5E" to "Red", "#F59E0B" to "Amber", "#06B6D4" to "Cyan")

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MomentoSurfaceContainerHigh)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("New Habit", style = MaterialTheme.typography.titleLarge, color = MomentoOnSurface)
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Habit Name *") }, modifier = Modifier.fillMaxWidth(), colors = dialogTextFieldColors(), singleLine = true)
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), colors = dialogTextFieldColors(), maxLines = 2)
                Text("Frequency", color = MomentoOnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("DAILY", "WEEKLY", "MONTHLY").forEach { f ->
                        FilterChip(selected = frequency == f, onClick = { frequency = f },
                            label = { Text(f, color = if (frequency == f) Color.White else MomentoOnSurfaceVariant) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MomentoPrimary, containerColor = Color.White.copy(alpha = 0.05f)))
                    }
                }
                Text("Color", color = MomentoOnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colorOptions.forEach { (hex, _) ->
                        val c = try { androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { MomentoPrimary }
                        Box(
                            modifier = Modifier.size(28.dp)
                                .background(c, androidx.compose.foundation.shape.CircleShape)
                                .then(if (color == hex) Modifier.background(Color.Transparent, androidx.compose.foundation.shape.CircleShape) else Modifier)
                                .run { if (color == hex) border(2.dp, Color.White, androidx.compose.foundation.shape.CircleShape) else this }
                                .clickable { color = hex }
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = MomentoOnSurfaceVariant) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { if (name.isNotBlank()) { onAdd(name.trim(), desc, frequency, color); onDismiss() } }, colors = ButtonDefaults.buttonColors(containerColor = MomentoPrimary)) { Text("Add Habit") }
                }
            }
        }
    }
}

// ── Add Note Dialog ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteDialog(
    initialTitle: String = "",
    initialContent: String = "",
    initialTags: String = "",
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var title   by remember { mutableStateOf(initialTitle) }
    var content by remember { mutableStateOf(initialContent) }
    var tags    by remember { mutableStateOf(initialTags) }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MomentoSurfaceContainerHigh)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(if (initialTitle.isBlank()) "New Note" else "Edit Note", style = MaterialTheme.typography.titleLarge, color = MomentoOnSurface)
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth(), colors = dialogTextFieldColors(), singleLine = true)
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content *") }, modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp), colors = dialogTextFieldColors(), maxLines = 8)
                OutlinedTextField(value = tags, onValueChange = { tags = it }, label = { Text("Tags (comma separated)") }, modifier = Modifier.fillMaxWidth(), colors = dialogTextFieldColors(), singleLine = true)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = MomentoOnSurfaceVariant) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { if (content.isNotBlank()) { onAdd(title.trim(), content.trim(), tags.trim()); onDismiss() } }, colors = ButtonDefaults.buttonColors(containerColor = MomentoSecondary)) { Text("Save Note") }
                }
            }
        }
    }
}

// ── Focus Mode Dialog (Pomodoro) ─────────────────────────────────────────────
@Composable
fun FocusModeDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)) {
        Box(modifier = Modifier.fillMaxSize().background(MomentoBackground).padding(32.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Text("Pomodoro Timer", style = MaterialTheme.typography.headlineLarge, color = MomentoOnSurface)
                Text("Stay focused. Break distraction.", color = MomentoOnSurfaceVariant, style = MaterialTheme.typography.bodyLarge)
                var timeLeft by remember { mutableStateOf(25 * 60) }
                var isRunning by remember { mutableStateOf(false) }
                var sessionsDone by remember { mutableStateOf(0) }

                androidx.compose.runtime.LaunchedEffect(isRunning) {
                    while (isRunning && timeLeft > 0) {
                        kotlinx.coroutines.delay(1000); timeLeft--
                    }
                    if (timeLeft == 0) { isRunning = false; sessionsDone++ }
                }

                val minutes = timeLeft / 60
                val seconds = timeLeft % 60
                val progress = timeLeft.toFloat() / (25 * 60)

                Box(contentAlignment = androidx.compose.ui.Alignment.Center, modifier = Modifier.size(180.dp)) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        val sw = 12.dp.toPx(); val inset = sw / 2
                        drawArc(color = MomentoPrimary.copy(alpha = 0.12f), -90f, 360f, false, topLeft = androidx.compose.ui.geometry.Offset(inset, inset), size = Size(size.width - sw, size.height - sw), style = androidx.compose.ui.graphics.drawscope.Stroke(sw, cap = androidx.compose.ui.graphics.StrokeCap.Round))
                        drawArc(color = MomentoPrimary, -90f, progress * 360f, false, topLeft = androidx.compose.ui.geometry.Offset(inset, inset), size = Size(size.width - sw, size.height - sw), style = androidx.compose.ui.graphics.drawscope.Stroke(sw, cap = androidx.compose.ui.graphics.StrokeCap.Round))
                    }
                    Text(String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds), style = MaterialTheme.typography.displayMedium.copy(fontSize = 48.sp), color = MomentoPrimary)
                }

                if (sessionsDone > 0) Text("✅ $sessionsDone session${if (sessionsDone > 1) "s" else ""} completed!", color = MomentoTertiary)

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { timeLeft = 25 * 60; isRunning = false }, colors = ButtonDefaults.outlinedButtonColors(contentColor = MomentoOnSurface)) { Text("Reset") }
                    Button(onClick = { isRunning = !isRunning }, colors = ButtonDefaults.buttonColors(containerColor = if (isRunning) MomentoSecondary else MomentoPrimary)) { Text(if (isRunning) "⏸ Pause" else "▶ Start") }
                    OutlinedButton(onClick = onDismiss, colors = ButtonDefaults.outlinedButtonColors(contentColor = MomentoOnSurface)) { Text("Exit") }
                }
            }
        }
    }
}
