package ru.hse.client.chapters

import android.os.CountDownTimer
import android.view.ViewParent

class CustomCountDownTimer(
    private val milliSeconds: Long,
    private val countDownInterval: Long
) {
    private val milliSecondsUntilFinish = milliSeconds
    private val timer = Timer(this, milliSeconds, countDownInterval)
    private var isRunning = false
    var onTick: ((milliSecondsUntilFinish: Long) -> Unit)? = null
    var onDone: (() -> Unit)? = null

    private class Timer(
        private val parent: CustomCountDownTimer,
        milliSeconds: Long,
        countDownInterval: Long
    ): CountDownTimer(milliSeconds, countDownInterval) {
        private var milliSecondsUntilFinish = parent.milliSecondsUntilFinish

        override fun onTick(millisUntilFinished: Long) {
            this.milliSecondsUntilFinish = millisUntilFinished
            parent.onTick?.invoke(millisUntilFinished)
        }

        override fun onFinish() {
            this.milliSecondsUntilFinish = 0
            parent.onDone?.invoke()
        }

    }

    fun startTimer() {
        timer.start()
        isRunning = true
    }
}