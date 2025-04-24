package com.example.yourpackage

object NativeImageFilters {
    init {
        System.loadLibrary("imagefilters")
    }

    external fun invertColors(bitmap: android.graphics.Bitmap)
    external fun addNoise(bitmap: android.graphics.Bitmap)
}