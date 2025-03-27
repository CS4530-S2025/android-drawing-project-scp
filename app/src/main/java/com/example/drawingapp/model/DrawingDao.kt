package com.example.drawingapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

/**
 * DAO for accessing drawing metadata in Room.
 */
@Dao
interface DrawingDao {
    @Query("SELECT * FROM drawings ORDER BY lastEdited DESC")
    suspend fun getAll(): List<DrawingEntity>

    @Insert
    suspend fun insert(drawing: DrawingEntity): Long

    @Delete
    suspend fun delete(drawing: DrawingEntity)
}
