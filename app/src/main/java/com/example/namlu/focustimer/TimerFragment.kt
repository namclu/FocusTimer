package com.example.namlu.focustimer

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

class TimerFragment : Fragment() {

    // Progress bar and views
    private var progressBarStatic: ProgressBar? = null
    private var progressBarUpdate: ProgressBar? = null
    private var textViewCountDownTimer: TextView? = null
    private var buttonTimerStartPause: Button? = null
    private var buttonTimerReset: Button? = null
    private var buttonTimerSet: Button? = null

    // Countdown timer
    private var countDownTimer: CountDownTimer? = null

    // Timer stuff
    // TODO don't use hardcoded time value
    var timeLeftInMilliseconds: Long = TEN_SECONDS_IN_MILLISECONDS.toLong()
    var isTimerRunning: Boolean = false

    companion object {
        const val TAG = "TimerFragment"
        const val ONE_SEC_IN_MILLISECONDS = 1000
        const val TEN_SECONDS_IN_MILLISECONDS = 10000

        fun newInstance() : TimerFragment {
            return TimerFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_timer, container, false)

        // Find views
        progressBarStatic = view.findViewById(R.id.pbar_static)
        progressBarUpdate = view.findViewById(R.id.pbar_update)
        textViewCountDownTimer = view.findViewById(R.id.tv_count_down_timer)

        // Find buttons
        buttonTimerStartPause = view.findViewById(R.id.btn_timer_start_pause)
        buttonTimerReset = view.findViewById(R.id.btn_timer_reset)
        buttonTimerSet = view.findViewById(R.id.btn_timer_set)

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

        buttonTimerSet?.setOnClickListener {
            Log.d(TAG, "setTime button clicked")
            val dialog: SetTimerDialog = SetTimerDialog.newInstance()
            dialog.show(fragmentManager, TAG)
        }

        updateCountDownText()

        return view
    }

    private fun startTimer() {
        // In the countdown interval, by subtracting time from the interval, it fixes a bug where
        // timer would stop with 00:01 left and also makes the progress bar update more smoothly
        countDownTimer = object : CountDownTimer(timeLeftInMilliseconds,
                ONE_SEC_IN_MILLISECONDS.toLong() - 900) {
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
        timeLeftInMilliseconds = TEN_SECONDS_IN_MILLISECONDS.toLong()
        buttonTimerReset?.visibility = View.INVISIBLE
        buttonTimerStartPause?.visibility = View.VISIBLE

        updateCountDownText()
    }

    private fun updateCountDownText() {
        val minutes = ((timeLeftInMilliseconds / 1000) / 60).toInt()
        val seconds = ((timeLeftInMilliseconds / 1000) % 60).toInt()
        val timeLeftFormatted = String.format("%02d:%02d", minutes, seconds)

        textViewCountDownTimer?.text = timeLeftFormatted
    }

    // Update timer bar progress as time progresses
    private fun updateProgressBar() {
        progressBarUpdate?.max = TEN_SECONDS_IN_MILLISECONDS
        progressBarUpdate?.progress = timeLeftInMilliseconds.toInt()
    }

    // Reset timer bar progress when user hits the "reset" button
    private fun resetProgressBar() {
        progressBarUpdate?.progress = TEN_SECONDS_IN_MILLISECONDS
        progressBarUpdate?.visibility = View.VISIBLE
    }
}
