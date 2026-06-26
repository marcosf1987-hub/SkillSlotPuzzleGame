package com.skillslot.app.di

import com.skillslot.puzzle.engine.IPuzzle
import com.skillslot.puzzle.engine.PuzzleRegistry
import com.skillslot.puzzles.ballsort.BallsortPuzzle
import com.skillslot.puzzles.boggle.BogglePuzzle
import com.skillslot.puzzles.connect.ConnectPuzzle
import com.skillslot.puzzles.maze.MazePuzzle
import com.skillslot.puzzles.memory.MemoryPuzzle
import com.skillslot.puzzles.nonogram.NonogramPuzzle
import com.skillslot.puzzles.sequence.SequencePuzzle
import com.skillslot.puzzles.sliding.SlidingPuzzle
import com.skillslot.puzzles.sudoku.SudokuPuzzle
import com.skillslot.puzzles.wordsearch.WordsearchPuzzle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PuzzleModule {
    @Provides
    @Singleton
    fun providePuzzleRegistry(): PuzzleRegistry {
        val puzzles: Set<IPuzzle> = setOf(
            WordsearchPuzzle(),
            SudokuPuzzle(),
            BallsortPuzzle(),
            MazePuzzle(),
            BogglePuzzle(),
            MemoryPuzzle(),
            NonogramPuzzle(),
            SlidingPuzzle(),
            ConnectPuzzle(),
            SequencePuzzle(),
        )
        return PuzzleRegistry(puzzles)
    }
}
