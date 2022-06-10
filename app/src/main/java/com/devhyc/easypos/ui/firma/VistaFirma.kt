package com.devhyc.easypos.ui.firma

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View

public class VistaFirma(context: Context?) : View(context){
    lateinit var x:Number
    lateinit var y:Number
    private var accion:String="accion"
    var path = Path()
    private var erase:Boolean = false
    var paint = Paint()

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10F
        paint.color = Color.BLUE

        if (erase)
        {
            //Si se borra
            path.reset()
            erase =false
        }
        else
        {
            //Si se dibuja
            if (accion=="down")
            {
                path.moveTo(x as Float, y as Float)
            }
            if (accion=="move")
            {
                path.lineTo(x as Float, y as Float)
            }
        }
        canvas.drawPath(path, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            x = event.x
        }
        if (event != null) {
            y = event.y
        }
        if (event != null) {
            if (event.action == MotionEvent.ACTION_DOWN) {
                accion = "down"
            }
        }
        if (event != null) {
            if (event.action == MotionEvent.ACTION_MOVE) {
                accion="move"
            }
        }
        invalidate()
        return true
    }

    fun SetErase(isErase:Boolean)
    {
        erase =isErase
    }
}