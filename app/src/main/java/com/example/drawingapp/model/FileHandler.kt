package com.example.drawingapp.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class FileHandler(private val context: Context) {

    //Save Bitmap to internal storage
    fun saveDrawing(bitmap: Bitmap, filename: String) {
        val file = File(context.filesDir, filename)
        try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //Load Bitmap from internal storage using a filename
    fun loadDrawing(filename: String): Bitmap? {
        val file = File(context.filesDir, filename)
        return if (file.exists()) {
            try {
                BitmapFactory.decodeFile(file.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else null
    }

    //Delete saved drawing file by filename
    fun deleteDrawing(filename: String) {
        val file = File(context.filesDir, filename)
        if (file.exists()) {
            file.delete()
        }
    }
}
