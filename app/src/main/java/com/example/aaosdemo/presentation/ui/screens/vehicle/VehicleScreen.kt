package com.example.aaosdemo.presentation.ui.screens.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aaosdemo.domain.model.FuelType
import com.example.aaosdemo.presentation.ui.screens.*
import com.example.aaosdemo.presentation.ui.theme.AAOSColors
import com.example.aaosdemo.presentation.viewmodel.VehicleInfoViewModel

@Composable
fun VehicleScreen(viewModel: VehicleInfoViewModel = hiltViewModel()) {
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
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Vehicle Info",
            style = MaterialTheme.typography.headlineMedium,
            color = AAOSColors.TextPrimary
        )

        // ── VEHICLE IDENTITY ──────────────────────────────────────────────
        state.info?.let { info ->
            IVICard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = AAOSColors.BorderAccent
            ) {
                SectionHeader("Identity")
                Spacer(Modifier.height(16.dp))

                InfoRow("Make", info.make)
                InfoRow("Model", info.model)
                InfoRow("Year", info.year.toString())
                InfoRow(
                    "Fuel Type",
                    when (info.fuelType) {
                        is FuelType.Electric -> "Electric (BEV)"
                        is FuelType.Hybrid   -> "Hybrid (PHEV)"
                        is FuelType.Petrol   -> "Petrol (ICE)"
                        else                 -> "Unknown"
                    }
                )
                InfoRow("Connector", info.connectorType)
            }

            // ── LIVE TELEMETRY ────────────────────────────────────────────
            IVICard(modifier = Modifier.fillMaxWidth()) {
                SectionHeader("Live Telemetry")
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatChip(
                        label = "Speed",
                        value = "${state.currentSpeed.toInt()} km/h",
                        valueColor = AAOSColors.Accent,
                        modifier = Modifier.weight(1f)
                    )
                    state.battery?.let { bat ->
                        StatChip(
                            label = "Battery",
                            value = "${"%.1f".format(bat.levelPercent)}%",
                            valueColor = AAOSColors.AccentGreen,
                            modifier = Modifier.weight(1f)
                        )
                        StatChip(
                            label = "Range",
                            value = "${bat.rangeKm.toInt()} km",
                            valueColor = AAOSColors.AccentAmber,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // ── AAOS API NOTE ─────────────────────────────────────────────
            IVICard(modifier = Modifier.fillMaxWidth()) {
                SectionHeader("AAOS API Used")
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "CarInfoManager.getString(BASIC_INFO_KEY_MANUFACTURER)\n" +
                           "CarInfoManager.getString(BASIC_INFO_KEY_MODEL)\n" +
                           "VehiclePropertyIds.PERF_VEHICLE_SPEED\n" +
                           "VehiclePropertyIds.EV_BATTERY_LEVEL",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    ),
                    color = AAOSColors.AccentGreen
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = AAOSColors.TextTertiary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = AAOSColors.TextPrimary
        )
    }
    Divider(color = AAOSColors.Border, thickness = 0.5.dp)
}
