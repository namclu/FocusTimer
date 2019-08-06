package com.example.namlu.focustimer

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView

class SetTimerDialog : DialogFragment() {

    private var textUpdateWorkTime: TextView? = null
    private var seekInputWorkTime: SeekBar? = null
    private var editInputBreakTime: EditText? = null
    private var buttonSetTime: Button? = null
    private lateinit var setTimeListener: OnSetTime

    companion object {
        const val TAG = "SetTimerDialog"

        fun newInstance(): SetTimerDialog {
            return SetTimerDialog()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            setTimeListener = targetFragment as OnSetTime
        } catch (e: ClassCastException) {
            Log.e(TAG, "onAttach: ClassCastException : " + e.message )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.dialog_set_timer, container, false)

        // Find views
        seekInputWorkTime = view.findViewById(R.id.sb_input_work_time)
        textUpdateWorkTime = view.findViewById(R.id.tv_label_work_time_minutes)
        editInputBreakTime = view.findViewById(R.id.et_input_break_time)
        buttonSetTime = view.findViewById(R.id.btn_set_time)

        val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // updated continuously as the user slides their thumb
                textUpdateWorkTime?.text = getString(R.string.work_time_minutes, seekInputWorkTime?.progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // called when the user first touches the SeekBar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // called after the user finishes moving the SeekBar
            }
        }

        seekInputWorkTime?.setOnSeekBarChangeListener(seekBarChangeListener)

        buttonSetTime?.setOnClickListener {
            val inputWorkTime = seekInputWorkTime?.progress
            if (inputWorkTime != null) {
                setTimeListener.setWorkTime(inputWorkTime)
                dismiss()
            } else {
                dismiss()
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        if (dialog != null) {
            // Sets the width, height for the DialogFragment
            // layout_width & layout_height in xml must be set to "match_parent" for this to work
            val dialogWidth = ViewGroup.LayoutParams.MATCH_PARENT
            val dialogHeight = 1000

            dialog?.window?.setLayout(dialogWidth, dialogHeight)
        } else {
            return
        }
    }

    // Interface to send time to caller
    interface OnSetTime {
        fun setWorkTime(timeInMillis: Int)
        fun setBreakTime(timeInMillis: Int)
    }
}