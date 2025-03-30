package com.example.drawingapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.drawingapp.model.DrawingDao
import com.example.drawingapp.model.DrawingDatabase
import com.example.drawingapp.model.DrawingEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawingDaoTest {

    private lateinit var db: DrawingDatabase
    private lateinit var dao: DrawingDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            DrawingDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.drawingDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndGetDrawing() = runBlocking {
        val drawing = DrawingEntity(
            name = "Test Drawing",
            filename = "test_drawing.png",
            lastEdited = System.currentTimeMillis()
        )

        dao.insert(drawing)

        val allDrawings = dao.getAll()
        assertEquals(1, allDrawings.size)
        assertEquals("test_drawing.png", allDrawings[0].filename)
        assertEquals("Test Drawing", allDrawings[0].name)
    }
}
