package com.example.drawingapp.views

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.drawingapp.R
import com.example.drawingapp.model.CustomCanvas
import com.example.drawingapp.model.FileHandler
import com.example.drawingapp.viewmodel.DrawViewModel
import kotlinx.coroutines.launch
import android.content.Intent
import com.example.drawingapp.model.DrawingEntity
import com.example.drawingapp.model.DrawingDatabase
import kotlinx.coroutines.Dispatchers
import androidx.core.content.FileProvider
import java.io.File

class DrawActivity : AppCompatActivity() {

    private lateinit var customCanvas: CustomCanvas
    private lateinit var fileHandler: FileHandler
    private lateinit var currentFilename: String

    // Get the ViewModel (this survives screen rotations)
    private val drawViewModel: DrawViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw)

        currentFilename = intent.getStringExtra("filename") ?: "drawing_${System.currentTimeMillis()}.png"


        currentFilename = intent.getStringExtra("filename") ?: "drawing_${System.currentTimeMillis()}.png"

        customCanvas = findViewById(R.id.drawCanvas)
        fileHandler = FileHandler(this)
        val drawingDao = DrawingDatabase.getDatabase(this).drawingDao()

      //  val filename = intent.getStringExtra("filename") ?: "drawing_${System.currentTimeMillis()}.png"
        val name = intent.getStringExtra("name") ?: "Untitled Drawing"

        val savedBitmap = fileHandler.loadDrawing(currentFilename)
        savedBitmap?.let {
            customCanvas.loadBitmap(it)
        }

        val sizeSeekBar = findViewById<SeekBar>(R.id.sizeSeekBar)
        val colorButton = findViewById<Button>(R.id.colorButton)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val loadButton = findViewById<Button>(R.id.loadButton)

        // Restore saved brush settings
        customCanvas.updateBrush(drawViewModel.brushColor, drawViewModel.brushSize)
        sizeSeekBar.progress = drawViewModel.brushSize.toInt()

        // Restore saved drawing if available
        //drawViewModel.getSavedBitmap()?.let { customCanvas.loadBitmap(it) }

        // Brush Size Handler
        sizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val newSize = progress.toFloat()
                customCanvas.updateBrush(customCanvas.getCurrentBrushColor(), newSize)
                drawViewModel.setBrush(customCanvas.getCurrentBrushColor(), newSize)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Color Picker Handler
        colorButton.setOnClickListener {
            val colors = arrayOf("Black", "Red", "Blue", "Green")
            val colorValues = arrayOf(android.graphics.Color.BLACK, android.graphics.Color.RED, android.graphics.Color.BLUE, android.graphics.Color.GREEN)

            android.app.AlertDialog.Builder(this)
                .setTitle("Pick a Color")
                .setItems(colors) { _, which ->
                    val newColor = colorValues[which]
                    customCanvas.updateBrush(newColor, sizeSeekBar.progress.toFloat())
                    drawViewModel.setBrush(newColor, sizeSeekBar.progress.toFloat())
                }
                .show()
        }

        //Save Button
        saveButton.setOnClickListener {
            val bitmap = customCanvas.getBitmap()

            fileHandler.saveDrawing(bitmap, currentFilename)

            val dao = DrawingDatabase.getDatabase(this).drawingDao()
            lifecycleScope.launch(Dispatchers.IO) {
                dao.insert(
                    DrawingEntity(
                        filename = currentFilename,
                        name = "Untitled", // Optional: add dialog later
                        lastEdited = System.currentTimeMillis()
                    )
                )
            }

            Toast.makeText(this, "Drawing saved!", Toast.LENGTH_SHORT).show()
        }



        //Load Button, go to drawing list
        loadButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Share button
        val shareButton = findViewById<Button>(R.id.shareButton)
        shareButton.setOnClickListener {
            shareDrawing(currentFilename)
        }
    }

    private fun shareDrawing(filename: String) {
        val file = File(filesDir, filename)

        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",  // Same as authorities in manifest
            file
        )

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(shareIntent, "Share Drawing"))
    }
}
