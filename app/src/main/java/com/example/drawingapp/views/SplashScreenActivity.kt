package com.example.drawingapp.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.drawingapp.databinding.ActivitySplashBinding

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Delay for 2 seconds then navigate to DrawActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, DrawActivity::class.java))
            finish()
        }, 2000)
    }
}