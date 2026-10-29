package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.MomentoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme { MomentoApp() }
        }
    }
}

// Routes that show the bottom nav + top bar
private val mainRoutes = setOf("home", "tasks", "habits", "events", "expenses")

@Composable
fun MomentoApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"
    val viewModel: MomentoViewModel = viewModel()

    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val profileInitials = viewModel.getInitials(profile?.name ?: "You")

    val showChrome = currentRoute in mainRoutes

    // Dialog state
    var showFocusMode by remember { mutableStateOf(false) }

    if (showFocusMode) FocusModeDialog(onDismiss = { showFocusMode = false })

    fun navigate(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        containerColor = MomentoBackground,
        bottomBar = {
            if (showChrome) {
                BottomNavBar(currentRoute = currentRoute) { route -> navigate(route) }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                // Top bar for main screens
                if (currentRoute == "home") {
                    val screenTitle = when (currentRoute) {
                        "home"     -> "Momento"
                        "tasks"    -> "Tasks"
                        "habits"   -> "Habits"
                        "events"   -> "Events"
                        "expenses" -> "Expenses"
                        else       -> "Momento"
                    }
                    MomentoTopBar(
                        title = screenTitle,
                        profileInitials = profileInitials,
                        onProfileClick = { navigate("profile") },
                        onSearchClick  = { navigate("search") }
                    )
                }

                NavHost(
                    navController = navController,
                    startDestination = "splash",
                    modifier = Modifier.weight(1f),
                    enterTransition = { fadeIn(animationSpec = tween(220)) + slideInHorizontally(tween(220)) { 40 } },
                    exitTransition  = { fadeOut(animationSpec = tween(180)) },
                    popEnterTransition = { fadeIn(tween(220)) + slideInHorizontally(tween(220)) { -40 } },
                    popExitTransition  = { fadeOut(tween(180)) + slideOutHorizontally(tween(220)) { 40 } }
                ) {
                    composable("splash") { SplashScreen(onNavigateToHome = { navigate("home") }) }
                    composable("home")     { DashboardScreen(viewModel, onFocusModeClick = { showFocusMode = true }, onNavigate = { navigate(it) }) }
                    composable("tasks")    { TasksScreen(viewModel) }
                    composable("habits")   { HabitsScreen(viewModel) }
                    composable("events")   { CalendarScreen(viewModel) }
                    composable("expenses") { ExpensesScreen(viewModel) }
                    composable("goals")    { GoalsScreen(viewModel) }
                    composable("notes")    { NotesScreen(viewModel) }
                    composable("analytics"){ AnalyticsScreen(viewModel) }
                    composable("search")   { SearchScreen(viewModel, onNavigate = { route -> navigate(route) }) }
                    composable("profile")  { ProfileScreen(viewModel, onNavigateBack = { navController.popBackStack() }) }
                    composable("settings") { SettingsScreen(viewModel, onNavigateBack = { navController.popBackStack() }) }
                }
        }
}

