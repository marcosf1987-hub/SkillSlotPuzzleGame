package com.skillslot.core.data.di

import android.content.Context
import androidx.room.Room
import com.skillslot.core.data.local.SkillSlotDatabase
import com.skillslot.core.data.local.dao.GameStateDao
import com.skillslot.core.data.local.dao.LeaderboardDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SkillSlotDatabase =
        Room.databaseBuilder(
            context,
            SkillSlotDatabase::class.java,
            "skillslot.db",
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideGameStateDao(database: SkillSlotDatabase): GameStateDao =
        database.gameStateDao()

    @Provides
    fun provideLeaderboardDao(database: SkillSlotDatabase): LeaderboardDao =
        database.leaderboardDao()
}
