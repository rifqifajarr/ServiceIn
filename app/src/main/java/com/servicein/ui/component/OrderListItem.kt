package com.servicein.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.servicein.core.util.MapUtil
import com.servicein.core.util.OrderType
import com.servicein.core.util.Util
import com.servicein.domain.model.Order
import java.time.LocalDateTime

@Composable
fun OrderListItem(
    modifier: Modifier = Modifier,
    order: Order,
) {
    val context = LocalContext.current
    val orderType = when (order.orderType) {
        OrderType.LIGHT_REPAIR.name -> "Perbaikan Ringan"
        OrderType.ROUTINE_SERVICE.name -> "Service Rutin"
        OrderType.EMERGENCY_SERVICE.name -> "Perbaikan Darurat"
        else -> ""
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Column {
            Text(
                orderType,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (order.dateTime != "") {
                Text(
                    Util.formatDateTime(LocalDateTime.parse(order.dateTime)),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                order.customerName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                MapUtil.getAddressFromLocation(context, LatLng(order.latitude, order.longitude)),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}