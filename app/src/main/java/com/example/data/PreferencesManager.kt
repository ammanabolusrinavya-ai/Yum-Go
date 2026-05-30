package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    companion object {
        val DELIVERY_ADDRESS = stringPreferencesKey("delivery_address")
    }

    val deliveryAddressFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[DELIVERY_ADDRESS] ?: "123 Main St, Apartment 4B"
        }

    suspend fun saveDeliveryAddress(address: String) {
        context.dataStore.edit { preferences ->
            preferences[DELIVERY_ADDRESS] = address
        }
    }
}
