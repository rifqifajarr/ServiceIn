package com.servicein.ui.screen.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.servicein.core.util.MapUtil
import com.servicein.ui.component.OrderHistoryItem

@Composable
fun HistoryView(
    viewModel: HistoryViewModel = viewModel(),
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val historyList by viewModel.historyList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getHistoryList()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "History Pesanan",
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(24.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(historyList) {
                OrderHistoryItem(
                    orderType = it.orderType,
                    address = MapUtil.getAddressFromLocation(context, it.address),
                    dateTime = it.dateTime,
                    value = it.value,
                    orderStatus = it.orderStatus,
                    modifier = Modifier
                        .clickable {
                            viewModel.selectHistoryitem(it)
                            navController.navigate("history_detail")
                        }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}