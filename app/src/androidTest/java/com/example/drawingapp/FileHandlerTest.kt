package com.example.drawingapp

import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import com.example.drawingapp.model.FileHandler
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File

class FileHandlerTest {

    private lateinit var fileHandler: FileHandler
    private lateinit var context: Context
    private var savedFilename: String? = null

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        fileHandler = FileHandler(context)
    }

    @Test
    fun testSaveAndLoadDrawing() {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

        // Save and get filename
        val filename = fileHandler.saveDrawing(bitmap)
        savedFilename = filename // save for cleanup

        // Load it back
        val loadedBitmap = fileHandler.loadDrawing(filename)
        assertNotNull("Loaded bitmap should not be null", loadedBitmap)
        assertEquals("Loaded bitmap width should match", 100, loadedBitmap!!.width)
        assertEquals("Loaded bitmap height should match", 100, loadedBitmap.height)
    }

    @Test
    fun testDeleteDrawing() {
        val bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
        val filename = fileHandler.saveDrawing(bitmap)
        savedFilename = filename

        // Manually delete the file
        val file = File(context.filesDir, filename)
        assertTrue("File should exist before deletion", file.exists())

        file.delete()

        val result = fileHandler.loadDrawing(filename)
        assertNull("Bitmap should be null after deletion", result)
    }

    @After
    fun tearDown() {
        savedFilename?.let {
            val file = File(context.filesDir, it)
            if (file.exists()) {
                file.delete()
            }
        }
    }
}
