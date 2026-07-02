package com.skillslot.core.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
    val tutorialSeen: Boolean = false,
    val totalSpins: Int = 0,
    val totalPuzzlesCompleted: Int = 0,
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
            tutorialSeen = prefs[Keys.TUTORIAL_SEEN] ?: false,
            totalSpins = prefs[Keys.TOTAL_SPINS] ?: 0,
            totalPuzzlesCompleted = prefs[Keys.TOTAL_PUZZLES_COMPLETED] ?: 0,
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

    suspend fun ensureLocalPlayerId(): String {
        val current = preferences.first().localPlayerId
        if (current.isNotBlank()) return current
        val id = java.util.UUID.randomUUID().toString()
        setLocalPlayerId(id)
        return id
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.SOUND_ENABLED] = enabled }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.VIBRATION_ENABLED] = enabled }
    }

    suspend fun setTutorialSeen(seen: Boolean = true) {
        dataStore.edit { it[Keys.TUTORIAL_SEEN] = seen }
    }

    suspend fun incrementSpins() {
        dataStore.edit { prefs ->
            val current = prefs[Keys.TOTAL_SPINS] ?: 0
            prefs[Keys.TOTAL_SPINS] = current + 1
        }
    }

    suspend fun incrementPuzzlesCompleted() {
        dataStore.edit { prefs ->
            val current = prefs[Keys.TOTAL_PUZZLES_COMPLETED] ?: 0
            prefs[Keys.TOTAL_PUZZLES_COMPLETED] = current + 1
        }
    }

    private object Keys {
        val IS_PREMIUM = booleanPreferencesKey("is_premium")
        val DEFAULT_ALIAS = stringPreferencesKey("default_alias")
        val LOCAL_PLAYER_ID = stringPreferencesKey("local_player_id")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val TUTORIAL_SEEN = booleanPreferencesKey("tutorial_seen")
        val TOTAL_SPINS = intPreferencesKey("total_spins")
        val TOTAL_PUZZLES_COMPLETED = intPreferencesKey("total_puzzles_completed")
    }
}
