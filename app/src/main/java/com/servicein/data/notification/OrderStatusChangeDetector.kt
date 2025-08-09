package com.servicein.data.notification

import android.content.Context
import android.content.Intent
import android.util.Log
import com.servicein.MainActivity
import com.servicein.core.util.OrderStatus
import com.servicein.core.util.Util
import com.servicein.domain.model.Order
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderStatusChangeDetector @Inject constructor(
    private val notificationService: NotificationService,
    @ApplicationContext private val context: Context
) {
    private val previousOrderStates = mutableMapOf<String, OrderStatus>()

    fun detectAndHandleStatusChanges(orders: List<Order>) {
        orders.forEach { order ->
            val previousStatus = previousOrderStates[order.id]
            val currentStatus = order.statusEnum

            if (previousStatus == OrderStatus.RECEIVED && currentStatus == OrderStatus.ACCEPTED) {
                Log.d("OrderStatusChangeDetector", "Order accepted: ${order.id}")
                notificationService.showNotification(
                    title = "Pesanan Anda Telah Diterima",
                    message = "Pesanan Anda telah diterima oleh ${order.shopName}.",
                    intent = Intent(context, MainActivity::class.java).apply {
                        putExtra("orderId", order.id)
                        putExtra("openOrderDetail", true)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                )

                val scheduleDateTime = LocalDateTime.parse(order.dateTime)

                if (scheduleDateTime != null) {
                    val dayOfNotificationId = "${order.id}_day_reminder"
                    notificationService.scheduleNotification(
                        notificationId = dayOfNotificationId,
                        title = "Anda Memiliki Jadwal Layanan Hari Ini",
                        message = "Layanan anda dijadwalkan pada ${Util.formatDateTime(scheduleDateTime)}",
                        scheduledTime = scheduleDateTime.withHour(6).withMinute(0)
                    )

                    val oneHourBefore = scheduleDateTime.minusHours(1)
                    val hourBeforeNotificationId = "${order.id}_hour_reminder"
                    notificationService.scheduleNotification(
                        notificationId = hourBeforeNotificationId,
                        title = "Pengingat Jadwal Pesanan",
                        message = "Anda Memiliki Jadwal Layanan Dalam 1 Jam",
                        scheduledTime = oneHourBefore
                    )
                }
            }

            if (previousStatus == OrderStatus.ACCEPTED && currentStatus == OrderStatus.FINISHED) {
                Log.d("OrderStatusChangeDetector", "Order finished: ${order.id}")
                notificationService.showNotification(
                    title = "Pesanan Anda Telah Selesai",
                    message = "Berikan penilaian anda untuk layanan yang telah diberikan.",
                    intent = null
                )
            }

            previousOrderStates[order.id] = currentStatus
        }

        val currentOrderIds = orders.map { it.id }.toSet()
        previousOrderStates.keys.removeAll { it !in currentOrderIds }
    }

    fun clearOrderHistory() {
        previousOrderStates.clear()
    }
}