package com.example.namlu.focustimer

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private var progressBarStatic: ProgressBar? = null
    private var progressBarUpdate: ProgressBar? = null
    private var textViewCountDownTimer: TextView? = null
    private var buttonTimerStartPause: Button? = null
    private var buttonTimerReset: Button? = null

    private var countDownTimer: CountDownTimer? = null
    var timeLeftInMilliseconds: Long = TEN_MINUTES_IN_MILLISECONDS.toLong() // 10 minutes
    var isTimerRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find views
        progressBarStatic = findViewById(R.id.pbar_static)
        progressBarUpdate = findViewById(R.id.pbar_update)
        textViewCountDownTimer = findViewById(R.id.tv_count_down_timer)

        // Find buttons
        buttonTimerStartPause = findViewById(R.id.btn_timer_start_pause)
        buttonTimerReset = findViewById(R.id.btn_timer_reset)

        // Setup button actions
        buttonTimerStartPause?.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        buttonTimerReset?.setOnClickListener {
            resetTimer()
            resetProgressBar()
        }

        updateCountDownText()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMilliseconds, ONE_SEC_IN_MILLISECONDS.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMilliseconds = millisUntilFinished
                progressBarUpdate?.visibility = View.VISIBLE
                // progressBarStatic acts like an initial placeholder, then disappears when the
                // timer starts so that progressBarUpdate takes over and updates itself
                progressBarStatic?.visibility = View.INVISIBLE

                updateCountDownText()
                updateProgressBar()
            }

            // When onFinish() is called, set the button text to "start", make it invisible, then
            // show the "reset" button
            override fun onFinish() {
                isTimerRunning = false
                buttonTimerStartPause?.text = getString(R.string.start_text)
                buttonTimerStartPause?.visibility = View.INVISIBLE
                buttonTimerReset?.visibility = View.VISIBLE

                progressBarUpdate?.visibility = View.INVISIBLE
            }
        }.start()

        isTimerRunning = true
        buttonTimerStartPause?.text = getString(R.string.pause_text)
        buttonTimerReset?.visibility = View.INVISIBLE
    }

    private fun pauseTimer() {
        isTimerRunning = false
        countDownTimer?.cancel()
        buttonTimerStartPause?.text = getString(R.string.start_text)
        buttonTimerReset?.visibility = View.VISIBLE
    }

    private fun resetTimer() {
        timeLeftInMilliseconds = TEN_MINUTES_IN_MILLISECONDS.toLong()
        buttonTimerReset?.visibility = View.INVISIBLE
        buttonTimerStartPause?.visibility = View.VISIBLE

        updateCountDownText()
    }

    private fun updateCountDownText() {
        var minutes = ((timeLeftInMilliseconds / 1000) / 60).toInt()
        var seconds = ((timeLeftInMilliseconds / 1000) % 60).toInt()
        var timeLeftFormatted = String.format("%02d:%02d", minutes, seconds)

        textViewCountDownTimer?.text = timeLeftFormatted
    }

    // Update timer bar progress as time progresses
    private fun updateProgressBar() {
        progressBarUpdate?.max = TEN_MINUTES_IN_MILLISECONDS
        progressBarUpdate?.progress = timeLeftInMilliseconds.toInt()
    }

    // Reset timer bar progress when user hits the "reset" button
    private fun resetProgressBar() {
        progressBarUpdate?.progress = TEN_MINUTES_IN_MILLISECONDS
        progressBarUpdate?.visibility = View.VISIBLE
    }

    companion object {
        const val ONE_SEC_IN_MILLISECONDS = 1000
        const val TEN_MINUTES_IN_MILLISECONDS = 10000
    }
}
