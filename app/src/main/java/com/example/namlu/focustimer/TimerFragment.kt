package com.example.namlu.focustimer

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

class TimerFragment : Fragment(), SetTimerDialog.OnSetTime {

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
    var startTimeInMilliseconds: Long = 0.toLong()
    var timeLeftInMilliseconds = 0.toLong()
    var isTimerRunning: Boolean = false

    companion object {
        const val TAG = "TimerFragment"
        const val SECONDS_IN_ONE_MINUTE = 60
        const val MILLIS_IN_ONE_SECOND = 1000

        fun newInstance() : TimerFragment {
            return TimerFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_timer, container, false)

        // Find views and buttons
        progressBarStatic = view.findViewById(R.id.pbar_static)
        progressBarUpdate = view.findViewById(R.id.pbar_update)
        textViewCountDownTimer = view.findViewById(R.id.tv_count_down_timer)
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
            val dialog: SetTimerDialog = SetTimerDialog.newInstance()
            dialog.setTargetFragment(this, 1)
            dialog.show(fragmentManager, TAG)
        }

        updateCountDownText()

        return view
    }

    // Abstract method from SetTimerDialog to set time
    override fun setTime(timeInMillis: Int) {
        // Take the user value entered and convert to minute(s)
        startTimeInMilliseconds =
                timeInMillis * MILLIS_IN_ONE_SECOND * SECONDS_IN_ONE_MINUTE.toLong()
        resetTimer()
    }

    private fun startTimer() {
        // In the countdown interval, by subtracting time from the interval, it fixes a bug where
        // timer would stop with 00:01 left and also makes the progress bar update more smoothly
        countDownTimer = object : CountDownTimer(timeLeftInMilliseconds,
                MILLIS_IN_ONE_SECOND.toLong() - 900) {
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
                buttonTimerStartPause?.text = getString(R.string.btn_text_start)
                buttonTimerStartPause?.visibility = View.INVISIBLE
                buttonTimerReset?.visibility = View.VISIBLE

                progressBarUpdate?.visibility = View.INVISIBLE
            }
        }.start()

        isTimerRunning = true
        buttonTimerStartPause?.text = getString(R.string.btn_text_pause)
        buttonTimerReset?.visibility = View.INVISIBLE
    }

    private fun pauseTimer() {
        isTimerRunning = false
        countDownTimer?.cancel()
        buttonTimerStartPause?.text = getString(R.string.btn_text_start)
        buttonTimerReset?.visibility = View.VISIBLE
    }

    private fun resetTimer() {
        timeLeftInMilliseconds = startTimeInMilliseconds
        buttonTimerReset?.visibility = View.INVISIBLE
        buttonTimerStartPause?.visibility = View.VISIBLE

        updateCountDownText()
    }

    private fun updateCountDownText() {
        val minutes = ((timeLeftInMilliseconds / 1000) / SECONDS_IN_ONE_MINUTE).toInt()
        val seconds = ((timeLeftInMilliseconds / 1000) % SECONDS_IN_ONE_MINUTE).toInt()

        textViewCountDownTimer?.text = String.format("%02d:%02d", minutes, seconds)
    }

    // Todo Add method updateButtonVisibility() which will handle the visibility of buttons

    // Update timer bar progress as time progresses
    private fun updateProgressBar() {
        progressBarUpdate?.max = startTimeInMilliseconds.toInt()
        progressBarUpdate?.progress = timeLeftInMilliseconds.toInt()
    }

    // Reset timer bar progress when user hits the "reset" button
    private fun resetProgressBar() {
        progressBarUpdate?.progress = startTimeInMilliseconds.toInt()
        progressBarUpdate?.visibility = View.VISIBLE
    }
}
