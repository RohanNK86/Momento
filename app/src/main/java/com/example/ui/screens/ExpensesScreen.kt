package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Expense
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.MomentoViewModel

@Composable
fun ExpensesScreen(viewModel: MomentoViewModel) {
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Column {
                Text("Expenses", style = MaterialTheme.typography.displayLarge, color = MomentoOnSurface)
                Text("Track and analyze your spending.", style = MaterialTheme.typography.bodyLarge, color = MomentoOnSurfaceVariant)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        GlassCard(alpha = 0.1f) {
            Column {
                Text("TOTAL MONTHLY SPEND", style = MaterialTheme.typography.labelSmall, color = MomentoOnSurfaceVariant)
                Text("$3,450.00", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = MomentoOnSurface)
                Text("↘ 12% less than last month", style = MaterialTheme.typography.labelSmall, color = MomentoTertiary)
                
                Spacer(modifier = Modifier.height(24.dp))
                // Faux Bar Chart
                Row(modifier = Modifier.fillMaxWidth().height(120.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
                    listOf(0.4f, 0.6f, 0.9f, 0.3f, 0.5f).forEachIndexed { i, heightMatch ->
                        val isCurrent = i == 2
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(heightMatch)
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(if (isCurrent) MomentoPrimary else Color.White.copy(alpha = 0.05f))
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Recent Transactions", style = MaterialTheme.typography.headlineMedium, color = MomentoOnSurface)
                    Text("VIEW ALL", style = MaterialTheme.typography.labelSmall, color = MomentoPrimary)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(expenses, key = { it.id }) { expense ->
                ExpenseItem(expense)
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense) {
    val (icon, color) = when (expense.category) {
        "Food" -> Icons.Default.Restaurant to MomentoSecondary
        "Housing" -> Icons.Default.Home to MomentoPrimary
        "Transport" -> Icons.Default.LocalGasStation to MomentoTertiary
        else -> Icons.Default.ShoppingCart to MomentoOutline
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Column {
                Text(expense.title, style = MaterialTheme.typography.bodyLarge, color = MomentoOnSurface)
                Text(expense.category, style = MaterialTheme.typography.labelSmall, color = MomentoOnSurfaceVariant)
            }
        }
        Text("-\$${String.format("%.2f", expense.amount)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MomentoOnSurface)
    }
}
