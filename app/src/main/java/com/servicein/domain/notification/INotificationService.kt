package com.servicein.domain.notification

import android.content.Intent
import java.time.LocalDateTime

interface INotificationService {
    fun showNotification(
        title: String,
        message: String,
        intent: Intent? = null
    )

    fun scheduleNotification(
        notificationId: String,
        title: String,
        message: String,
        scheduledTime: LocalDateTime
    ): Result<Unit>

    fun checkNotificationPermission(): Boolean
    fun requestNotificationPermission()
}