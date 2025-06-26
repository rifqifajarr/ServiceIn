package com.servicein.domain.usecase

import com.servicein.domain.preferences.IAppPreferencesManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManagePreferencesUseCase @Inject constructor(
    private val preferencesManager: IAppPreferencesManager
) {
    suspend fun setCustomerId(customerId: String) {
        preferencesManager.setCustomerId(customerId)
    }

    suspend fun setCustomerName(customerName: String) {
        preferencesManager.setCustomerName(customerName)
    }

    suspend fun clearAllPreferences() {
        preferencesManager.clearAll()
    }

    val customerId: Flow<String>
        get() = preferencesManager.customerId

    val customerName: Flow<String>
        get() = preferencesManager.customerName
}