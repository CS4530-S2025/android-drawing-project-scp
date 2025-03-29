package com.example.drawingapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drawings")
data class DrawingEntity(
    @PrimaryKey val filename: String,
    val timestamp: Long = System.currentTimeMillis()
)
