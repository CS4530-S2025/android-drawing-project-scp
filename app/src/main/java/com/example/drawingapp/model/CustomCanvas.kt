package com.example.drawingapp.model

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CustomCanvas(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    // Data class to hold Path and its Paint
    private data class Stroke(val path: Path, val paint: Paint)

    // List to store all strokes
    private val strokes = mutableListOf<Stroke>()
    private var currentPath = Path()
    private var currentPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 10f  // Default size
    }

    private var path: Path = Path()
    private var brushSettings = BrushSettings()

    // Update brush settings for new strokes only
    fun updateBrush(color: Int, size: Float) {
        currentPaint.color = color
        currentPaint.strokeWidth = size
    }

    fun getCurrentBrushColor(): Int {
        return currentPaint.color
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw all stored strokes
        for (stroke in strokes) {
            canvas.drawPath(stroke.path, stroke.paint)
        }

        // Draw the current active path
        canvas.drawPath(currentPath, currentPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath = Path()
                currentPath.moveTo(x, y)
                // Create a new Paint object for this stroke
                val newPaint = Paint(currentPaint)
                strokes.add(Stroke(currentPath, newPaint))
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath.lineTo(x, y)
            }
            MotionEvent.ACTION_UP -> {
            }
        }

        invalidate() // Redraw the canvas
        return true
    }
}