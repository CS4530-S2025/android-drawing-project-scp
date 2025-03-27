package com.example.drawingapp.viewmodel

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import androidx.lifecycle.ViewModel

class DrawViewModel : ViewModel() {

    // Stores the current state of drawing bitmap (saved when the screen rotates)
    var drawingBitmap: Bitmap? = null

    // Stores brush settings
    var brushColor: Int = Color.BLACK
    var brushSize: Float = 10f

    // Set the brush settings
    fun setBrush(color: Int, size: Float) {
        brushColor = color
        brushSize = size
    }

}
