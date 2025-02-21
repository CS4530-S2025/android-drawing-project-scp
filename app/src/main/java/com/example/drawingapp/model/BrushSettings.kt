package com.example.drawingapp.model

import android.graphics.Color

class BrushSettings {
    var brushColor: Int = Color.BLACK  // Default color
    var brushSize: Float = 10f         // Default size

    fun setColor(color: Int) {
        brushColor = color
    }

    fun setSize(size: Float) {
        brushSize = size.coerceAtLeast(1f) // Prevent zero/negative size
    }
}
