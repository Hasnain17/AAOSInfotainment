// ============================================================
// test/ClimateViewModelTest.kt
// ============================================================
// TESTING — Unit Tests with JUnit 5 + Mockk + Coroutine Test.
//
// This is exactly the testing pattern Volvo described in the JD:
//   "unit testing (JUnit, Mockk)"
//
// KEY TESTING CONCEPTS:
//   • @ExtendWith(MockKExtension) — enables Mockk annotations
//   • @MockK — creates a mock (fake) of an interface
//   • coEvery { } — stub a suspend function call
//   • coVerify { } — assert a suspend function was called
//   • runTest { } — runs coroutine test with TestCoroutineScheduler
//   • MainDispatcherRule — replaces Main dispatcher with TestDispatcher
//     (avoids "Module with the Main dispatcher had failed to initialize" errors)
//   • StandardTestDispatcher — controlled, doesn't auto-advance
//   • advanceUntilIdle() — runs all pending coroutines
// ============================================================

package com.example.aaosdemo

import com.example.aaosdemo.domain.model.ClimateState
import com.example.aaosdemo.domain.usecase.*
import com.example.aaosdemo.presentation.viewmodel.ClimateViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

// ── MAIN DISPATCHER RULE ───────────────────────────────────────────────────
// Replaces Dispatchers.Main with a test dispatcher.
// Without this, ViewModel init {} crashes in unit tests.
class MainDispatcherRule(
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : org.junit.rules.TestWatcher() {
    override fun starting(description: org.junit.runner.Description) {
        Dispatchers.setMain(testDispatcher)
    }
    override fun finished(description: org.junit.runner.Description) {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}

// ── CLIMATE VIEWMODEL TEST ─────────────────────────────────────────────────
class ClimateViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    // Mockk creates a test double for each use case
    private val observeClimate: ObserveClimateUseCase = mockk()
    private val setDriverTemp: SetDriverTemperatureUseCase = mockk()
    private val setPassengerTemp: SetPassengerTemperatureUseCase = mockk()
    private val setAcEnabled: SetAcEnabledUseCase = mockk()
    private val setFanSpeed: SetFanSpeedUseCase = mockk()

    private lateinit var viewModel: ClimateViewModel

    private val fakeClimateState = ClimateState(
        driverTempCelsius = 22.0f,
        passengerTempCelsius = 21.5f,
        isAcOn = true,
        isFanOn = true,
        fanSpeed = 3,
        isDefrostOn = false
    )

    @Before
    fun setUp() {
        // Stub observeClimate to return a flow of one item
        every { observeClimate() } returns flowOf(fakeClimateState)

        // Create the ViewModel with mocked use cases
        viewModel = ClimateViewModel(
            observeClimate,
            setDriverTemp,
            setPassengerTemp,
            setAcEnabled,
            setFanSpeed
        )
    }

    @Test
    fun `initial state is populated from observeClimate flow`() =
        mainDispatcherRule.testDispatcher.runBlockingTest {
            val state = viewModel.uiState.value
            assertEquals(22.0f, state.driverTemp)
            assertEquals(21.5f, state.passengerTemp)
            assertTrue(state.isAcOn)
            assertEquals(3, state.fanSpeed)
        }

    @Test
    fun `onDriverTempChange calls setDriverTemp use case`() =
        mainDispatcherRule.testDispatcher.runBlockingTest {
            // Arrange: stub the suspend use case
            coEvery { setDriverTemp(any()) } just Runs

            // Act
            viewModel.onDriverTempChange(24.0f)
            advanceUntilIdle()

            // Assert: verify the use case was called with the correct temp
            coVerify(exactly = 1) { setDriverTemp(24.0f) }
        }

    @Test
    fun `onAcToggle calls setAcEnabled with opposite of current state`() =
        mainDispatcherRule.testDispatcher.runBlockingTest {
            coEvery { setAcEnabled(any()) } just Runs

            // AC is currently ON (from fakeClimateState), so toggle should turn it OFF
            viewModel.onAcToggle()
            advanceUntilIdle()

            coVerify { setAcEnabled(false) }
        }

    @Test
    fun `onFanSpeedChange calls setFanSpeed with given level`() =
        mainDispatcherRule.testDispatcher.runBlockingTest {
            coEvery { setFanSpeed(any()) } just Runs

            viewModel.onFanSpeedChange(5)
            advanceUntilIdle()

            coVerify { setFanSpeed(5) }
        }
}

// ── EXAMPLE: USE CASE TEST ─────────────────────────────────────────────────
// Testing Use Cases in isolation — they hold business rules.
class SetDriverTemperatureUseCaseTest {

    private val repository: com.example.aaosdemo.domain.model.ClimateRepository = mockk()
    private val useCase = SetDriverTemperatureUseCase(repository)

    @Test
    fun `temperature below 16 is clamped to 16`() = runTest {
        coEvery { repository.setDriverTemperature(any()) } just Runs

        useCase(10f)  // Call with 10°C (below minimum)

        // Use case should clamp to 16°C before calling repository
        coVerify { repository.setDriverTemperature(16f) }
    }

    @Test
    fun `temperature above 30 is clamped to 30`() = runTest {
        coEvery { repository.setDriverTemperature(any()) } just Runs

        useCase(40f)  // Call with 40°C (above maximum)

        coVerify { repository.setDriverTemperature(30f) }
    }

    @Test
    fun `valid temperature is passed through unchanged`() = runTest {
        coEvery { repository.setDriverTemperature(any()) } just Runs

        useCase(22.5f)

        coVerify { repository.setDriverTemperature(22.5f) }
    }
}
