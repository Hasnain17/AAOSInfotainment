package com.example.aaosdemo.domain.model

import kotlinx.coroutines.flow.Flow

// ── VEHICLE REPOSITORY ─────────────────────────────────────────────────────
// Provides a continuous stream of vehicle data via Kotlin Flow.
// Flow is perfect here because vehicle properties change over time.
interface VehicleRepository {

    // Emits a new VehicleSpeed every time VHAL reports a change.
    // In real AAOS: backed by CarPropertyManager.registerCallback()
    fun observeSpeed(): Flow<VehicleSpeed>

    // Emits battery updates (charge level, range, charging status)
    fun observeBattery(): Flow<BatteryState>

    // Static info — fetched once, not a stream
    suspend fun getVehicleInfo(): VehicleInfo
}

// ── CLIMATE REPOSITORY ─────────────────────────────────────────────────────
interface ClimateRepository {

    // Observe the full HVAC state as a Flow
    // Emits whenever any property changes (temp, fan, AC)
    fun observeClimate(): Flow<ClimateState>

    // Commands — suspend functions because they involve I/O (VHAL write)
    // In real AAOS: CarHvacManager.setFloatProperty(...)
    suspend fun setDriverTemperature(celsius: Float)
    suspend fun setPassengerTemperature(celsius: Float)
    suspend fun setAcEnabled(enabled: Boolean)
    suspend fun setFanSpeed(level: Int)  // 0–7
}

// ── MEDIA REPOSITORY ───────────────────────────────────────────────────────
interface MediaRepository {

    fun observeMedia(): Flow<MediaState>

    suspend fun play()
    suspend fun pause()
    suspend fun nextTrack()
    suspend fun previousTrack()
    suspend fun setVolume(level: Int)   // 0 to maxVolume
}

// ── NAVIGATION REPOSITORY ──────────────────────────────────────────────────
interface NavigationRepository {
    fun observeNavigation(): Flow<NavigationState>
    suspend fun startNavigation(destination: String)
    suspend fun stopNavigation()
}
