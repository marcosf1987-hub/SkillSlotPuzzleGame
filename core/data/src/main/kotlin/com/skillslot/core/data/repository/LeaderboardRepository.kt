package com.skillslot.core.data.repository

import com.skillslot.core.data.local.dao.LeaderboardDao
import com.skillslot.core.data.local.entity.LeaderboardEntryEntity
import com.skillslot.core.domain.LeaderboardRepositoryContract
import com.skillslot.core.model.LeaderboardEntry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardRepository @Inject constructor(
    private val leaderboardDao: LeaderboardDao,
) : LeaderboardRepositoryContract {
    override suspend fun submit(entry: LeaderboardEntry) {
        leaderboardDao.insert(entry.toEntity())
    }

    override suspend fun topScores(limit: Int): List<LeaderboardEntry> =
        leaderboardDao.topScores(limit).map { it.toModel() }

    private fun LeaderboardEntry.toEntity() = LeaderboardEntryEntity(
        id = id,
        alias = alias,
        sessionScore = sessionScore,
        tierReached = tierReached,
        puzzlesCompleted = puzzlesCompleted,
        playerId = playerId,
        submittedAtEpochMs = submittedAtEpochMs,
    )

    private fun LeaderboardEntryEntity.toModel() = LeaderboardEntry(
        id = id,
        alias = alias,
        sessionScore = sessionScore,
        tierReached = tierReached,
        puzzlesCompleted = puzzlesCompleted,
        playerId = playerId,
        submittedAtEpochMs = submittedAtEpochMs,
    )
}

@Singleton
class LeaderboardRepositoryAdapter @Inject constructor(
    private val repository: LeaderboardRepository,
) : LeaderboardRepositoryContract by repository
