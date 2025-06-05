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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.LatLng
import com.servicein.R
import com.servicein.core.util.MapUtil
import com.servicein.ui.component.OrderHistoryItem
import java.time.LocalDateTime

@Composable
fun HistoryView(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val context = LocalContext.current
    val historyList by viewModel.historyList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getHistoryList()
    }

    Column(
        verticalArrangement = if (isLoading) Arrangement.Center else Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.order_history),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(24.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(historyList) {
                    val location = LatLng(it.latitude, it.longitude)
                    OrderHistoryItem(
                        orderType = it.typeEnum,
                        address = MapUtil.getAddressFromLocation(context, location),
                        dateTime = if (it.dateTime != "") LocalDateTime.parse(it.dateTime) else null,
                        value = it.value,
                        orderStatus = it.statusEnum,
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
}