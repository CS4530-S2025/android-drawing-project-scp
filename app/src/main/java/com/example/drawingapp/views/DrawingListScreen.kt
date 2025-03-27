package com.example.drawingapp.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.drawingapp.viewmodel.DrawingListViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.example.drawingapp.model.DrawingEntity
import androidx.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)

/**
 * Displays a list of saved drawings with a FAB to create a new one.
 */
@Composable
fun DrawingListScreen(
    navController: NavController,
    viewModel: DrawingListViewModel = viewModel()
) {
    val drawings by viewModel.drawings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Drawings") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Create a new drawing with unique filename
                val filename = "drawing_${System.currentTimeMillis()}.png"
                navController.navigate("draw/$filename")
            }) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(drawings) { drawing ->
                DrawingListItem(
                    name = drawing.name,
                    timestamp = drawing.lastEdited,
                    onClick = {
                        navController.navigate("draw/${drawing.filename}")
                    }
                )
            }
        }
    }
}

/**
 * A single row showing the drawing name and timestamp.
 */
@Composable
fun DrawingListItem(name: String, timestamp: Long, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US).format(Date(timestamp)),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SafeDrawingListItemPreview() {
    DrawingListItem(
        name = "Sketch of Dog",
        timestamp = 1700000000000,
        onClick = {}
    )
}
