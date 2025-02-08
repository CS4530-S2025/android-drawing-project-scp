package com.example.drawingapp.model

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CustomCanvas(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var paint: Paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 10f
        style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Placeholder for drawing logic
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Placeholder for touch handling
        return true
    }
}