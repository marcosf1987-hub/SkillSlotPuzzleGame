package com.skillslot.core.model

data class GameState(
    val slotPoints: Int = 0,
    val pointsThreshold: Int = ProgressionConfig.pointsThresholdForTier(1),
    val currentTier: Int = 1,
    val completedPuzzlesInTier: Int = 0,
    val totalPuzzlesEverCompleted: Int = 0,
    val highestTierReached: Int = 1,
    val tiersCompleted: Int = 0,
    val lives: Int = ProgressionConfig.DEFAULT_MAX_LIVES,
    val maxLives: Int = ProgressionConfig.DEFAULT_MAX_LIVES,
    val puzzleQueue: List<PuzzleType> = PuzzleType.defaultQueue,
    val playerMode: PlayerMode = PlayerMode.FREE,
    val isSessionActive: Boolean = true,
) {
    val currentPuzzleType: PuzzleType?
        get() = puzzleQueue.getOrNull(completedPuzzlesInTier)

    val puzzleUnlockAvailable: Boolean
        get() = slotPoints >= pointsThreshold && currentPuzzleType != null

    val puzzleProgressInTier: Float
        get() = completedPuzzlesInTier / ProgressionConfig.PUZZLES_PER_TIER.toFloat()

    val thresholdProgress: Float
        get() = if (pointsThreshold <= 0) 0f else (slotPoints.toFloat() / pointsThreshold).coerceIn(0f, 1f)

    val sessionScore: Int
        get() = ProgressionConfig.calculateSessionScore(
            currentTier = currentTier,
            completedPuzzlesInTier = completedPuzzlesInTier,
            totalPuzzlesEverCompleted = totalPuzzlesEverCompleted,
            slotPoints = slotPoints,
        )

    fun withThresholdForCurrentTier(): GameState =
        copy(
            pointsThreshold = ProgressionConfig.pointsThresholdForTier(currentTier),
            maxLives = ProgressionConfig.settings.maxLives,
        )

    companion object {
        fun newSession(playerMode: PlayerMode = PlayerMode.FREE): GameState =
            GameState(
                playerMode = playerMode,
                lives = ProgressionConfig.settings.maxLives,
                maxLives = ProgressionConfig.settings.maxLives,
            ).withThresholdForCurrentTier()
    }
}
