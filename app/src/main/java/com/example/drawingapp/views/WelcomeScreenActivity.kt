package com.example.drawingapp.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.drawingapp.databinding.ActivityWelcomeScreenBinding
import com.example.drawingapp.views.DrawActivity

class WelcomeScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use View Binding to access UI elements
        binding = ActivityWelcomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Delay for 1.5 seconds before transitioning to the main draw screen
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Prevent user from returning to this screen
        }, 1500) // 1500ms = 1.5 seconds
    }
}
