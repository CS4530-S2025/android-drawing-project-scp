package com.example.drawingapp.views

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import com.example.drawingapp.data.AppDatabase
import com.example.drawingapp.data.DrawingEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun DrawingListScreen(
    onDrawingSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).drawingDao() }
    var drawings by remember { mutableStateOf<List<DrawingEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            drawings = dao.getAllDrawings()
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(drawings) { drawing ->
            DrawingListItem(
                filename = drawing.filename,
                onClick = { onDrawingSelected(drawing.filename) }
            )
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

@Composable
fun DrawingListItem(
    filename: String,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val file = File(context.filesDir, filename)
    val bitmap = remember(file) {
        if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = filename,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.alignByBaseline()
        )
    }
}
