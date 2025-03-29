package com.example.drawingapp.data

import androidx.room.*

@Dao
interface DrawingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrawing(drawing: DrawingEntity)

    @Query("SELECT * FROM drawings ORDER BY timestamp DESC")
    suspend fun getAllDrawings(): List<DrawingEntity>

    @Delete
    suspend fun deleteDrawing(drawing: DrawingEntity)
}
