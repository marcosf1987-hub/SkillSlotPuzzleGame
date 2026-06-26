package com.skillslot.core.data.repository

import com.skillslot.core.data.preferences.UserPreferencesRepository
import com.skillslot.core.domain.GameStateRepositoryContract
import com.skillslot.core.domain.UserPreferencesContract
import com.skillslot.core.model.GameState
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class GameStateRepositoryAdapter @Inject constructor(
    private val repository: GameStateRepository,
) : GameStateRepositoryContract {
    override suspend fun loadInitialGameState(): GameState = repository.loadInitialGameState()

    override suspend fun saveGameState(state: GameState) = repository.saveGameState(state)

    override suspend fun clearSavedGameState() = repository.clearSavedGameState()
}

@Singleton
class UserPreferencesAdapter @Inject constructor(
    private val repository: UserPreferencesRepository,
) : UserPreferencesContract {
    override suspend fun isPremium(): Boolean = repository.preferences.first().isPremium
}
