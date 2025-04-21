package com.example.drawingapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Drawing(
    val filename: String,
    val name: String,
    val lastEdited: Long
)