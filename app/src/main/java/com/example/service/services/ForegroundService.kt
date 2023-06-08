package com.example.service.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.service.FibonacciCalculator
import com.example.service.MainActivity.Companion.FOREGROUND_KEY
import com.example.service.MainActivity.Companion.INTENT_FILTER
import com.example.service.R
import kotlinx.coroutines.*

class ForegroundService : Service() {

    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()
        isConnected = true
        val calculator = FibonacciCalculator()
        CoroutineScope(Dispatchers.Main + SupervisorJob())
            .launch {
                while (isConnected) {
                    delay(1000)
                    val intent1 = Intent(INTENT_FILTER)
                    intent1.putExtra(FOREGROUND_KEY, calculator.getNextNumber())
                    LocalBroadcastManager.getInstance(this@ForegroundService).sendBroadcast(intent1)
                }
        }
        return START_STICKY
    }

    private fun startForeground() {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            } else "ForegroundService"

        val notification = NotificationCompat.Builder(this, channelId)
            .setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        startForeground(FOREGROUND_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onDestroy() {
        isConnected = false
        super.onDestroy()
    }

    companion object {
        var isConnected = false
        const val FOREGROUND_ID = 123
    }
}