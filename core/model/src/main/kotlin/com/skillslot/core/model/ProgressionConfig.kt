package com.skillslot.core.model

import kotlin.random.Random

object ProgressionConfig {
    var settings: ProgressionSettings = ProgressionSettings()
        private set

    const val PUZZLES_PER_TIER = 10
    const val MAX_TIER = 10

    val DEFAULT_MAX_LIVES: Int get() = settings.maxLives
    val BASE_THRESHOLD: Int get() = settings.baseThreshold
    val GROWTH_FACTOR: Double get() = settings.growthFactor

    fun applySettings(newSettings: ProgressionSettings) {
        settings = newSettings
    }

    fun pointsThresholdForTier(tier: Int): Int {
        val safeTier = tier.coerceIn(1, MAX_TIER)
        return (settings.baseThreshold * (1 + (safeTier - 1) * settings.growthFactor)).toInt()
    }

    fun puzzleDifficulty(tier: Int): Int = tier.coerceIn(1, MAX_TIER)

    fun puzzleTimeLimitSeconds(tier: Int): Int {
        val safeTier = tier.coerceIn(1, MAX_TIER)
        return 90 + safeTier * 15
    }

    fun shuffledQueueForTier(tier: Int, seed: Long): List<PuzzleType> =
        if (tier <= 1) {
            PuzzleType.defaultQueue
        } else {
            PuzzleType.defaultQueue.shuffled(Random(seed xor tier.toLong() * 31L))
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
