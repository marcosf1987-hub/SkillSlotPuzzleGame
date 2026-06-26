package com.skillslot.core.data.repository

import com.skillslot.core.data.local.dao.GameStateDao
import com.skillslot.core.data.local.toEntity
import com.skillslot.core.data.local.toGameState
import com.skillslot.core.data.preferences.UserPreferencesRepository
import com.skillslot.core.model.GameState
import com.skillslot.core.model.PlayerMode
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

@Singleton
class GameStateRepository @Inject constructor(
    private val gameStateDao: GameStateDao,
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    fun observeSavedGameState(): Flow<GameState?> =
        gameStateDao.observeSavedGame().combine(userPreferencesRepository.preferences) { entity, prefs ->
            if (!prefs.isPremium) return@combine null
            entity?.toGameState()?.copy(playerMode = PlayerMode.PREMIUM)
        }

    suspend fun loadInitialGameState(): GameState {
        val prefs = userPreferencesRepository.preferences.first()
        if (!prefs.isPremium) {
            return GameState.newSession(PlayerMode.FREE)
        }
        val saved = gameStateDao.getSavedGame()
        return saved?.toGameState() ?: GameState.newSession(PlayerMode.PREMIUM)
    }

    suspend fun saveGameState(state: GameState) {
        val isPremium = userPreferencesRepository.preferences.first().isPremium
        if (!isPremium) return
        gameStateDao.upsert(state.copy(playerMode = PlayerMode.PREMIUM).toEntity())
    }

    suspend fun clearSavedGameState() {
        gameStateDao.clear()
    }
}
