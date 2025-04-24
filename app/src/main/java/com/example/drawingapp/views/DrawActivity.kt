package com.example.drawingapp.views

import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.drawingapp.R
import com.example.drawingapp.model.CustomCanvas
import com.example.drawingapp.model.FileHandler
import com.example.drawingapp.viewmodel.DrawViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.drawingapp.NativeImageFilters
import com.example.drawingapp.model.DrawingDatabase
import com.example.drawingapp.model.DrawingEntity

class DrawActivity : AppCompatActivity() {

    private lateinit var customCanvas: CustomCanvas
    private lateinit var fileHandler: FileHandler
    private lateinit var currentFilename: String

    // 🎨 This is the clean source we always draw to
    private var baseCanvasBitmap: Bitmap? = null

    // Filter toggles
    private var isInvertOn = false
    private var isNoiseOn = false

    private val drawViewModel: DrawViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw)

        // Init canvas + filename
        currentFilename = intent.getStringExtra("filename") ?: "drawing_${System.currentTimeMillis()}.png"
        customCanvas = findViewById(R.id.drawCanvas)
        fileHandler = FileHandler(this)

        // Load any saved bitmap
        val savedBitmap = fileHandler.loadDrawing(currentFilename)
        savedBitmap?.let {
            customCanvas.loadBitmap(it)

            // Delay snapshot of clean version
            customCanvas.post {
                baseCanvasBitmap = customCanvas.getBitmap().copy(Bitmap.Config.ARGB_8888, true)
                applySelectedFilters()
                Log.d("FILTER_INIT", "Mirror snapshot created after bitmap load")
            }
        }

        // Init canvas snapshot if nothing was loaded
        customCanvas.post {
            if (baseCanvasBitmap == null) {
                updateCanvasMirror()
                applySelectedFilters()
            }
        }

        // Set up drawing brush
        val sizeSeekBar = findViewById<SeekBar>(R.id.sizeSeekBar)
        val colorButton = findViewById<Button>(R.id.colorButton)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val loadButton = findViewById<Button>(R.id.loadButton)

        customCanvas.updateBrush(drawViewModel.brushColor, drawViewModel.brushSize)
        sizeSeekBar.progress = drawViewModel.brushSize.toInt()

        sizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val newSize = progress.toFloat()
                customCanvas.updateBrush(customCanvas.getCurrentBrushColor(), newSize)
                drawViewModel.setBrush(customCanvas.getCurrentBrushColor(), newSize)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

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

        saveButton.setOnClickListener {
            val bitmap = customCanvas.getBitmap()
            fileHandler.saveDrawing(bitmap, currentFilename)

            val dao = DrawingDatabase.getDatabase(this).drawingDao()
            lifecycleScope.launch(Dispatchers.IO) {
                dao.insert(
                    DrawingEntity(
                        filename = currentFilename,
                        name = "Untitled",
                        lastEdited = System.currentTimeMillis()
                    )
                )
            }

            Toast.makeText(this, "Drawing saved!", Toast.LENGTH_SHORT).show()
        }

        loadButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 🎯 Attach listener to capture clean drawing updates
        customCanvas.setOnDrawListener(object : CustomCanvas.OnDrawListener {
            override fun onStrokeCompleted() {
                updateCanvasMirror()
                applySelectedFilters()
            }
        })



        // 🧪 Filter toggle buttons
        val invertToggle = findViewById<ToggleButton>(R.id.toggleInvert)
        val noiseToggle = findViewById<ToggleButton>(R.id.toggleNoise)

        invertToggle.setOnClickListener {
            isInvertOn = invertToggle.isChecked
            applySelectedFilters()
        }

        noiseToggle.setOnClickListener {
            isNoiseOn = noiseToggle.isChecked
            applySelectedFilters()
        }
    }

    // 🧠 This function NEVER updates baseCanvasBitmap
    // It always applies filters to a copy of the clean version
    private fun applySelectedFilters() {
        val source = baseCanvasBitmap ?: return
        val result = source.copy(Bitmap.Config.ARGB_8888, true)

        if (isInvertOn) NativeImageFilters.invertColors(result)
        if (isNoiseOn) NativeImageFilters.addNoise(result)

        Log.d("FILTERS", "Applied filters: Invert=$isInvertOn, Noise=$isNoiseOn")
        customCanvas.loadBitmap(result)
        Log.d("FILTERS", "baseCanvas size = ${source.width}x${source.height}")
    }

    private fun updateCanvasMirror() {
        baseCanvasBitmap = customCanvas.getBitmap().copy(Bitmap.Config.ARGB_8888, true)
        Log.d("CANVAS", "Updated baseCanvasBitmap snapshot")
    }

}
