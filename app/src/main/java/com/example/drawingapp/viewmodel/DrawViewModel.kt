package com.example.drawingapp.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawingapp.data.AppDatabase
import com.example.drawingapp.data.DrawingEntity
import com.example.drawingapp.model.FileHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DrawViewModel(application: Application) : AndroidViewModel(application) {

    // Stores the drawing bitmap (saved when the screen rotates)
    var drawingBitmap: Bitmap? = null

    // Stores brush settings
    var brushColor: Int = Color.BLACK
    var brushSize: Float = 10f

    private val fileHandler = FileHandler(application)
    private val drawingDao = AppDatabase.getDatabase(application).drawingDao()

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

    fun persistDrawing(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            val filename = fileHandler.saveDrawing(bitmap)
            val drawing = DrawingEntity(filename = filename, timestamp = System.currentTimeMillis())
            drawingDao.insertDrawing(drawing)
        }
    }
}
