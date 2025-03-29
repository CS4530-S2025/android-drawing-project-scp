package com.example.drawingapp.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import coil.compose.rememberAsyncImagePainter
import com.example.drawingapp.data.AppDatabase
import com.example.drawingapp.data.DrawingEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import androidx.navigation.fragment.findNavController
import coil.compose.rememberAsyncImagePainter


class DrawingListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                DrawingListScreen(
                    onDrawingSelected = { filename ->
                        val action = DrawingListFragmentDirections.actionDrawingListToDraw(filename)
                        findNavController().navigate(action)
                    }
                )
            }
        }
    }

    @Composable
    fun DrawingListScreen() {
        val context = requireContext()
        var drawings by remember { mutableStateOf(listOf<DrawingEntity>()) }

        // Load data in a Composable-safe coroutine
        LaunchedEffect(Unit) {
            val dao = AppDatabase.getDatabase(context).drawingDao()
            drawings = withContext(Dispatchers.IO) {
                dao.getAllDrawings()
            }
        }

        MaterialTheme {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {

                Button(
                    onClick = {
                        startActivity(Intent(context, DrawActivity::class.java))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("New Drawing")
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(drawings) { drawing ->
                        DrawingListItem(drawing)
                    }
                }
            }
        }
    }

    @Composable
    fun DrawingListItem(drawing: DrawingEntity) {
        val file = File(requireContext().filesDir, drawing.filename)
        if (file.exists()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        val intent = Intent(requireContext(), DrawActivity::class.java)
                        intent.putExtra("drawing_filename", drawing.filename)
                        startActivity(intent)
                    }
            ) {
                Image(
                    painter = rememberAsyncImagePainter(file),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Text("Saved at: ${drawing.timestamp}")
            }
        }
    }
}
