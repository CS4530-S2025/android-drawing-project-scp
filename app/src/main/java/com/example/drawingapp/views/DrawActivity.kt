package com.example.drawingapp.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.drawingapp.databinding.ActivityDrawBinding
import com.example.drawingapp.viewmodel.DrawViewModel

class DrawActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrawBinding
    private val drawViewModel: DrawViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDrawBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Placeholder for drawing logic
    }
}