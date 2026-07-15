package com.unichat.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val SERVER_URL = stringPreferencesKey("server_url")
        val SELECTED_MODEL = stringPreferencesKey("selected_model")
    }

    val serverUrl: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[SERVER_URL] ?: "http://192.168.1.100:8080/v1/"
    }

    val selectedModel: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[SELECTED_MODEL] ?: "local"
    }

    suspend fun saveServerUrl(url: String) {
        context.dataStore.edit { prefs ->
            prefs[SERVER_URL] = if (url.endsWith("/")) url else "$url/"
        }
    }

    suspend fun saveSelectedModel(model: String) {
        context.dataStore.edit { prefs ->
            prefs[SELECTED_MODEL] = model
        }
    }
}
