package com.example.internet_speed_meter

import android.content.Intent
import android.os.Build
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val CHANNEL = "com.example.internet_speed_meter/speed"
    private var methodChannel: MethodChannel? = null

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        methodChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)

        methodChannel?.setMethodCallHandler { call, result ->
            if (call.method == "startService") {
                startSpeedService()
                result.success(true)
            } else {
                result.notImplemented()
            }
        }

        // Listen for speed updates from the service
        SpeedMonitorService.listener = { speed ->
            methodChannel?.invokeMethod("updateSpeed", speed)
        }
    }

    private fun startSpeedService() {
        val serviceIntent = Intent(this, SpeedMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}
