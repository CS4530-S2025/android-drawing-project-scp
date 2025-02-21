package com.example.drawingapp

import android.graphics.Color
import com.example.drawingapp.model.BrushSettings
import org.junit.Assert.assertEquals
import org.junit.Test

class BrushSettingsTest {

    @Test
    fun testBrushColorChange() {
        val brushSettings = BrushSettings()
        brushSettings.setColor(Color.RED)
        assertEquals("Brush color should be RED", Color.RED, brushSettings.brushColor)

        brushSettings.setColor(Color.BLUE)
        assertEquals("Brush color should be BLUE", Color.BLUE, brushSettings.brushColor)
    }

    @Test
    fun testBrushSizeUpdate() {
        val brushSettings = BrushSettings()
        brushSettings.setSize(20f)
        assertEquals("Brush size should be 20", 20f, brushSettings.brushSize)

        brushSettings.setSize(5f)
        assertEquals("Brush size should be 5", 5f, brushSettings.brushSize)
    }

    @Test
    fun testInvalidBrushSize() {
        val brushSettings = BrushSettings()
        brushSettings.setSize(-10f)  // Invalid size
        assertEquals("Brush size should default to minimum 1", 1f, brushSettings.brushSize)

        brushSettings.setSize(0f)    // Zero size
        assertEquals("Brush size should default to minimum 1", 1f, brushSettings.brushSize)
    }
}
