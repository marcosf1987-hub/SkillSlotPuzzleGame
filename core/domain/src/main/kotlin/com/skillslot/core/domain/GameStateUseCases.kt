package com.skillslot.core.domain

import com.skillslot.core.model.GameState

interface GameStateRepositoryContract {
    suspend fun loadInitialGameState(): GameState
    suspend fun saveGameState(state: GameState)
    suspend fun clearSavedGameState()
}

interface UserPreferencesContract {
    suspend fun isPremium(): Boolean
}

class LoadGameStateUseCase(
    private val gameStateRepository: GameStateRepositoryContract,
) {
    suspend operator fun invoke(): GameState = gameStateRepository.loadInitialGameState()
}

class SaveProgressUseCase(
    private val gameStateRepository: GameStateRepositoryContract,
    private val userPreferences: UserPreferencesContract,
) {
    suspend operator fun invoke(state: GameState) {
        if (!userPreferences.isPremium()) return
        gameStateRepository.saveGameState(state)
    }
}

class StartNewSessionUseCase(
    private val gameStateRepository: GameStateRepositoryContract,
    private val userPreferences: UserPreferencesContract,
) {
    suspend operator fun invoke(): GameState {
        gameStateRepository.clearSavedGameState()
        val isPremium = userPreferences.isPremium()
        return GameState.newSession(
            playerMode = if (isPremium) {
                com.skillslot.core.model.PlayerMode.PREMIUM
            } else {
                com.skillslot.core.model.PlayerMode.FREE
            },
        )
    }
}
