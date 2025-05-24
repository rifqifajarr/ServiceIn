package com.servicein.ui.screen.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.servicein.R
import com.servicein.core.util.OrderType
import com.servicein.ui.component.OrderTypeItem
import com.servicein.ui.component.ScheduleOrderBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTypeView(
    modifier: Modifier = Modifier,
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController,
    shopId: Int,
) {
    val scheduleSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showScheduleBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(showScheduleBottomSheet) {
        if (showScheduleBottomSheet) {
            scheduleSheetState.show()
        } else {
            scheduleSheetState.hide()
        }
    }

    if (showScheduleBottomSheet) {
        ScheduleOrderBottomSheet(
            sheetState = scheduleSheetState,
            onDismissRequest = {
                showScheduleBottomSheet = false
            },
            onSubmitButtonClick = { time ->
                viewModel.setSelectedDate(time)
                navController.navigate("order_location/$shopId")
            }
        )
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.order_type),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(24.dp))
        OrderTypeItem(
            orderType = OrderType.EMERGENCY_SERVICE,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.selectOrder(OrderType.EMERGENCY_SERVICE)
                    navController.navigate("order_location/$shopId")
                }
        )
        Spacer(Modifier.height(12.dp))
        OrderTypeItem(
            orderType = OrderType.ROUTINE_SERVICE,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.selectOrder(OrderType.ROUTINE_SERVICE)
                    showScheduleBottomSheet = true
                }
        )
        Spacer(Modifier.height(12.dp))
        OrderTypeItem(
            orderType = OrderType.LIGHT_SERVICE,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.selectOrder(OrderType.LIGHT_SERVICE)
                    showScheduleBottomSheet = true
                }
        )
    }
}