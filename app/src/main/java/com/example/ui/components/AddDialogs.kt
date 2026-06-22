package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.Task
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onAdd: (String, Task.Priority) -> Unit) {
    var title by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Task.Priority.MEDIUM) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MomentoSurfaceContainerHigh),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Add Task", style = MaterialTheme.typography.headlineMedium, color = MomentoOnSurface)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title", color = MomentoOnSurfaceVariant) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        focusedTextColor = MomentoOnSurface,
                        unfocusedTextColor = MomentoOnSurface,
                        focusedIndicatorColor = MomentoPrimary
                    )
                )
                // Priority Selection
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Task.Priority.values().forEach { p ->
                        FilterChip(
                            selected = p == priority,
                            onClick = { priority = p },
                            label = { Text(p.name, color = if(p == priority) MomentoOnPrimaryContainer else MomentoOnSurfaceVariant) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MomentoPrimaryContainer)
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = MomentoOnSurfaceVariant) }
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = MomentoPrimary),
                        onClick = { if (title.isNotBlank()) { onAdd(title, priority); onDismiss() } }
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalDialog(onDismiss: () -> Unit, onAdd: (String) -> Unit) {
    var title by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MomentoSurfaceContainerHigh)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Add Goal", style = MaterialTheme.typography.headlineMedium, color = MomentoOnSurface)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Goal Title", color = MomentoOnSurfaceVariant) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        focusedTextColor = MomentoOnSurface,
                        unfocusedTextColor = MomentoOnSurface,
                        focusedIndicatorColor = MomentoTertiary
                    )
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = MomentoOnSurfaceVariant) }
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = MomentoTertiary),
                        onClick = { if (title.isNotBlank()) { onAdd(title); onDismiss() } }
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Personal") }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MomentoSurfaceContainerHigh)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Add Event", style = MaterialTheme.typography.headlineMedium, color = MomentoOnSurface)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Event Title", color = MomentoOnSurfaceVariant) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        focusedTextColor = MomentoOnSurface,
                        unfocusedTextColor = MomentoOnSurface,
                        focusedIndicatorColor = MomentoSecondary
                    )
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category", color = MomentoOnSurfaceVariant) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        focusedTextColor = MomentoOnSurface,
                        unfocusedTextColor = MomentoOnSurface,
                        focusedIndicatorColor = MomentoSecondary
                    )
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = MomentoOnSurfaceVariant) }
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = MomentoSecondary),
                        onClick = { if (title.isNotBlank()) { onAdd(title, category); onDismiss() } }
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(onDismiss: () -> Unit, onAdd: (String, Double, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Other") }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MomentoSurfaceContainerHigh)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Add Expense", style = MaterialTheme.typography.headlineMedium, color = MomentoOnSurface)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Expense Title", color = MomentoOnSurfaceVariant) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        focusedTextColor = MomentoOnSurface,
                        unfocusedTextColor = MomentoOnSurface,
                        focusedIndicatorColor = MomentoError
                    )
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount ($)", color = MomentoOnSurfaceVariant) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        focusedTextColor = MomentoOnSurface,
                        unfocusedTextColor = MomentoOnSurface,
                        focusedIndicatorColor = MomentoError
                    )
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category", color = MomentoOnSurfaceVariant) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        focusedTextColor = MomentoOnSurface,
                        unfocusedTextColor = MomentoOnSurface,
                        focusedIndicatorColor = MomentoError
                    )
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = MomentoOnSurfaceVariant) }
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = MomentoError),
                        onClick = {
                            val amountDouble = amount.toDoubleOrNull()
                            if (title.isNotBlank() && amountDouble != null) { 
                                onAdd(title, amountDouble, category)
                                onDismiss() 
                            } 
                        }
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}
