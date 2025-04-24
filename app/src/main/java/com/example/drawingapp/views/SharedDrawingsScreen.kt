package com.example.drawingapp.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.drawingapp.viewmodel.SharedDrawingsViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.drawingapp.network.DrawingApiService.BASE_URL


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedDrawingsScreen(navController: NavController, viewModel: SharedDrawingsViewModel = viewModel()) {
    val drawings by viewModel.sharedDrawings.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSharedDrawings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shared Drawings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )

        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            items(drawings) { drawing ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Image(
                            painter = rememberAsyncImagePainter("$BASE_URL/drawingImage/${drawing.filename}"),
                            contentDescription = "Drawing Preview",
                            modifier = Modifier
                                .size(64.dp)
                                .padding(end = 12.dp)
                        )

                        Column {
                            Text("Name: ${drawing.name}")
                            Text("File: ${drawing.filename}")
                            Text("Last Edited: ${drawing.lastEdited}")
                        }
                    }
                }
            }
        }
    }
}
