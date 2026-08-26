// ============================================================
// data/repository/RepositoryImpls.kt
// ============================================================
// DATA LAYER — Repository Implementations.
//
// Each class:
//   1. Implements a domain interface (from domain/model/Repositories.kt)
//   2. Holds a reference to the DataSource (injected by Hilt)
//   3. Converts DataSource data if needed (e.g. adding error handling)
//   4. Is injected into Use Cases via constructor injection
//
// Notice: the domain layer has NO idea these classes exist.
// The ViewModel/UseCase only see the INTERFACE.
// ============================================================

package com.example.aaosdemo.data.repository

import com.example.aaosdemo.data.datasource.FakeVhalDataSource
import com.example.aaosdemo.domain.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ── VEHICLE REPOSITORY IMPL ────────────────────────────────────────────────
class VehicleRepositoryImpl @Inject constructor(
    private val dataSource: FakeVhalDataSource
) : VehicleRepository {

    override fun observeSpeed(): Flow<VehicleSpeed> =
        dataSource.speedFlow

    override fun observeBattery(): Flow<BatteryState> =
        dataSource.batteryFlow

    override suspend fun getVehicleInfo(): VehicleInfo =
        dataSource.getVehicleInfo()
}

// ── CLIMATE REPOSITORY IMPL ────────────────────────────────────────────────
class ClimateRepositoryImpl @Inject constructor(
    private val dataSource: FakeVhalDataSource
) : ClimateRepository {

    override fun observeClimate(): Flow<ClimateState> =
        dataSource.climateFlow

    override suspend fun setDriverTemperature(celsius: Float) {
        dataSource.setDriverTemp(celsius)
    }

    override suspend fun setPassengerTemperature(celsius: Float) {
        dataSource.setPassengerTemp(celsius)
    }

    override suspend fun setAcEnabled(enabled: Boolean) {
        dataSource.setAc(enabled)
    }

    override suspend fun setFanSpeed(level: Int) {
        dataSource.setFanSpeed(level)
    }
}

// ── MEDIA REPOSITORY IMPL ──────────────────────────────────────────────────
class MediaRepositoryImpl @Inject constructor(
    private val dataSource: FakeVhalDataSource
) : MediaRepository {

    override fun observeMedia(): Flow<MediaState> =
        dataSource.mediaFlow

    override suspend fun play()          = dataSource.play()
    override suspend fun pause()         = dataSource.pause()
    override suspend fun nextTrack()     = dataSource.nextTrack()
    override suspend fun previousTrack() = dataSource.previousTrack()
    override suspend fun setVolume(level: Int) = dataSource.setVolume(level)
}

// ── NAVIGATION REPOSITORY IMPL ─────────────────────────────────────────────
class NavigationRepositoryImpl @Inject constructor(
    private val dataSource: FakeVhalDataSource
) : NavigationRepository {

    override fun observeNavigation(): Flow<NavigationState> =
        dataSource.navigationFlow

    override suspend fun startNavigation(destination: String) {
        dataSource.startNavigation(destination)
    }

    override suspend fun stopNavigation() {
        dataSource.stopNavigation()
    }
}
