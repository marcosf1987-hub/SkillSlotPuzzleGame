package com.skillslot.core.domain

import com.skillslot.core.model.LeaderboardEntry

interface LeaderboardRepositoryContract {
    suspend fun submit(entry: LeaderboardEntry)
    suspend fun topScores(limit: Int = 100): List<LeaderboardEntry>
}

class SubmitLeaderboardScoreUseCase(
    private val repository: LeaderboardRepositoryContract,
) {
    sealed interface Result {
        data object Success : Result
        data class Error(val message: String) : Result
    }

    suspend operator fun invoke(
        alias: String,
        sessionScore: Int,
        tierReached: Int,
        puzzlesCompleted: Int,
        playerId: String,
    ): Result {
        val trimmed = alias.trim()
        if (trimmed.length < 3) return Result.Error("El alias debe tener al menos 3 caracteres")
        if (trimmed.length > 16) return Result.Error("El alias no puede superar 16 caracteres")
        repository.submit(
            LeaderboardEntry(
                alias = trimmed,
                sessionScore = sessionScore,
                tierReached = tierReached,
                puzzlesCompleted = puzzlesCompleted,
                playerId = playerId,
                submittedAtEpochMs = System.currentTimeMillis(),
            ),
        )
        return Result.Success
    }
}

class GetLeaderboardUseCase(
    private val repository: LeaderboardRepositoryContract,
) {
    suspend operator fun invoke(limit: Int = 100): List<LeaderboardEntry> =
        repository.topScores(limit)
}
