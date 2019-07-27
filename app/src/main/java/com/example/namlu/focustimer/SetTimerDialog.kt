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
import java.lang.ClassCastException

class SetTimerDialog : DialogFragment() {

    private var editTextInputTime: EditText? = null
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

        if (context is OnSetTime) {
            setTimeListener = context
        } else {
            throw ClassCastException(
                    "${context.toString()} must implement OnSetTime"
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.dialog_set_timer, container, false)

        // Find views
        editTextInputTime = view.findViewById(R.id.et_input_time)
        buttonSetTime = view.findViewById(R.id.btn_set_time)

        buttonSetTime?.setOnClickListener {
            Log.d(TAG, "buttonSetTime clicked")

            val inputTime = editTextInputTime?.text.toString().toInt()

            // Todo check for empty and non-numeric inputs
            if (editTextInputTime == null) {
                dialog.dismiss()
            }

            if (editTextInputTime?.text!!.isNotEmpty()) {
                if (inputTime > 0) {
                    // Get time and send to caller
                    setTimeListener.setTime(inputTime)
                    dialog.dismiss()
                }
            } else {
                dialog.dismiss()
            }
        }

        return view
    }

    // Interface to send time to caller
    interface OnSetTime {
        fun setTime(time: Int)
    }
}