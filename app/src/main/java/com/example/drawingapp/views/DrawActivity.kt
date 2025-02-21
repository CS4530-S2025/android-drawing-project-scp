package com.example.drawingapp.view

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.drawingapp.R
import com.example.drawingapp.model.CustomCanvas
import com.example.drawingapp.model.FileHandler
import com.example.drawingapp.viewmodel.DrawViewModel

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

        val sizeSeekBar = findViewById<SeekBar>(R.id.sizeSeekBar)
        val colorButton = findViewById<Button>(R.id.colorButton)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val loadButton = findViewById<Button>(R.id.loadButton)

        // Restore saved brush settings
        customCanvas.updateBrush(drawViewModel.brushColor, drawViewModel.brushSize)
        sizeSeekBar.progress = drawViewModel.brushSize.toInt()

        // Restore saved drawing if available
        drawViewModel.getSavedBitmap()?.let { customCanvas.loadBitmap(it) }

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
            fileHandler.saveDrawing(bitmap)
            drawViewModel.saveBitmap(bitmap) // Save to ViewModel
        }

        // Load Button
        loadButton.setOnClickListener {
            fileHandler.loadDrawing()?.let {
                customCanvas.loadBitmap(it)
                drawViewModel.saveBitmap(it) // Save to ViewModel
            }
        }
    }
}
