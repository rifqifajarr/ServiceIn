package com.servicein.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.servicein.R
import com.servicein.core.util.OrderStatus
import com.servicein.core.util.OrderType
import com.servicein.core.util.Util
import java.time.LocalDateTime

@Composable
fun OrderHistoryItem(
    orderType: OrderType,
    address: String,
    dateTime: LocalDateTime,
    value: Int,
    orderStatus: OrderStatus,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = when (orderType) {
                    OrderType.LIGHT_SERVICE -> painterResource(R.drawable.light_repair_bike)
                    OrderType.ROUTINE_SERVICE -> painterResource(R.drawable.routine_service_bike)
                    OrderType.EMERGENCY_SERVICE -> painterResource(R.drawable.emergency_service_bike)
                },
                contentDescription = "Order Type",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.width(116.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    when (orderType) {
                        OrderType.LIGHT_SERVICE -> "Perbaikan Ringan"
                        OrderType.ROUTINE_SERVICE -> "Service Rutin"
                        OrderType.EMERGENCY_SERVICE -> "Perbaikan Darurat"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    address,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    Util.formatDateTime(dateTime),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    Util.formatRupiah(value),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(
                                if (orderStatus == OrderStatus.COMPLETED) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondary
                            )
                    ) {
                        Icon(
                            imageVector = if (orderStatus == OrderStatus.COMPLETED) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = "Order Status Icon",
                            tint = Color.White,
                            modifier = Modifier
                                .size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (orderStatus == OrderStatus.COMPLETED) "Pesanan Selesai" else "Pesanan Dibatalkan",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}