// ============================================================
// presentation/viewmodel/ViewModels.kt
// ============================================================
// PRESENTATION LAYER — ViewModels.
//
// ViewModels are the bridge between Domain (use cases) and UI (Compose).
//
// KEY PATTERNS:
//   • @HiltViewModel + @Inject constructor → Hilt creates the ViewModel
//   • viewModelScope.launch → coroutines tied to ViewModel lifecycle
//   • StateFlow<UiState> → single observable UI state object
//   • combine() → merge multiple flows into one UI state
//   • _uiState (private MutableStateFlow) / uiState (public read-only)
//
// WHY StateFlow instead of LiveData?
//   • Kotlin-native, works with coroutines natively
//   • collectAsStateWithLifecycle() in Compose is lifecycle-safe
//   • Flow operators (map, combine, filter) are powerful
//   • No Android dependency — testable in plain JUnit
// ============================================================

package com.example.aaosdemo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aaosdemo.domain.model.*
import com.example.aaosdemo.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ═══════════════════════════════════════════════════════════════════════════
// DASHBOARD VIEWMODEL
// ═══════════════════════════════════════════════════════════════════════════

// UI State — a single data class for everything the Dashboard screen needs.
// This pattern (single UiState object) avoids UI state fragmentation.
data class DashboardUiState(
    val speed: Float = 0f,
    val batteryPercent: Float = 0f,
    val rangeKm: Float = 0f,
    val isCharging: Boolean = false,
    val vehicleInfo: VehicleInfo? = null,
    val isLoading: Boolean = true,
    val navigation: NavigationState? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val observeSpeed: ObserveVehicleSpeedUseCase,
    private val observeBattery: ObserveBatteryUseCase,
    private val getVehicleInfo: GetVehicleInfoUseCase,
    private val observeNavigation: ObserveNavigationUseCase
) : ViewModel() {

    // MutableStateFlow — writable internally
    private val _uiState = MutableStateFlow(DashboardUiState())

    // Public StateFlow — read-only for the UI
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {

        // TECHNIQUE: combine() merges multiple Flows into one.
        // The lambda runs whenever ANY of the flows emits a new value.
        // This keeps the UI state in sync with all data sources.
        viewModelScope.launch {
            combine(
                observeSpeed(),
                observeBattery(),
                observeNavigation()
            ) { speed, battery, nav ->
                DashboardUiState(
                    speed = speed.kmh,
                    batteryPercent = battery.levelPercent,
                    rangeKm = battery.rangeKm,
                    isCharging = battery.isCharging,
                    navigation = nav,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.update { it.copy(
                    speed = state.speed,
                    batteryPercent = state.batteryPercent,
                    rangeKm = state.rangeKm,
                    isCharging = state.isCharging,
                    navigation = state.navigation,
                    isLoading = false
                )}
            }
        }

        // Load static vehicle info separately (suspend, not a stream)
        viewModelScope.launch {
            val info = getVehicleInfo()
            _uiState.update { it.copy(vehicleInfo = info) }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// CLIMATE VIEWMODEL
// ═══════════════════════════════════════════════════════════════════════════

data class ClimateUiState(
    val driverTemp: Float = 22f,
    val passengerTemp: Float = 21.5f,
    val isAcOn: Boolean = true,
    val isFanOn: Boolean = true,
    val fanSpeed: Int = 3,
    val isDefrostOn: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class ClimateViewModel @Inject constructor(
    private val observeClimate: ObserveClimateUseCase,
    private val setDriverTemp: SetDriverTemperatureUseCase,
    private val setPassengerTemp: SetPassengerTemperatureUseCase,
    private val setAcEnabled: SetAcEnabledUseCase,
    private val setFanSpeed: SetFanSpeedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClimateUiState())
    val uiState: StateFlow<ClimateUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // map() transforms each emitted ClimateState into a ClimateUiState
            observeClimate()
                .map { climate ->
                    ClimateUiState(
                        driverTemp = climate.driverTempCelsius,
                        passengerTemp = climate.passengerTempCelsius,
                        isAcOn = climate.isAcOn,
                        isFanOn = climate.isFanOn,
                        fanSpeed = climate.fanSpeed,
                        isDefrostOn = climate.isDefrostOn
                    )
                }
                .collect { _uiState.value = it }
        }
    }

    // User actions — launched in viewModelScope so they're cancelled
    // automatically when the ViewModel is cleared (user leaves screen)
    fun onDriverTempChange(celsius: Float) {
        viewModelScope.launch { setDriverTemp(celsius) }
    }

    fun onPassengerTempChange(celsius: Float) {
        viewModelScope.launch { setPassengerTemp(celsius) }
    }

    fun onAcToggle() {
        viewModelScope.launch { setAcEnabled(!_uiState.value.isAcOn) }
    }

    fun onFanSpeedChange(level: Int) {
        viewModelScope.launch { setFanSpeed(level) }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// MEDIA VIEWMODEL
// ═══════════════════════════════════════════════════════════════════════════

data class MediaUiState(
    val trackTitle: String = "",
    val artistName: String = "",
    val isPlaying: Boolean = false,
    val progressFraction: Float = 0f,
    val volume: Int = 10,
    val maxVolume: Int = 15,
    val progressText: String = "0:00"
)

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val observeMedia: ObserveMediaUseCase,
    private val playPause: PlayPauseUseCase,
    private val skipTrack: SkipTrackUseCase,
    private val setVolume: SetVolumeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MediaUiState())
    val uiState: StateFlow<MediaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeMedia().collect { media ->
                val elapsed = (media.progressFraction * media.durationSeconds).toInt()
                _uiState.value = MediaUiState(
                    trackTitle = media.trackTitle,
                    artistName = media.artistName,
                    isPlaying = media.isPlaying,
                    progressFraction = media.progressFraction,
                    volume = media.volume,
                    maxVolume = media.maxVolume,
                    progressText = formatTime(elapsed)
                )
            }
        }
    }

    fun onPlayPause() {
        viewModelScope.launch { playPause(_uiState.value.isPlaying) }
    }

    fun onNext()     { viewModelScope.launch { skipTrack.next() } }
    fun onPrevious() { viewModelScope.launch { skipTrack.previous() } }

    fun onVolumeChange(level: Int) {
        viewModelScope.launch { setVolume(level) }
    }

    private fun formatTime(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return "$m:${s.toString().padStart(2, '0')}"
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// VEHICLE INFO VIEWMODEL
// ═══════════════════════════════════════════════════════════════════════════

data class VehicleInfoUiState(
    val info: VehicleInfo? = null,
    val currentSpeed: Float = 0f,
    val battery: BatteryState? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class VehicleInfoViewModel @Inject constructor(
    private val getVehicleInfo: GetVehicleInfoUseCase,
    private val observeSpeed: ObserveVehicleSpeedUseCase,
    private val observeBattery: ObserveBatteryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VehicleInfoUiState())
    val uiState: StateFlow<VehicleInfoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val info = getVehicleInfo()
            _uiState.update { it.copy(info = info, isLoading = false) }
        }

        viewModelScope.launch {
            observeSpeed().collect { speed ->
                _uiState.update { it.copy(currentSpeed = speed.kmh) }
            }
        }

        viewModelScope.launch {
            observeBattery().collect { battery ->
                _uiState.update { it.copy(battery = battery) }
            }
        }
    }
}
