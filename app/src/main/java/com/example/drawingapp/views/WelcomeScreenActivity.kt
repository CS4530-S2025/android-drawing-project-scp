package com.example.drawingapp.views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.drawingapp.databinding.ActivityWelcomeScreenBinding
import com.example.drawingapp.network.DrawingApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WelcomeScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val scope = CoroutineScope(Dispatchers.Main)

        binding.signupButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (username.isBlank() || password.isBlank()) {
                toast("Please enter both username and password")
                return@setOnClickListener
            }

            scope.launch {
                val success = DrawingApiService.signup(username, password)
                if (success) {
                    toast("Signup successful!")
                    navigateToMain()
                } else {
                    toast("Signup failed.")
                }
            }
        }

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (username.isBlank() || password.isBlank()) {
                toast("Please enter both username and password")
                return@setOnClickListener
            }

            scope.launch {
                val token = DrawingApiService.login(username, password)
                if (token != null) {
                    toast("Login successful!")
                    navigateToMain()
                } else {
                    toast("Login failed.")
                }
            }
        }

        binding.skipButton.setOnClickListener {
            toast("Skipping login...")
            navigateToMain()
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
