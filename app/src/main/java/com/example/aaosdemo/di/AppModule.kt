// ============================================================
// di/AppModule.kt
// ============================================================
// DEPENDENCY INJECTION — Hilt Module.
//
// This is where the "wiring" happens.
// Hilt reads these @Provides functions to know HOW to build
// each dependency when a class asks for it via @Inject.
//
// HOW HILT WORKS (summary):
//   1. @HiltAndroidApp on Application — bootstraps Hilt
//   2. @AndroidEntryPoint on Activity/Fragment — enables injection there
//   3. @HiltViewModel on ViewModel — Hilt creates it with injected params
//   4. @Inject constructor(...) on a class — Hilt can create it automatically
//   5. @Provides in a @Module — tells Hilt how to build an interface/3rd party
//
// WHY we need @Provides here:
//   - ClimateRepository is an interface. Hilt can't instantiate an interface.
//   - We tell Hilt: "when someone asks for ClimateRepository, give them ClimateRepositoryImpl"
//   - FakeVhalDataSource has @Inject constructor so Hilt builds it automatically.
// ============================================================

package com.example.aaosdemo.di

import com.example.aaosdemo.data.repository.*
import com.example.aaosdemo.domain.model.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// @Module tells Hilt this class contains binding instructions
// @InstallIn(SingletonComponent) = these bindings live for the app's lifetime
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    // @Binds is more efficient than @Provides for interface → implementation binding.
    // It tells Hilt: "when someone asks for VehicleRepository, inject VehicleRepositoryImpl"
    //
    // @Singleton ensures only ONE instance is created and shared everywhere.
    // This is important for StateFlow — we want ONE shared stream, not duplicates.

    @Binds
    @Singleton
    abstract fun bindVehicleRepository(
        impl: VehicleRepositoryImpl
    ): VehicleRepository

    @Binds
    @Singleton
    abstract fun bindClimateRepository(
        impl: ClimateRepositoryImpl
    ): ClimateRepository

    @Binds
    @Singleton
    abstract fun bindMediaRepository(
        impl: MediaRepositoryImpl
    ): MediaRepository

    @Binds
    @Singleton
    abstract fun bindNavigationRepository(
        impl: NavigationRepositoryImpl
    ): NavigationRepository
}

// ── HOW INJECTION FLOWS ───────────────────────────────────────────────────
//
//  FakeVhalDataSource (@Singleton, @Inject constructor)
//         ↓ injected into
//  VehicleRepositoryImpl (@Inject constructor)
//         ↓ bound as
//  VehicleRepository (interface)
//         ↓ injected into
//  ObserveVehicleSpeedUseCase (@Inject constructor)
//         ↓ injected into
//  DashboardViewModel (@HiltViewModel, @Inject constructor)
//         ↓ provided to
//  DashboardScreen (via hiltViewModel())
