package com.servicein.data.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.servicein.MainActivity
import com.servicein.R

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId =
            intent.getStringExtra(NotificationService.EXTRA_NOTIFICATION_ID) ?: return
        val title = intent.getStringExtra(NotificationService.EXTRA_TITLE) ?: return
        val message = intent.getStringExtra(NotificationService.EXTRA_MESSAGE) ?: return

        showNotification(context, notificationId, title, message)
    }

    private fun showNotification(
        context: Context,
        notificationId: String,
        title: String,
        message: String
    ) {
        val tapIntent = Intent(context, MainActivity::class.java)
        val tapPendingIntent = PendingIntent.getActivity(
            context,
            0,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notification = NotificationCompat.Builder(context, NotificationService.CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(tapPendingIntent)
            .setAutoCancel(true)
            .build()

        // Show the notification
        val notificationManager = NotificationManagerCompat.from(context)
        val notificationCode = notificationId.hashCode()

        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(notificationCode, notification)
        }
    }
}