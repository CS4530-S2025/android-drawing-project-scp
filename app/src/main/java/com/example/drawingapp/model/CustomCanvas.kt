package com.example.drawingapp.model

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CustomCanvas @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

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

    private lateinit var canvasBitmap: Bitmap    //Holds the image pixels
    private lateinit var drawCanvas: Canvas      //Used to draw on the bitmap
    private var pendingBitmapToLoad: Bitmap? = null  //Bitmap to be drawn after size is set

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap)
        drawCanvas.drawColor(Color.WHITE)

        //Handle delayed bitmap load
        pendingBitmapToLoad?.let {
            drawCanvas.drawBitmap(it, 0f, 0f, null)
            pendingBitmapToLoad = null
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawBitmap(canvasBitmap, 0f, 0f, null)
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
                drawCanvas.drawPath(currentPath, currentPaint)
            }
            MotionEvent.ACTION_UP -> {
                currentPath.reset()
            }
        }

        invalidate()
        return true
    }

    fun getBitmap(): Bitmap {
        return canvasBitmap.copy(Bitmap.Config.ARGB_8888, false)
    }

    fun loadBitmap(bitmap: Bitmap) {
        if (::canvasBitmap.isInitialized) {
            drawCanvas.drawBitmap(bitmap, 0f, 0f, null)
            invalidate()
        } else {
            pendingBitmapToLoad = bitmap
        }
    }

    fun updateBrush(color: Int, size: Float) {
        currentPaint.color = color
        currentPaint.strokeWidth = size
    }

    fun getCurrentBrushColor(): Int {
        return currentPaint.color
    }
}
