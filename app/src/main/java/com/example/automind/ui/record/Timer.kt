package com.example.automind.ui.record

import android.os.Handler
import android.os.Looper

class Timer(listener:OnTimerTickListener) {

    interface OnTimerTickListener{
        fun OnTimerTick(duration:String)
    }

    private var handler = Handler(Looper.getMainLooper())
    private lateinit var runnable:Runnable

    private var duration = 0L
    private var delay = 100L

    init {
        runnable = Runnable {
            duration += delay
            handler.postDelayed(runnable,delay)
            listener.OnTimerTick(format())

        }
    }

    fun start(){
        handler.postDelayed(runnable,delay)
    }

    fun pause(){
        handler.removeCallbacks(runnable)
    }

    fun format():String{
        val seconds = (duration / 1000) % 60
        val minutes = (duration / (1000 * 60)) % 60

        var formatted = "%02d:%02d".format(minutes,seconds)

        return formatted
    }

}