package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.BottomNavBar
import com.example.ui.screens.*
import com.example.ui.theme.AppTheme
import com.example.ui.theme.MomentoBackground
import com.example.ui.theme.MomentoPrimary
import com.example.ui.theme.MomentoSecondary
import com.example.ui.theme.MomentoTertiary
import com.example.viewmodel.MomentoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                MomentoApp()
            }
        }
    }
}

@Composable
fun MomentoApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"
    
    val viewModel: MomentoViewModel = viewModel()
    
    var showTaskDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var showGoalDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var showEventDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var showExpenseDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    if (showTaskDialog) com.example.ui.components.AddTaskDialog(onDismiss = { showTaskDialog = false }) { title, priority -> viewModel.addTask(title, priority) }
    if (showGoalDialog) com.example.ui.components.AddGoalDialog(onDismiss = { showGoalDialog = false }) { title -> viewModel.addGoal(title, 0f) }
    if (showEventDialog) com.example.ui.components.AddEventDialog(onDismiss = { showEventDialog = false }) { title, category -> viewModel.addEvent(title, System.currentTimeMillis(), category) }
    if (showExpenseDialog) com.example.ui.components.AddExpenseDialog(onDismiss = { showExpenseDialog = false }) { title, amount, category -> viewModel.addExpense(title, amount, category) }

    Scaffold(
        bottomBar = {
            BottomNavBar(currentRoute = currentRoute) { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        },
        floatingActionButton = {
            com.example.ui.components.FabMenu(
                onAddTask = { showTaskDialog = true },
                onAddGoal = { showGoalDialog = true },
                onAddEvent = { showEventDialog = true },
                onAddExpense = { showExpenseDialog = true }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().background(MomentoBackground)) {
            // Glowing Orbs effect
            Box(modifier = Modifier
                .fillMaxWidth(1.2f)
                .fillMaxHeight(0.6f)
                .align(Alignment.TopStart)
                .offset(x = (-100).dp, y = (-100).dp)
                .background(Brush.radialGradient(listOf(MomentoPrimary.copy(alpha = 0.25f), Color.Transparent), radius = 800f))
            )
            Box(modifier = Modifier
                .fillMaxWidth(1.2f)
                .fillMaxHeight(0.6f)
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 100.dp)
                .background(Brush.radialGradient(listOf(MomentoTertiary.copy(alpha = 0.2f), Color.Transparent), radius = 800f))
            )
            Box(modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.5f)
                .align(Alignment.CenterEnd)
                .background(Brush.radialGradient(listOf(MomentoSecondary.copy(alpha = 0.15f), Color.Transparent), radius = 600f))
            )
            
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                NavHost(navController, startDestination = "home") {
                    composable("home") { DashboardScreen(viewModel) }
                    composable("tasks") { TasksScreen(viewModel) }
                    composable("goals") { GoalsScreen(viewModel) }
                    composable("calendar") { CalendarScreen(viewModel) }
                    composable("expenses") { ExpensesScreen(viewModel) }
                }
            }
        }
    }
}
