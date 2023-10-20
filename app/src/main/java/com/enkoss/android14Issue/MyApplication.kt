package com.enkoss.android14Issue

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.enkoss.Android14IssueApp.R
import com.enkoss.android14Issue.MyApplication.Companion.NOTIFICATION_ID_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class MyApplication: Application(), LifecycleEventObserver {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "notificationChannel"
        const val NOTIFICATION_ID_KEY = "notificationId"
    }

    private lateinit var mediaPlayer: MediaPlayer
    private var currentActivity: Activity? = null
    private var delayAudioJob: Job? = null
    private var notificationId = 1

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.channel_name)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
            description = getString(R.string.channel_description)
        }
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        println("onStateChanged, event: $event")
        if (event == Lifecycle.Event.ON_STOP && currentActivity == null) {
            // We can be here in 2 cases:
            // - when the app was closed (swiped away from the recent app list)
            // - when the app was backgrounded
            println("Seems the last activity was closed. Now we can talk to user.")
            surpriseUser()
        }
    }

    /**
     * The app will start playing audio explaining what is going on.
     * It will also send local notifications to prove the app process is alive.
     */
    private fun surpriseUser() {
        playAudioWithDelay(1000L)
        scheduleNotificationIn(10000)
        scheduleNotificationIn(13500)
    }

    private fun playAudioWithDelay(millis: Long) {
        println("Will start playing audio in $millis milliseconds")
        delayAudioJob?.cancel()
        delayAudioJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                delay(millis)
                CoroutineScope(Dispatchers.Main).launch MainScope@ {
                    if (currentActivity != null) {
                        return@MainScope
                    }

                    println("Start playing audio")
                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio)
                    mediaPlayer.setOnCompletionListener {
                        mediaPlayer.release()
                    }
                    mediaPlayer.start()
                }
                delayAudioJob?.cancel()
            }
        }
    }

    /**
     * The function scheduling sending a local notification.
     * If not call it the issue will still persist. It doesn't keep app alive when it shouldn't be.
     */
    private fun scheduleNotificationIn(millis: Long) {
        println("Schedule a notification sending in $millis milliseconds")
        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(applicationContext, AlarmReceiver::class.java)
        intent.putExtra(NOTIFICATION_ID_KEY, notificationId)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, Random.nextInt(), intent, FLAG_IMMUTABLE)
        val triggerTime = System.currentTimeMillis() + millis
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        notificationId++
    }

    private val activityLifecycleCallbacks = object : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

        override fun onActivityStarted(activity: Activity) {}

        override fun onActivityResumed(activity: Activity) {
            currentActivity = activity
        }

        override fun onActivityPaused(activity: Activity) {}

        override fun onActivityStopped(activity: Activity) {
            if (currentActivity == activity) {
                currentActivity = null
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {}
    }
}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(NOTIFICATION_ID_KEY, 1)
        val postNotificationPermissionStatus = ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        if (postNotificationPermissionStatus == PackageManager.PERMISSION_GRANTED) {
            val notificationBuilder = NotificationCompat.Builder(context, MyApplication.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Background notification $notificationId")
                .setContentText("I thought the app is closed")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, notificationBuilder.build())
            println("Sent notification with id $notificationId")
        } else {
            println("Failed to send notification with id $notificationId. No permission.")
        }
    }
}