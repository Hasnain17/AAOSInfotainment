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
