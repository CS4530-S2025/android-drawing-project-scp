package com.example.drawingapp.views
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class CustomDrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    //Create a bitmap and canvas to draw on
    //For simplicity(better practice...) doing this in the view
    private var bitmap: Bitmap? = null
    private lateinit var canvasBitmap: Canvas

    //Paint object to draw in black
    private val paint = Paint().apply {
        color = Color.BLACK
       // style = Paint.Style.STROKE
    }

    //Called when size of the view changes
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        //Create a new bitmap of size of view
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        canvasBitmap = Canvas(bitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //Draw the bitmap (contains our drawing) onto view's canvas
        bitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            Log.d("CustomDrawingView", "Touch detected at: x = ${event.x}, y = ${event.y}")
            canvasBitmap.drawPoint(event.x, event.y, paint)
            invalidate()
        }
        return true
    }
}