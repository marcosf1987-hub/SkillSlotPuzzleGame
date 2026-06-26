package com.skillslot.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skillslot.core.model.PuzzleType

@Entity(tableName = "saved_game_state")
data class SavedGameStateEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val slotPoints: Int,
    val pointsThreshold: Int,
    val currentTier: Int,
    val completedPuzzlesInTier: Int,
    val totalPuzzlesEverCompleted: Int,
    val lives: Int,
    val maxLives: Int,
    val puzzleQueue: List<PuzzleType>,
    val updatedAtEpochMs: Long,
) {
    companion object {
        const val SINGLETON_ID = 1
    }
}
