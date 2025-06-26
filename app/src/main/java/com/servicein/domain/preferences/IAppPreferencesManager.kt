package com.servicein.domain.preferences

import kotlinx.coroutines.flow.Flow

interface IAppPreferencesManager {
    suspend fun setCustomerId(customerId: String)
    val customerId: Flow<String>
    suspend fun setCustomerName(customerName: String)
    val customerName: Flow<String>
    suspend fun clearAll()
}