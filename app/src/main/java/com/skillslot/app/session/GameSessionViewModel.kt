package com.skillslot.app.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillslot.core.domain.CompletePuzzleUseCase
import com.skillslot.core.domain.EnterPuzzleUseCase
import com.skillslot.core.domain.FailPuzzleUseCase
import com.skillslot.core.domain.SaveProgressUseCase
import com.skillslot.core.domain.SessionStateHolder
import com.skillslot.core.domain.SpinSlotUseCase
import com.skillslot.core.domain.StartNewSessionUseCase
import com.skillslot.core.model.GameState
import com.skillslot.core.model.ProgressionConfig
import com.skillslot.core.model.PuzzleType
import com.skillslot.core.model.SlotSpinResult
import com.skillslot.puzzle.engine.PuzzleRegistry
import com.skillslot.puzzle.engine.PuzzleSession
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface GameSessionEvent {
    data object NavigateToPuzzle : GameSessionEvent
    data object NavigateToSlots : GameSessionEvent
    data object NavigateToGameOver : GameSessionEvent
}

@HiltViewModel
class GameSessionViewModel @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val spinSlotUseCase: SpinSlotUseCase,
    private val enterPuzzleUseCase: EnterPuzzleUseCase,
    private val completePuzzleUseCase: CompletePuzzleUseCase,
    private val failPuzzleUseCase: FailPuzzleUseCase,
    private val saveProgressUseCase: SaveProgressUseCase,
    private val startNewSessionUseCase: StartNewSessionUseCase,
    private val puzzleRegistry: PuzzleRegistry,
) : ViewModel() {
    val gameState: StateFlow<GameState> = sessionStateHolder.state

    private val _lastSpin = MutableStateFlow<SlotSpinResult?>(null)
    val lastSpin: StateFlow<SlotSpinResult?> = _lastSpin.asStateFlow()

    private val _isSpinning = MutableStateFlow(false)
    val isSpinning: StateFlow<Boolean> = _isSpinning.asStateFlow()

    private val _showUnlockDialog = MutableStateFlow(false)
    val showUnlockDialog: StateFlow<Boolean> = _showUnlockDialog.asStateFlow()

    private val _puzzleSession = MutableStateFlow<PuzzleSession?>(null)
    val puzzleSession: StateFlow<PuzzleSession?> = _puzzleSession.asStateFlow()

    private val _slotReturnMessage = MutableStateFlow<String?>(null)
    val slotReturnMessage: StateFlow<String?> = _slotReturnMessage.asStateFlow()

    private val _events = MutableSharedFlow<GameSessionEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<GameSessionEvent> = _events.asSharedFlow()

    fun spin() {
        if (_isSpinning.value) return
        viewModelScope.launch {
            _isSpinning.value = true
            kotlinx.coroutines.delay(960)
            val outcome = spinSlotUseCase(sessionStateHolder.state.value)
            sessionStateHolder.replace(outcome.newState)
            _lastSpin.value = outcome.spinResult
            _isSpinning.value = false
            if (outcome.newState.puzzleUnlockAvailable) {
                _showUnlockDialog.value = true
            }
            persist(outcome.newState)
            if (!outcome.newState.isSessionActive) {
                _events.tryEmit(GameSessionEvent.NavigateToGameOver)
            }
        }
    }

    fun dismissUnlockDialog() {
        _showUnlockDialog.value = false
    }

    fun acceptPuzzle() {
        val updated = enterPuzzleUseCase(sessionStateHolder.state.value)
        sessionStateHolder.replace(updated)
        _showUnlockDialog.value = false
        _puzzleSession.value = buildPuzzleSession(updated)
        persist(updated)
        _events.tryEmit(GameSessionEvent.NavigateToPuzzle)
    }

    fun tryOpenPuzzleSession(): PuzzleSession? {
        _puzzleSession.value?.let { return it }
        val state = sessionStateHolder.state.value
        if (!state.puzzleUnlockAvailable) return null
        val updated = enterPuzzleUseCase(state)
        sessionStateHolder.replace(updated)
        _puzzleSession.value = buildPuzzleSession(updated)
        persist(updated)
        return _puzzleSession.value
    }

    private fun buildPuzzleSession(state: GameState): PuzzleSession? {
        val type = state.currentPuzzleType ?: return null
        val puzzle = puzzleRegistry.get(type) ?: return null
        val difficulty = ProgressionConfig.puzzleDifficulty(state.currentTier)
        val seed = puzzleSeed(state, type)
        return puzzle.createSession(difficulty, seed)
    }

    fun onPuzzleCompleted() {
        val updated = completePuzzleUseCase(sessionStateHolder.state.value)
        sessionStateHolder.replace(updated)
        _puzzleSession.value = null
        _slotReturnMessage.value = "¡Puzzle completado! Sigue acumulando puntos."
        persist(updated)
        _events.tryEmit(GameSessionEvent.NavigateToSlots)
    }

    fun onPuzzleFailed() {
        val updated = failPuzzleUseCase(sessionStateHolder.state.value)
        sessionStateHolder.replace(updated)
        _puzzleSession.value = null
        _slotReturnMessage.value = if (updated.isSessionActive) {
            "Perdiste una vida. ¡Vuelve a intentarlo!"
        } else {
            null
        }
        persist(updated)
        if (!updated.isSessionActive) {
            _events.tryEmit(GameSessionEvent.NavigateToGameOver)
        } else {
            _events.tryEmit(GameSessionEvent.NavigateToSlots)
        }
    }

    fun clearSlotReturnMessage() {
        _slotReturnMessage.value = null
    }

    fun startNewSession() {
        viewModelScope.launch {
            val state = startNewSessionUseCase()
            sessionStateHolder.replace(state)
            _lastSpin.value = null
            _showUnlockDialog.value = false
            _puzzleSession.value = null
        }
    }

    private fun persist(state: GameState) {
        viewModelScope.launch {
            saveProgressUseCase(state)
        }
    }

    private fun puzzleSeed(state: GameState, type: PuzzleType): Long {
        var hash = 17L
        hash = hash * 31 + state.currentTier
        hash = hash * 31 + state.completedPuzzlesInTier
        hash = hash * 31 + type.ordinal
        return hash
    }
}
