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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.servicein.R
import com.servicein.core.util.OrderType

@Composable
fun OrderTypeItem(
    orderType: OrderType,
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
                    when (orderType) {
                        OrderType.LIGHT_SERVICE -> stringResource(R.string.light_repair_desc)
                        OrderType.ROUTINE_SERVICE -> stringResource(R.string.routine_service_desc)
                        OrderType.EMERGENCY_SERVICE -> stringResource(R.string.emergency_order_desc)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
        }
    }
}