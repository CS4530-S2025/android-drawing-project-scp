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

    private var paint: Paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 10f
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private var path: Path = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)  // Draw the user's path
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)  // Start a new path
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)  // Draw line as finger moves
            }
           // MotionEvent.ACTION_UP -> {
                // Optional: Add logic for lifting the finger if needed
           // }
            else -> return false
        }

        invalidate() // Redraw the canvas
        return true
    }
}