package com.enkoss.android14Issue

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.enkoss.Android14IssueApp.R
import com.enkoss.android14Issue.ui.theme.Android14IssueAppTheme

class SecondActivity : ComponentActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Android14IssueAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    SecondActivityContent()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        println("Start playing audio")
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.titanium_170190)
            mediaPlayer?.setOnCompletionListener { it.release() }
        }
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    @Composable
    fun SecondActivityContent(modifier: Modifier = Modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "The app should play the audio now. Follow the next steps to reproduce the issue:" +
                            "\n 1. Open the recently running apps list" +
                            "\n 2. Swipe this app away from the list",
                    textAlign = TextAlign.Start
                )

                Text(
                    text = "Android 14" +
                            "\nThe app will continue playing the audio as it is not terminated.",
                    modifier = Modifier.padding(top = 16.dp),
                    textAlign = TextAlign.Start)

                Text(
                    text = "Android 13" +
                            "\nThe app will always stop the audio as it is terminated completely.",
                    modifier = Modifier.padding(top = 16.dp),
                    textAlign = TextAlign.Start
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun SecondActivityContentPreview() {
        Android14IssueAppTheme {
            SecondActivityContent()
        }
    }
}