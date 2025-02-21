package com.example.drawingapp.model

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CustomCanvas(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private data class Stroke(val path: Path, val paint: Paint)

    private val strokes = mutableListOf<Stroke>()
    private var currentPath = Path()
    private var currentPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 10f
        color = Color.BLACK
    }

    private lateinit var bitmap: Bitmap
    private lateinit var canvasBitmap: Canvas

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        canvasBitmap = Canvas(bitmap)
        canvasBitmap.drawColor(Color.WHITE)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawBitmap(bitmap, 0f, 0f, null)
        for (stroke in strokes) {
            canvas.drawPath(stroke.path, stroke.paint)
        }

        canvas.drawPath(currentPath, currentPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath = Path()
                currentPath.moveTo(x, y)
                val newPaint = Paint(currentPaint)
                strokes.add(Stroke(currentPath, newPaint))
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath.lineTo(x, y)
                canvasBitmap.drawPath(currentPath, currentPaint)
            }
            MotionEvent.ACTION_UP -> {
                currentPath.reset()
            }
        }

        invalidate()
        return true
    }

    fun getBitmap(): Bitmap {
        return bitmap.copy(Bitmap.Config.ARGB_8888, false)
    }

    fun loadBitmap(savedBitmap: Bitmap) {
        bitmap = savedBitmap.copy(Bitmap.Config.ARGB_8888, true)
        canvasBitmap.setBitmap(bitmap)
        invalidate()
    }

    fun updateBrush(color: Int, size: Float) {
        currentPaint.color = color
        currentPaint.strokeWidth = size
    }

    fun getCurrentBrushColor(): Int {
        return currentPaint.color
    }
}
