package com.skillslot.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.skillslot.core.data.local.entity.LeaderboardEntryEntity

@Dao
interface LeaderboardDao {
    @Insert
    suspend fun insert(entry: LeaderboardEntryEntity)

    @Query(
        """
        SELECT * FROM leaderboard_entries
        ORDER BY sessionScore DESC, submittedAtEpochMs ASC
        LIMIT :limit
        """,
    )
    suspend fun topScores(limit: Int): List<LeaderboardEntryEntity>

    @Query("SELECT COUNT(*) FROM leaderboard_entries WHERE playerId = :playerId AND submittedAtEpochMs >= :sinceEpochMs")
    suspend fun countSubmissionsSince(playerId: String, sinceEpochMs: Long): Int
}
