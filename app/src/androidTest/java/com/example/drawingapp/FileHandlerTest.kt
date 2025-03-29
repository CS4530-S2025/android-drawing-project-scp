package com.example.drawingapp

import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import com.example.drawingapp.model.FileHandler
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FileHandlerTest {

    private lateinit var fileHandler: FileHandler
    private lateinit var context: Context

    private val testFilename = "unit_test_drawing.png" //Set a filename for test

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        fileHandler = FileHandler(context)
    }

    @Test
    fun testSaveAndLoadDrawing() {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

        //Save the drawing with filename
        fileHandler.saveDrawing(bitmap, testFilename)

        //Load the saved drawing
        val loadedBitmap = fileHandler.loadDrawing()
        assertNotNull("Loaded bitmap should not be null", loadedBitmap)
    }

    @Test
    fun testDeleteDrawing() {
        fileHandler.deleteDrawing()
        assertNull("File should be deleted", fileHandler.loadDrawing())
    }

    @After
    fun tearDown() {
        fileHandler.deleteDrawing() // Clean up
    }
}
