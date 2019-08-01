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
    private var startTimeInMillis: Long = 0.toLong()
    var timeLeftInMillis = 0.toLong()
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

    // Abstract methods from SetTimerDialog to set work and break times
    override fun setWorkTime(timeInMillis: Int) {
        // Take the user value entered and convert to minute(s)
        startTimeInMillis =
                timeInMillis * MILLIS_IN_ONE_SECOND * SECONDS_IN_ONE_MINUTE.toLong()
        resetTimer()
        resetProgressBar()
    }

    override fun setBreakTime(timeInMillis: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun startTimer() {
        // In the countdown interval, by subtracting time from the interval, it fixes a bug where
        // timer would stop with 00:01 left and also makes the progress bar update more smoothly
        countDownTimer = object : CountDownTimer(timeLeftInMillis,
                MILLIS_IN_ONE_SECOND.toLong() - 900) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
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
                updateButtonVisibility()
            }
        }.start()

        isTimerRunning = true
        updateButtonVisibility()
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        updateButtonVisibility()
    }

    private fun resetTimer() {
        timeLeftInMillis = startTimeInMillis
        isTimerRunning = false
        updateButtonVisibility()
        updateCountDownText()
    }

    private fun updateCountDownText() {
        val minutes = ((timeLeftInMillis / 1000) / SECONDS_IN_ONE_MINUTE).toInt()
        val seconds = ((timeLeftInMillis / 1000) % SECONDS_IN_ONE_MINUTE).toInt()

        textViewCountDownTimer?.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun updateButtonVisibility() {
        // If timer is running, only show the pause button and set other buttons to invisible
        if (isTimerRunning) {
            buttonTimerStartPause?.text = getString(R.string.btn_text_pause)
            buttonTimerStartPause?.visibility = View.VISIBLE
            buttonTimerReset?.visibility = View.INVISIBLE
            buttonTimerSet?.visibility = View.INVISIBLE
            // If timer is not running, there's 3 possibilities:
        } else {
            buttonTimerStartPause?.text = getString(R.string.btn_text_start)
            when {
                // 1) time has been set and not started
                timeLeftInMillis == startTimeInMillis -> {
                    buttonTimerStartPause?.visibility = View.VISIBLE
                    buttonTimerReset?.visibility = View.INVISIBLE
                    buttonTimerSet?.visibility = View.VISIBLE
                }
                // 2) timer has reached 0
                timeLeftInMillis < MILLIS_IN_ONE_SECOND -> {
                    buttonTimerStartPause?.visibility = View.INVISIBLE
                    buttonTimerReset?.visibility = View.VISIBLE
                    buttonTimerSet?.visibility = View.VISIBLE
                    progressBarUpdate?.visibility = View.INVISIBLE
                }
                // 3) timer is paused during the middle
                timeLeftInMillis < startTimeInMillis -> {
                    buttonTimerStartPause?.visibility = View.VISIBLE
                    buttonTimerReset?.visibility = View.VISIBLE
                    buttonTimerSet?.visibility = View.VISIBLE
                }
            }
        }
    }

    // Update timer bar progress as time progresses
    private fun updateProgressBar() {
        progressBarUpdate?.max = startTimeInMillis.toInt()
        progressBarUpdate?.progress = timeLeftInMillis.toInt()
    }

    // Reset timer bar progress when user hits the "reset" button
    private fun resetProgressBar() {
        progressBarUpdate?.progress = startTimeInMillis.toInt()
        progressBarUpdate?.visibility = View.VISIBLE
    }
}
