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
import androidx.navigation.compose.rememberNavController


/**
 * This Composable sets up your app's navigation graph using NavHost.
 * It defines two "screens": a home list screen, and a drawing editor screen.
 */
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {

        // Home screen showing list of saved drawings
        composable("home") {
            DrawingListScreen(onDrawingSelected = { filename ->
                navController.navigate("draw/$filename")
            })
        }

        // Drawing editor screen — accepts filename as argument
        composable("draw/{filename}") { backStackEntry ->
            val context = LocalContext.current
            val filename = backStackEntry.arguments?.getString("filename")

            // ✅ Guard against nulls!
            if (filename != null) {
                context.startActivity(
                    Intent(context, DrawActivity::class.java).apply {
                        putExtra("filename", filename)
                        putExtra("name", "My Sketch")
                    }
                )
            }
        }
    }
}
