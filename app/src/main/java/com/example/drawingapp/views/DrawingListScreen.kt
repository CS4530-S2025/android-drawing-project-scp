package com.example.drawingapp.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.drawingapp.viewmodel.DrawingListViewModel
import com.example.drawingapp.viewmodel.DrawViewModel
import com.example.drawingapp.model.DrawingEntity
import com.example.drawingapp.model.Drawing
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingListScreen(
    navController: NavController,
    viewModel: DrawingListViewModel = viewModel(),
    drawViewModel: DrawViewModel = viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    var drawingName by remember { mutableStateOf("") }
    var selectedDrawing by remember { mutableStateOf<DrawingEntity?>(null) }

    LaunchedEffect(Unit) {
        viewModel.refreshDrawings()
    }

    val drawings by viewModel.drawings.collectAsState()

    if (showDialog && selectedDrawing != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Upload Drawing") },
            text = {
                Column {
                    Text("Enter a name for the drawing:")
                    OutlinedTextField(
                        value = drawingName,
                        onValueChange = { drawingName = it },
                        placeholder = { Text("My cool sketch") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedDrawing?.let { entity ->
                        val drawing = Drawing(
                            filename = entity.filename,
                            name = drawingName,
                            lastEdited = System.currentTimeMillis()
                        )
                        drawViewModel.uploadCurrentDrawing(drawing)
                    }
                    showDialog = false
                    drawingName = ""
                }) {
                    Text("Upload")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    drawingName = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Drawings") },
                actions = {
                    IconButton(onClick = { navController.navigate("shared") }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Go to shared drawings"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                //create a new drawing with unique filename
                val filename = "drawing_${System.currentTimeMillis()}.png"
                navController.navigate("draw/$filename")
            }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(drawings) { drawing ->
                    DrawingListItem(
                        name = drawing.name,
                        timestamp = drawing.lastEdited,
                        onClick = {
                            navController.navigate("draw/${drawing.filename}")
                        },
                        onUploadClick = {
                            selectedDrawing = drawing
                            drawingName = drawing.name
                            showDialog = true
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tap upload next to a drawing to sync it online",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun DrawingListItem(
    name: String,
    timestamp: Long,
    onClick: () -> Unit,
    onUploadClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onClick() }
        ) {
            Text(text = name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US).format(Date(timestamp)),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = { onUploadClick() },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text("Upload")
        }
    }
}
