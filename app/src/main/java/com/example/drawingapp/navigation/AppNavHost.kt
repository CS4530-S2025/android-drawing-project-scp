package com.example.drawingapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.drawingapp.views.DrawingListScreen
import android.content.Intent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.drawingapp.views.DrawActivity

/**
 * This Composable sets up your app's navigation graph using NavHost.
 * It defines two "screens": a home list screen, and a drawing editor screen.
 */
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {

        // Home screen showing list of saved drawings
        composable("home") {
            DrawingListScreen(navController = navController)
        }

        // Drawing editor screen — accepts filename as argument
        composable("draw/{filename}") { backStackEntry ->
            val context = LocalContext.current
            val filename = backStackEntry.arguments?.getString("filename") ?: return@composable

            LaunchedEffect(Unit) {
                val intent = Intent(context, DrawActivity::class.java).apply {
                    putExtra("filename", filename)
                    putExtra("name", "Default name")
                }
                context.startActivity(intent)
            }
        }
    }
}
