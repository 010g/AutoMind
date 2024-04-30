package com.example.automind.ui.record

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.text.Typography.amp

class WaveformView(context: Context?, attrs: AttributeSet?):View (context,attrs){

    private var paint = Paint()
    var amplitudes = ArrayList<Float>()
    var spikes = ArrayList<RectF>()

    private var radius = 6f
    private var width = 9f
    private var space = 6f

    private var screenWidth = 0f
    private var screenHigh = 300f

    private var maxSpikes = 0

    init{
        paint.color = Color.rgb(255,255,255)

        screenWidth = resources.displayMetrics.widthPixels.toFloat()

        maxSpikes = (screenWidth / (width + space)).toInt()
    }

    fun addAmplitude(amp: Float){
        var norm = (amp / 55).coerceAtMost(screenHigh)
        amplitudes.add(norm)


        spikes.clear()
        var amps = amplitudes.takeLast(maxSpikes)
        for(i in amps.indices){

            var left = screenWidth - i * (width + space)
            var top = screenHigh / 2 - amps[i] / 2
            var right = left + width
            var bottom = top + amps[i]

            spikes.add(RectF(left,top,right,bottom))

        }

        invalidate()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        spikes.forEach{
            canvas?.drawRoundRect(it, radius,radius,paint)
        }
    }

    fun reset() {
        amplitudes.clear()
        invalidate()
    }

}