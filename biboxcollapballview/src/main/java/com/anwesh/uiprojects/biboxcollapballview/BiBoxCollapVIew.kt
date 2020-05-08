package com.anwesh.uiprojects.biboxcollapballview

/**
 * Created by anweshmishra on 08/05/20.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF

val nodes : Int = 5
val lines : Int = 2
val scGap : Float = 0.02f
val strokeFactor : Int = 90
val sizeFactor : Float = 2.9f
val foreColor : Int = Color.parseColor("#4CAF50")
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20
val parts : Int = 3

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawCollapBox(i : Int, sf : Float, size : Float, w : Float, paint : Paint) {
    val sfi : Float = sf.divideScale(i * 2, parts)
    save()
    translate((w - size) * i, -size / 2)
    drawRect(RectF(size * i * sfi, 0f, size * i + size * (1 - i) * (1 - sfi), size), paint)
    restore()
}

fun Canvas.drawMovingCircle(sf : Float, w : Float, size : Float, paint : Paint) {
    val sf1 : Float = sf.divideScale(0, parts)
    val sf2 : Float = sf.divideScale(1, parts)
    val sf3 : Float = sf.divideScale(2, parts)
    val x : Float = size + size * sf1 + (w - 2 * size) * sf2 + size * sf3
    drawCircle(x, 0f, size, paint)
}

fun Canvas.drawBiCollapCircle(scale : Float, w : Float, size : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    drawMovingCircle(sf, w, size, paint)
    for (i in 0..1) {
        drawCollapBox(i, sf, size, w, paint)
    }
}

fun Canvas.drawBCCNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.color = foreColor
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.strokeCap = Paint.Cap.ROUND
    save()
    translate(0f, gap * (i + 1))
    drawBiCollapCircle(scale, w, size, paint)
    restore()
}

class BiBoxCollapView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }
}