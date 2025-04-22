package com.example.drawingapp.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drawingapp.viewmodel.SharedDrawingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedDrawingsScreen(viewModel: SharedDrawingsViewModel = viewModel()) {
    val drawings by viewModel.sharedDrawings.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSharedDrawings()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Shared Drawings") })
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
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Name: ${drawing.name}")
                        Text("File: ${drawing.filename}")
                        Text("Last Edited: ${drawing.lastEdited}")
                    }
                }
            }
        }
    }
}
