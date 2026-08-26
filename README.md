# AAOS Infotainment Demo App
## Android Automotive OS · Kotlin · Jetpack Compose · Dagger Hilt · Kotlin Flow

This project simulates an Android Automotive OS infotainment system.
It is designed as a **learning codebase** — read the files in the order below.

---

## Reading Order (Start Here)

```
STEP 1 — Project Setup
  build.gradle.kts (root)
  app/build.gradle.kts

STEP 2 — Data Layer (bottom of stack)
  domain/model/         ← Pure Kotlin data classes (no Android deps)
  data/datasource/      ← Fake VHAL / Car Service simulators
  data/repository/      ← Repository implementations

STEP 3 — Domain Layer (business logic)
  domain/usecase/       ← Use cases that wrap repositories

STEP 4 — DI (wiring it all together)
  di/AppModule.kt       ← Hilt modules

STEP 5 — Presentation Layer (UI)
  presentation/viewmodel/   ← ViewModels with StateFlow
  presentation/ui/theme/    ← Compose theme
  presentation/ui/screens/  ← Compose screens
  MainActivity.kt
  AAOSApplication.kt
```

---

## Architecture: Clean Architecture + MVVM

```
┌─────────────────────────────────────────────┐
│  PRESENTATION LAYER                          │
│  Composables  ←  ViewModel  ←  UseCase       │
└──────────────────────┬──────────────────────┘
                       │ interface only
┌──────────────────────▼──────────────────────┐
│  DOMAIN LAYER                                │
│  Models  +  Repository Interfaces            │
└──────────────────────┬──────────────────────┘
                       │ implements
┌──────────────────────▼──────────────────────┐
│  DATA LAYER                                  │
│  Repository Impl  ←  DataSource (VHAL/Fake)  │
└─────────────────────────────────────────────┘
```

## Key Concepts Demonstrated
- `StateFlow` for reactive UI state
- `callbackFlow` for wrapping callback-style Car Service APIs
- `Hilt` for injecting Car Managers / Repositories
- `Clean Architecture` — use cases isolate ViewModel from data sources
- `Jetpack Compose` — declarative IVI screens
- `Fake DataSource` — simulates VHAL data so you can run on any device
