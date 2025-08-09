package com.servicein.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun NoInternetDialog(
    onDismiss: () -> Unit = {},
    onOpenSettings: () -> Unit,
    onRetry: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.SignalWifiOff,
                contentDescription = "No Internet",
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = "Tidak Ada Koneksi Internet",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = "Aplikasi memerlukan koneksi internet untuk berfungsi dengan baik. " +
                        "Periksa koneksi Anda dan coba lagi.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text("Coba Lagi")
            }
        },
        dismissButton = {
            TextButton(onClick = onOpenSettings) {
                Text("Pengaturan")
            }
        }
    )
}