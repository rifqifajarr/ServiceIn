package com.servicein.domain.preference

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val Context.dataStore by preferencesDataStore("app_preferences")
        private val CUSTOMER_ID = stringPreferencesKey("customer_id")
        private val CUSTOMER_NAME = stringPreferencesKey("customer_name")
    }

    suspend fun setCustomerId(customerId: String) {
        context.dataStore.edit { preferences ->
            preferences[CUSTOMER_ID] = customerId
        }
    }

    val customerId: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[CUSTOMER_ID] ?: ""
        }

    suspend fun setCustomerName(customerName: String) {
        context.dataStore.edit { preferences ->
            preferences[CUSTOMER_NAME] = customerName
        }
    }

    val customerName: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[CUSTOMER_NAME] ?: ""
        }
}