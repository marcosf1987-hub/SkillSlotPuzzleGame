package com.skillslot.core.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "skillslot_user_preferences",
)

data class UserPreferences(
    val isPremium: Boolean = false,
    val defaultAlias: String = "",
    val localPlayerId: String = "",
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
)

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val dataStore = context.userPreferencesDataStore

    val preferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            isPremium = prefs[Keys.IS_PREMIUM] ?: false,
            defaultAlias = prefs[Keys.DEFAULT_ALIAS] ?: "",
            localPlayerId = prefs[Keys.LOCAL_PLAYER_ID] ?: "",
            soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
            vibrationEnabled = prefs[Keys.VIBRATION_ENABLED] ?: true,
        )
    }

    suspend fun setPremium(isPremium: Boolean) {
        dataStore.edit { it[Keys.IS_PREMIUM] = isPremium }
    }

    suspend fun setDefaultAlias(alias: String) {
        dataStore.edit { it[Keys.DEFAULT_ALIAS] = alias }
    }

    suspend fun setLocalPlayerId(playerId: String) {
        dataStore.edit { it[Keys.LOCAL_PLAYER_ID] = playerId }
    }

    private object Keys {
        val IS_PREMIUM = booleanPreferencesKey("is_premium")
        val DEFAULT_ALIAS = stringPreferencesKey("default_alias")
        val LOCAL_PLAYER_ID = stringPreferencesKey("local_player_id")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
    }
}
