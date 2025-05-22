package com.servicein.core.util

import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object Util {
    fun formatRupiah(value: Int): String {
        val formatter = NumberFormat.getNumberInstance(java.util.Locale("in", "ID"))
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
}