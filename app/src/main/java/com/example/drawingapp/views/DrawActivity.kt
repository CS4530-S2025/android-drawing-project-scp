package com.example.drawingapp.views

import android.graphics.Bitmap
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
import com.example.drawingapp.views.MainActivity


class DrawActivity : AppCompatActivity() {

    private lateinit var customCanvas: CustomCanvas
    private lateinit var fileHandler: FileHandler

    // Get the ViewModel (this survives screen rotations)
    private val drawViewModel: DrawViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw)

        customCanvas = findViewById(R.id.drawCanvas)
        fileHandler = FileHandler(this)
        val drawingDao = DrawingDatabase.getDatabase(this).drawingDao()

        val filename = intent.getStringExtra("filename") ?: "drawing_$(System.currentTimeMillis()}.png"
        val name = intent.getStringExtra("name") ?: "Untitled Drawing"

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
                drawViewModel.setBrush(customCanvas.getCurrentBrushColor(), newSize) // ✅ Save to ViewModel
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
                    drawViewModel.setBrush(newColor, sizeSeekBar.progress.toFloat()) // ✅ Save to ViewModel
                }
                .show()
        }

        // Save Button
        saveButton.setOnClickListener {
            val bitmap: Bitmap = customCanvas.getBitmap()
            //upload to local storage
            fileHandler.saveDrawing(bitmap, filename)

            //uploads to database
            lifecycleScope.launch{
                val entity = DrawingEntity(
                    name = name,
                    filename = filename,
                    lastEdited = System.currentTimeMillis()
                )
                drawingDao.insert(entity)
            }
            //see if working
            Toast.makeText(this, "Drawing saved successfully", Toast.LENGTH_SHORT).show()
        }

        // Load Button, go to drawing list
        loadButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
