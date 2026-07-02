package com.skillslot.core.data.local

import com.skillslot.core.data.local.entity.SavedGameStateEntity
import com.skillslot.core.model.GameState
import com.skillslot.core.model.PuzzleType

fun GameState.toEntity(updatedAtEpochMs: Long = System.currentTimeMillis()): SavedGameStateEntity =
    SavedGameStateEntity(
        slotPoints = slotPoints,
        pointsThreshold = pointsThreshold,
        currentTier = currentTier,
        completedPuzzlesInTier = completedPuzzlesInTier,
        totalPuzzlesEverCompleted = totalPuzzlesEverCompleted,
        highestTierReached = highestTierReached,
        tiersCompleted = tiersCompleted,
        lives = lives,
        maxLives = maxLives,
        puzzleQueue = puzzleQueue,
        updatedAtEpochMs = updatedAtEpochMs,
    )

fun SavedGameStateEntity.toGameState(): GameState =
    GameState(
        slotPoints = slotPoints,
        pointsThreshold = pointsThreshold,
        currentTier = currentTier,
        completedPuzzlesInTier = completedPuzzlesInTier,
        totalPuzzlesEverCompleted = totalPuzzlesEverCompleted,
        highestTierReached = highestTierReached,
        tiersCompleted = tiersCompleted,
        lives = lives,
        maxLives = maxLives,
        puzzleQueue = puzzleQueue.ifEmpty { PuzzleType.defaultQueue },
        playerMode = com.skillslot.core.model.PlayerMode.PREMIUM,
        isSessionActive = true,
    )
