package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Expense
import com.example.ui.components.AddExpenseDialog
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.MomentoViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExpensesScreen(viewModel: MomentoViewModel) {
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<Expense?>(null) }
    var selectedCat by remember { mutableStateOf("All") }

    if (showAddDialog) {
        AddExpenseDialog(onDismiss = { showAddDialog = false }) { title, amount, category ->
            viewModel.addExpense(title, amount, category)
        }
    }
    deleteTarget?.let { expense ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete Expense", color = MomentoOnSurface) },
            text = { Text("Remove \"${expense.title}\"?", color = MomentoOnSurfaceVariant) },
            confirmButton = { TextButton(onClick = { viewModel.deleteExpense(expense); deleteTarget = null }) { Text("Delete", color = MomentoError) } },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("Cancel", color = MomentoOnSurfaceVariant) } },
            containerColor = MomentoSurfaceContainerHigh
        )
    }

    val categories = listOf("All") + expenses.map { it.category }.distinct()
    val filtered = if (selectedCat == "All") expenses else expenses.filter { it.category == selectedCat }
    val total = filtered.sumOf { it.amount }
    val catColors = listOf(MomentoPrimary, MomentoSecondary, MomentoTertiary, MomentoError, Color(0xFFF59E0B), Color(0xFF06B6D4))
    val catMap = expenses.groupBy { it.category }.mapValues { it.value.sumOf { e -> e.amount } }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Expenses", style = MaterialTheme.typography.displaySmall, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
                Text("${expenses.size} transactions", color = MomentoOnSurfaceVariant, fontSize = 13.sp)
            }
            FloatingActionButton(onClick = { showAddDialog = true }, modifier = Modifier.size(48.dp), containerColor = Color.Transparent, shape = RoundedCornerShape(14.dp)) {
                Box(modifier = Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(MomentoError, Color(0xFFF59E0B))), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Total card
        GlassCard {
            Column {
                Text("Total Spent", color = MomentoOnSurfaceVariant, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("₹${String.format(java.util.Locale.US, "%.2f", total)}", color = MomentoOnSurface, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                // Category mini bars
                catMap.entries.forEachIndexed { idx, (cat, amt) ->
                    val ratio = if (total > 0) (amt / total).toFloat() else 0f
                    val cc = catColors[idx % catColors.size]
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(cc))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(cat, color = MomentoOnSurfaceVariant, fontSize = 12.sp, modifier = Modifier.width(80.dp))
                        val animRatio by animateFloatAsState(ratio, tween(800, easing = EaseOutCubic), label = "cat_$cat")
                        LinearProgressIndicator(progress = { animRatio }, modifier = Modifier.weight(1f).height(5.dp).clip(RoundedCornerShape(3.dp)), color = cc, trackColor = Color.White.copy(alpha = 0.05f))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("₹${String.format(java.util.Locale.US, "%.0f", amt)}", color = cc, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Category filter
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.forEach { cat ->
                        val sel = selectedCat == cat
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                .background(if (sel) MomentoError else Color.White.copy(alpha = 0.05f))
                                .border(1.dp, if (sel) MomentoError else Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                                .clickable { selectedCat = cat }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) { Text(cat, color = if (sel) Color.White else MomentoOnSurfaceVariant, fontSize = 12.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal) }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            if (filtered.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Payments, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(48.dp))
                            Text("No expenses", color = MomentoOnSurfaceVariant, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            } else {
                items(filtered, key = { it.id }) { expense ->
                    ExpenseItem(expense, catColors, catMap.keys.toList()) { deleteTarget = expense }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense, catColors: List<Color>, catList: List<String>, onDelete: () -> Unit) {
    val catIdx = catList.indexOf(expense.category).coerceAtLeast(0)
    val cc = catColors[catIdx % catColors.size]
    val dateFmt = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(cc.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Payments, null, tint = cc, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(expense.title, color = MomentoOnSurface, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(expense.category, color = cc, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                Text(dateFmt.format(Date(expense.dateMillis)), color = MomentoOnSurfaceVariant, fontSize = 11.sp)
            }
        }
        Text("₹${String.format(java.util.Locale.US, "%.2f", expense.amount)}", color = MomentoOnSurface, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Box {
            IconButton(onClick = { showMenu = true }, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.MoreVert, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(14.dp)) }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }, modifier = Modifier.background(MomentoSurfaceContainerHigh)) {
                DropdownMenuItem(text = { Text("Delete", color = MomentoError) }, onClick = { onDelete(); showMenu = false }, leadingIcon = { Icon(Icons.Default.Delete, null, tint = MomentoError) })
            }
        }
    }
}
