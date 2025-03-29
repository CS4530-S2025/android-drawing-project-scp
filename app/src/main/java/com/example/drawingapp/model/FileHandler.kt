package com.example.drawingapp.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class FileHandler(private val context: Context) {

    private val fileName = "temp_drawing.png"

    //Save Bitmap to Internal Storage
    fun saveDrawing(bitmap: Bitmap, filename: String) {
        val file = File(context.filesDir, filename)
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream) // Save as PNG
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            outputStream?.close()
        }
    }

    //Load Bitmap from Internal Storage
    fun loadDrawing(filename: String): Bitmap? {
        val file = File(context.filesDir, filename)
        return if (file.exists()) {
            BitmapFactory.decodeStream(FileInputStream(file))
        } else null
    }

    //Delete Saved Drawing
    fun deleteDrawing(filename: String) {
        val file = File(context.filesDir, filename)
        if (file.exists()) {
            file.delete()
        }
    }
}