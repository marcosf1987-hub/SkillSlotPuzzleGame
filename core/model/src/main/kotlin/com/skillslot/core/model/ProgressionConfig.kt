package com.skillslot.core.model

object ProgressionConfig {
    const val DEFAULT_MAX_LIVES = 3
    const val PUZZLES_PER_TIER = 10
    const val MAX_TIER = 10
    const val BASE_THRESHOLD = 500
    const val GROWTH_FACTOR = 0.5

    fun pointsThresholdForTier(tier: Int): Int {
        val safeTier = tier.coerceIn(1, MAX_TIER)
        return (BASE_THRESHOLD * (1 + (safeTier - 1) * GROWTH_FACTOR)).toInt()
    }

    fun puzzleDifficulty(tier: Int): Int = tier.coerceIn(1, MAX_TIER)

    /** Segundos para completar un puzzle; escala con tier y palabras/grid más grandes. */
    fun puzzleTimeLimitSeconds(tier: Int): Int {
        val safeTier = tier.coerceIn(1, MAX_TIER)
        return 90 + safeTier * 15
    }

    fun calculateSessionScore(
        currentTier: Int,
        completedPuzzlesInTier: Int,
        totalPuzzlesEverCompleted: Int,
        slotPoints: Int,
    ): Int =
        (currentTier * 1000) +
            (completedPuzzlesInTier * 100) +
            (totalPuzzlesEverCompleted * 50) +
            (slotPoints / 10)
}
