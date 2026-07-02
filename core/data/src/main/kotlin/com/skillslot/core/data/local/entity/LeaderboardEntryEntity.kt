package com.skillslot.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leaderboard_entries")
data class LeaderboardEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val alias: String,
    val sessionScore: Int,
    val tierReached: Int,
    val puzzlesCompleted: Int,
    val playerId: String,
    val submittedAtEpochMs: Long,
)
