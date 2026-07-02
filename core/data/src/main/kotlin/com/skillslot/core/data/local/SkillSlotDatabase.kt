package com.skillslot.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.skillslot.core.data.local.dao.GameStateDao
import com.skillslot.core.data.local.dao.LeaderboardDao
import com.skillslot.core.data.local.entity.LeaderboardEntryEntity
import com.skillslot.core.data.local.entity.PuzzleTypeConverters
import com.skillslot.core.data.local.entity.SavedGameStateEntity

@Database(
    entities = [
        SavedGameStateEntity::class,
        LeaderboardEntryEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
@TypeConverters(PuzzleTypeConverters::class)
abstract class SkillSlotDatabase : RoomDatabase() {
    abstract fun gameStateDao(): GameStateDao
    abstract fun leaderboardDao(): LeaderboardDao
}
