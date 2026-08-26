// ============================================================
// presentation/ui/screens/climate/ClimateScreen.kt
// ============================================================

package com.example.aaosdemo.presentation.ui.screens.climate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aaosdemo.presentation.ui.screens.*
import com.example.aaosdemo.presentation.ui.theme.AAOSColors
import com.example.aaosdemo.presentation.viewmodel.ClimateViewModel

@Composable
fun ClimateScreen(viewModel: ClimateViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AAOSColors.Background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "Climate Control",
            style = MaterialTheme.typography.headlineMedium,
            color = AAOSColors.TextPrimary
        )

        // ── DUAL ZONE TEMPERATURE ─────────────────────────────────────────
        IVICard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = AAOSColors.BorderAccent
        ) {
            SectionHeader("Zone Temperature")
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Driver zone
                TemperatureControl(
                    label = "Driver",
                    temperature = state.driverTemp,
                    onDecrease = { viewModel.onDriverTempChange(state.driverTemp - 0.5f) },
                    onIncrease = { viewModel.onDriverTempChange(state.driverTemp + 0.5f) }
                )

                // Vertical divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(120.dp)
                        .background(AAOSColors.Border)
                )

                // Passenger zone
                TemperatureControl(
                    label = "Passenger",
                    temperature = state.passengerTemp,
                    onDecrease = { viewModel.onPassengerTempChange(state.passengerTemp - 0.5f) },
                    onIncrease = { viewModel.onPassengerTempChange(state.passengerTemp + 0.5f) }
                )
            }
        }

        // ── CLIMATE TOGGLES ───────────────────────────────────────────────
        IVICard(modifier = Modifier.fillMaxWidth()) {
            SectionHeader("Controls")
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IVIToggle(
                    label = "A/C",
                    icon = "❄",
                    enabled = state.isAcOn,
                    onClick = { viewModel.onAcToggle() },
                    modifier = Modifier.weight(1f)
                )
                IVIToggle(
                    label = "Fan",
                    icon = "💨",
                    enabled = state.isFanOn,
                    onClick = { viewModel.onFanSpeedChange(if (state.isFanOn) 0 else 3) },
                    modifier = Modifier.weight(1f)
                )
                IVIToggle(
                    label = "Defrost",
                    icon = "🌡",
                    enabled = state.isDefrostOn,
                    onClick = { /* add defrost toggle to VM */ },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ── FAN SPEED SLIDER ──────────────────────────────────────────────
        IVICard(modifier = Modifier.fillMaxWidth()) {
            SectionHeader("Fan Speed")
            Spacer(Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("0", color = AAOSColors.TextTertiary)
                // Compose Slider — maps 0..7 steps
                Slider(
                    value = state.fanSpeed.toFloat(),
                    onValueChange = { viewModel.onFanSpeedChange(it.toInt()) },
                    valueRange = 0f..7f,
                    steps = 6,  // 8 positions: 0,1,2,3,4,5,6,7
                    colors = SliderDefaults.colors(
                        thumbColor = AAOSColors.Accent,
                        activeTrackColor = AAOSColors.Accent,
                        inactiveTrackColor = AAOSColors.Border
                    ),
                    modifier = Modifier.weight(1f)
                )
                Text("7", color = AAOSColors.TextTertiary)
                Text(
                    text = state.fanSpeed.toString(),
                    color = AAOSColors.Accent,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
