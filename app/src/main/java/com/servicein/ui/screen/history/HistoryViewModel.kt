package com.servicein.ui.screen.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicein.domain.model.Order
import com.servicein.domain.usecase.GetOrderUseCase
import com.servicein.domain.usecase.ManagePreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor (
    private val getOrderUseCase: GetOrderUseCase,
    private val preferencesUseCase: ManagePreferencesUseCase,
): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _historyList = MutableStateFlow<List<Order>>(emptyList())
    val historyList: StateFlow<List<Order>> = _historyList

    private val _selectedHistoryItem = MutableStateFlow<Order?>(null)
    val selectedHistoryItem: StateFlow<Order?> = _selectedHistoryItem

    fun selectHistoryitem(order: Order) {
        _selectedHistoryItem.value = order
        Log.d("HistoryViewModel", "Selected history item: $order")
    }

    fun getHistoryList() {
        _isLoading.value = true
        viewModelScope.launch {
            getOrderUseCase.getOrderHistory(
                preferencesUseCase.customerId.first(),
            ).fold(
                onSuccess = {
                    _historyList.value = it
                    Log.d("HistoryViewModel", "History list: $it")
                    _isLoading.value = false
                },
                onFailure = {
                    Log.e("HistoryViewModel", "Error getting history list", it)
                    _isLoading.value = false
                }
            )
        }
    }
}