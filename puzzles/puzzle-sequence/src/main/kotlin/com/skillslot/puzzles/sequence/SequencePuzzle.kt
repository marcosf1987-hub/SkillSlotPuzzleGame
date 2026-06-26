package com.skillslot.puzzles.sequence

import com.skillslot.core.model.PuzzleType
import com.skillslot.puzzle.engine.IPuzzle
import com.skillslot.puzzle.engine.PuzzleSession
import com.skillslot.puzzle.engine.PuzzleUiState
import com.skillslot.puzzle.engine.PuzzleAction
import com.skillslot.puzzle.engine.PuzzleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow

/** Stub Fase 0 — implementación en fases posteriores. */
class SequencePuzzle : IPuzzle {
    override val type: PuzzleType = PuzzleType.SEQUENCE

    override fun createSession(difficulty: Int, seed: Long): PuzzleSession =
        StubPuzzleSession(type.displayName)
}

private class StubPuzzleSession(title: String) : PuzzleSession {
    override val state = MutableStateFlow(PuzzleUiState(title = title, subtitle = "Próximamente"))
    override fun onUserAction(action: PuzzleAction) = Unit
    override val result: Flow<PuzzleResult> = emptyFlow()
}
