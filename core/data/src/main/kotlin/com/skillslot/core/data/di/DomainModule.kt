package com.skillslot.core.data.di

import com.skillslot.core.data.repository.GameStateRepositoryAdapter
import com.skillslot.core.data.repository.UserPreferencesAdapter
import com.skillslot.core.domain.CompletePuzzleUseCase
import com.skillslot.core.domain.EnterPuzzleUseCase
import com.skillslot.core.domain.FailPuzzleUseCase
import com.skillslot.core.domain.RecoverLifeUseCase
import com.skillslot.core.domain.GameStateRepositoryContract
import com.skillslot.core.domain.LoadGameStateUseCase
import com.skillslot.core.domain.SaveProgressUseCase
import com.skillslot.core.domain.SessionStateHolder
import com.skillslot.core.domain.SlotEngine
import com.skillslot.core.domain.SpinSlotUseCase
import com.skillslot.core.domain.StartNewSessionUseCase
import com.skillslot.core.domain.UserPreferencesContract
import com.skillslot.core.model.GameState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Provides
    @Singleton
    fun provideGameStateRepositoryContract(
        adapter: GameStateRepositoryAdapter,
    ): GameStateRepositoryContract = adapter

    @Provides
    @Singleton
    fun provideUserPreferencesContract(
        adapter: UserPreferencesAdapter,
    ): UserPreferencesContract = adapter

    @Provides
    @Singleton
    fun provideLoadGameStateUseCase(
        repository: GameStateRepositoryContract,
    ): LoadGameStateUseCase = LoadGameStateUseCase(repository)

    @Provides
    @Singleton
    fun provideSaveProgressUseCase(
        repository: GameStateRepositoryContract,
        preferences: UserPreferencesContract,
    ): SaveProgressUseCase = SaveProgressUseCase(repository, preferences)

    @Provides
    @Singleton
    fun provideStartNewSessionUseCase(
        repository: GameStateRepositoryContract,
        preferences: UserPreferencesContract,
    ): StartNewSessionUseCase = StartNewSessionUseCase(repository, preferences)

    @Provides
    @Singleton
    fun provideSessionStateHolder(): SessionStateHolder =
        SessionStateHolder(GameState.newSession())

    @Provides
    @Singleton
    fun provideSlotEngine(): SlotEngine = SlotEngine()

    @Provides
    fun provideSpinSlotUseCase(engine: SlotEngine): SpinSlotUseCase = SpinSlotUseCase(engine)

    @Provides
    fun provideEnterPuzzleUseCase(): EnterPuzzleUseCase = EnterPuzzleUseCase(consumePointsOnStart = true)

    @Provides
    fun provideCompletePuzzleUseCase(): CompletePuzzleUseCase = CompletePuzzleUseCase()

    @Provides
    fun provideFailPuzzleUseCase(): FailPuzzleUseCase = FailPuzzleUseCase()

    @Provides
    fun provideRecoverLifeUseCase(): RecoverLifeUseCase = RecoverLifeUseCase()
}
