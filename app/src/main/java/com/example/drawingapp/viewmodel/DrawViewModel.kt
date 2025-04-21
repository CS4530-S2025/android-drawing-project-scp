package com.example.drawingapp.viewmodel

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.drawingapp.model.Drawing
import com.example.drawingapp.network.DrawingApiService
import kotlinx.coroutines.launch

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

    fun uploadCurrentDrawing(drawing: Drawing) {
        viewModelScope.launch {
            val success = DrawingApiService.uploadDrawing(drawing)
            if (success) {
                Log.d("Upload", "Drawing uploaded successfully.")
            } else {
                Log.e("Upload", "Failed to upload drawing.")
            }
        }
    }

}
