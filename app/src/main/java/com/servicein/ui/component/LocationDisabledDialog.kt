package com.servicein.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun LocationDisabledDialog(
    onDismiss: () -> Unit = {},
    onOpenSettings: () -> Unit,
    onIgnore: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.LocationOff,
                contentDescription = "Location Disabled",
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = "Layanan Lokasi Dinonaktifkan",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = "Aplikasi memerlukan akses lokasi untuk memberikan layanan terbaik. " +
                        "Aktifkan layanan lokasi di pengaturan perangkat Anda.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onOpenSettings) {
                Text("Buka Pengaturan")
            }
        },
        dismissButton = {
            Box { }
        }
    )
}