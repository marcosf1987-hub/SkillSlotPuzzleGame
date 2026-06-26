package com.skillslot.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skillslot.core.data.local.entity.SavedGameStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameStateDao {
    @Query("SELECT * FROM saved_game_state WHERE id = :id LIMIT 1")
    fun observeSavedGame(id: Int = SavedGameStateEntity.SINGLETON_ID): Flow<SavedGameStateEntity?>

    @Query("SELECT * FROM saved_game_state WHERE id = :id LIMIT 1")
    suspend fun getSavedGame(id: Int = SavedGameStateEntity.SINGLETON_ID): SavedGameStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SavedGameStateEntity)

    @Query("DELETE FROM saved_game_state")
    suspend fun clear()
}
