package com.example.drawingapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drawings")
data class DrawingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val filename: String,
    val lastEdited: Long
)
