package com.example.drawingapp.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID //// guhhh

class FileHandler(private val context: Context) {

    private val fileName = "temp_drawing.png"

    // Save a bitmap with a unique filename and return the filename
    fun saveDrawing(bitmap: Bitmap): String {
        val filename = "drawing_${UUID.randomUUID()}.png"
        val file = File(context.filesDir, filename)
        try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return filename
    }

    // Load a specific drawing by filename
    fun loadDrawing(filename: String): Bitmap? {
        val file = File(context.filesDir, filename)
        return if (file.exists()) {
            BitmapFactory.decodeStream(FileInputStream(file))
        } else {
            null
        }
    }

    // Delete a specific drawing by filename
    fun deleteDrawing(filename: String) {
        val file = File(context.filesDir, filename)
        if (file.exists()) {
            file.delete()
        }
    }

}
