package com.enkoss.android14Issue

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.app.ActivityCompat
import com.enkoss.android14Issue.ui.theme.Android14IssueAppTheme

class SecondActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        println("User granted notification permission: $isGranted")
    }

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
        val postNotificationPermissionStatus = ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
        if (postNotificationPermissionStatus != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    @Composable
    fun SecondActivityContent(modifier: Modifier = Modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "All you need to do now is to close the app by swiping it away from the recent apps list. In a second after, the app will start playing audio.\n\nRemember: it is not always reproducible, please try several times.",
                    modifier = Modifier.padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
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