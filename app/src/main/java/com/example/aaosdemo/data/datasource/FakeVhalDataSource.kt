// ============================================================
// data/datasource/FakeVhalDataSource.kt
// ============================================================
// DATA LAYER — Fake DataSource (simulates Android Car Services).
//
// In a REAL AAOS app, this class would:
//   1. Hold a reference to CarPropertyManager, CarHvacManager etc.
//   2. Use callbackFlow { } to wrap their callback-based APIs into Flow.
//   3. Be annotated @Singleton and injected by Hilt.
//
// Since this demo runs on any Android device (no AAOS system image),
// we use coroutine timers to emit realistic fake data.
//
// The PATTERN is identical to real AAOS — only the emission source differs.
//
// KEY KOTLIN FLOW CONCEPTS DEMONSTRATED:
//   • StateFlow   — hot stream, holds current value, perfect for UI state
//   • callbackFlow — wraps callback APIs into cold Flow
//   • flow { }    — cold stream, starts fresh per collector
//   • MutableStateFlow.update() — thread-safe state mutation
// ============================================================

package com.example.aaosdemo.data.datasource

import com.example.aaosdemo.domain.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sin
import kotlin.random.Random

@Singleton  // One instance for the app lifecycle (Hilt scope)
class FakeVhalDataSource @Inject constructor() {

    // CoroutineScope for background data simulation
    // SupervisorJob means one failing child doesn't cancel others
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // ── VEHICLE SPEED ──────────────────────────────────────────────────────
    // MutableStateFlow: holds CURRENT value + replays it to new collectors.
    // This is the AAOS equivalent of reading PERF_VEHICLE_SPEED.
    private val _speed = MutableStateFlow(VehicleSpeed(kmh = 68f, isReliable = true))

    // Simulate speed changes — like a car accelerating/decelerating on a route
    init {
        scope.launch {
            var t = 0.0
            while (true) {
                delay(1500)
                t += 0.1
                // Smooth sine-wave speed variation between 50–100 km/h
                val newSpeed = 75f + (sin(t) * 25f).toFloat()
                _speed.update { it.copy(kmh = newSpeed) }
            }
        }
    }

    // Public read-only view — callers can only observe, not push values
    val speedFlow: StateFlow<VehicleSpeed> = _speed.asStateFlow()

    // ── BATTERY STATE ──────────────────────────────────────────────────────
    private val _battery = MutableStateFlow(
        BatteryState(
            levelPercent = 78f,
            rangeKm = 312f,
            isCharging = false,
            chargeRateKw = 0f
        )
    )

    init {
        scope.launch {
            while (true) {
                delay(5000)
                // Slowly drain battery while driving
                _battery.update { current ->
                    val newLevel = (current.levelPercent - 0.1f).coerceAtLeast(0f)
                    current.copy(
                        levelPercent = newLevel,
                        rangeKm = newLevel * 4f  // ~4km per % for an EV
                    )
                }
            }
        }
    }

    val batteryFlow: StateFlow<BatteryState> = _battery.asStateFlow()

    // ── CLIMATE STATE ──────────────────────────────────────────────────────
    // In real AAOS, this would be a callbackFlow wrapping:
    //   carHvacManager.registerCallback(callback, VehicleArea.SEAT, updateRate)
    //
    // HOW callbackFlow WORKS (shown conceptually below):
    //   val realClimateFlow = callbackFlow<ClimateState> {
    //       val callback = object : CarHvacManager.CarHvacEventCallback {
    //           override fun onChangeEvent(value: CarPropertyValue<*>) {
    //               trySend(buildClimateState(value))  // push to flow
    //           }
    //       }
    //       carHvacManager.registerCallback(callback)
    //       awaitClose { carHvacManager.unregisterCallback(callback) }
    //   }

    private val _climate = MutableStateFlow(
        ClimateState(
            driverTempCelsius = 22.0f,
            passengerTempCelsius = 21.5f,
            isAcOn = true,
            isFanOn = true,
            fanSpeed = 3,
            isDefrostOn = false
        )
    )

    val climateFlow: StateFlow<ClimateState> = _climate.asStateFlow()

    // Commands — update state directly (VHAL write simulation)
    fun setDriverTemp(celsius: Float) {
        _climate.update { it.copy(driverTempCelsius = celsius.coerceIn(16f, 30f)) }
    }

    fun setPassengerTemp(celsius: Float) {
        _climate.update { it.copy(passengerTempCelsius = celsius.coerceIn(16f, 30f)) }
    }

    fun setAc(enabled: Boolean) {
        _climate.update { it.copy(isAcOn = enabled) }
    }

    fun setFanSpeed(level: Int) {
        _climate.update { it.copy(fanSpeed = level.coerceIn(0, 7), isFanOn = level > 0) }
    }

    // ── MEDIA STATE ────────────────────────────────────────────────────────
    private val playlist = listOf(
        Triple("Midnight Drive", "Synthwave Artist", 214),
        Triple("Electric Feel", "MGMT", 232),
        Triple("Digital Love", "Daft Punk", 301),
        Triple("Running Up That Hill", "Kate Bush", 298),
    )
    private var trackIndex = 0

    private val _media = MutableStateFlow(
        MediaState(
            trackTitle = playlist[0].first,
            artistName = playlist[0].second,
            albumArt = "album_art_0",
            isPlaying = true,
            progressFraction = 0f,
            durationSeconds = playlist[0].third,
            volume = 10,
            maxVolume = 15
        )
    )

    init {
        scope.launch {
            while (true) {
                delay(500)
                _media.update { current ->
                    if (!current.isPlaying) return@update current
                    val newProgress = current.progressFraction + (0.5f / current.durationSeconds)
                    if (newProgress >= 1f) {
                        // Auto-advance to next track
                        trackIndex = (trackIndex + 1) % playlist.size
                        val next = playlist[trackIndex]
                        current.copy(
                            trackTitle = next.first,
                            artistName = next.second,
                            progressFraction = 0f,
                            durationSeconds = next.third
                        )
                    } else {
                        current.copy(progressFraction = newProgress)
                    }
                }
            }
        }
    }

    val mediaFlow: StateFlow<MediaState> = _media.asStateFlow()

    fun play()  { _media.update { it.copy(isPlaying = true) } }
    fun pause() { _media.update { it.copy(isPlaying = false) } }

    fun nextTrack() {
        trackIndex = (trackIndex + 1) % playlist.size
        val next = playlist[trackIndex]
        _media.update { it.copy(
            trackTitle = next.first,
            artistName = next.second,
            progressFraction = 0f,
            durationSeconds = next.third
        )}
    }

    fun previousTrack() {
        trackIndex = (trackIndex - 1 + playlist.size) % playlist.size
        val prev = playlist[trackIndex]
        _media.update { it.copy(
            trackTitle = prev.first,
            artistName = prev.second,
            progressFraction = 0f,
            durationSeconds = prev.third
        )}
    }

    fun setVolume(level: Int) {
        _media.update { it.copy(volume = level.coerceIn(0, it.maxVolume)) }
    }

    // ── VEHICLE INFO ───────────────────────────────────────────────────────
    // Static — fetched once. No Flow needed.
    suspend fun getVehicleInfo(): VehicleInfo {
        delay(300) // Simulate async read
        return VehicleInfo(
            make = "Volvo",
            model = "EX90",
            year = 2024,
            fuelType = FuelType.Electric,
            connectorType = "CCS2"
        )
    }

    // ── NAVIGATION STATE ───────────────────────────────────────────────────
    private val _navigation = MutableStateFlow(
        NavigationState(
            isNavigating = true,
            destination = "Lindholmen Science Park, Göteborg",
            distanceToTurnMeters = 350,
            nextManeuver = Maneuver.TURN_RIGHT,
            etaMinutes = 4
        )
    )

    init {
        scope.launch {
            while (true) {
                delay(2000)
                _navigation.update { current ->
                    if (!current.isNavigating) return@update current
                    val newDist = (current.distanceToTurnMeters - Random.nextInt(10, 30))
                        .coerceAtLeast(0)
                    current.copy(distanceToTurnMeters = newDist)
                }
            }
        }
    }

    val navigationFlow: StateFlow<NavigationState> = _navigation.asStateFlow()

    fun startNavigation(dest: String) {
        _navigation.update { it.copy(isNavigating = true, destination = dest) }
    }

    fun stopNavigation() {
        _navigation.update { it.copy(isNavigating = false) }
    }
}
