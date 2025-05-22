package com.servicein.ui.screen.history

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.servicein.core.util.OrderStatus
import com.servicein.core.util.OrderType
import com.servicein.domain.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime

class HistoryViewModel(): ViewModel() {
    private val _historyList = MutableStateFlow<List<Order>>(emptyList())
    val historyList: StateFlow<List<Order>> = _historyList

    private val _selectedHistoryItem = MutableStateFlow<Order?>(null)
    val selectedHistoryItem: StateFlow<Order?> = _selectedHistoryItem

    fun selectHistoryitem(order: Order) {
        _selectedHistoryItem.value = order
        Log.d("HistoryViewModel", "Selected history item: $order")
    }

    fun getHistoryList() {
        _historyList.value = listOf(
            Order(
                orderType = OrderType.LIGHT_SERVICE,
                customerName = "John Doe",
                address = LatLng(37.7749, -122.4194),
                dateTime = LocalDateTime.now(),
                value = 100000,
                orderStatus = OrderStatus.COMPLETED
            ),
            Order(
                orderType = OrderType.ROUTINE_SERVICE,
                customerName = "Jane Smith",
                address = LatLng(34.8890, -122.4194),
                dateTime = LocalDateTime.now(),
                value = 150000,
                orderStatus = OrderStatus.COMPLETED
            ),
            Order(
                orderType = OrderType.EMERGENCY_SERVICE,
                customerName = "Bob Johnson",
                address = LatLng(37.7749, -122.4194),
                dateTime = LocalDateTime.now(),
                value = 200000,
                orderStatus = OrderStatus.COMPLETED
            ),
            Order(
                orderType = OrderType.LIGHT_SERVICE,
                customerName = "John Doe",
                address = LatLng(37.7749, -122.4194),
                dateTime = LocalDateTime.now(),
                value = 100000,
                orderStatus = OrderStatus.REJECTED
            ),
            Order(
                orderType = OrderType.ROUTINE_SERVICE,
                customerName = "Jane Smith",
                address = LatLng(34.8890, -122.4194),
                dateTime = LocalDateTime.now(),
                value = 150000,
                orderStatus = OrderStatus.COMPLETED
            ),
            Order(
                orderType = OrderType.EMERGENCY_SERVICE,
                customerName = "Bob Johnson",
                address = LatLng(37.7749, -122.4194),
                dateTime = LocalDateTime.now(),
                value = 200000,
                orderStatus = OrderStatus.COMPLETED
            ),
            Order(
                orderType = OrderType.LIGHT_SERVICE,
                customerName = "John Doe",
                address = LatLng(37.7749, -122.4194),
                dateTime = LocalDateTime.now(),
                value = 100000,
                orderStatus = OrderStatus.REJECTED
            ),
        )
    }
}