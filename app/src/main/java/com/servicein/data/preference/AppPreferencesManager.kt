package com.servicein.data.preference

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.servicein.domain.preferences.IAppPreferencesManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) : IAppPreferencesManager {
    companion object {
        private val Context.dataStore by preferencesDataStore("app_preferences")
        private val CUSTOMER_ID = stringPreferencesKey("customer_id")
        private val CUSTOMER_NAME = stringPreferencesKey("customer_name")
    }

    override suspend fun setCustomerId(customerId: String) {
        context.dataStore.edit { preferences ->
            preferences[CUSTOMER_ID] = customerId
        }
    }

    override val customerId: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[CUSTOMER_ID] ?: ""
        }

    override suspend fun setCustomerName(customerName: String) {
        context.dataStore.edit { preferences ->
            preferences[CUSTOMER_NAME] = customerName
        }
    }

    override val customerName: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[CUSTOMER_NAME] ?: ""
        }

    override suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}