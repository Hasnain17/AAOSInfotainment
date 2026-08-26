// ============================================================
// presentation/ui/screens/dashboard/DashboardScreen.kt
// ============================================================
// PRESENTATION LAYER — Dashboard Screen (Composable).
//
// The "home screen" of the IVI — shows speed, battery, nav info.
//
// COMPOSE CONCEPTS:
//   • collectAsStateWithLifecycle() — observes StateFlow safely
//     It cancels collection when the Composable leaves the screen.
//     This is BETTER than collectAsState() for Android.
//   • LaunchedEffect — runs a side effect when a key changes
//   • Recomposition — Compose re-runs the Composable when state changes
// ============================================================

package com.example.aaosdemo.presentation.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aaosdemo.domain.model.Maneuver
import com.example.aaosdemo.presentation.ui.screens.*
import com.example.aaosdemo.presentation.ui.theme.AAOSColors
import com.example.aaosdemo.presentation.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    // hiltViewModel() — Hilt creates the ViewModel with injected use cases
    // No factory needed — Hilt handles it all
    viewModel: DashboardViewModel = hiltViewModel()
) {
    // collectAsStateWithLifecycle() — the key pattern for Flow → Compose
    // Reads the current StateFlow value and subscribes to future updates.
    // When state changes, Compose automatically recomposes this function.
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AAOSColors.Accent)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AAOSColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Vehicle Identity Row
        state.vehicleInfo?.let { info ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "${info.make} ${info.model}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = AAOSColors.TextPrimary
                    )
                    Text(
                        info.year.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                // Live clock
                var time by remember { mutableStateOf("") }
                LaunchedEffect(Unit) {
                    // LaunchedEffect: runs this coroutine while the Composable is in composition
                    while (true) {
                        val now = java.time.LocalTime.now()
                        time = "%02d:%02d".format(now.hour, now.minute)
                        kotlinx.coroutines.delay(30_000)
                    }
                }
                Text(
                    text = time,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    color = AAOSColors.Accent,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // ── SPEED + BATTERY GAUGES ROW ────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IVICard(modifier = Modifier.weight(1f)) {
                SectionHeader("Speed")
                Spacer(Modifier.height(12.dp))
                CircularGauge(
                    value = state.speed,
                    maxValue = 180f,
                    label = "km/h",
                    color = AAOSColors.Accent,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(Modifier.width(16.dp))

            IVICard(modifier = Modifier.weight(1f)) {
                SectionHeader("Battery")
                Spacer(Modifier.height(12.dp))
                CircularGauge(
                    value = state.batteryPercent,
                    maxValue = 100f,
                    label = "%",
                    color = when {
                        state.batteryPercent > 50f -> AAOSColors.AccentGreen
                        state.batteryPercent > 20f -> AAOSColors.AccentAmber
                        else -> AAOSColors.Error
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        // ── STAT CHIPS ROW ────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatChip(
                label = "Range",
                value = "${state.rangeKm.toInt()} km",
                valueColor = AAOSColors.AccentGreen,
                modifier = Modifier.weight(1f)
            )
            StatChip(
                label = "Status",
                value = if (state.isCharging) "Charging" else "Driving",
                valueColor = if (state.isCharging) AAOSColors.AccentAmber else AAOSColors.Accent,
                modifier = Modifier.weight(1f)
            )
            state.vehicleInfo?.let { info ->
                StatChip(
                    label = "Connector",
                    value = info.connectorType,
                    valueColor = AAOSColors.AccentPurple,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ── NAVIGATION PANEL ─────────────────────────────────────────────
        state.navigation?.let { nav ->
            if (nav.isNavigating) {
                IVICard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = AAOSColors.BorderAccent
                ) {
                    SectionHeader("Navigation")
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = nav.destination,
                                style = MaterialTheme.typography.titleMedium,
                                color = AAOSColors.Accent
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "ETA: ${nav.etaMinutes} min",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        // Maneuver icon
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = when (nav.nextManeuver) {
                                    Maneuver.TURN_LEFT  -> Icons.Default.TurnLeft
                                    Maneuver.TURN_RIGHT -> Icons.Default.TurnRight
                                    Maneuver.ARRIVE     -> Icons.Default.Place
                                    else                -> Icons.Default.Straight
                                },
                                contentDescription = nav.nextManeuver.name,
                                tint = AAOSColors.AccentAmber,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "${nav.distanceToTurnMeters}m",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AAOSColors.AccentAmber
                            )
                        }
                    }
                }
            }
        }
    }
}
