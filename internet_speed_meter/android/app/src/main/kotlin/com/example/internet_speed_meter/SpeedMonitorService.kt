package com.example.internet_speed_meter

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.TrafficStats
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import java.util.Timer
import java.util.TimerTask

class SpeedMonitorService : Service() {

    private val CHANNEL_ID = "SpeedMonitorServiceChannel"
    private var lastRxBytes: Long = 0
    private var lastTxBytes: Long = 0
    private var lastTime: Long = 0
    private var timer: Timer? = null

    companion object {
        var listener: ((String) -> Unit)? = null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification("0 KB/s")
        startForeground(1, notification)

        startMonitoring()

        return START_STICKY
    }

    private fun startMonitoring() {
        lastRxBytes = TrafficStats.getTotalRxBytes()
        lastTxBytes = TrafficStats.getTotalTxBytes()
        lastTime = System.currentTimeMillis()

        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val currentRxBytes = TrafficStats.getTotalRxBytes()
                val currentTxBytes = TrafficStats.getTotalTxBytes()
                val currentTime = System.currentTimeMillis()

                val timeDiff = currentTime - lastTime
                if (timeDiff > 0) {
                    val rxDiff = currentRxBytes - lastRxBytes
                    val txDiff = currentTxBytes - lastTxBytes

                    val totalSpeedBytesPerSec = (rxDiff + txDiff) * 1000 / timeDiff
                    val speedText = formatSpeed(totalSpeedBytesPerSec)

                    // Update Notification
                    updateNotification(speedText)

                    // Notify Flutter
                    Handler(Looper.getMainLooper()).post {
                        listener?.invoke(speedText)
                    }
                }

                lastRxBytes = currentRxBytes
                lastTxBytes = currentTxBytes
                lastTime = currentTime
            }
        }, 1000, 1000)
    }

    private fun formatSpeed(bytesPerSec: Long): String {
        return when {
            bytesPerSec < 1024 -> "$bytesPerSec B/s"
            bytesPerSec < 1024 * 1024 -> "${bytesPerSec / 1024} KB/s"
            else -> String.format("%.2f MB/s", bytesPerSec / (1024.0 * 1024.0))
        }
    }

    private fun createNotification(speedText: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Internet Speed")
            .setContentText("Current Speed: $speedText")
            .setSmallIcon(R.mipmap.ic_launcher) // Need an actual small icon
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(speedText: String) {
        val notification = createNotification(speedText)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Internet Speed Monitor",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
