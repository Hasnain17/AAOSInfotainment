package com.example.aaosdemo.domain.model

// ── VEHICLE SPEED ──────────────────────────────────────────────────────────
// VHAL gives speed in m/s. We convert it to km/h in the DataSource.
data class VehicleSpeed(
    val kmh: Float,          // Human-readable speed in km/h
    val isReliable: Boolean  // False if VHAL returned an error
)

// ── CLIMATE / HVAC ─────────────────────────────────────────────────────────
// Represents the state of the car's heating/ventilation/AC system.
// Maps to: CarHvacManager properties in real AAOS.
data class ClimateState(
    val driverTempCelsius: Float,       // Driver zone setpoint
    val passengerTempCelsius: Float,    // Passenger zone setpoint
    val isAcOn: Boolean,                // AC compressor state
    val isFanOn: Boolean,               // Fan blower state
    val fanSpeed: Int,                  // 0–7 fan speed level
    val isDefrostOn: Boolean            // Rear window defrost
)

// ── MEDIA / AUDIO ──────────────────────────────────────────────────────────
// Maps to: CarAudioManager + MediaSession in real AAOS.
data class MediaState(
    val trackTitle: String,
    val artistName: String,
    val albumArt: String,               // URL or resource name (simplified)
    val isPlaying: Boolean,
    val progressFraction: Float,        // 0.0f → 1.0f
    val durationSeconds: Int,
    val volume: Int,                    // 0–15 (CarAudioManager group volume)
    val maxVolume: Int
)

// ── BATTERY / EV ───────────────────────────────────────────────────────────
// Maps to: VehiclePropertyIds.EV_BATTERY_LEVEL in real AAOS.
data class BatteryState(
    val levelPercent: Float,            // 0.0 → 100.0
    val rangeKm: Float,                 // Estimated range
    val isCharging: Boolean,
    val chargeRateKw: Float             // Current charge power (0 if not charging)
)

// ── VEHICLE INFO ───────────────────────────────────────────────────────────
// Maps to: CarInfoManager in real AAOS.
data class VehicleInfo(
    val make: String,           // e.g. "Volvo"
    val model: String,          // e.g. "EX90"
    val year: Int,              // e.g. 2024
    val fuelType: FuelType,
    val connectorType: String   // e.g. "CCS2"
)

// Sealed class for fuel types (maps to CarInfoManager.BASIC_INFO_KEY_FUEL_TYPES)
sealed class FuelType {
    object Electric : FuelType()
    object Hybrid : FuelType()
    object Petrol : FuelType()
    data class Unknown(val code: Int) : FuelType()
}

// ── NAVIGATION ─────────────────────────────────────────────────────────────
data class NavigationState(
    val isNavigating: Boolean,
    val destination: String,
    val distanceToTurnMeters: Int,
    val nextManeuver: Maneuver,
    val etaMinutes: Int
)

enum class Maneuver {
    STRAIGHT, TURN_LEFT, TURN_RIGHT, ROUNDABOUT, ARRIVE, NONE
}
