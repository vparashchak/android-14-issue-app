package com.enkoss.android14Issue

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class FirstActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Auto open the second activity in order to reproduce the issue
        // It is only reproducible when close the app while the second activity is presented
        startActivity(Intent(applicationContext, SecondActivity::class.java))
    }
}