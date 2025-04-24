package com.example.drawingapp

object NativeImageFilters {
    init {
        System.loadLibrary("drawingapp")
    }

    external fun invertColors(bitmap: android.graphics.Bitmap)
    external fun addNoise(bitmap: android.graphics.Bitmap)
}