package com.skillslot.app.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillslot.core.data.preferences.UserPreferencesRepository
import com.skillslot.core.domain.AdsManagerContract
import com.skillslot.core.domain.CompletePuzzleUseCase
import com.skillslot.core.domain.EnterPuzzleUseCase
import com.skillslot.core.domain.FailPuzzleUseCase
import com.skillslot.core.domain.PremiumManagerContract
import com.skillslot.core.domain.RecoverLifeUseCase
import com.skillslot.core.domain.SaveProgressUseCase
import com.skillslot.core.domain.SessionStateHolder
import com.skillslot.core.domain.SpinSlotUseCase
import com.skillslot.core.domain.StartNewSessionUseCase
import com.skillslot.core.model.GameState
import com.skillslot.core.model.PlayerMode
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface GameSessionEvent {
    data object NavigateToPuzzle : GameSessionEvent
    data object NavigateToSlots : GameSessionEvent
    data object NavigateToGameOver : GameSessionEvent
    data object ShowInterstitial : GameSessionEvent
    data object ShowRewarded : GameSessionEvent
}

@HiltViewModel
class GameSessionViewModel @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val spinSlotUseCase: SpinSlotUseCase,
    private val enterPuzzleUseCase: EnterPuzzleUseCase,
    private val completePuzzleUseCase: CompletePuzzleUseCase,
    private val failPuzzleUseCase: FailPuzzleUseCase,
    private val recoverLifeUseCase: RecoverLifeUseCase,
    private val saveProgressUseCase: SaveProgressUseCase,
    private val startNewSessionUseCase: StartNewSessionUseCase,
    private val puzzleRegistry: PuzzleRegistry,
    private val premiumManager: PremiumManagerContract,
    private val adsManager: AdsManagerContract,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    val gameState: StateFlow<GameState> = sessionStateHolder.state

    val isPremium: StateFlow<Boolean> = premiumManager.isPremium
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private val _showAds = MutableStateFlow(true)
    val showAds: StateFlow<Boolean> = _showAds.asStateFlow()

    init {
        viewModelScope.launch {
            premiumManager.isPremium.collect { premium ->
                _showAds.value = adsManager.canShowAds(premium)
                sessionStateHolder.update { state ->
                    state.copy(
                        playerMode = if (premium) PlayerMode.PREMIUM else PlayerMode.FREE,
                    )
                }
            }
        }
    }

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

    private val _events = MutableSharedFlow<GameSessionEvent>(extraBufferCapacity = 4)
    val events: SharedFlow<GameSessionEvent> = _events.asSharedFlow()

    private var pendingAfterInterstitial: (() -> Unit)? = null
    private var pendingAfterRewarded: (() -> Unit)? = null

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
            userPreferencesRepository.incrementSpins()
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

    fun onBeforeVictory(onReady: () -> Unit) {
        runWithInterstitial(onReady)
    }

    fun onBeforeDefeat(onReady: () -> Unit) {
        runWithInterstitial(onReady)
    }

    fun onPuzzleCompleted() {
        val before = sessionStateHolder.state.value
        val updated = completePuzzleUseCase(before)
        val tierUp = updated.currentTier > before.currentTier
        sessionStateHolder.replace(updated)
        _puzzleSession.value = null
        _slotReturnMessage.value = when {
            tierUp -> "¡Subiste al tier ${updated.currentTier}! Nuevo umbral: ${updated.pointsThreshold} pts"
            else -> "¡Puzzle completado! Sigue acumulando puntos."
        }
        viewModelScope.launch {
            userPreferencesRepository.incrementPuzzlesCompleted()
        }
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

    fun onWatchRewardedForLife() {
        pendingAfterRewarded = {
            val recovered = recoverLifeUseCase(sessionStateHolder.state.value)
            if (recovered != null) {
                sessionStateHolder.replace(recovered)
                _puzzleSession.value = null
                persist(recovered)
                _slotReturnMessage.value = "¡Vida recuperada!"
                _events.tryEmit(GameSessionEvent.NavigateToSlots)
            }
        }
        _events.tryEmit(GameSessionEvent.ShowRewarded)
    }

    fun onInterstitialFinished() {
        pendingAfterInterstitial?.invoke()
        pendingAfterInterstitial = null
    }

    fun showPendingInterstitial() {
        viewModelScope.launch {
            adsManager.showInterstitial { onInterstitialFinished() }
        }
    }

    fun showPendingRewarded() {
        viewModelScope.launch {
            var earned = false
            adsManager.showRewarded(
                onReward = { earned = true },
                onFinished = { onRewardedFinished(earned) },
            )
        }
    }

    fun onRewardedFinished(didEarnReward: Boolean) {
        if (didEarnReward) {
            pendingAfterRewarded?.invoke()
        }
        pendingAfterRewarded = null
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

    private fun runWithInterstitial(onReady: () -> Unit) {
        viewModelScope.launch {
            val premium = premiumManager.isPremium.first()
            if (!premium && adsManager.canShowInterstitial(premium)) {
                pendingAfterInterstitial = onReady
                _events.tryEmit(GameSessionEvent.ShowInterstitial)
            } else {
                onReady()
            }
        }
    }

    private fun buildPuzzleSession(state: GameState): PuzzleSession? {
        val type = state.currentPuzzleType ?: return null
        val puzzle = puzzleRegistry.get(type) ?: return null
        val difficulty = ProgressionConfig.puzzleDifficulty(state.currentTier)
        val seed = puzzleSeed(state, type)
        return puzzle.createSession(difficulty, seed)
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
