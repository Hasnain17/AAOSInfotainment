package com.example.aaosdemo.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.aaosdemo.presentation.ui.screens.climate.ClimateScreen
import com.example.aaosdemo.presentation.ui.screens.dashboard.DashboardScreen
import com.example.aaosdemo.presentation.ui.screens.media.MediaScreen
import com.example.aaosdemo.presentation.ui.screens.vehicle.VehicleScreen
import com.example.aaosdemo.presentation.ui.theme.AAOSColors

// ── ROUTES ─────────────────────────────────────────────────────────────────
// Sealed class = type-safe route definitions. No stringly-typed mistakes.
sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Climate   : Screen("climate",   "Climate",   Icons.Default.AcUnit)
    object Media     : Screen("media",     "Media",     Icons.Default.MusicNote)
    object Vehicle   : Screen("vehicle",   "Vehicle",   Icons.Default.DirectionsCar)
}

val bottomNavScreens = listOf(
    Screen.Dashboard,
    Screen.Climate,
    Screen.Media,
    Screen.Vehicle
)

// ── MAIN APP SCAFFOLD ──────────────────────────────────────────────────────
@Composable
fun AAOSApp() {
    val navController = rememberNavController()

    Scaffold(
        containerColor = AAOSColors.Background,
        bottomBar = {
            AAOSBottomNav(navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier
                .padding(paddingValues)
                .background(AAOSColors.Background)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen() }
            composable(Screen.Climate.route)   { ClimateScreen() }
            composable(Screen.Media.route)     { MediaScreen() }
            composable(Screen.Vehicle.route)   { VehicleScreen() }
        }
    }
}

// ── BOTTOM NAV BAR ─────────────────────────────────────────────────────────
@Composable
fun AAOSBottomNav(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = AAOSColors.Surface,
        tonalElevation = 0.dp
    ) {
        bottomNavScreens.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to start destination to avoid building up a huge backstack
                        popUpTo(Screen.Dashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        screen.icon,
                        contentDescription = screen.label,
                        modifier = Modifier.then(Modifier)
                    )
                },
                label = {
                    Text(
                        screen.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AAOSColors.Accent,
                    selectedTextColor = AAOSColors.Accent,
                    unselectedIconColor = AAOSColors.TextTertiary,
                    unselectedTextColor = AAOSColors.TextTertiary,
                    indicatorColor = AAOSColors.SurfaceVariant
                )
            )
        }
    }
}
