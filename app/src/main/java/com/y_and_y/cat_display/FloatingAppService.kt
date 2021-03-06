package com.y_and_y.cat_display

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.view.WindowManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.util.*

class FloatingAppService : Service() {

    companion object {
        val ACTION_START = "start"
        val ACTION_STOP = "stop"
    }

    private val notificationId = Random().nextInt()

    private var button: FloatingButton? = null

    override fun onCreate() {
        super.onCreate()
        startNotification()
    }

    private fun startNotification() {
        val activityIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0)
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this,channelId)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(FloatingAppService::class.simpleName)
                    .setContentText("Service is running.")
                    .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(FloatingAppService::class.simpleName)
                .setContentText("Service is running.")
                .build()
        }
        startForeground(notificationId, notification)
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

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null || intent.action == ACTION_START) {
            startOverlay()
        } else {
            stopSelf()
        }
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopOverlay()
    }

    private fun startOverlay() {
        ImageView(this).run {
            val windowManager = getSystemService(Service.WINDOW_SERVICE) as WindowManager
            Glide.with(this).load(when (MainActivity.catType) {
                0 -> R.raw.gray_cat
                1 -> R.raw.cat
                else -> R.raw.gray_cat
            }
            ).into(this)
            button = FloatingButton(windowManager, this).apply {
                visible = true
            }
        }
    }

    private fun stopOverlay() {
        button?.run {
            visible = false
        }
        button = null
    }
}