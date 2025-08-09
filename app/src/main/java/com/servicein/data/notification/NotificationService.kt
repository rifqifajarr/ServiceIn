package com.servicein.data.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.servicein.MainActivity
import com.servicein.R
import com.servicein.core.util.Util
import com.servicein.domain.model.Order
import com.servicein.domain.notification.INotificationService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class NotificationService @Inject constructor(@ApplicationContext private val context: Context) :
    INotificationService {
    companion object {
        const val CHANNEL_ID = "service_in_shop_channel"
        private const val CHANNEL_NAME = "ServiceIn Shop Notification"
        private const val CHANNEL_DESCRIPTION = "Notification for Order Status"
        private const val NOTIFICATION_ID = 1001

        const val EXTRA_NOTIFICATION_ID = "notification_id"
        const val EXTRA_TITLE = "title"
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_ORDER_ID = "order_id"
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun showNotification(
        title: String,
        message: String,
        intent: Intent?,
    ) {
        val pendingIntent = intent?.let {
            PendingIntent.getActivity(
                context,
                0,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .apply {
                pendingIntent?.let { setContentIntent(it) }
            }
            .build()

        with(NotificationManagerCompat.from(context)) {
            if (checkNotificationPermission())
                notify(NOTIFICATION_ID, notification)
        }
    }

    override fun scheduleNotification(
        notificationId: String,
        title: String,
        message: String,
        scheduledTime: LocalDateTime
    ): Result<Unit> {
        return try {
            val triggerTime = scheduledTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            if (triggerTime <= System.currentTimeMillis()) {
                return Result.failure(IllegalArgumentException("Scheduled time must be in the future."))
            }

            val intent = Intent(context, NotificationBroadcastReceiver::class.java).apply {
                putExtra(EXTRA_NOTIFICATION_ID, notificationId)
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_MESSAGE, message)
            }

            val requestCode = notificationId.hashCode()
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d("NotificationService", "masuk")
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                Log.d("NotificationService", "masuk else")
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun showNewOrderNotification(order: Order) {
        if (!checkNotificationPermission()) return

        val title = "Pesanan Baru Diterima"
        val message = buildString {
            append(order.customerName)
            append(
                " - ${Util.formatDateTime(LocalDateTime.parse(order.dateTime))} - ${
                    Util.formatRupiah(
                        order.value
                    )
                }"
            )
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("orderId", order.id)
            putExtra("openOrderDetail", true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        showNotification(title, message, intent)
    }

    override fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        } else {
            true
        }
    }

    override fun requestNotificationPermission() {
        // This will be handled in the Compose UI layer
        // This method is here for interface completeness
    }
}