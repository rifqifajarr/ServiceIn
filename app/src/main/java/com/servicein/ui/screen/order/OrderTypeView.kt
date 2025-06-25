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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.servicein.R
import com.servicein.core.util.OrderType
import com.servicein.ui.component.OrderTypeItem
import com.servicein.ui.component.ScheduleOrderBottomSheet
import com.servicein.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTypeView(
    modifier: Modifier = Modifier,
    viewModel: OrderViewModel = hiltViewModel(),
    navController: NavHostController,
    shopId: String?,
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
                if (shopId == "") {
                    navController.navigate(Screen.OrderLocation.createRoute(""))
                } else {
                    navController.navigate(Screen.OrderLocation.createRoute(shopId))
                }
            }
        )
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
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
                    viewModel.selectOrderType(OrderType.EMERGENCY_SERVICE)
                    if (shopId == "") {
                        navController.navigate(Screen.OrderLocation.createRoute(""))
                    } else {
                        navController.navigate(Screen.OrderLocation.createRoute(shopId))
                    }
                }
        )
        Spacer(Modifier.height(12.dp))
        OrderTypeItem(
            orderType = OrderType.ROUTINE_SERVICE,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.selectOrderType(OrderType.ROUTINE_SERVICE)
                    showScheduleBottomSheet = true
                }
        )
        Spacer(Modifier.height(12.dp))
        OrderTypeItem(
            orderType = OrderType.LIGHT_REPAIR,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.selectOrderType(OrderType.LIGHT_REPAIR)
                    showScheduleBottomSheet = true
                }
        )
    }
}