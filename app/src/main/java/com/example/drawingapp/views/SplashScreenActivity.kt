package com.example.drawingapp.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Use official splash screen API
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Keep splash screen visible for a fixed time (e.g., 2 seconds)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, DrawActivity::class.java))
            finish() // Prevents returning to splash screen
        }, 3000) // 2000ms = 2 seconds
    }
}
