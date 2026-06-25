package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    val profileInitials = viewModel.getInitials(profile?.name ?: "Rohan")

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

    val sessionStatus by viewModel.sessionStatus.collectAsState()
    
    // Global redirect to login on sign out
    LaunchedEffect(sessionStatus) {
        if (sessionStatus is io.github.jan.supabase.gotrue.SessionStatus.NotAuthenticated && currentRoute != "splash" && currentRoute != "register") {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true } // Clear entire backstack
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (showChrome) {
                BottomNavBar(currentRoute = currentRoute) { route -> navigate(route) }
            }
        }
    ) { innerPadding ->
        // Glowing ambient background
        Box(modifier = Modifier.fillMaxSize().background(MomentoBackground)) {
            Box(modifier = Modifier.fillMaxWidth(1.4f).fillMaxHeight(0.55f).align(Alignment.TopStart).offset(x = (-80).dp, y = (-80).dp).background(Brush.radialGradient(listOf(MomentoPrimary.copy(alpha = 0.18f), Color.Transparent), radius = 900f)))
            Box(modifier = Modifier.fillMaxWidth(1.2f).fillMaxHeight(0.45f).align(Alignment.BottomEnd).offset(x = 80.dp, y = 80.dp).background(Brush.radialGradient(listOf(MomentoTertiary.copy(alpha = 0.12f), Color.Transparent), radius = 700f)))
            Box(modifier = Modifier.fillMaxWidth(0.7f).fillMaxHeight(0.4f).align(Alignment.CenterEnd).background(Brush.radialGradient(listOf(MomentoSecondary.copy(alpha = 0.10f), Color.Transparent), radius = 500f)))

            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                // Top bar for main screens
                if (showChrome) {
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
                        onProfileClick = { navController.navigate("profile") },
                        onSearchClick  = { navController.navigate("search") }
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
                    composable("splash") {
                        SplashScreen(
                            viewModel = viewModel,
                            onNavigateToHome = { navigate("home") },
                            onNavigateToLogin = { navigate("login") }
                        )
                    }
                    composable("login") {
                        LoginScreen(
                            viewModel = viewModel,
                            onNavigateToRegister = { navigate("register") },
                            onNavigateToHome = { navigate("home") }
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            viewModel = viewModel,
                            onNavigateToLogin = { navigate("login") },
                            onNavigateToHome = { navigate("home") }
                        )
                    }
                    composable("home")     { DashboardScreen(viewModel, onFocusModeClick = { showFocusMode = true }, onNavigate = { navController.navigate(it) }) }
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
    }
}
