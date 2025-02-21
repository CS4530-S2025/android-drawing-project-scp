package com.example.drawingapp.viewmodel

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import androidx.lifecycle.ViewModel

class DrawViewModel : ViewModel() {

    // Stores the drawing bitmap (saved when the screen rotates)
    var drawingBitmap: Bitmap? = null

    // Stores brush settings
    var brushColor: Int = Color.BLACK
    var brushSize: Float = 10f

    // Set the brush settings
    fun setBrush(color: Int, size: Float) {
        brushColor = color
        brushSize = size
    }

    // Save the current canvas bitmap
    fun saveBitmap(bitmap: Bitmap) {
        drawingBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false)
    }

    // Retrieve the saved bitmap
    fun getSavedBitmap(): Bitmap? {
        return drawingBitmap
    }
}
