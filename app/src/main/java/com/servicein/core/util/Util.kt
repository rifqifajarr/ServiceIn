package com.servicein.core.util

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object Util {
    fun formatRupiah(value: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
        return "Rp ${formatter.format(value)}"
    }

    // Fungsi format: "1000" -> "1.000"
    fun formatRupiahNumber(input: String): String {
        return input
            .toLongOrNull()
            ?.let {
                NumberFormat.getInstance(Locale("id", "ID")).format(it)
            } ?: ""
    }

    fun formatDateTime(dateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy | HH:mm", Locale("id", "ID"))
        return dateTime.format(formatter)
    }

    fun calculatePriceBreakdown(orderType: OrderType, distanceInMeters: Int): List<Int> {
        val serviceFee = when (orderType) {
            OrderType.ROUTINE_SERVICE -> 100_000
            OrderType.LIGHT_SERVICE -> 75_000
            OrderType.EMERGENCY_SERVICE -> 125_000
        }

        val distanceInKm = distanceInMeters / 1000.0
        val transportFee = if (distanceInKm <= 3.0) {
            6_000
        } else {
            val multiplier = kotlin.math.ceil(distanceInKm / 3.0).toInt()
            multiplier * 5_000
        }

        val appFee = 5_000

        return listOf(serviceFee, transportFee, appFee)
    }

    fun calculateDistanceInMeters(start: LatLng, end: LatLng): Float {
        val result = FloatArray(1)
        Location.distanceBetween(
            start.latitude, start.longitude,
            end.latitude, end.longitude,
            result
        )
        return result[0]
    }

    fun OrderType?.toDisplayString(): String = when (this) {
        OrderType.ROUTINE_SERVICE -> "Service Rutin"
        OrderType.LIGHT_SERVICE -> "Perbaikan Ringan"
        OrderType.EMERGENCY_SERVICE -> "Perbaikan Darurat"
        else -> ""
    }
}