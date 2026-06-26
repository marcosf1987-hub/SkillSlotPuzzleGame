package com.skillslot.core.domain

import com.skillslot.core.model.GameState
import com.skillslot.core.model.ProgressionConfig
import com.skillslot.core.model.SlotSpinResult

class SpinSlotUseCase(
    private val slotEngine: SlotEngine,
) {
    data class Outcome(
        val newState: GameState,
        val spinResult: SlotSpinResult,
    )

    operator fun invoke(state: GameState): Outcome {
        if (!state.isSessionActive) {
            return Outcome(state, SlotSpinResult(emptyList(), 0, null))
        }
        val spin = slotEngine.spin()
        val newState = state.copy(slotPoints = state.slotPoints + spin.pointsAwarded)
        return Outcome(newState, spin)
    }
}

class EnterPuzzleUseCase(
    private val consumePointsOnStart: Boolean = true,
) {
    operator fun invoke(state: GameState): GameState {
        if (!state.puzzleUnlockAvailable) return state
        val newPoints = if (consumePointsOnStart) {
            (state.slotPoints - state.pointsThreshold).coerceAtLeast(0)
        } else {
            state.slotPoints
        }
        return state.copy(slotPoints = newPoints)
    }
}

class CompletePuzzleUseCase {
    operator fun invoke(state: GameState): GameState {
        if (!state.isSessionActive) return state
        var updated = state.copy(
            completedPuzzlesInTier = state.completedPuzzlesInTier + 1,
            totalPuzzlesEverCompleted = state.totalPuzzlesEverCompleted + 1,
        )
        if (updated.completedPuzzlesInTier >= ProgressionConfig.PUZZLES_PER_TIER) {
            val nextTier = (updated.currentTier + 1).coerceAtMost(ProgressionConfig.MAX_TIER)
            updated = updated.copy(
                currentTier = nextTier,
                completedPuzzlesInTier = 0,
                pointsThreshold = ProgressionConfig.pointsThresholdForTier(nextTier),
            )
        }
        return updated
    }
}

class FailPuzzleUseCase {
    operator fun invoke(state: GameState): GameState {
        if (!state.isSessionActive) return state
        val newLives = (state.lives - 1).coerceAtLeast(0)
        return state.copy(
            lives = newLives,
            isSessionActive = newLives > 0,
        )
    }
}
