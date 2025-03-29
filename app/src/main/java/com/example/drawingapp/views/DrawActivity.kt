package com.example.drawingapp.views

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.drawingapp.R
import com.example.drawingapp.data.AppDatabase
import com.example.drawingapp.data.DrawingEntity
import com.example.drawingapp.model.CustomCanvas
import com.example.drawingapp.model.FileHandler
import com.example.drawingapp.viewmodel.DrawViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import android.content.Intent
import com.example.drawingapp.model.DrawingEntity
import com.example.drawingapp.model.DrawingDatabase
import com.example.drawingapp.views.MainActivity


class DrawActivity : AppCompatActivity() {

    private lateinit var customCanvas: CustomCanvas
    private lateinit var drawViewModel: DrawViewModel
    private lateinit var fileHandler: FileHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw)

        lifecycleScope.launch(Dispatchers.IO) {
            val dao = AppDatabase.getDatabase(this@DrawActivity).drawingDao()
            val drawings = dao.getAllDrawings()

            // Pick the latest (by timestamp) if any
            val latest = drawings.maxByOrNull { it.timestamp }
            latest?.let { drawing ->
                val bitmap = fileHandler.loadDrawing(drawing.filename)
                if (bitmap != null) {
                    withContext(Dispatchers.Main) {
                        customCanvas.loadBitmap(bitmap)
                        Log.d("DrawActivity", "Loaded last saved drawing: ${drawing.filename}")
                    }
                }
            }
        }

        drawViewModel = ViewModelProvider(this)[DrawViewModel::class.java]
        fileHandler = FileHandler(this)
        customCanvas = findViewById(R.id.drawCanvas)
        val drawingDao = DrawingDatabase.getDatabase(this).drawingDao()

        val filename = intent.getStringExtra("filename") ?: "drawing_${System.currentTimeMillis()}.png"
        val name = intent.getStringExtra("name") ?: "Untitled Drawing"

        val sizeSeekBar = findViewById<SeekBar>(R.id.sizeSeekBar)
        val colorButton = findViewById<Button>(R.id.colorButton)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val loadButton = findViewById<Button>(R.id.loadButton)

        // Restore brush settings
        customCanvas.updateBrush(drawViewModel.brushColor, drawViewModel.brushSize)
        sizeSeekBar.progress = drawViewModel.brushSize.toInt()

        // Restore drawing if rotated
        drawViewModel.getSavedBitmap()?.let {
            customCanvas.loadBitmap(it)
        }

        // Load from file if opened via DrawingListFragment
        val filename = intent.getStringExtra("drawing_filename")
        if (filename != null) {
            val bitmap = fileHandler.loadDrawing(filename)
            if (bitmap != null) {
                customCanvas.loadBitmap(bitmap)
            }
        }

        // Brush size adjustment
        sizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val newSize = progress.toFloat()
                customCanvas.updateBrush(customCanvas.getCurrentBrushColor(), newSize)
                drawViewModel.setBrush(customCanvas.getCurrentBrushColor(), newSize)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Color picker
        colorButton.setOnClickListener {
            val colors = arrayOf("Black", "Red", "Blue", "Green")
            val colorValues = arrayOf(
                android.graphics.Color.BLACK,
                android.graphics.Color.RED,
                android.graphics.Color.BLUE,
                android.graphics.Color.GREEN
            )

            AlertDialog.Builder(this)
                .setTitle("Pick a Color")
                .setItems(colors) { _, which ->
                    val newColor = colorValues[which]
                    customCanvas.updateBrush(newColor, sizeSeekBar.progress.toFloat())
                    drawViewModel.setBrush(newColor, sizeSeekBar.progress.toFloat())
                }
                .show()
        }

        // Save drawing
        saveButton.setOnClickListener {
            Log.d("DrawActivity", "Save button clicked")

            val bitmap = customCanvas.getBitmap()
            val filename = fileHandler.saveDrawing(bitmap)

            // Save metadata to DB
            val dao = AppDatabase.getDatabase(this).drawingDao()
            lifecycleScope.launch(Dispatchers.IO) {
                dao.insertDrawing(DrawingEntity(filename = filename))
            }

            Log.d("DrawActivity", "Saved to file and DB: $filename")
        }

        // Load from rotation cache
        loadButton.setOnClickListener {
            Log.d("DrawActivity", "Load button clicked")

            val savedBitmap = drawViewModel.getSavedBitmap()
            if (savedBitmap != null) {
                customCanvas.loadBitmap(savedBitmap)
                Log.d("DrawActivity", "Loaded bitmap from ViewModel cache")
            } else {
                Log.d("DrawActivity", "No saved bitmap found")
            }
        }
    }
}
