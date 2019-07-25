package com.example.namlu.focustimer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

class MainActivity : AppCompatActivity(), SetTimerDialog.OnSetTime {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.root_layout, TimerFragment.newInstance())
                    .commit()
        }
    }

    override fun setTime(time: Int) {
        Toast.makeText(this, "setTime() = $time", Toast.LENGTH_SHORT).show()
    }
}
