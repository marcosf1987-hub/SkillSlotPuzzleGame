package com.skillslot.app.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillslot.core.data.preferences.UserPreferencesRepository
import com.skillslot.core.domain.GetLeaderboardUseCase
import com.skillslot.core.domain.SubmitLeaderboardScoreUseCase
import com.skillslot.core.model.GameState
import com.skillslot.core.model.LeaderboardEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LeaderboardUiState(
    val entries: List<LeaderboardEntry> = emptyList(),
    val localPlayerId: String = "",
    val isLoading: Boolean = true,
)

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val getLeaderboardUseCase: GetLeaderboardUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val playerId = userPreferencesRepository.ensureLocalPlayerId()
            val entries = getLeaderboardUseCase()
            _uiState.value = LeaderboardUiState(
                entries = entries,
                localPlayerId = playerId,
                isLoading = false,
            )
        }
    }
}

data class GameOverUiState(
    val alias: String = "",
    val submitMessage: String? = null,
    val isSubmitting: Boolean = false,
    val submitted: Boolean = false,
)

@HiltViewModel
class GameOverViewModel @Inject constructor(
    private val submitLeaderboardScoreUseCase: SubmitLeaderboardScoreUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameOverUiState())
    val uiState: StateFlow<GameOverUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val prefs = userPreferencesRepository.preferences.first()
            _uiState.update { it.copy(alias = prefs.defaultAlias) }
        }
    }

    fun onAliasChange(alias: String) {
        _uiState.update { it.copy(alias = alias, submitMessage = null) }
    }

    fun submitScore(gameState: GameState) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, submitMessage = null) }
            val playerId = userPreferencesRepository.ensureLocalPlayerId()
            when (
                val result = submitLeaderboardScoreUseCase(
                    alias = _uiState.value.alias,
                    sessionScore = gameState.sessionScore,
                    tierReached = gameState.highestTierReached,
                    puzzlesCompleted = gameState.totalPuzzlesEverCompleted,
                    playerId = playerId,
                )
            ) {
                is SubmitLeaderboardScoreUseCase.Result.Success -> {
                    userPreferencesRepository.setDefaultAlias(_uiState.value.alias.trim())
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            submitted = true,
                            submitMessage = "¡Puntuación publicada!",
                        )
                    }
                }
                is SubmitLeaderboardScoreUseCase.Result.Error -> {
                    _uiState.update {
                        it.copy(isSubmitting = false, submitMessage = result.message)
                    }
                }
            }
        }
    }
}
