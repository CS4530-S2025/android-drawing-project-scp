package com.example.drawingapp.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.drawingapp.navigation.AppNavHost

/**
 * MainActivity now launches your Jetpack Compose app using a NavController.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set Compose content with your navigation graph
        setContent {
            val navController = rememberNavController()
            AppNavHost(navController)
        }
    }
}
