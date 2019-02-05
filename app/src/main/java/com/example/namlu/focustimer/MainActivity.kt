package com.example.namlu.focustimer

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private var textViewCountDownTimer: TextView? = null
    private var buttonTimerStartPause: Button? = null
    private var buttonTimerReset: Button? = null

    private var countDownTimer : CountDownTimer? = null
    var timeLeftInMilliseconds: Long = TEN_MINUTES_IN_MILLISECONDS.toLong() // 10 minutes
    var isTimerRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find views
        textViewCountDownTimer = findViewById(R.id.tv_count_down_timer)

        // Find buttons
        buttonTimerStartPause = findViewById(R.id.btn_timer_start_pause)
        buttonTimerReset = findViewById(R.id.btn_timer_reset)

        // Setup button actions
        buttonTimerStartPause?.setOnClickListener {
            if (isTimerRunning) {
                Log.d("MainActivity","pauseTimer()")
                pauseTimer()
            } else {
                Log.d("MainActivity","startTimer()")
                startTimer()
            }
        }

        buttonTimerReset?.setOnClickListener {
            Log.d("MainActivity", "resetTimer()")
            resetTimer()
        }

        updateCountDownText()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMilliseconds, ONE_SEC_IN_MILLISECONDS.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMilliseconds = millisUntilFinished
                updateCountDownText()
            }
            // When onFinish() is called, set the button text to "start", make it invisible, then
            // show the "reset" button
            override fun onFinish() {
                isTimerRunning = false
                buttonTimerStartPause?.text = getString(R.string.start_text)
                buttonTimerStartPause?.visibility = View.INVISIBLE
                buttonTimerReset?.visibility = View.VISIBLE
            }
        }.start()

        isTimerRunning = true
        buttonTimerStartPause?.text = getString(R.string.pause_text)
        buttonTimerReset?.visibility = View.INVISIBLE
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        buttonTimerStartPause?.text = getString(R.string.start_text)
        buttonTimerReset?.visibility = View.VISIBLE
    }

    private fun resetTimer() {
        timeLeftInMilliseconds = TEN_MINUTES_IN_MILLISECONDS.toLong()
        updateCountDownText()
        buttonTimerReset?.visibility = View.INVISIBLE
        buttonTimerStartPause?.visibility = View.VISIBLE
    }

    private fun updateCountDownText() {
        var minutes = ((timeLeftInMilliseconds / 1000) / 60).toInt()
        var seconds = ((timeLeftInMilliseconds / 1000) % 60).toInt()

        var timeLeftFormatted = String.format("%02d:%02d", minutes, seconds)
        textViewCountDownTimer?.text = timeLeftFormatted
    }

    companion object {
        const val ONE_SEC_IN_MILLISECONDS = 1000
        const val TEN_MINUTES_IN_MILLISECONDS = 600000
    }
}
