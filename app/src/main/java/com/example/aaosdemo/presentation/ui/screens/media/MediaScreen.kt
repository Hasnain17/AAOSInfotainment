package com.example.aaosdemo.presentation.ui.screens.media

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aaosdemo.presentation.ui.screens.*
import com.example.aaosdemo.presentation.ui.theme.AAOSColors
import com.example.aaosdemo.presentation.viewmodel.MediaViewModel

@Composable
fun MediaScreen(viewModel: MediaViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AAOSColors.Background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "Media",
            style = MaterialTheme.typography.headlineMedium,
            color = AAOSColors.TextPrimary
        )

        // ── ALBUM ART + TRACK INFO ────────────────────────────────────────
        IVICard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = AAOSColors.BorderAccent
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Album art placeholder
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AAOSColors.SurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text("♪", fontSize = 36.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.trackTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = AAOSColors.TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = state.artistName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AAOSColors.TextSecondary
                    )
                    Spacer(Modifier.height(16.dp))

                    // Progress bar
                    MediaProgressBar(
                        progress = state.progressFraction,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = state.progressText,
                        style = MaterialTheme.typography.labelSmall,
                        color = AAOSColors.TextTertiary
                    )
                }
            }
        }

        // ── PLAYBACK CONTROLS ─────────────────────────────────────────────
        IVICard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous
                IconButton(onClick = { viewModel.onPrevious() }) {
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = AAOSColors.TextSecondary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Play/Pause — larger, accented
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(AAOSColors.Accent),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { viewModel.onPlayPause() }) {
                        Icon(
                            imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (state.isPlaying) "Pause" else "Play",
                            tint = AAOSColors.Background,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Next
                IconButton(onClick = { viewModel.onNext() }) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = AAOSColors.TextSecondary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // ── VOLUME CONTROL ────────────────────────────────────────────────
        IVICard(modifier = Modifier.fillMaxWidth()) {
            SectionHeader("Volume")
            Spacer(Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.VolumeDown,
                    contentDescription = null,
                    tint = AAOSColors.TextTertiary,
                    modifier = Modifier.size(20.dp)
                )
                Slider(
                    value = state.volume.toFloat(),
                    onValueChange = { viewModel.onVolumeChange(it.toInt()) },
                    valueRange = 0f..state.maxVolume.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = AAOSColors.Accent,
                        activeTrackColor = AAOSColors.Accent,
                        inactiveTrackColor = AAOSColors.Border
                    ),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = AAOSColors.TextTertiary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "${state.volume}",
                    color = AAOSColors.Accent,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.width(28.dp)
                )
            }
        }
    }
}
