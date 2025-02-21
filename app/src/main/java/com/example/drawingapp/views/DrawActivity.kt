package com.example.drawingapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.app.AlertDialog
import android.graphics.Color
import android.widget.Button
import android.widget.SeekBar
import com.example.drawingapp.R
import com.example.drawingapp.model.CustomCanvas

class DrawActivity : AppCompatActivity() {

    private lateinit var customCanvas: CustomCanvas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw)

        customCanvas = findViewById(R.id.drawCanvas)
        val sizeSeekBar = findViewById<SeekBar>(R.id.sizeSeekBar)
        val colorButton = findViewById<Button>(R.id.colorButton)

        // Brush Size Handler
        sizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                customCanvas.updateBrush(customCanvas.getCurrentBrushColor(), progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Color Picker Handler
        colorButton.setOnClickListener {
            val colors = arrayOf("Black", "Red", "Blue", "Green")
            val colorValues = arrayOf(Color.BLACK, Color.RED, Color.BLUE, Color.GREEN)

            AlertDialog.Builder(this)
                .setTitle("Pick a Color")
                .setItems(colors) { _, which ->
                    customCanvas.updateBrush(colorValues[which], sizeSeekBar.progress.toFloat())
                }
                .show()
        }
    }
}