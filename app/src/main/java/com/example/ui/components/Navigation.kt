package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MomentoPrimary
import com.example.ui.theme.MomentoSecondary
import com.example.ui.theme.MomentoSurfaceContainer
import com.example.ui.theme.MomentoSurfaceContainerHigh
import com.example.ui.theme.MomentoOnSurface

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavBarItem("Home", Icons.Default.Home, currentRoute == "home") { onNavigate("home") }
        NavBarItem("Tasks", Icons.Default.TaskAlt, currentRoute == "tasks") { onNavigate("tasks") }
        NavBarItem("Goals", Icons.Default.EmojiEvents, currentRoute == "goals") { onNavigate("goals") }
        NavBarItem("Calendar", Icons.Default.CalendarMonth, currentRoute == "calendar") { onNavigate("calendar") }
        NavBarItem("Expenses", Icons.Default.Payments, currentRoute == "expenses") { onNavigate("expenses") }
    }
}

@Composable
fun NavBarItem(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    val navColor = if (isSelected) MomentoPrimary else Color.Gray
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
             Icon(icon, contentDescription = label, tint = navColor)
             Text(text = label, color = navColor, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
        }
    }
}

@Composable
fun FabMenu(
    onAddTask: () -> Unit,
    onAddGoal: () -> Unit,
    onAddEvent: () -> Unit,
    onAddExpense: () -> Unit
) {
    var expanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    Box {
        FloatingActionButton(
            onClick = { expanded = true },
            containerColor = Color.Transparent,
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(listOf(MomentoPrimary, MomentoSecondary)),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
        }
        DropdownMenu(
            expanded = expanded, 
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MomentoSurfaceContainerHigh)
        ) {
            DropdownMenuItem(
                text = { Text("Add Task", color = MomentoOnSurface) }, 
                onClick = { expanded = false; onAddTask() },
                leadingIcon = { Icon(Icons.Default.TaskAlt, contentDescription = null, tint = MomentoOnSurface) }
            )
            DropdownMenuItem(
                text = { Text("Add Goal", color = MomentoOnSurface) }, 
                onClick = { expanded = false; onAddGoal() },
                leadingIcon = { Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = MomentoOnSurface) }
            )
            DropdownMenuItem(
                text = { Text("Add Event", color = MomentoOnSurface) }, 
                onClick = { expanded = false; onAddEvent() },
                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MomentoOnSurface) }
            )
            DropdownMenuItem(
                text = { Text("Add Expense", color = MomentoOnSurface) }, 
                onClick = { expanded = false; onAddExpense() },
                leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null, tint = MomentoOnSurface) }
            )
        }
    }
}
