package com.example.drawingapp.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.drawingapp.view.WelcomeScreenActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Use splash screen API for Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen()
        }

        super.onCreate(savedInstanceState)

        // Transition to the new Welcome Screen
        startActivity(Intent(this, WelcomeScreenActivity::class.java))
        finish() // Prevents user from going back to the splash screen
    }
}
