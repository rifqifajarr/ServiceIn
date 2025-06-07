package com.servicein.ui.screen.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.servicein.R
import com.servicein.core.util.OrderType
import com.servicein.core.util.Util
import java.time.LocalDateTime

@Composable
fun HistoryDetailView(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val selectedHistoryItem by viewModel.selectedHistoryItem.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 48.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                stringResource(R.string.order_summary),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    when (selectedHistoryItem?.orderType) {
                        OrderType.LIGHT_REPAIR.name -> "Perbaikan Ringan"
                        OrderType.ROUTINE_SERVICE.name -> "Service Rutin"
                        OrderType.EMERGENCY_SERVICE.name -> "Perbaikan Darurat"
                        else -> ""
                    },
                    style = MaterialTheme.typography.titleMedium
                )
                if (selectedHistoryItem?.dateTime != "") {
                    Text(
                        Util.formatDateTime(
                            LocalDateTime.parse(selectedHistoryItem?.dateTime)
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                "Rating",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < (selectedHistoryItem?.rating
                                ?: 3)
                        ) MaterialTheme.colorScheme.secondary else Color.LightGray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "\"${selectedHistoryItem?.review}\"",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(48.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.avatar),
                    contentDescription = "Avatar",
                    modifier = Modifier.size(42.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    selectedHistoryItem?.technicianName ?: "",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                stringResource(R.string.subtotal),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(24.dp))
            if (selectedHistoryItem != null) {
                RenderPriceBreakdown(
                    Util.calculateSubtotal(
                        selectedHistoryItem!!.typeEnum,
                        selectedHistoryItem!!.value
                    )
                )
            }
        }
    }
}

@Composable
fun RenderPriceBreakdown(
    priceList: List<Int>,
) {
    val labels = listOf("Jasa Service", "Biaya Transportasi", "Fee Aplikasi")

    labels.zip(priceList).forEach { (label, price) ->
        Spacer(Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(Util.formatRupiah(price), style = MaterialTheme.typography.bodyMedium)
        }
    }

    Spacer(Modifier.height(12.dp))
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Subtotal:", style = MaterialTheme.typography.titleMedium)
        Text(Util.formatRupiah(priceList.sum()), style = MaterialTheme.typography.titleMedium)
    }
}