package com.skillslot.core.model

data class ProgressionSettings(
    val baseThreshold: Int = 500,
    val growthFactor: Double = 0.5,
    val maxLives: Int = 3,
    val consumePointsOnPuzzleStart: Boolean = true,
    val pairPayout: Int = 50,
    val consolationMin: Int = 10,
    val consolationMax: Int = 30,
)

data class LeaderboardEntry(
    val id: Long = 0,
    val alias: String,
    val sessionScore: Int,
    val tierReached: Int,
    val puzzlesCompleted: Int,
    val playerId: String,
    val submittedAtEpochMs: Long,
)
