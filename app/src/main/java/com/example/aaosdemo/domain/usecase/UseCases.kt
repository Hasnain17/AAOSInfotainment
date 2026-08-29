package com.example.aaosdemo.domain.usecase

import com.example.aaosdemo.domain.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ── VEHICLE USE CASES ──────────────────────────────────────────────────────

class ObserveVehicleSpeedUseCase @Inject constructor(
    private val repository: VehicleRepository
) {
    // operator fun invoke() lets you call this as: observeSpeed()
    operator fun invoke(): Flow<VehicleSpeed> = repository.observeSpeed()
}

class ObserveBatteryUseCase @Inject constructor(
    private val repository: VehicleRepository
) {
    operator fun invoke(): Flow<BatteryState> = repository.observeBattery()
}

class GetVehicleInfoUseCase @Inject constructor(
    private val repository: VehicleRepository
) {
    suspend operator fun invoke(): VehicleInfo = repository.getVehicleInfo()
}

// ── CLIMATE USE CASES ──────────────────────────────────────────────────────

class ObserveClimateUseCase @Inject constructor(
    private val repository: ClimateRepository
) {
    operator fun invoke(): Flow<ClimateState> = repository.observeClimate()
}

class SetDriverTemperatureUseCase @Inject constructor(
    private val repository: ClimateRepository
) {
    // Business rule: temperature must be between 16°C and 30°C
    // This validation lives in the USE CASE, not in the ViewModel or UI.
    suspend operator fun invoke(celsius: Float) {
        val clamped = celsius.coerceIn(16f, 30f)
        repository.setDriverTemperature(clamped)
    }
}

class SetPassengerTemperatureUseCase @Inject constructor(
    private val repository: ClimateRepository
) {
    suspend operator fun invoke(celsius: Float) {
        repository.setPassengerTemperature(celsius.coerceIn(16f, 30f))
    }
}

class SetAcEnabledUseCase @Inject constructor(
    private val repository: ClimateRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setAcEnabled(enabled)
    }
}

class SetFanSpeedUseCase @Inject constructor(
    private val repository: ClimateRepository
) {
    // Business rule: fan level 0–7 only
    suspend operator fun invoke(level: Int) {
        repository.setFanSpeed(level.coerceIn(0, 7))
    }
}

// ── MEDIA USE CASES ────────────────────────────────────────────────────────

class ObserveMediaUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    operator fun invoke(): Flow<MediaState> = repository.observeMedia()
}

class PlayPauseUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    // Toggle play/pause based on current state
    suspend operator fun invoke(currentlyPlaying: Boolean) {
        if (currentlyPlaying) repository.pause() else repository.play()
    }
}

class SkipTrackUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    suspend fun next()     = repository.nextTrack()
    suspend fun previous() = repository.previousTrack()
}

class SetVolumeUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    suspend operator fun invoke(level: Int) = repository.setVolume(level)
}

// ── NAVIGATION USE CASES ───────────────────────────────────────────────────

class ObserveNavigationUseCase @Inject constructor(
    private val repository: NavigationRepository
) {
    operator fun invoke(): Flow<NavigationState> = repository.observeNavigation()
}
