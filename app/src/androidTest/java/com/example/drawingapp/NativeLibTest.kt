package com.example.drawingapp

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NativeLibTest {

    @Test
    fun testInvertColors_changesColor() {
        val bmp = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        bmp.eraseColor(Color.RED)

        NativeImageFilters.invertColors(bmp)
        val resultColor = bmp.getPixel(0, 0)

        // Should not be RED after inversion
        assertNotEquals("Color should have been inverted from RED", Color.RED, resultColor)
    }

    @Test
    fun testAddNoise_appliesNoise() {
        val bmp = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        bmp.eraseColor(Color.BLUE)

        NativeImageFilters.addNoise(bmp)
        val resultColor = bmp.getPixel(5, 5)

        // Likely different from pure blue
        assertNotEquals("Pixel color should be changed by noise", Color.BLUE, resultColor)
    }
}
