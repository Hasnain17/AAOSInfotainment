package com.example.aaosdemo

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.aaosdemo.presentation.ui.AAOSApp
import com.example.aaosdemo.presentation.ui.theme.AAOSTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

// ============================================================
// AAOSApplication.kt
// ============================================================
// The Application class is the entry point for Hilt.
// @HiltAndroidApp generates the Hilt component hierarchy.
@HiltAndroidApp
class AAOSApplication : Application()

// ============================================================
// MainActivity.kt
// ============================================================
// The single Activity in this app (Single Activity Architecture).
// @AndroidEntryPoint enables Hilt injection for this Activity.
@AndroidEntryPoint  // Enables Hilt for this Activity (and its ViewModels)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Draws behind status/nav bars (modern Android)

        setContent {
            // Wrap everything in the theme
            AAOSTheme {
                AAOSApp()
            }
        }
    }
}
